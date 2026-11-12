/*
 * Copyright (c) 2026 by Oliver Boehm
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.cups4j.operations.cups;

import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CupsAddModifyPrinterOperation}.
 *
 * @author oboehm
 * @since 0.8.4
 */
public class CupsAddModifyPrinterOperationTest extends AbstractIppOperationTest {

    private static final String ADDED_PRINTER_NAME = "test-printer-added";
    private static final URI DEVICE_URI = URI.create("socket://localhost:9100");
    private static final URI CUPS_PRINTER_URI = URI.create("http://cups.example.com:631/printers/test-printer");

    /**
     * Removes the printer created by a test so that the tests do not influence each other.
     *
     * @throws IOException          in case of I/O problems
     * @throws InterruptedException if the container command is interrupted
     */
    @AfterEach
    public void removeAddedPrinter() throws IOException, InterruptedException {
        this.cups.execInContainer("lpadmin", "-x", ADDED_PRINTER_NAME);
    }

    /**
     * Verifies that the generated IPP header uses the CUPS-Add-Modify-Printer operation ID
     * ({@code 0x4003}).
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void operationIdIsCupsAddModifyPrinter() throws IOException {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation();
        // WHEN
        ByteBuffer header = operation.getIppHeader(CUPS_PRINTER_URI, new HashMap<>());
        // THEN
        assertEquals("0x4003", getOperationId(header));
    }

    /**
     * Verifies that all supported printer attributes are encoded in the printer-attributes group
     * of the IPP header.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void ippHeaderContainsPrinterAttributes() throws IOException {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        attributes.put("device-uri", "ipp://192.168.1.50:631/ipp/print");
        attributes.put("printer-info", "Network printer");
        attributes.put("printer-location", "1st floor");
        attributes.put("printer-is-accepting-jobs", "true");
        attributes.put("printer-is-shared", "true");
        attributes.put("ppd-name", "everywhere");
        // WHEN
        ByteBuffer header = new CupsAddModifyPrinterOperation().getIppHeader(CUPS_PRINTER_URI, attributes);
        // THEN
        checkAttribute(header, "printer-uri", "http://cups.example.com/printers/test-printer");
        checkAttribute(header, "device-uri", "ipp://192.168.1.50:631/ipp/print");
        checkAttribute(header, "printer-info", "Network printer");
        checkAttribute(header, "printer-location", "1st floor");
        checkAttribute(header, "printer-is-accepting-jobs", "true");
        checkAttribute(header, "printer-is-shared", "true");
        checkAttribute(header, "ppd-name", "everywhere");
    }

    /**
     * Verifies that a {@code printer-uri} supplied in the attribute map takes precedence over the
     * URI passed to {@link CupsAddModifyPrinterOperation#getIppHeader(URI, Map)}. This is required
     * because the request is sent to the CUPS admin resource ({@code /admin/}) while the
     * {@code printer-uri} has to point to the printer resource ({@code /printers/<name>}).
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void ippHeaderUsesPrinterUriFromAttributes() throws IOException {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        attributes.put("printer-uri", "ipp://cups.example.com/printers/other-printer");
        attributes.put("device-uri", "socket://192.168.1.50:9100");
        attributes.put("requesting-user-name", "admin");
        // WHEN
        ByteBuffer header = new CupsAddModifyPrinterOperation().getIppHeader(CUPS_PRINTER_URI, attributes);
        // THEN
        checkAttribute(header, "printer-uri", "ipp://cups.example.com/printers/other-printer");
        checkAttribute(header, "device-uri", "socket://192.168.1.50:9100");
        checkAttribute(header, "requesting-user-name", "admin");
    }

    /**
     * Verifies that boolean printer attributes with the value {@code false} are encoded as
     * {@code false} and are not omitted from the IPP header.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void ippHeaderContainsFalseBoolean() throws IOException {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        attributes.put("printer-is-accepting-jobs", "false");
        attributes.put("printer-is-shared", "false");
        // WHEN
        ByteBuffer header = new CupsAddModifyPrinterOperation().getIppHeader(CUPS_PRINTER_URI, attributes);
        // THEN
        checkAttribute(header, "printer-is-accepting-jobs", "false");
        checkAttribute(header, "printer-is-shared", "false");
    }

    /**
     * Adds a new printer to the CUPS test container and verifies that the printer can be retrieved
     * afterwards with the expected name, device URI, description and location.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addModifyPrinterAddsPrinter() throws Exception {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation(this.cups.getFirstMappedPort());
        // WHEN
        boolean success = operation.addModifyPrinter(this.cups.getHost(), ADDED_PRINTER_NAME, DEVICE_URI,
                createPrinterAttributes(), this.creds);
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals(ADDED_PRINTER_NAME, printer.getName());
        assertEquals(DEVICE_URI.toString(), printer.getDeviceURI());
        assertEquals("Added by cups4j", printer.getDescription());
        assertEquals("1st floor", printer.getLocation());
    }

    /**
     * Adds a printer to the CUPS test container, modifies its location in a second call and
     * verifies that the modified value is persisted by the CUPS server.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addModifyPrinterModifiesPrinter() throws Exception {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation(this.cups.getFirstMappedPort());
        URI cupsURI = URI.create("http://" + this.cups.getHost() + ":" + this.cups.getFirstMappedPort());
        assertTrue(operation.addModifyPrinter(cupsURI, ADDED_PRINTER_NAME, DEVICE_URI,
                createPrinterAttributes(), this.creds));
        Map<String, String> modifiedAttributes = createPrinterAttributes();
        modifiedAttributes.put("printer-location", "2nd floor");
        // WHEN
        boolean success = operation.addModifyPrinter(cupsURI, ADDED_PRINTER_NAME, DEVICE_URI,
                modifiedAttributes, this.creds);
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals("2nd floor", printer.getLocation());
    }

    /**
     * Verifies that adding a printer with invalid admin credentials fails with an
     * {@link IOException} instead of silently creating or modifying a printer.
     */
    @Test
    public void addModifyPrinterRequiresAdminCredentials() {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation(this.cups.getFirstMappedPort());
        CupsAuthentication wrongCredentials = new CupsAuthentication("admin", "wrong");
        // WHEN
        Executable addPrinter = () -> operation.addModifyPrinter(this.cups.getHost(),
                ADDED_PRINTER_NAME, DEVICE_URI, createPrinterAttributes(), wrongCredentials);
        // THEN
        assertThrows(IOException.class, addPrinter);
    }

    /**
     * Verifies that an empty printer name is rejected with an {@link IllegalArgumentException}
     * before a request is sent to the CUPS server.
     */
    @Test
    public void addModifyPrinterRejectsEmptyPrinterName() {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation();
        // WHEN
        Executable addPrinter = () -> operation.addModifyPrinter(URI.create("http://cups.example.com:631"), "",
                URI.create("ipp://192.168.1.50"), null, null);
        // THEN
        assertThrows(IllegalArgumentException.class, addPrinter);
    }

    /**
     * Verifies that a missing device URI is rejected with an {@link IllegalArgumentException}
     * before a request is sent to the CUPS server.
     */
    @Test
    public void addModifyPrinterRejectsNullDeviceUri() {
        // GIVEN
        CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation();
        // WHEN
        Executable addPrinter = () -> operation.addModifyPrinter(URI.create("http://cups.example.com:631"),
                "test-printer", null, null, null);
        // THEN
        assertThrows(IllegalArgumentException.class, addPrinter);
    }

    private static Map<String, String> createPrinterAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("printer-info", "Added by cups4j");
        attributes.put("printer-location", "1st floor");
        attributes.put("printer-is-shared", "true");
        attributes.put("printer-is-accepting-jobs", "true");
        return attributes;
    }

    private static String getOperationId(ByteBuffer header) {
        ByteBuffer buffer = header.duplicate();
        buffer.rewind();
        buffer.position(2);
        return String.format("0x%04X", (int) buffer.getShort());
    }

}

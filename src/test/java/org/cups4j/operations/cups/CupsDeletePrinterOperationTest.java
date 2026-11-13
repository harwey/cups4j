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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CupsDeletePrinterOperation}.
 *
 * @author oboehm
 * @since 0.8.4
 */
public class CupsDeletePrinterOperationTest extends AbstractIppOperationTest {

    private static final String DELETED_PRINTER_NAME = "test-printer-deleted";
    private static final URI CUPS_PRINTER_URI = URI.create("http://cups.example.com:631/printers/test-printer");

    /**
     * Removes the printer created by a test so that the tests do not influence each other.
     *
     * @throws IOException          in case of I/O problems
     * @throws InterruptedException if the container command is interrupted
     */
    @AfterEach
    public void removeTestPrinter() throws IOException, InterruptedException {
        this.cups.execInContainer("lpadmin", "-x", DELETED_PRINTER_NAME);
    }

    /**
     * Verifies that the generated IPP header uses the CUPS-Delete-Printer operation ID
     * ({@code 0x4004}).
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void operationIdIsCupsDeletePrinter() throws IOException {
        // GIVEN
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation();
        // WHEN
        ByteBuffer header = operation.getIppHeader(CUPS_PRINTER_URI, new HashMap<>());
        // THEN
        assertEquals("0x4004", getOperationId(header));
    }

    /**
     * Verifies that the {@code printer-uri} and the requesting user are encoded in the
     * operation-attributes group of the IPP header.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void ippHeaderContainsPrinterUri() throws IOException {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        attributes.put("printer-uri", "ipp://cups.example.com/printers/test-printer");
        attributes.put("requesting-user-name", "admin");
        // WHEN
        ByteBuffer header = new CupsDeletePrinterOperation().getIppHeader(CUPS_PRINTER_URI, attributes);
        // THEN
        checkAttribute(header, "printer-uri", "ipp://cups.example.com/printers/test-printer");
        checkAttribute(header, "requesting-user-name", "admin");
    }

    /**
     * Verifies that the URI passed to {@link CupsDeletePrinterOperation#getIppHeader(URI, Map)} is
     * used as {@code printer-uri} if the attribute map does not contain one.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void ippHeaderUsesUriAsFallback() throws IOException {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        // WHEN
        ByteBuffer header = new CupsDeletePrinterOperation().getIppHeader(CUPS_PRINTER_URI, attributes);
        // THEN
        checkAttribute(header, "printer-uri", "http://cups.example.com/printers/test-printer");
    }

    /**
     * Deletes a printer from the CUPS test container and verifies that it is no longer available.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void deletePrinterDeletesPrinter() throws Exception {
        // GIVEN
        createPrinter();
        assertNotNull(this.client.getPrinter(DELETED_PRINTER_NAME));
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation(this.cups.getFirstMappedPort());
        // WHEN
        boolean success = operation.deletePrinter(this.cups.getHost(), DELETED_PRINTER_NAME, this.creds);
        // THEN
        assertTrue(success);
        assertNull(this.client.getPrinter(DELETED_PRINTER_NAME));
    }

    /**
     * Deletes a printer from the CUPS test container using the URI variant and verifies that it is
     * no longer available.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void deletePrinterWithUriDeletesPrinter() throws Exception {
        // GIVEN
        createPrinter();
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation(this.cups.getFirstMappedPort());
        URI cupsURI = URI.create("http://" + this.cups.getHost() + ":" + this.cups.getFirstMappedPort());
        // WHEN
        boolean success = operation.deletePrinter(cupsURI, DELETED_PRINTER_NAME, this.creds);
        // THEN
        assertTrue(success);
        assertNull(this.client.getPrinter(DELETED_PRINTER_NAME));
    }

    /**
     * Verifies that deleting a printer with invalid admin credentials fails with an
     * {@link IOException} and that the printer still exists.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void deletePrinterRequiresAdminCredentials() throws Exception {
        // GIVEN
        createPrinter();
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation(this.cups.getFirstMappedPort());
        CupsAuthentication wrongCredentials = new CupsAuthentication("admin", "wrong");
        // WHEN
        Executable deletePrinter = () -> operation.deletePrinter(this.cups.getHost(),
                DELETED_PRINTER_NAME, wrongCredentials);
        // THEN
        assertThrows(IOException.class, deletePrinter);
        assertNotNull(this.client.getPrinter(DELETED_PRINTER_NAME));
    }

    /**
     * Verifies that an empty printer name is rejected with an {@link IllegalArgumentException}
     * before a request is sent to the CUPS server.
     */
    @Test
    public void deletePrinterRejectsEmptyPrinterName() {
        // GIVEN
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation();
        // WHEN
        Executable deletePrinter = () -> operation.deletePrinter(URI.create("http://cups.example.com:631"), "",
                null);
        // THEN
        assertThrows(IllegalArgumentException.class, deletePrinter);
    }

    /**
     * Verifies that a {@code null} CUPS URI is rejected with an
     * {@link IllegalArgumentException} before a request is sent to the CUPS server.
     */
    @Test
    public void deletePrinterRejectsNullCupsUri() {
        // GIVEN
        CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation();
        // WHEN
        Executable deletePrinter = () -> operation.deletePrinter((URI) null, "test-printer", null);
        // THEN
        assertThrows(IllegalArgumentException.class, deletePrinter);
    }

    private void createPrinter() throws IOException, InterruptedException {
        this.cups.execInContainer("lpadmin", "-p", DELETED_PRINTER_NAME, "-E", "-v",
                "socket://localhost:9100", "-o", "printer-is-shared=true");
    }

    private static String getOperationId(ByteBuffer header) {
        ByteBuffer buffer = header.duplicate();
        buffer.rewind();
        buffer.position(2);
        return String.format("0x%04X", (int) buffer.getShort());
    }

}

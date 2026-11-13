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
package org.cups4j;

import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the printer administration methods
 * {@link CupsClient#addPrinter(String, URI)} and
 * {@link CupsClient#deletePrinter(String)} against a real CUPS server.
 *
 * @author oboehm
 * @since 0.8.4
 */
public class CupsClientIT extends AbstractIppOperationTest {

    private static final String ADDED_PRINTER_NAME = "test-printer-client";
    private static final URI DEVICE_URI = URI.create("socket://localhost:9100");

    /**
     * Removes the printer created by a test so that the tests do not
     * influence each other.
     *
     * @throws IOException          in case of I/O problems
     * @throws InterruptedException if the container command is interrupted
     */
    @AfterEach
    public void removeAddedPrinter() throws IOException, InterruptedException {
        this.cups.execInContainer("lpadmin", "-x", ADDED_PRINTER_NAME);
    }

    /**
     * Verifies that {@link CupsClient#addPrinter(String, URI)} creates a
     * new printer with the given name and device URI.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addPrinterAddsPrinter() throws Exception {
        // WHEN
        boolean success = this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI);
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals(ADDED_PRINTER_NAME, printer.getName());
        assertEquals(DEVICE_URI.toString(), printer.getDeviceURI());
    }

    /**
     * Verifies that
     * {@link CupsClient#addPrinter(String, URI, String, String)} stores the
     * given description and location of the new printer.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addPrinterWithInfoAndLocation() throws Exception {
        // WHEN
        boolean success = this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI,
                "Added by CupsClient", "2nd floor");
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals("Added by CupsClient", printer.getDescription());
        assertEquals("2nd floor", printer.getLocation());
    }

    /**
     * Verifies that
     * {@link CupsClient#addPrinter(String, URI, Map)} passes additional
     * printer attributes such as {@code printer-location} to the CUPS
     * server.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addPrinterWithAttributes() throws Exception {
        // GIVEN
        Map<String, String> attributes = new HashMap<>();
        attributes.put("printer-info", "Added with attributes");
        attributes.put("printer-location", "3rd floor");
        attributes.put("printer-is-shared", "true");
        // WHEN
        boolean success = this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI, attributes);
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals("Added with attributes", printer.getDescription());
        assertEquals("3rd floor", printer.getLocation());
    }

    /**
     * Verifies that a second call of
     * {@link CupsClient#addPrinter(String, URI, String, String)} modifies an
     * existing printer instead of failing.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void addPrinterModifiesPrinter() throws Exception {
        // GIVEN
        assertTrue(this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI, "version 1", "1st floor"));
        // WHEN
        boolean success = this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI, "version 2", "2nd floor");
        // THEN
        assertTrue(success);
        CupsPrinter printer = this.client.getPrinter(ADDED_PRINTER_NAME);
        assertNotNull(printer);
        assertEquals("version 2", printer.getDescription());
        assertEquals("2nd floor", printer.getLocation());
    }

    /**
     * Verifies that adding a printer with invalid admin credentials fails
     * with an {@link IOException} instead of silently creating the printer.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    public void addPrinterRequiresAdminCredentials() throws IOException {
        // GIVEN
        CupsClient wrongClient = new CupsClient(this.cups.getHost(), this.cups.getFirstMappedPort(),
                this.userName, new CupsAuthentication(this.userName, "wrong"));
        // WHEN
        Executable addPrinter = () -> wrongClient.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI);
        // THEN
        assertThrows(IOException.class, addPrinter);
        assertNull(this.client.getPrinter(ADDED_PRINTER_NAME));
    }

    /**
     * Verifies that a missing device URI is rejected with an
     * {@link IllegalArgumentException} before a request is sent to the CUPS
     * server.
     */
    @Test
    public void addPrinterRejectsNullDeviceUri() {
        // GIVEN
        // WHEN
        Executable addPrinter = () -> this.client.addPrinter(ADDED_PRINTER_NAME, null);
        // THEN
        assertThrows(IllegalArgumentException.class, addPrinter);
    }

    /**
     * Verifies that {@link CupsClient#deletePrinter(String)} removes an
     * existing printer.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void deletePrinterDeletesPrinter() throws Exception {
        // GIVEN
        assertTrue(this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI));
        assertNotNull(this.client.getPrinter(ADDED_PRINTER_NAME));
        // WHEN
        boolean success = this.client.deletePrinter(ADDED_PRINTER_NAME);
        // THEN
        assertTrue(success);
        assertNull(this.client.getPrinter(ADDED_PRINTER_NAME));
    }

    /**
     * Verifies that deleting a printer with invalid admin credentials fails
     * with an {@link IOException} and that the printer still exists.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void deletePrinterRequiresAdminCredentials() throws Exception {
        // GIVEN
        assertTrue(this.client.addPrinter(ADDED_PRINTER_NAME, DEVICE_URI));
        CupsClient wrongClient = new CupsClient(this.cups.getHost(), this.cups.getFirstMappedPort(),
                this.userName, new CupsAuthentication(this.userName, "wrong"));
        // WHEN
        Executable deletePrinter = () -> wrongClient.deletePrinter(ADDED_PRINTER_NAME);
        // THEN
        assertThrows(IOException.class, deletePrinter);
        assertNotNull(this.client.getPrinter(ADDED_PRINTER_NAME));
    }

    /**
     * Verifies that an empty printer name is rejected with an
     * {@link IllegalArgumentException} before a request is sent to the CUPS
     * server.
     */
    @Test
    public void deletePrinterRejectsEmptyPrinterName() {
        // GIVEN
        // WHEN
        Executable deletePrinter = () -> this.client.deletePrinter("");
        // THEN
        assertThrows(IllegalArgumentException.class, deletePrinter);
    }

}

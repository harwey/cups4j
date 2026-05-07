/*
 * Copyright (c) 2026 by Oli B.
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
package org.cups4j.operations;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public final class IppHttpTest {

    private static final URI PRINTER_URI = URI.create("ipp://localhost:631/printers/test");
    private static final String PRINTER_NAME = "my-test-printer";
    private static final CupsAuthentication AUTH = new CupsAuthentication("alice", "secret");

    @Test
    public void testCreateHttpClient() {
        HttpClient client = IppHttp.createHttpClient();
        assertNotNull(client);
    }

    @Test
    public void testSetHttpHeadersWithNullPrinter() throws Exception {
        HttpRequest request = new BasicHttpRequest("POST", "/ipp/print");
        IppHttp.setHttpHeaders(request, null, null);
        assertEquals("local", request.getHeader("target-group").getValue());
    }

    @Test
    public void testSetHttpHeadersWithPrinter() throws Exception {
        HttpRequest request = new BasicHttpRequest("POST", "/ipp/print");
        CupsPrinter printer = new CupsPrinter(null, PRINTER_URI, PRINTER_NAME);
        IppHttp.setHttpHeaders(request, printer, null);
        assertEquals(PRINTER_NAME, request.getHeader("target-group").getValue());
    }

    @Test
    public void testSetHttpHeadersNoAuthWhenNull() throws Exception {
        HttpRequest request = new BasicHttpRequest("POST", "/ipp/print");
        IppHttp.setHttpHeaders(request, null, null);
        assertNull(request.getHeader("Authorization"));
    }

    @Test
    public void testSetHttpHeadersNoAuthWhenBlankUserid() throws Exception {
        HttpRequest request = new BasicHttpRequest("POST", "/ipp/print");
        CupsAuthentication blankAuth = new CupsAuthentication("", "secret");
        IppHttp.setHttpHeaders(request, null, blankAuth);
        assertNull(request.getHeader("Authorization"));
    }

    @Test
    public void testSetHttpHeadersWithBasicAuth() throws Exception {
        HttpRequest request = new BasicHttpRequest("POST", "/ipp/print");
        IppHttp.setHttpHeaders(request, null, AUTH);
        String authHeader = request.getHeader("Authorization").getValue();
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
    }

}

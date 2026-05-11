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
import org.apache.hc.core5.http.HttpEntity;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;
import org.cups4j.http.IppRequest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;

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
    public void testCreateHttpClientIsSingleton() {
        HttpClient first = IppHttp.createHttpClient();
        HttpClient second = IppHttp.createHttpClient();
        assertSame(first, second);
    }

    @Test
    public void testSetHttpHeadersWithNullPrinter() {
        TestHttpRequest request = new TestHttpRequest();
        IppHttp.setHttpHeaders(request, null, null);
        assertEquals("local", request.getHeader("target-group"));
    }

    @Test
    public void testSetHttpHeadersWithPrinter() {
        TestHttpRequest request = new TestHttpRequest();
        CupsPrinter printer = new CupsPrinter(null, PRINTER_URI, PRINTER_NAME);
        IppHttp.setHttpHeaders(request, printer, null);
        assertEquals(PRINTER_NAME, request.getHeader("target-group"));
    }

    @Test
    public void testSetHttpHeadersNoAuthWhenNull() {
        TestHttpRequest request = new TestHttpRequest();
        IppHttp.setHttpHeaders(request, null, null);
        assertNull(request.getHeader("Authorization"));
    }

    @Test
    public void testSetHttpHeadersNoAuthWhenBlankUserid() {
        TestHttpRequest request = new TestHttpRequest();
        CupsAuthentication blankAuth = new CupsAuthentication("", "secret");
        IppHttp.setHttpHeaders(request, null, blankAuth);
        assertNull(request.getHeader("Authorization"));
    }

    @Test
    public void testSetHttpHeadersWithBasicAuth() {
        TestHttpRequest request = new TestHttpRequest();
        IppHttp.setHttpHeaders(request, null, AUTH);
        String authHeader = request.getHeader("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
    }

    private static final class TestHttpRequest implements IppRequest {

        private final Map<String, List<String>> headers = new HashMap<>();

        @Override
        public void addHeader(String name, String value) {
            headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, new ArrayList<>(Collections.singletonList(value)));
        }

        @Override
        public void setEntity(HttpEntity entity) {
            throw new UnsupportedOperationException("not implemented: setEntity " + entity);
        }

        String getHeader(String name) {
            List<String> values = headers.get(name);
            return (values != null && !values.isEmpty()) ? values.get(0) : null;
        }

    }

}

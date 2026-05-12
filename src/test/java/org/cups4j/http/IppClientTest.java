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
package org.cups4j.http;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class IppClientTest {

    private static IppRequest postRequest() {
        return ApacheIppRequest.post("http://localhost:631/ipp");
    }

    @Test
    void successfulRequest() throws IOException {
        CloseableHttpClient stub = stubClient(200, "OK", "success");
        IppClient client = new ApacheIppClient(stub);
        String result = client.execute(postRequest(), (statusCode, reasonPhrase, body) -> {
            assertEquals(200, statusCode);
            assertEquals("OK", reasonPhrase);
            return new String(IOUtils.toByteArray(body), StandardCharsets.UTF_8);
        });
        assertEquals("success", result);
    }

    @Test
    void nullEntity() throws IOException {
        CloseableHttpClient stub = stubClient(204, "No Content", (byte[]) null);
        IppClient client = new ApacheIppClient(stub);
        Integer result = client.execute(postRequest(), (statusCode, reasonPhrase, body) -> {
            assertEquals(204, statusCode);
            assertNull(body);
            return 42;
        });
        assertEquals(42, result);
    }

    @Test
    void clientErrorStatusCode() throws IOException {
        CloseableHttpClient stub = stubClient(404, "Not Found", "error");
        IppClient client = new ApacheIppClient(stub);
        String result = client.execute(postRequest(), (statusCode, reasonPhrase, body) -> {
            assertEquals(404, statusCode);
            assertEquals("Not Found", reasonPhrase);
            return "handled";
        });
        assertEquals("handled", result);
    }

    @Test
    void emptyBody() throws IOException {
        CloseableHttpClient stub = stubClient(200, "OK", new byte[0]);
        IppClient client = new ApacheIppClient(stub);
        byte[] result = client.execute(postRequest(), (statusCode, reasonPhrase, body) ->
                IOUtils.toByteArray(body));
        assertEquals(0, result.length);
    }

    @Test
    void largeBody() throws IOException {
        byte[] largeData = new byte[65536];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        CloseableHttpClient stub = stubClient(200, "OK", largeData);
        IppClient client = new ApacheIppClient(stub);
        byte[] result = client.execute(postRequest(), (statusCode, reasonPhrase, body) ->
                IOUtils.toByteArray(body));
        assertArrayEquals(largeData, result);
    }

    @Test
    void handlerReturnValue() throws IOException {
        CloseableHttpClient stub = stubClient(200, "OK", (byte[]) null);
        IppClient client = new ApacheIppClient(stub);
        String result = client.execute(postRequest(), (statusCode, reasonPhrase, body) -> "hello");
        assertEquals("hello", result);
    }

    @Test
    void ioExceptionPropagation() {
        CloseableHttpClient stub = new CloseableHttpClient() {
            @Override
            protected CloseableHttpResponse doExecute(HttpHost host, ClassicHttpRequest request,
                                                      HttpContext context) throws IOException {
                throw new IOException("connection refused");
            }

            @Override
            public void close() {}

            @Override
            public void close(org.apache.hc.core5.io.CloseMode closeMode) {}
        };
        IppClient client = new ApacheIppClient(stub);
        IOException ex = assertThrows(IOException.class,
                () -> client.execute(postRequest(), (sc, rp, body) -> "x"));
        assertTrue(ex.getMessage().contains("connection refused"));
    }

    @Test
    void classCastExceptionOnNonApacheRequest() {
        CloseableHttpClient stub = new CloseableHttpClient() {
            @Override
            protected CloseableHttpResponse doExecute(HttpHost host, ClassicHttpRequest request,
                                                      HttpContext context) {
                return adapt(new BasicClassicHttpResponse(200, "OK"));
            }

            @Override
            public void close() {}

            @Override
            public void close(org.apache.hc.core5.io.CloseMode closeMode) {}
        };
        IppClient client = new ApacheIppClient(stub);
        IppRequest nonApacheRequest = new IppRequest() {
            @Override
            public void addHeader(String name, String value) {}

            @Override
            public void setHeader(String name, String value) {}

            @Override
            public void setEntity(InputStream content, String contentType) {}
        };
        assertThrows(ClassCastException.class,
                () -> client.execute(nonApacheRequest, (sc, rp, body) -> "x"));
    }

    private static CloseableHttpClient stubClient(int statusCode, String reasonPhrase, String body) {
        byte[] data = body != null ? body.getBytes(StandardCharsets.UTF_8) : null;
        return stubClient(statusCode, reasonPhrase, data);
    }

    private static CloseableHttpClient stubClient(int statusCode, String reasonPhrase, byte[] data) {
        BasicClassicHttpResponse basicResponse = new BasicClassicHttpResponse(statusCode, reasonPhrase);
        if (data != null) {
            basicResponse.setEntity(new InputStreamEntity(
                    new ByteArrayInputStream(data), ContentType.APPLICATION_OCTET_STREAM));
        }
        CloseableHttpResponse response = adapt(basicResponse);
        return new CloseableHttpClient() {
            @Override
            protected CloseableHttpResponse doExecute(HttpHost host, ClassicHttpRequest request,
                                                      HttpContext context) {
                return response;
            }

            @Override
            public void close() {}

            @Override
            public void close(org.apache.hc.core5.io.CloseMode closeMode) {}
        };
    }

    private static CloseableHttpResponse adapt(BasicClassicHttpResponse basicResponse) {
        return CloseableHttpResponse.adapt(basicResponse);
    }

}

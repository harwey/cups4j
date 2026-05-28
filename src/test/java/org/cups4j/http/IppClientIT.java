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
import org.cups4j.operations.AbstractIppOperationTest;
import org.cups4j.operations.IppHttp;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link IppClient}.
 *
 * @author oboehm
 * @since 0.8.2 (14.05.26)
 */
public class IppClientIT extends AbstractIppOperationTest {

    protected IppClient createHttpClient() {
        return IppHttp.createHttpClient();
    }

    protected IppRequest createRequest() {
        return IppHttp.createRequest(getPrintersURI());
    }

    /**
     * As unit test we post the content of "ipp/Get-Printers.ipp". This
     * resource is a record of run of
     * {@link org.cups4j.operations.cups.CupsGetPrintersOperation}.
     *
     * @throws IOException in case of I/O problems
     */
    @Test
    void successfulRequest() throws IOException {
        try (InputStream istream = getClass().getClassLoader().getResourceAsStream("ipp/Get-Printers.ipp")) {
            assertNotNull(istream);
            IppRequest request = getRequest(istream);
            IppClient ippClient = createHttpClient();
            IppResponseHandler<byte[]> handler = (statusCode, reasonPhrase, body) -> {
                assertEquals(200, statusCode, "reason phrase: " + reasonPhrase);
                return IOUtils.toByteArray(body);
            };
            byte[] body = ippClient.execute(request, handler);
            assertTrue(body.length > 0);
        }
    }

    private IppRequest getRequest(InputStream istream) {
        IppRequest request = createRequest();
        request.setEntity(istream, "application/ipp");
        return request;
    }

    protected URI getPrintersURI() {
        int port = cups.getFirstMappedPort();
        return URI.create(String.format("http://localhost:%d/printers", port));
    }

}

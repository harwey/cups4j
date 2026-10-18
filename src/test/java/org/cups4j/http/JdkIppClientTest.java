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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link JdkIppClient}.
 *
 * @author oboehm
 * @since 0.8.3
 */
class JdkIppClientTest {

    private static final Logger log = LoggerFactory.getLogger(JdkIppClientTest.class);

    /**
     * Simulates a network failure by sending a request to a port where no
     * server is listening. The OS answers with "connection refused" which
     * must be propagated as {@link IOException}.
     *
     * @throws IOException if the unused port cannot be determined
     */
    @Test
    void networkFailure() throws IOException {
        JdkIppRequest request = JdkIppRequest.post(unusedURI());
        IppClient client = new JdkIppClient(HttpClient.newHttpClient());
        IppResponseHandler<byte[]> handler = (statusCode, reasonPhrase, body) ->
                fail("handler must not be called if the network is down");
        IOException ex = assertThrows(IOException.class,
                () -> client.execute(request, handler));
        log.info("Execution failed as expected with message '{}'.", ex.getMessage(), ex);
        assertThat(ex.getMessage(), containsString(request.getHttpRequest().uri().toString()));
    }

    private static URI unusedURI() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return URI.create("http://localhost:" + socket.getLocalPort() + "/printers");
        }
    }

}

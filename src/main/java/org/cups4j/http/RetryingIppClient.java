/*
 * Copyright (C) 2026 Oli B.
 *
 * This file is part of Cups4J. Cups4J is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Cups4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Cups4J. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.cups4j.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.http.HttpClient;

/**
 * The class RetryingIppClient ...
 *
 * @author oboehm
 * @since 0.8.3 (18.10.26)
 */
public class RetryingIppClient extends JdkIppClient implements IppClient {

    private static final Logger log = LoggerFactory.getLogger(RetryingIppClient.class);

    public RetryingIppClient(HttpClient delegate) {
        super(delegate);
    }

    @Override
    public <T> T execute(IppRequest request, IppResponseHandler<T> handler) throws IOException {
        NetworkException exception = new NetworkException("cannot connect to " + request);
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return super.execute(request, handler);
            } catch (SocketException sex) {
                log.warn("Connection {} failed ({} - use DEBUG level to see stacktrace).", request, sex.getMessage());
                log.debug("Details:", sex);
                exception.addSuppressed(sex);
                log.info("Trying reconnect of {}...", request);
            }
        }
        throw exception;
    }
}

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

import org.apache.commons.lang3.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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
        IOException last = null;
        for (int attempt = 1; ; attempt++) {
            try {
                log.debug("{}. try to connect with {}.", attempt, request);
                return super.execute(request, handler);
            } catch (SocketException sex) {
                log.warn("Connection {} failed ({} - use DEBUG level to see stacktrace).", request, sex.getMessage());
                log.debug("Details:", sex);
                if ((last != null) && !isSimilar(sex, last)) {
                    sex.addSuppressed(last);
                    throw new NetworkException("cannot connect to " + request, sex);
                }
                last = sex;
                if (attempt >= 3) {
                    log.debug("Give up to connect to {} after {} try.", request, attempt);
                    break;
                }
                Duration delay = Duration.of(attempt * 50L, ChronoUnit.MILLIS);
                log.trace("Waiting {} after {} try to connect to {}.", delay, attempt, request );
                ThreadUtils.sleepQuietly(delay);
            }
        }
        throw new NetworkException("cannot connect to " + request, last);
    }

    private static boolean isSimilar(Throwable t1, Throwable t2) {
        return t1.getClass().equals(t2.getClass())
                && Objects.equals(t1.getMessage(), t2.getMessage());
    }

}

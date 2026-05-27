/**
 * Copyright (C) 2026 Oli B.
 * <p>
 * This file is part of Cups4J. Cups4J is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * Cups4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with Cups4J. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package org.cups4j.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Integration tests for {@link JdkIppClient}.
 *
 * @author oboehm
 * @since 0.8.2 (27.05.26)
 */
public class JdkIppClientIT extends IppClientIT {

    @Override
    protected IppClient createHttpClient() {
        return new JdkIppClient(HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofMillis(10000))
                .build());
    }

    @Override
    protected IppRequest createRequest() {
        URI uri = getPrintersURI();
        return JdkIppRequest.post(uri);
    }

}

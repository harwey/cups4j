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

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link IppClient} backed by an Apache {@link HttpClient}.
 *
 * @author oboehm
 * @since 0.8.2
 */
public class ApacheIppClient implements IppClient {

    private final HttpClient delegate;

    public ApacheIppClient(HttpClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T execute(IppRequest request, IppResponseHandler<T> handler) throws IOException {
        ApacheIppRequest apacheRequest = (ApacheIppRequest) request;
        HttpClientResponseHandler<T> apacheHandler = response -> {
            int statusCode = response.getCode();
            String reasonPhrase = response.getReasonPhrase();
            InputStream body = response.getEntity() != null ? response.getEntity().getContent() : null;
            return handler.handleResponse(statusCode, reasonPhrase, body);
        };
        return delegate.execute(apacheRequest.getHttpRequest(), apacheHandler);
    }

}

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

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class JdkIppRequest implements IppRequest {

    private final URI uri;
    private final List<String[]> headers = new ArrayList<>();
    private InputStream content;
    private String contentType;

    public JdkIppRequest(URI uri) {
        this.uri = uri;
    }

    public static JdkIppRequest post(URI uri) {
        return new JdkIppRequest(uri);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.add(new String[]{name, value});
    }

    @Override
    public void setHeader(String name, String value) {
        headers.removeIf(h -> h[0].equalsIgnoreCase(name));
        headers.add(new String[]{name, value});
    }

    @Override
    public void setEntity(InputStream content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public HttpRequest getHttpRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        for (String[] header : headers) {
            builder.header(header[0], header[1]);
        }
        if (contentType != null) {
            builder.setHeader("Content-Type", contentType);
        }
        HttpRequest.BodyPublisher bodyPublisher = content != null
                ? HttpRequest.BodyPublishers.ofInputStream(() -> content)
                : HttpRequest.BodyPublishers.noBody();
        builder.method("POST", bodyPublisher);
        return builder.build();
    }

}

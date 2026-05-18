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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;

import java.io.InputStream;
import java.net.URI;

/**
 * Implementation of {@link IppRequest} backed by an Apache {@link ClassicHttpRequest}.
 *
 * @author oboehm
 * @since 0.8
 */
public class ApacheIppRequest implements IppRequest {

    private final ClassicHttpRequest delegate;

    public ApacheIppRequest(ClassicHttpRequest delegate) {
        this.delegate = delegate;
    }

    public static ApacheIppRequest post(URI uri) {
        return new ApacheIppRequest(new HttpPost(uri));
    }

    @Override
    public void addHeader(String name, String value) {
        delegate.addHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value) {
        delegate.setHeader(name, value);
    }

    @Override
    public void setEntity(InputStream content, String contentType) {
        delegate.setEntity(new InputStreamEntity(content, -1, ContentType.create(contentType)));
    }

    public ClassicHttpRequest getHttpRequest() {
        return delegate;
    }

}

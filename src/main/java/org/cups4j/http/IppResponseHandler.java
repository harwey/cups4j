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

import java.io.IOException;
import java.io.InputStream;

/**
 * Callback for handling IPP HTTP responses.
 *
 * @param <T> the return type from the handler
 * @author oboehm
 * @since 0.8.2
 */
@FunctionalInterface
public interface IppResponseHandler<T> {

    T handleResponse(int statusCode, String reasonPhrase, InputStream body) throws IOException;

}

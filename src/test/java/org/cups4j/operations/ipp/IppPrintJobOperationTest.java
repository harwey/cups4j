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
 *
 * (c)reated 14.03.26 by oboehm
 */
package org.cups4j.operations.ipp;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit-tests for {@link IppPrintJobOperation} class.
 *
 * @author oboehm
 */
public class IppPrintJobOperationTest {

    private final IppPrintJobOperation operation = new IppPrintJobOperation(631);

    @Test
    public void getIppHeader() throws UnsupportedEncodingException {
        // GIVEN
        Map<String, String> map = new HashMap<>();
        map.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome#sides:keyword:one-sided");
        map.put("job-name", "testjob");
        map.put("requesting-user-name", "oli");
        // WHEN
        ByteBuffer ippHeader = operation.getIppHeader(URI.create("http://localhost:631/printers/testfax"), map);
        byte[] bytes = new byte[ippHeader.limit()];
        ippHeader.get(bytes);
        // THEN
        String text = new String(bytes);
        assertThat(text, containsString("one-sided"));
    }

}
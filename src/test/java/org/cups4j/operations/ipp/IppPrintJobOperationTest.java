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

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.ipp.attributes.Attribute;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Map<String, String> map = createMapWithJobAttributes(
                "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome#sides:keyword:one-sided");
        // WHEN
        ByteBuffer ippHeader = operation.getIppHeader(URI.create("http://localhost:631/printers/testfax"), map);
        byte[] bytes = new byte[ippHeader.limit()];
        ippHeader.get(bytes);
        // THEN
        String text = new String(bytes);
        assertThat(text, containsString("one-sided"));
    }

    @Test
    public void getIppHeaderWithMediaCollection() throws UnsupportedEncodingException {
        // GIVEN
        Map<String, String> map = new HashMap<>();
        map.put("media-col", "media-source:keyword:tray-2");
        map.put("requesting-user-name", "oli");
        // WHEN
        ByteBuffer ippHeader = operation.getIppHeader(URI.create("http://localhost:631/printers/testfax"), map);
        byte[] bytes = new byte[ippHeader.limit()];
        ippHeader.get(bytes);
        // THEN: media-col collection members are encoded as required by RFC 8010.
        int mediaCollection = indexOf(bytes, "media-col".getBytes());
        int memberName = indexOf(bytes, "media-source".getBytes());
        assertEquals(0x34, bytes[mediaCollection - 3] & 0xff);
        assertEquals(0x4a, bytes[memberName - 5] & 0xff);
        assertEquals(0, bytes[memberName - 4]);
        assertEquals(0, bytes[memberName - 3]);
        assertEquals(0x44, bytes[memberName + "media-source".length()] & 0xff);
    }

    @Test
    void getIppHeaderWithResolution() throws IOException {
        // GIVEN
        Map<String, String> map = createMapWithJobAttributes("printer-resolution:resolution:75,75,4");
        // WHEN
        ByteBuffer ippHeader = operation.getIppHeader(URI.create("http://localhost:631/printers/testfax"), map);
        // THEN
        assertResolution("75,75,4", ippHeader);
    }

    @Test
    void getIppHeaderWithResolution300dpi() throws IOException {
        // GIVEN
        Map<String, String> map = createMapWithJobAttributes("printer-resolution:resolution:300dpi");
        // WHEN
        ByteBuffer ippHeader = operation.getIppHeader(URI.create("http://localhost:631/printers/testfax"), map);
        // THEN: IPP expects "x,y,units", so "300dpi" should be transformed to it
        assertResolution("300,300,3", ippHeader);
    }

    private static @NotNull Map<String, String> createMapWithJobAttributes(String value) {
        Map<String, String> map = new HashMap<>();
        map.put("job-attributes", value);
        map.put("job-name", "testjob");
        map.put("requesting-user-name", "oli");
        return map;
    }

    private static void assertResolution(String expected, ByteBuffer ippHeader) throws IOException {
        byte[] bytes = new byte[ippHeader.limit()];
        ippHeader.get(bytes);
        IppResult ippResult = new IppResponse().getResponse(ByteBuffer.wrap(bytes));
        Attribute resolution = ippResult.getAttributeGroup("job-attributes-tag")
                .getAttribute("printer-resolution");
        assertEquals(expected, resolution.getValue());
    }

    private static int indexOf(byte[] bytes, byte[] value) {
        for (int i = 0; i <= bytes.length - value.length; i++) {
            int j = 0;
            while (j < value.length && bytes[i + j] == value[j]) {
                j++;
            }
            if (j == value.length) {
                return i;
            }
        }
        throw new AssertionError("Expected value not found in IPP header");
    }

}

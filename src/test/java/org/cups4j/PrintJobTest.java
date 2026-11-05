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

package org.cups4j;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrintJobTest {

    /**
     * Unit tests for {@link PrintJob} class and its builder.
     *
     * @author oboehm
     */
    @Test
    void testBuilder() {
        PrintJob printJob = new PrintJob.Builder("Hello world!".getBytes())
                .attribute("print-color-mode", "color")
                .attribute("ColorModel", "RGB")
                .build();
        assertEquals(2, printJob.getAttributes().size());
    }

    @Test
    void testResolution() {
        PrintJob printJob = new PrintJob.Builder("testResolution".getBytes())
                .resolution("300dpi")
                .build();
        assertEquals("300,300,3", printJob.getResolution());
    }

    @Test
    void testMediaSource() {
        PrintJob printJob = new PrintJob.Builder("testMediaSource".getBytes())
                .mediaSource("tray-1")
                .build();
        assertEquals(1, printJob.getAttributes().size());
        assertEquals("media-source:keyword:tray-1", printJob.getAttributes().get("media-col"));
    }

    @Test
    void testNumberUp() {
        PrintJob printJob = new PrintJob.Builder("testNumberUp".getBytes())
                .numberUp(4)
                .numberUpLayout("tbrl")
                .build();
        assertEquals(1, printJob.getAttributes().size());
        assertEquals("number-up:integer:4#number-up-layout:keyword:tbrl",
                printJob.getAttributes().get("job-attributes"));
    }

    @Test
    void testNumberUpLayoutIsCaseInsensitive() {
        PrintJob printJob = new PrintJob.Builder("testNumberUpLayout".getBytes())
                .numberUpLayout("LRBT")
                .build();
        assertEquals("number-up-layout:keyword:lrbt", printJob.getAttributes().get("job-attributes"));
    }

    @Test
    void testNumberUpLayoutWithAllSupportedValues() {
        for (String layout : List.of("btlr", "btrl", "lrbt", "lrtb", "rlbt", "rltb", "tblr", "tbrl")) {
            PrintJob printJob = new PrintJob.Builder("test".getBytes()).numberUpLayout(layout).build();
            assertEquals("number-up-layout:keyword:" + layout, printJob.getAttributes().get("job-attributes"));
        }
    }

    @Test
    void testNumberUpKeepsExistingJobAttributes() {
        PrintJob printJob = new PrintJob.Builder("testNumberUp".getBytes())
                .attribute("job-attributes", "sides:keyword:two-sided-long-edge")
                .numberUp(2)
                .build();
        assertEquals("sides:keyword:two-sided-long-edge#number-up:integer:2",
                printJob.getAttributes().get("job-attributes"));
    }

    @Test
    void testNumberUpWithInvalidValue() {
        PrintJob.Builder builder = new PrintJob.Builder("testNumberUp".getBytes());
        assertThrows(IllegalArgumentException.class, () -> builder.numberUp(0));
        assertThrows(IllegalArgumentException.class, () -> builder.numberUp(-2));
    }

    @Test
    void testNumberUpLayoutWithInvalidValue() {
        PrintJob.Builder builder = new PrintJob.Builder("testNumberUpLayout".getBytes());
        assertThrows(IllegalArgumentException.class, () -> builder.numberUpLayout("diagonal"));
        assertThrows(IllegalArgumentException.class, () -> builder.numberUpLayout(""));
        assertThrows(IllegalArgumentException.class, () -> builder.numberUpLayout(null));
    }

}
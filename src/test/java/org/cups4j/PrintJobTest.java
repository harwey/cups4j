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

import org.apache.commons.lang3.StringUtils;
import org.cups4j.ipp.units.NumberUpLayout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

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
    void testPageFormat() {
        PrintJob printJob = new PrintJob.Builder("testPageFormat".getBytes())
                .pageFormat("iso-a4")
                .build();
        assertEquals("media:keyword:iso-a4", printJob.getAttributes().get("job-attributes"));
        assertEquals("iso-a4", printJob.getPageFormat());
    }

    @Test
    void testEmptyPageFormat() {
        PrintJob emptyFormat = new PrintJob.Builder("testPageFormat".getBytes()).pageFormat("").build();
        assertTrue(StringUtils.isEmpty(emptyFormat.getPageFormat()));
    }

    @Test
    void testPageFormatFromJobAttributes() {
        PrintJob printJob = new PrintJob.Builder("testPageFormat".getBytes())
                .attribute("job-attributes", "media:keyword:a5#sides:keyword:one-sided")
                .build();
        assertEquals("a5", printJob.getPageFormat());
    }

    @Test
    void testPageFormatKeepsExistingJobAttributes() {
        PrintJob printJob = new PrintJob.Builder("testPageFormat".getBytes())
                .attribute("job-attributes", "sides:keyword:one-sided")
                .pageFormat("iso-a4")
                .build();
        assertEquals("sides:keyword:one-sided#media:keyword:iso-a4",
                printJob.getAttributes().get("job-attributes"));
        assertEquals("iso-a4", printJob.getPageFormat());
    }

    @Test
    void testNumberUpLayout() {
        PrintJob printJob = new PrintJob.Builder("testNumberUpLayout".getBytes())
                .numberUpLayout(NumberUpLayout.LRBT)
                .build();
        assertEquals("number-up-layout:keyword:lrbt", printJob.getAttributes().get("job-attributes"));
        assertEquals(NumberUpLayout.LRBT, printJob.getNumberUpLayout());
    }

    @Test
    void testEmptyNumberUpLayout() {
        PrintJob printJob = new PrintJob.Builder("testNumberUpLayout".getBytes()).build();
        assertEquals(NumberUpLayout.NONE, printJob.getNumberUpLayout());
    }

    @Test
    void testNumberUpLayoutFromJobAttributes() {
        PrintJob printJob = new PrintJob.Builder("testNumberUpLayout".getBytes())
                .attribute("job-attributes", "number-up-layout:keyword:tbrl#sides:keyword:one-sided")
                .build();
        assertEquals(NumberUpLayout.TBRL, printJob.getNumberUpLayout());
    }

    @Test
    void testNumberUpLayoutWithNull() {
        PrintJob.Builder builder = new PrintJob.Builder("testNumberUpLayout".getBytes());
        assertThrows(NullPointerException.class, () -> builder.numberUpLayout(null));
    }

    @ParameterizedTest
    @EnumSource(NumberUpLayout.class)
    void testNumberUpLayout(NumberUpLayout layout) {
        PrintJob printJob = new PrintJob.Builder("test".getBytes())
                .numberUp(4)
                .numberUpLayout(layout)
                .build();
        assertEquals("number-up:integer:4#number-up-layout:keyword:" + layout.getValue(),
                printJob.getAttributes().get("job-attributes"));
        assertEquals(layout, printJob.getNumberUpLayout());
    }

    @Test
    void testNumberUpNotSet() {
        PrintJob printJob = new PrintJob.Builder("testNumberUp".getBytes()).build();
        assertEquals(0, printJob.getNumberUp());
    }

    @Test
    void testNumberUpFromJobAttributes() {
        PrintJob printJob = new PrintJob.Builder("testNumberUp".getBytes())
                .attribute("job-attributes", "number-up:integer:9#sides:keyword:one-sided")
                .build();
        assertEquals(9, printJob.getNumberUp());
    }

    @ParameterizedTest
    @CsvSource({
            "-1, false",
            "0, false",
            "1, true",
            "2, true",
            "3, false",
            "4, true"
    })
    void testNumberUp(int n, boolean valid) {
        PrintJob.Builder builder = new PrintJob.Builder("testNumberUp".getBytes())
                .attribute("job-attributes", "sides:keyword:two-sided-long-edge");
        if (valid) {
            PrintJob printJob = builder.numberUp(n).build();
            assertEquals("sides:keyword:two-sided-long-edge#number-up:integer:" + n,
                    printJob.getAttributes().get("job-attributes"));
            assertEquals(n, printJob.getNumberUp());
        } else {
            assertThrows(IllegalArgumentException.class, () -> builder.numberUp(n));
        }

    }

}
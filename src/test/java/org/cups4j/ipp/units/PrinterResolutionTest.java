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
package org.cups4j.ipp.units;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link PrinterResolution}.
 *
 * @author oboehm
 */
public final class PrinterResolutionTest {

    @ParameterizedTest
    @CsvSource({
            "300dpi,      '300,300,3'",
            "118dpcm,     '118,118,4'",
            "600x300dpi,  '600,300,3'",
            "'600,300,4', '600,300,4'"
    })
    public void testResolution(String input, String expected) {
        PrinterResolution resolution = PrinterResolution.of(input);
        assertEquals(expected, resolution.getResolution());
        assertEquals(expected, resolution.toString());
    }

    @Test
    public void testResolutionValues() {
        PrinterResolution resolution = PrinterResolution.of("600,300,4");
        assertEquals(600, resolution.getX());
        assertEquals(300, resolution.getY());
        assertEquals((byte) 4, resolution.getUnit());
    }

    @Test
    public void testInvalidResolution() {
        assertThrows(IllegalArgumentException.class, () -> PrinterResolution.of("300"));
    }

}

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
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NumberUpLayout}.
 *
 * @author oboehm
 */
public final class NumberUpLayoutTest {

    @ParameterizedTest
    @CsvSource({
            "BTLR, btlr",
            "BTRL, btrl",
            "LRBT, lrbt",
            "LRTB, lrtb",
            "RLBT, rlbt",
            "RLTB, rltb",
            "TBLR, tblr",
            "TBRL, tbrl"
    })
    public void testLayout(NumberUpLayout layout, String keyword) {
        assertEquals(keyword, layout.getValue());
        assertSame(layout, NumberUpLayout.of(keyword));
    }

    @ParameterizedTest
    @ValueSource(strings = {"btlr", "BTLR", " btlr ", "BtLr"})
    public void testOfIsCaseInsensitiveAndTrims(String keyword) {
        assertSame(NumberUpLayout.BTLR, NumberUpLayout.of(keyword));
    }

    @Test
    public void testNone() {
        assertEquals(NumberUpLayout.NONE, NumberUpLayout.of(""));
    }

}

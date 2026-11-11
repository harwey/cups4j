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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Represents the IPP {@code number-up-layout} keyword which defines the order
 * in which document pages are placed on a sheet when more than one page is
 * printed per side.
 *
 * @author oboehm
 * @since 0.8.4
 */
public enum NumberUpLayout {

    /** No layout. */
    NONE,

    /** Bottom to top, left to right. */
    BTLR,

    /** Bottom to top, right to left. */
    BTRL,

    /** Left to right, bottom to top. */
    LRBT,

    /** Left to right, top to bottom (default). */
    LRTB,

    /** Right to left, bottom to top. */
    RLBT,

    /** Right to left, top to bottom. */
    RLTB,

    /** Top to bottom, left to right. */
    TBLR,

    /** Top to bottom, right to left. */
    TBRL;

    private static final Logger log = LoggerFactory.getLogger(NumberUpLayout.class);

    /**
     * Returns the layout for the given IPP keyword (e.g. {@code "lrtb"}). The
     * keyword is matched case-insensitively and surrounding whitespace is
     * ignored.
     *
     * @param keyword IPP {@code number-up-layout} keyword
     * @return the matching layout
     * @throws IllegalArgumentException if the keyword is {@code null}, blank or
     *                                  not a known layout
     */
    public static NumberUpLayout of(String keyword) {
        if (keyword != null) {
            String value = keyword.trim().toLowerCase(Locale.ROOT);
            for (NumberUpLayout layout : values()) {
                if (layout.getValue().equals(value)) {
                    return layout;
                }
            }
        }
        log.info("Unknown keyword '{}' is mapped to NONE.", keyword);
        return NONE;
    }

    /**
     * Returns the IPP keyword of this layout (e.g. {@code "lrtb"}). The
     * value is the lowercase enum name.
     *
     * @return the IPP keyword
     */
    public String getValue() {
        return name().toLowerCase(Locale.ROOT);
    }

}

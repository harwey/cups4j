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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an IPP printer resolution.
 * <p>
 * The IPP {@code resolution} syntax consists of the cross-feed resolution
 * ({@code x}), the feed resolution ({@code y}) and the unit. The unit is
 * {@code 3} for dots per inch (dpi) and {@code 4} for dots per centimeter
 * (dpcm).
 * </p>
 * <p>
 * The human readable CUPS notation is converted into the IPP notation by
 * {@link #of(String)}:
 * </p>
 *
 * <pre>
 * PrinterResolution.of("300dpi").toString()     // "300,300,3"
 * PrinterResolution.of("118dpcm").toString()    // "118,118,4"
 * PrinterResolution.of("600x300dpi").toString() // "600,300,3"
 * </pre>
 *
 * @author oboehm
 * @since 0.8.4
 */
public class PrinterResolution {

    private static final Pattern CUPS_PATTERN =
            Pattern.compile("(\\d+)(?:x(\\d+))?(dpi|dpcm)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IPP_PATTERN =
            Pattern.compile("(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*([34])");

    private final int x;
    private final int y;
    private final byte unit;

    private PrinterResolution(int x, int y, byte unit) {
        this.x = x;
        this.y = y;
        this.unit = unit;
    }

    /**
     * Creates a printer resolution from CUPS notation (e.g. {@code "300dpi"})
     * or from the IPP notation (e.g. {@code "300,300,3"}).
     *
     * @param resolution resolution in CUPS or IPP notation
     * @return the printer resolution
     * @throws IllegalArgumentException if the resolution is not in CUPS or IPP
     *                                  notation
     */
    public static PrinterResolution of(String resolution) {
        Objects.requireNonNull(resolution, "resolution");
        String value = resolution.trim();
        Matcher cupsMatcher = CUPS_PATTERN.matcher(value);
        if (cupsMatcher.matches()) {
            int x = Integer.parseInt(cupsMatcher.group(1));
            int y = cupsMatcher.group(2) == null ? x : Integer.parseInt(cupsMatcher.group(2));
            byte unit = (byte) ("dpcm".equalsIgnoreCase(cupsMatcher.group(3)) ? 4 : 3);
            return new PrinterResolution(x, y, unit);
        }
        Matcher ippMatcher = IPP_PATTERN.matcher(value);
        if (ippMatcher.matches()) {
            return new PrinterResolution(Integer.parseInt(ippMatcher.group(1)),
                    Integer.parseInt(ippMatcher.group(2)),
                    (byte) Integer.parseInt(ippMatcher.group(3)));
        }
        throw new IllegalArgumentException("unknown printer resolution: " + resolution);
    }

    /**
     * Returns the cross-feed direction resolution.
     *
     * @return the cross-feed direction resolution
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the feed direction resolution.
     *
     * @return the feed direction resolution
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the resolution unit: {@code 3} for dpi and {@code 4} for dpcm.
     *
     * @return the resolution unit
     */
    public byte getUnit() {
        return unit;
    }

    /**
     * Returns the resolution in the IPP notation {@code x,y,unit}.
     *
     * @return the resolution in IPP notation
     */
    public String getResolution() {
        return x + "," + y + "," + unit;
    }

    /**
     * Returns the resolution in the IPP notation {@code x,y,unit}.
     *
     * @return the resolution in IPP notation
     */
    @Override
    public String toString() {
        return getResolution();
    }

}

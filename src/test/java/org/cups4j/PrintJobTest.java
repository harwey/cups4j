/**
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

import org.junit.Test;

import static org.junit.Assert.*;

public class PrintJobTest {

    /**
     * Unit tests for {@link PrintJob} class and its builder.
     *
     * @author oboehm
     */
    @Test
    public void testBuilder() {
        PrintJob printJob = new PrintJob.Builder("Hello world!".getBytes())
                .attribute("print-color-mode", "color")
                .attribute("ColorModel", "RGB")
                .build();
        assertEquals(2, printJob.getAttributes().size());
    }

}
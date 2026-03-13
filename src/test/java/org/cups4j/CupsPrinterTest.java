package org.cups4j;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link CupsPrinter} class.
 *
 * @author oboehm
 */
public final class CupsPrinterTest {

    @Test
    public void testEquals() {
        URI printerURI = URI.create("ipp://pippi.fax");
        CupsPrinter a = new CupsPrinter(null, printerURI, "langstrumpf");
        CupsPrinter b = new CupsPrinter(null, printerURI, "langstrumpf");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

}

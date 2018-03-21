package org.cups4j;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link CupsClient} class.
 *
 * @author oliver (boehm@javatux.de)
 */
public class CupsClientTest {

    private static CupsClient client;

    @BeforeClass
    public static void setUpClient() throws Exception {
        client = new CupsClient();
    }

    @Test
    public void getPrinters() throws Exception {
        List<CupsPrinter> printers = client.getPrinters();
        assertFalse(printers.isEmpty());
    }

}

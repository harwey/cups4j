package org.cups4j;

import cups4j.TestCups;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link CupsClient} class.
 *
 * @author oliver (boehm@javatux.de)
 */
public class CupsClientTest {

    private static CupsClient client;

    @BeforeClass
    public static void setUpClient() throws Exception {
        client = TestCups.getCupsClient();
    }

    @Test
    public void testGetPrinters() throws Exception {
        List<CupsPrinter> printers = client.getPrinters();
        assertFalse(printers.isEmpty());
    }
    
    @Test
    public void testGetPrinterByName() throws Exception {
        CupsPrinter firstPrinter = client.getPrinters().get(0);
        assertNotNull(firstPrinter);
        CupsPrinter namedPrinter = client.getPrinter(firstPrinter.getName());
        assertEquals(firstPrinter.getName(), namedPrinter.getName());
    }

}

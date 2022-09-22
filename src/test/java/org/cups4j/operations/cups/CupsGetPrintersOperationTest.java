package org.cups4j.operations.cups;

import org.cups4j.CupsPrinter;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link CupsGetPrintersOperation}
 *
 * @author mweise
 */
public class CupsGetPrintersOperationTest extends AbstractIppOperationTest {
	
	@Test
	public void getPrinters() throws Exception {
		List<CupsPrinter> printers = this.client.getPrinters();
		assertFalse(printers.isEmpty());
		for (CupsPrinter printer : printers) {
			assertNotNull(printer.getName());
			assertFalse(printer.getName().isEmpty());
		}
	}
	
	@Test
	public void getPrinterByName() throws Exception {
		CupsPrinter printer = this.client.getPrinter(this.printerName);
		assertNotNull(printer);
		assertEquals(this.printerName, printer.getName());
	}
	
	@Test
	public void getPrinterByURL() throws Exception {
		URL printerURL = this.getPrinterURL();
		CupsPrinter printer = this.client.getPrinter(printerURL);
		assertNotNull(printer);
		assertEquals(printerURL.toString(), printer.getPrinterURL().toString());
	}
}

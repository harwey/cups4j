package org.cups4j.operations.cups;

import org.cups4j.CupsPrinter;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		URI printerURL = this.getPrinterURL();
		CupsPrinter printer = this.client.getPrinter(printerURL);
		assertNotNull(printer);
		assertEquals(printerURL.toString(), printer.getPrinterURL().toString());
	}
}

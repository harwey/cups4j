package org.cups4j.operations.cups;

import org.cups4j.CupsPrinter;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link CupsGetDefaultOperation}
 *
 * @author mweise
 */
public class CupsGetDefaultOperationTest extends AbstractIppOperationTest {
	
	@Test
	public void getDefaultPrinter() throws Exception {
		CupsPrinter defaultPrinter = this.client.getDefaultPrinter();
		assertNotNull(defaultPrinter);
		assertEquals(this.printerName, defaultPrinter.getName());
		assertTrue(defaultPrinter.isDefault());
	}
}

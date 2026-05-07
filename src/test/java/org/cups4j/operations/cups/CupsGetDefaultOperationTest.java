package org.cups4j.operations.cups;

import org.cups4j.CupsPrinter;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

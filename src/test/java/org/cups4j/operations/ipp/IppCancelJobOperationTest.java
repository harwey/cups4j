package org.cups4j.operations.ipp;

import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link IppCancelJobOperation}
 *
 * @author mweise
 */
public class IppCancelJobOperationTest extends AbstractIppOperationTest {
	private CupsPrinter printer;
	
	@BeforeEach
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void cancelJob() throws Exception {
		PrintRequestResult result = printTestJob(this.printer);
		
		assertTrue(this.client.cancelJob(this.printer, result.getJobId()));
		PrintJobAttributes job = this.client.getJobAttributes(result.getJobId());
		assertNotNull(job);
		assertEquals(result.getJobId(), job.getJobID());
		assertEquals(JobStateEnum.CANCELED, job.getJobState());
	}
}

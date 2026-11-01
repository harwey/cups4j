package org.cups4j.operations.ipp;

import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintRequestResult;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link IppReleaseJobOperation}
 *
 * @author mweise
 */
public class IppReleaseJobOperationTest extends AbstractIppOperationTest {
	private CupsPrinter printer;
	
	@BeforeEach
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void releaseJob() throws Exception {
		PrintRequestResult result = printTestJob(this.printer);
		int jobId = result.getJobId();
		
		assertJobState(jobId, JobStateEnum.PROCESSING);
		
		assertTrue(this.client.holdJob(this.printer, jobId));
		assertJobState(jobId, JobStateEnum.PENDING_HELD);
		
		assertTrue(this.client.releaseJob(this.printer, jobId));
		assertJobState(jobId, JobStateEnum.PROCESSING);
	}
}

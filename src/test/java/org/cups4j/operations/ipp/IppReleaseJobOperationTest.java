package org.cups4j.operations.ipp;

import org.apache.commons.io.FileUtils;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJob.Builder;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IppReleaseJobOperation}
 *
 * @author mweise
 */
public class IppReleaseJobOperationTest extends AbstractIppOperationTest {
	private CupsPrinter printer;
	
	@Before
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void releaseJob() throws Exception {
		byte[] contents = FileUtils.readFileToByteArray(new File("src/test/resources/test.pdf"));
		Builder jobBuilder = new Builder(contents);
		jobBuilder.userName(this.userName);
		PrintRequestResult result = this.printer.print(jobBuilder.build());
		int jobId = result.getJobId();
		
		PrintJobAttributes createdJob = this.client.getJobAttributes(jobId);
		assertNotNull(createdJob);
		assertSame(jobId, createdJob.getJobID());
		assertSame(JobStateEnum.PROCESSING, createdJob.getJobState());
		
		assertTrue(this.client.holdJob(this.printer, jobId));
		PrintJobAttributes heldJob = this.client.getJobAttributes(jobId);
		assertNotNull(heldJob);
		assertSame(jobId, heldJob.getJobID());
		assertSame(JobStateEnum.PENDING_HELD, heldJob.getJobState());
		
		assertTrue(this.client.releaseJob(this.printer, jobId));
		PrintJobAttributes releasedJob = this.client.getJobAttributes(jobId);
		assertNotNull(releasedJob);
		assertSame(jobId, releasedJob.getJobID());
		assertSame(JobStateEnum.PROCESSING, releasedJob.getJobState());
	}
}

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
 * Tests for {@link IppCancelJobOperation}
 *
 * @author mweise
 */
public class IppCancelJobOperationTest extends AbstractIppOperationTest {
	private CupsPrinter printer;
	
	@Before
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void cancelJob() throws Exception {
		byte[] contents = FileUtils.readFileToByteArray(new File("src/test/resources/test.pdf"));
		Builder jobBuilder = new Builder(contents);
		jobBuilder.userName(this.userName);
		PrintRequestResult result = this.printer.print(jobBuilder.build());
		
		assertTrue(this.client.cancelJob(this.printer, result.getJobId()));
		PrintJobAttributes job = this.client.getJobAttributes(result.getJobId());
		assertNotNull(job);
		assertSame(result.getJobId(), job.getJobID());
		assertSame(JobStateEnum.CANCELED, job.getJobState());
	}
}

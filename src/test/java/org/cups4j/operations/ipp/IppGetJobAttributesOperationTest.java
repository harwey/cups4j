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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link IppGetJobAttributesOperation}
 *
 * @author mweise
 */
public class IppGetJobAttributesOperationTest extends AbstractIppOperationTest {
	
	private CupsPrinter printer;
	
	@Before
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void getPrintJobAttributes() throws Exception {
		byte[] contents = FileUtils.readFileToByteArray(new File("src/test/resources/test.pdf"));
		Builder jobBuilder = new Builder(contents);
		jobBuilder.userName(this.userName);
		PrintRequestResult result = this.printer.print(jobBuilder.build());
		
		PrintJobAttributes job = this.client.getJobAttributes(this.userName, result.getJobId());
		assertNotNull(job);
		assertSame(result.getJobId(), job.getJobID());
		assertSame(JobStateEnum.PROCESSING, job.getJobState());
		assertEquals(this.userName, job.getUserName());
		assertEquals(this.printer.getPrinterURL().toString(), job.getPrinterURL().toString());
	}
}

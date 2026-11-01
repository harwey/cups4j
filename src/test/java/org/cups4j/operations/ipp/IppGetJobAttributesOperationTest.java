package org.cups4j.operations.ipp;

import org.cups4j.CupsAuthentication;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link IppGetJobAttributesOperation}
 *
 * @author mweise
 */
public class IppGetJobAttributesOperationTest extends AbstractIppOperationTest {
	
	private CupsPrinter printer;
	
	@BeforeEach
	public void setUpPrinter() {
		this.printer = this.getPrinter();
	}
	
	@Test
	public void getPrintJobAttributes() throws Exception {
		PrintRequestResult result = printTestJob(this.printer);

		PrintJobAttributes job = this.client.getJobAttributes(this.userName, result.getJobId());
		assertJobOfTestUser(result.getJobId(), job);
		assertEquals(JobStateEnum.PROCESSING, job.getJobState());
		assertEquals(this.printer.getPrinterURI(), job.getPrinterURI());
	}

	@Test
	public void getPrintJobAttributesWithCredentials() throws Exception {
		PrintRequestResult result = printTestJob(this.printer);
		IppGetJobAttributesOperation command = new IppGetJobAttributesOperation(this.cups.getFirstMappedPort());
		PrintJobAttributes job = command.getPrintJobAttributes(this.getPrinterURL(), result.getJobId(), this.creds);
		assertJobOfTestUser(result.getJobId(), job);
	}

	private void assertJobOfTestUser(int jobId, PrintJobAttributes job) {
		assertNotNull(job);
		assertEquals(jobId, job.getJobID());
		assertEquals(this.userName, job.getUserName());
	}

	@Test
	public void testGetUserName() {
		assertEquals("alice", IppGetJobAttributesOperation.getUserName(new CupsAuthentication("alice", "secret")));
		assertEquals(CupsClient.DEFAULT_USER, IppGetJobAttributesOperation.getUserName(null));
	}
}

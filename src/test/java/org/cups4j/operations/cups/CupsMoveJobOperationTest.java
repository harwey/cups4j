package org.cups4j.operations.cups;

import org.cups4j.CupsPrinter;
import org.cups4j.PrintRequestResult;
import org.cups4j.WhichJobsEnum;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CupsMoveJobOperation}
 *
 * @author mweise
 */
public class CupsMoveJobOperationTest extends AbstractIppOperationTest {
	
	@Override
	protected void createTestPrinters() throws IOException, InterruptedException {
		this.cups.execInContainer("lpadmin", "-p", this.printerName, "-E", "-v", "socket://localhost:9100", "-o", "printer-is-shared=true");
		this.cups.execInContainer("lpadmin", "-p", "test-printer-2", "-E", "-v", "socket://localhost:9200", "-o", "printer-is-shared=true");
	}
	
	@Test
	public void moveJob() throws Exception {
		CupsPrinter printer1 = this.client.getPrinter(this.printerName);
		PrintRequestResult result = printTestJob(printer1);
		int jobId = result.getJobId();
		assertTrue(this.client.getJobs(printer1, WhichJobsEnum.ALL, this.userName, true).stream().anyMatch(j -> j.getJobID() == jobId));
		
		CupsPrinter printer2 = this.client.getPrinter("test-printer-2");
		assertNotNull(printer2);
		
		assertTrue(this.client.moveJob(jobId, this.userName, printer1, printer2));
		assertFalse(this.client.getJobs(printer1, WhichJobsEnum.ALL, this.userName, true).stream().anyMatch(j -> j.getJobID() == jobId));
		assertTrue(this.client.getJobs(printer2, WhichJobsEnum.ALL, this.userName, true).stream().anyMatch(j -> j.getJobID() == jobId));
	}
}

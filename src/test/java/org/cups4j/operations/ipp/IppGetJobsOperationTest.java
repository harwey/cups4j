/*
 * Copyright (c) 2026 by Oliver Boehm
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.cups4j.operations.ipp;

import org.apache.commons.lang3.ThreadUtils;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJobAttributes;
import org.cups4j.WhichJobsEnum;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link IppGetJobsOperation}.
 * <p>
 * A Get-Jobs response only contains the creation and completion time of a job
 * if {@code time-at-creation} resp. {@code time-at-completed} are requested
 * (see <a href="https://github.com/harwey/cups4j/issues/67">issue #67</a>).
 * </p>
 */
public class IppGetJobsOperationTest extends AbstractIppOperationTest {

    private static final String COMPLETING_PRINTER_NAME = "test-printer-completes";
    private static final long TIMEOUT_IN_MS = 30_000;

    private CupsPrinter completingPrinter;

    @Override
    protected void createTestPrinters() throws IOException, InterruptedException {
        super.createTestPrinters();
        // a file device finishes every job immediately, so a completion time exists
        this.cups.execInContainer("lpadmin", "-p", COMPLETING_PRINTER_NAME, "-E", "-v", "file:/dev/null",
                "-o", "printer-is-shared=true");
    }

    @BeforeEach
    public void setUpPrinter() throws Exception {
        this.completingPrinter = this.client.getPrinter(COMPLETING_PRINTER_NAME);
    }

    @Test
    public void getPrintJobsContainsCompletionTime() throws Exception {
        // GIVEN a printer that completes every job immediately
        int jobId = printTestJob(this.completingPrinter).getJobId();
        // WHEN the completed job is requested via Get-Jobs
        PrintJobAttributes job = waitForJob(this.completingPrinter, jobId, JobStateEnum.COMPLETED);
        // THEN it contains a consistent creation and completion time
        Date completionTime = job.getJobCompleteTime();
        assertNotNull(completionTime, "job " + jobId + " has no completion time");
        assertNotNull(job.getJobCreateTime(), "job " + jobId + " has no creation time");
        assertTrue(completionTime.getTime() >= job.getJobCreateTime().getTime(),
                "job " + jobId + " was completed before it was created");
        PrintJobAttributes details = this.client.getJobAttributes(jobId);
        assertEquals(details.getJobCompleteTime(), completionTime,
                "completion time of Get-Jobs differs from Get-Job-Attributes");
    }

    @Test
    public void getPrintJobsKeepsOtherAttributes() throws Exception {
        // GIVEN a printer that completes every job immediately
        int jobId = printTestJob(this.completingPrinter).getJobId();
        // WHEN the completed job is requested via Get-Jobs
        PrintJobAttributes job = waitForJob(this.completingPrinter, jobId, JobStateEnum.COMPLETED);
        // THEN all other job attributes are still filled
        assertEquals(jobId, job.getJobID());
        assertEquals(this.userName, job.getUserName());
        assertNotNull(job.getJobName());
        assertNotNull(job.getJobURI());
        assertEquals(this.completingPrinter.getPrinterURI(), job.getPrinterURI());
    }

    @Test
    public void getPrintJobsOfUncompletedJobHasNoCompletionTime() throws Exception {
        // GIVEN a printer that keeps the job in the queue
        CupsPrinter printer = this.client.getPrinter(this.printerName);
        int jobId = printTestJob(printer).getJobId();
        // WHEN the still running job is requested via Get-Jobs
        PrintJobAttributes job = waitForJob(printer, jobId, JobStateEnum.PROCESSING);
        // THEN it has a creation time but no completion time
        assertNull(job.getJobCompleteTime(), "uncompleted job " + jobId + " has a completion time");
        assertNotNull(job.getJobCreateTime());
    }

    private PrintJobAttributes waitForJob(CupsPrinter printer, int jobId, JobStateEnum expectedState) throws Exception {
        long deadline = System.currentTimeMillis() + TIMEOUT_IN_MS;
        while (System.currentTimeMillis() < deadline) {
            List<PrintJobAttributes> jobs = this.client.getJobs(printer, WhichJobsEnum.ALL, this.userName, false);
            for (PrintJobAttributes job : jobs) {
                if (job.getJobID() == jobId && job.getJobState() == expectedState) {
                    return job;
                }
            }
            ThreadUtils.sleepQuietly(Duration.ofMillis(500));
        }
        fail("job " + jobId + " did not reach state " + expectedState + " within " + TIMEOUT_IN_MS + " ms");
        return null;
    }

}

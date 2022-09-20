package org.cups4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CupsPrinter} class.
 *
 * @author oboehm
 */
public final class CupsPrinterTest extends AbstractIppOperationTest {

    private static final Logger LOG = LoggerFactory.getLogger(CupsPrinterTest.class);
    private CupsPrinter printer;

    @Before
    public void setUpPrinter() {
        printer = this.getPrinter();
        LOG.info("Use printer '{}' for testing", printer.getName());
    }

    @Test
    public void testPrintPDF() {
        File pdfFile = new File("src/test/resources/test.pdf");
        PrintRequestResult result = this.print(printer, pdfFile);
        assertNotNull(result);
        assertTrue(result.isSuccessfulResult());
        assertTrue(result.getJobId() > 0);
    }
    
    @Test
    public void testPrintText() {
        File textFile = new File("src/test/resources/test.txt");
        PrintRequestResult result = this.print(printer, textFile);
        assertNotNull(result);
        assertTrue(result.isSuccessfulResult());
        assertTrue(result.getJobId() > 0);
    }

    private PrintRequestResult print(CupsPrinter printer, File file) {
        PrintJob job = createPrintJob(file);
        LOG.info("Print job '{}' will be sent to {}.", job, printer);
        try {
            return printer.print(job);
        } catch (Exception ex) {
            throw new IllegalStateException("print of '" + file + "' failed", ex);
        }
    }

    @Test
    public void testPrintList() {
        File file = new File("src/test/resources/test.txt");
        PrintJob job1 = createPrintJob(file);
        PrintJob job2 = createPrintJob(file);
        printer.print(job1, job2);
    }

    @Test(expected = IllegalStateException.class)
    public void testPrintListWithDifferentUsers() {
        File file = new File("src/test/resources/test.txt");
        printer.print(createPrintJob(file, "oli"), createPrintJob(file, "stan"));
    }

    private PrintJob createPrintJob(File file) {
        return createPrintJob(file, CupsClient.DEFAULT_USER);
    }

    @Test
    public void testPrintListWithNoUser() {
        PrintJob job = new PrintJob.Builder("secret".getBytes()).jobName("testPrintListWithNoUser").build();
        printer.print(job, job);
    }
    
    private PrintJob createPrintJob(File file, String userName) {
        String jobname = generateJobnameFor(file);
        try {
            byte[] content = FileUtils.readFileToByteArray(file);
            return new PrintJob.Builder(content).jobName(jobname).userName(userName).build();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("cannot read '" + file + "'", ioe);
        }
    }

    private static String generateJobnameFor(File file) {
        String basename = file.getName().split("\\.")[0];
        return generateJobNameFor(basename);
    }

    private static String generateJobNameFor(String basename) {
        byte[] epochTime = Base64.encodeBase64(BigInteger.valueOf(System.currentTimeMillis()).toByteArray());
        return basename + new String(epochTime).substring(2);
    }

}

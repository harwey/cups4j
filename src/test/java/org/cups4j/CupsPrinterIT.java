package org.cups4j;

import cups4j.TestCups;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNotNull;

/**
 * Integration tests for {@link CupsPrinter} class.
 *
 * @author oboehm
 */
public final class CupsPrinterIT {

    private static final Logger LOG = LoggerFactory.getLogger(CupsPrinterIT.class);
    private CupsPrinter printer;

    @Before
    public void setUpPrinter() throws Exception {    
        printer = getPrinter();
        LOG.info("Printer {} was choosen for testing.", printer);
    }

    @Test
    public void testPrinter() {
        assertNotNull(printer);
    }

    @Test
    @Ignore
    public void testPrintPDF() {
        print(printer, new File("src/test/resources/test.pdf"));
    }

    @Test
    @Ignore
    public void testPrintTwoPagesDuplex() throws Exception {
        printTwoPages(true);
    }

    @Test
    @Ignore
    public void testPrintTwoPagesSimplex() throws Exception {
        printTwoPages(false);
    }

    private void printTwoPages(boolean duplex) throws Exception {
        File file = new File("src/test/resources/twopages.pdf");
        String jobname = generateJobnameFor(file);
        byte[] content = FileUtils.readFileToByteArray(file);
        PrintJob job = new PrintJob.Builder(content)
                .jobName(jobname)
                .duplex(duplex)
                .build();
        PrintRequestResult result = printer.print(job);
        assertNotNull(result);
    }

    @Test
    @Ignore
    public void testPrintText() {
        print(printer, new File("src/test/resources/test.txt"));
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
    @Ignore
    public void testPrintList() {
        File file = new File("src/test/resources/test.txt");
        printer.print(createPrintJob(file), createPrintJob(file));
    }

    @Test(expected = IllegalStateException.class)
    @Ignore
    public void testPrintListWithDifferentUsers() {
        File file = new File("src/test/resources/test.txt");
        printer.print(createPrintJob(file, "oli"), createPrintJob(file, "stan"));
    }

    private PrintJob createPrintJob(File file) {
        return createPrintJob(file, CupsClient.DEFAULT_USER);
    }

    @Test
    @Ignore
    public void testPrintListWithNoUser() {
        PrintJob job = new PrintJob.Builder("secret".getBytes()).jobName("testPrintListWithNoUser").build();
        printer.print(job, job);
    }
    
    private PrintJob createPrintJob(File file, String userName) {
        String jobname = generateJobnameFor(file);
        try {
            byte[] content = FileUtils.readFileToByteArray(file);
            return new PrintJob.Builder(content)
                    .jobName(jobname)
                    .userName(userName)
                    .build();
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

    /**
     * There was reported an error as COM-3000 that the following code works
     * with v0.7.8 but not with v0.7.9. This test was called with the following
     * system properties:
     * <ol>
     *     <li>-Dcups.url=https://cups.int.ad.drgueldener.de:9443 -Dprinter=ps-opt-mfp075</li>
     * </ol>
     *
     * @throws Exception in case of error
     * @since 11-Mar-2025 (oboehm)
     */
    @Test
    public void testOnePrintJob() throws Exception {
        PrintJob printJob = new PrintJob.Builder("Test-Druck".getBytes())
                .jobName("OlisJob")
                .userName("mmustermann")
                .copies(1)
                .build();
        CupsPrinter printer = getPrinter();
        int jobId = printer.createJob(printJob.getJobName(), printJob.getUserName());
        printer.print(printJob, jobId, true);
    }

    /**
     * If you use HTTPS {@link CupsPrinter#getJobStatus(String, int)} does not
     * work (COM-3003). This was tested with the following system properties:
     * <ol>
     *     <li>-Dcups.url=http://drgsse04.ad.drgueldener.de:12197 -Dprinter=OPTDN075</li>
     *     <li>-Dcups.url=https://cups.int.ad.drgueldener.de:9443 -Dprinter=OPTDN075</li>
     * </ol>
     *
     * @throws Exception in case of error
     * @since 06-May-2025 (oboehm)
     */
    @Test
    public void testGetJobStatus() throws Exception {
        PrintJob printJob = new PrintJob.Builder("Test-Druck".getBytes())
                .jobName("OlisJob")
                .userName("mmustermann")
                .copies(1)
                .build();
        CupsPrinter printer = getPrinter();
        int jobId = printer.createJob(printJob.getJobName(), printJob.getUserName());
        JobStateEnum jobStatus = printer.getJobStatus("mmustermann", jobId);
        assertNotNull(jobStatus);
        assertEquals("OlisJob", TestCups.getCupsClient().getJobAttributes(jobId).getJobName());
    }

    /**
     * Gets a printer for testing. This is either the printer defined by the
     * system property 'printer' or the default printer.
     *
     * @return the printer
     * @throws Exception in case of error
     */
    public static CupsPrinter getPrinter() throws Exception  {
        String name = System.getProperty("printer");
        if (name == null) {
            LOG.info("To specify printer please set system property 'printer'.");
            CupsPrinter printer = TestCups.getCupsClient().getDefaultPrinter();
            assumeNotNull(printer);
            return printer;
        } else {
            return getPrinter(name);
        }
    }

    /**
     * Returns the printer with the given name. The search of the name is
     * not case sensitiv.
     *
     * @param name name of the printer
     * @return printer
     */
    public static CupsPrinter getPrinter(String name) {
        try {
            List<CupsPrinter> printers = TestCups.getCupsClient().getPrinters();
            for (CupsPrinter p : printers) {
                if (name.equalsIgnoreCase(p.getName())) {
                    return p;
                }
            }
            throw new IllegalArgumentException("not a valid printer name: " + name);
        } catch (Exception ex) {
            throw new IllegalStateException("cannot get printers", ex);
        }
    }

}

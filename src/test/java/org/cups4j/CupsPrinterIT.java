/*
 * Copyright (c) 2018-2026 by Oli B.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details. You should have a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * (c)reated 23.03.2018 by oboehm
 */
package org.cups4j;

import cups4j.TestCups;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.cups4j.ipp.attributes.Attribute;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CupsPrinter} class.
 *
 * @author oboehm
 */
public final class CupsPrinterIT {

    private static final Logger log = LoggerFactory.getLogger(CupsPrinterIT.class);
    private CupsPrinter printer;

    @BeforeEach
    public void setUpPrinter() throws Exception {    
        printer = getPrinter();
        log.info("Printer {} was choosen for testing.", printer);
    }

    @Test
    public void testPrinter() {
        assertNotNull(printer);
    }

    @Test
    @Disabled
    public void testPrintPDF() throws IOException {
        print(printer, new File("src/test/resources/test.pdf"));
    }

    @Test
    @Disabled
    public void testPrintTwoPagesDuplex() throws Exception {
        printTwoPages(true);
    }

    @Test
    @Disabled
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
    @Disabled
    public void testPrintText() throws IOException {
        print(printer, new File("src/test/resources/test.txt"));
    }

    private PrintRequestResult print(CupsPrinter printer, File file) throws IOException {
        PrintJob job = createPrintJob(file);
        log.info("Print job '{}' will be sent to {}.", job, printer);
        return printer.print(job);
    }

    @Test
    @Disabled
    public void testPrintList() {
        File file = new File("src/test/resources/test.txt");
        printer.print(createPrintJob(file), createPrintJob(file));
    }

    @Test
    @Disabled
    public void testPrintListWithDifferentUsers() {
      assertThrows(IllegalStateException.class, () -> {
        File file = new File("src/test/resources/test.txt");
        printer.print(createPrintJob(file, "oli"), createPrintJob(file, "stan"));
      });
    }

    private PrintJob createPrintJob(File file) {
        return createPrintJob(file, CupsClient.DEFAULT_USER);
    }

    @Test
    @Disabled
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
                    .color(true)
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

    @Test
    public void testGetPrinterAttributes()  throws Exception {
        CupsPrinter printer = getPrinter();
        List<Attribute> attributes = printer.getAttributes();
        assertFalse(attributes.isEmpty());
        log.info("Printer {} supports media {}.", printer, printer.getMediaSupported());
        log.info("Printer {} supports printer trays {}.", printer, printer.getMediaSourceSupported());
    }

    /**
     * This test was inserted with issue #21 to test the handling of
     * different paper trays. It was started with VM arguments
     * <ul>
     *     <li>-Dcups.url=... -Dprinter=ps-opt-mfp075</li>
     * </ul>
     *
     * @throws Exception in case of error
     */
    @Test
    public void testPrintWithTray2() throws Exception {
        CupsPrinter printer = getPrinter();
        List<String> supported = printer.getMediaSourceSupported();
        log.info("Printer {} supports as media sources {}.", printer, supported);
        if (supported.contains("tray-2")) {
            PrintJob printJob = new PrintJob.Builder("Print Test with 'tray-2'".getBytes())
                    .jobName("TestTray2")
                    .copies(1)
                    .attribute("media-col", "media-source:keyword:tray-2")
                    .build();
            printer.print(printJob);
        } else {
            log.warn("No job will be printed becauce printer {} supports not 'tray-2' as media-source {}.", printer, supported);
        }
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
            log.info("To specify printer please set system property 'printer'.");
            CupsPrinter printer = TestCups.getCupsClient().getDefaultPrinter();
            Assumptions.assumeFalse(printer == null);
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

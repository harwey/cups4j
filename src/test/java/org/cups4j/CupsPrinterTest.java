package org.cups4j;

import cups4j.TestCups;
import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CupsPrinter} class.
 *
 * @author oboehm
 */
public final class CupsPrinterTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(CupsPrinterTest.class);
    private static CupsPrinter printer;
    
    @BeforeClass
    public static void setUpPrinter() {
        String name = System.getProperty("printer");
        if (name == null) {
            LOG.info("To specify printer please set system property 'printer'.");
        } else {
            printer = TestCups.getCupsClient().getPrinter(name);
        }
    }

    @Test
    public void testPrintPDF() {
        print(printer, new File("src/test/resources/test.pdf"));
    }
    
    @Test
    public void testPrintText() {
        print(printer, new File("src/test/resources/test.txt"));
    }

    private PrintRequestResult print(CupsPrinter printer, File file) {
        String jobname = generateJobnameFor(file);
        InputStream istream = null;
        try {
            istream = new FileInputStream(file);
            PrintJob job = new PrintJob.Builder(istream).jobName(jobname).build();
            LOG.info("Druckjob '{}' wird zu {} geschickt.", jobname, printer);
            return printer.print(job);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("cannot read '"+ file + "'", ioe);
        } catch (Exception ex) {
            throw new IllegalStateException("print of '" + file + "' failed", ex);
        } finally {
            close(istream);
        }
    }

    private static String generateJobnameFor(File file) {
        String basename = file.getName().split("\\.")[0];
        byte[] epochTime = Base64.encodeBase64(BigInteger.valueOf(System.currentTimeMillis()).toByteArray());
        return basename + new String(epochTime).substring(2);
    }
    
    private static void close(InputStream istream) {
        if (istream != null) {
            try {
                istream.close();
            } catch (IOException ex) {
                LOG.warn("Close of {} failed:", istream, ex);
            }
        }
    }

}

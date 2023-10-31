package org.cups4j;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cups4j.TestCups;

/**
 * Unit tests for {@link CupsClient} class.
 *
 * @author oliver (boehm@javatux.de)
 */
public class CupsClientTest {

  private static CupsClient client;
  private static final Logger LOG = LoggerFactory.getLogger(CupsClientTest.class);

  @BeforeAll
  public static void setUpClient() throws Exception {
    client = TestCups.getCupsClient();
  }

  @Test
  @Tag("LiveServerTest")
  public void getPrinters() throws Exception {
    List<CupsPrinter> printers = client.getPrinters();

    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[isClass=" + printer.isPrinterClass() + "]");
    }

    assertFalse(printers.isEmpty());
  }

  @Test
  @Tag("LiveServerTest")
  public void testMakeAndModel() throws Exception {
    List<CupsPrinter> printers = client.getPrinters();

    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[makeAndModel=" + printer.getMakeAndModel() + "]");
    }
  }

}

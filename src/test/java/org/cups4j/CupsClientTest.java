package org.cups4j;

import cups4j.TestCups;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for {@link CupsClient} class.
 *
 * @author oliver (boehm@javatux.de)
 */
public class CupsClientTest {

  private static CupsClient client;
  private static final Logger LOG = LoggerFactory.getLogger(CupsClientTest.class);

  @BeforeAll
  public static void setUpClient() {
    client = TestCups.getCupsClient();
  }

  @Test
  public void getPrinters() throws Exception {
    List<CupsPrinter> printers = client.getPrinters();
    assertFalse(printers.isEmpty());
    for (CupsPrinter printer : printers) {
      LOG.info("printer: {} [isClass={}]", printer.getName(), printer.isPrinterClass());
      assertThat(printer.getPrinterURI().getPath(), startsWith("/printer"));
    }
  }

  @Test
  public void getDefaultPrinter() throws Exception {
    CupsPrinter defaultPrinter = client.getDefaultPrinter();
    if (defaultPrinter != null) {
      List<CupsPrinter> printers = client.getPrinters();
      assertThat(printers, hasItem(defaultPrinter));
    }
  }

  @Test
  public void testMakeAndModel() throws Exception {
    List<CupsPrinter> printers = client.getPrinters();

    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[makeAndModel=" + printer.getMakeAndModel() + "]");
    }
  }


}

package cups4j;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestCups {
  @Test
  public void testCupsClient() throws Exception {
    CupsClient client = getCupsClient();
    List<CupsPrinter> printers = client.getPrinters();
    for (CupsPrinter p : printers) {
      System.out.println("Printer: " + p.toString());
      System.out.println(" Media supported:");
      for (String media : p.getMediaSupported()) {
        System.out.println("  - " + media);
      }
      System.out.println(" Resolution supported:");
      for (String res : p.getResolutionSupported()) {
        System.out.println("  - " + res);
      }
      System.out.println(" Mime-Types supported:");
      for (String mime : p.getMimeTypesSupported()) {
        System.out.println("  - " + mime);
      }
    }
  }

  /**
   * If you have no CUPS running on your local machine you must set the
   * envrionment variables 'host' and 'port' to your CUPS server in the
   * network. Otherwise the test fails.
   * 
   * @return your CupsClient for testing
   */
  public static CupsClient getCupsClient() {
    String host = System.getProperty("host", "localhost");
    int port = Integer.parseInt(System.getProperty("port", "631"));
    try {
      return new CupsClient(host, port);
    } catch (Exception ex) {
      throw new IllegalStateException("cannot get CUPS client for " + host + ":" + port);
    }
  }

}

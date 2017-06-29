package cups4j;

import java.util.List;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.junit.Test;

public class TestCups {
  @Test
  public void testCupsClient() throws Exception {
    CupsClient client = new CupsClient();
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
}

package cups4j;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.List;

import static org.junit.Assume.assumeTrue;

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
   * envrionment variable 'cups.url' to your CUPS server in the network.
   * Otherwise the test fails.
   * <p>
   * Default for testing is <a href="http://localhost:631/printers/"
   * >localhost:631</a>. If you wan't to use it check if it's available.
   * On a Mac you man need to call
   * <pre>
   *     cupsctl WebInterface=yes
   * </pre>
   * to activate it.
   * </p>
   *
   * @return your CupsClient for testing
   */
  public static CupsClient getCupsClient() {
    URI cupsURI = URI.create(System.getProperty("cups.url", "http://localhost:631"));
    assumeTrue(cupsURI + " not available", isOnline(cupsURI.getHost(), cupsURI.getPort()));
    return new CupsClient(cupsURI);
  }

  private static boolean isOnline(String host, int port) {
    try (Socket socket = new Socket(host, port)) {
      return socket.isConnected();
    } catch (IOException ex) {
      System.err.println("Failed to connect to " + host + ":" + port + " - " + ex.getMessage());
      return false;
    }
  }

}

package org.cups4j;

import org.cups4j.operations.cups.CupsGetDefaultOperation;
import org.cups4j.operations.cups.CupsGetPrintersOperation;
import org.cups4j.operations.cups.CupsMoveJobOperation;
import org.cups4j.operations.ipp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Main Client for accessing CUPS features like
 * <p>
 * - get printers
 * </p>
 * <p>
 * - print documents
 * </p>
 * <p>
 * - get job attributes
 * </p>
 * <p>
 * - ...
 * </p>
 */
public class CupsClient {
  private static final Logger log = LoggerFactory.getLogger(CupsClient.class);
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 631;
  public static final String DEFAULT_USER = System.getProperty("user.name", "anonymous");

  private final URI cupsURL;
  private final String user;

  private final CupsAuthentication creds;

  /**
   * Creates a CupsClient for localhost port 631 with user anonymous.
   */
  public CupsClient() {
    this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_USER);
  }

  /**
   * Creates a CupsClient for provided host and port with user anonymous
   * 
   * @param host host
   * @param port port
   */
  public CupsClient(String host, int port) {
    this(host, port, DEFAULT_USER);
  }

  /**
   * Creates a CupsClient for provided host, port and user
   * 
   * @param host     host
   * @param port     port
   * @param userName user name
   */
  public CupsClient(String host, int port, String userName) {
    this(host, port, userName, null);
  }

  /**
   * Creates a CupsClient for provided host, port and user
   *
   * @param host     host
   * @param port     port
   * @param userName user name
   */
  public CupsClient(String host, int port, String userName, CupsAuthentication creds) {
    this(toURI(host, port), userName, creds);
  }

  public static URI toURI(String host, int port) {
    if (host == null || host.isEmpty()) {
      throw new IllegalArgumentException("The hostname specified: <" + host + "> is not valid");
    }
    if (port < 0) {
      throw new IllegalArgumentException("The specified port number: <" + port + "> is not valid");
    }
    return URI.create(String.format("http://%s:%d", host, port));
  }

  public CupsClient(URI cupsURL) {
    this(cupsURL, DEFAULT_USER, null);
  }

  public CupsClient(URI cupsURL, String userName, CupsAuthentication creds) {
    this.cupsURL = cupsURL;
    this.user = userName;
    this.creds = creds;
  }

  private String getHost() {
    return cupsURL.getHost();
  }

  private int getPort() {
    return cupsURL.getPort();
  }

  /**
   * Returns all available printers
   * 
   * @return List of Printers
   * @throws Exception in case of problems
   */
  public List<CupsPrinter> getPrinters() throws Exception {
    return new CupsGetPrintersOperation(getPort()).getPrinters(cupsURL, creds);
  }

  /**
   * Returns all available printers except CUPS specific default printer
   * 
   * @return List of Printers
   * @throws Exception in case of problems
   */
  public List<CupsPrinter> getPrintersWithoutDefault() throws Exception {
    CupsGetPrintersOperation cgp = new CupsGetPrintersOperation();
    return cgp.getPrinters(getHost(), getPort(), creds);
  }

  /**
   * Returns the printer for the provided URL
   * 
   * @param printerURL
   *          an URL like "http://localhost:631/printers/printername"
   * @return printer
   * @throws Exception in case of problems
   * @deprecated replaced by {@link #getPrinter(URI)}
   */
  @Deprecated
  public CupsPrinter getPrinter(URL printerURL) throws Exception {
    return getPrinter(printerURL.toURI());
  }

  /**
   * Returns the printer for the provided URL
   *
   * @param printerURL an URI like "http://localhost:631/printers/printername"
   * @return printer
   * @throws Exception in case of problems
   * @since 0.8.1
   */
  public CupsPrinter getPrinter(URI printerURL) throws Exception {
    List<CupsPrinter> printers = getPrinters();
    for (CupsPrinter printer : printers) {
      if (printer.getPrinterURI().equals(printerURL))
        return printer;
    }
    log.info("No printer for URI '{}' found.",  printerURL);
    return null;
  }

  /**
   * Returns the printer for the provided name
   *
   * @param printerName
   *          the printer name
   * @return printer
   * @throws Exception in case of problems
   */
  public CupsPrinter getPrinter(String printerName) throws Exception {
    List<CupsPrinter> printers = getPrinters();
    for (CupsPrinter printer : printers) {
      if (printer.getName().equals(printerName))
        return printer;
    }
    return null;
  }

  /**
   * Returns default printer
   * 
   * @return default printer
   * @throws Exception in case of problems
   */
  public CupsPrinter getDefaultPrinter() throws Exception {
    return new CupsGetDefaultOperation().getDefaultPrinter(cupsURL, creds);
  }

  /**
   * Returns the printer for the provided URL on the current host
   *
   * @param printerURL
   *          a URL like /printers/printername
   * @return printer
   * @throws Exception in case of problems
   */
  public CupsPrinter getPrinterOnCurrentHost(String printerURL) throws Exception {
    return getPrinter(URI.create(cupsURL + "/" + printerURL));
  }

  /**
   * Returns job attributes for the job associated with the provided jobID
   * 
   * @param jobID job ID
   * @return Job attributes
   * @throws Exception in case of problems
   */
  public PrintJobAttributes getJobAttributes(int jobID) throws Exception {
    return getJobAttributes(getHost(), user, jobID);
  }

  /**
   * Returns job attributes for the job associated with the provided jobID and
   * user name
   * 
   * @param userName
   * @param jobID
   * @return Job attributes
   * @throws Exception in case of problems
   */
  public PrintJobAttributes getJobAttributes(String userName, int jobID) throws Exception {
    return getJobAttributes(getHost(), userName, jobID);
  }

  /**
   * Returns job attributes for the job associated with the provided jobID on
   * provided host and port
   * 
   * @param hostname
   * @param jobID
   * @return Job attributes
   * @throws Exception in case of problems
   */
  private PrintJobAttributes getJobAttributes(String hostname, String userName, int jobID) throws Exception {
    if (userName == null || "".equals(userName)) {
      userName = DEFAULT_USER;
    }
    if (hostname == null || "".equals(hostname)) {
      hostname = DEFAULT_HOST;
    }

    return new IppGetJobAttributesOperation(getPort()).getPrintJobAttributes(cupsURL, userName, jobID, creds);
  }

  /**
   * Returns all jobs for given printer and user Name
   * <p>
   * Currently all Jobs on the server are returned by this method.
   * </p>
   * <p>
   * user and printer names are provided in the resulting PrintJobAttributes
   * </p>
   * 
   * @param printer
   * @param userName
   * @return List of job attributes
   * @throws Exception in case of problems
   */
  public List<PrintJobAttributes> getJobs(CupsPrinter printer, WhichJobsEnum whichJobs, String userName, boolean myJobs)
      throws Exception {
    return new IppGetJobsOperation(getPort()).getPrintJobs(printer, whichJobs, userName, myJobs, creds);
  }

  /**
   * Cancel the job with the provided jobID on the current host wit current user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception in case of problems
   */
  public boolean cancelJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppCancelJobOperation(getPort()).cancelJob(getHost(), user, jobID, printer, creds);
  }

  /**
   * Hold the job with the provided jobID on the current host wit current set
   * user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception in case of problems
   */
  public boolean holdJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppHoldJobOperation(getPort()).holdJob(getHost(), user, jobID, printer, creds);
  }

  /**
   * Release the held job with the provided jobID on the current host wit
   * current set user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception in case of problems
   */
  public boolean releaseJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppReleaseJobOperation(getPort()).releaseJob(getHost(), user, jobID, printer, creds);
  }

  /**
   * Moves the print job with job ID jobID from currentPrinter to targetPrinter
   * 
   * @param jobID
   * @param userName
   * @param currentPrinter
   * @param targetPrinter
   * @return boolean successs
   * @throws Exception in case of problems
   */
  public boolean moveJob(int jobID, String userName, CupsPrinter currentPrinter, CupsPrinter targetPrinter)
      throws Exception {
    String currentHost = currentPrinter.getPrinterURI().getHost();

    return new CupsMoveJobOperation(getPort()).moveJob(currentPrinter, currentHost, userName, jobID,
        targetPrinter.getPrinterURI(), creds);
  }

}

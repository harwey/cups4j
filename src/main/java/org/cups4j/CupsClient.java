package org.cups4j;

import java.net.URL;
import java.util.List;

import org.cups4j.operations.cups.CupsGetDefaultOperation;

/**
 * Copyright (C) 2009 Harald Weyhing
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 */

import org.cups4j.operations.cups.CupsGetPrintersOperation;
import org.cups4j.operations.cups.CupsMoveJobOperation;
import org.cups4j.operations.ipp.IppCancelJobOperation;
import org.cups4j.operations.ipp.IppGetJobAttributesOperation;
import org.cups4j.operations.ipp.IppGetJobsOperation;
import org.cups4j.operations.ipp.IppHoldJobOperation;
import org.cups4j.operations.ipp.IppReleaseJobOperation;

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
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 631;
  public static final String DEFAULT_USER = System.getProperty("user.name", "anonymous");

  private String host = null;
  private int port = -1;
  private String user = null;

  private CupsAuthentication creds = null;

  /**
   * Creates a CupsClient for localhost port 631 with user anonymous
   * 
   * @throws Exception
   */
  public CupsClient() throws Exception {
    this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_USER);
  }

  /**
   * Creates a CupsClient for provided host and port with user anonymous
   * 
   * @param host
   * @param port
   * @throws Exception
   */
  public CupsClient(String host, int port) throws Exception {
    this(host, port, DEFAULT_USER);
  }

  /**
   * Creates a CupsClient for provided host, port and user
   * 
   * @param host
   * @param port
   * @param userName
   * @throws Exception
   */
  public CupsClient(String host, int port, String userName) throws Exception {
    if (host != null && !"".equals(host)) {
      this.host = host;
    } else {
      throw new Exception("The hostname specified: <" + host + "> is not valid");
    }

    if (port > 0) {
      this.port = port;
    } else {
      throw new Exception("The specified port number: <" + port + "> is not valid");
    }

    if (userName != null && !"".equals(userName)) {
      this.user = userName;
    }
  }

  /**
   * Creates a CupsClient for provided host, port and user
   * 
   * @param host
   * @param port
   * @param userName
   * @throws Exception
   */
  public CupsClient(String host, int port, String userName, CupsAuthentication creds) throws Exception {
    super();
    this.creds = creds;
    if (host != null && !"".equals(host)) {
      this.host = host;
    } else {
      throw new Exception("The hostname specified: <" + host + "> is not valid");
    }

    if (port > 0) {
      this.port = port;
    } else {
      throw new Exception("The specified port number: <" + port + "> is not valid");
    }

    if (userName != null && !"".equals(userName)) {
      this.user = userName;
    }
  }

  /**
   * Returns all available printers
   * 
   * @return List of Printers
   * @throws Exception
   */
  public List<CupsPrinter> getPrinters() throws Exception {
    return new CupsGetPrintersOperation(port).getPrinters(host, port, creds);
  }

  /**
   * Returns all available printers except CUPS specific default printer
   * 
   * @return List of Printers
   * @throws Exception
   */
  public List<CupsPrinter> getPrintersWithoutDefault() throws Exception {
    CupsGetPrintersOperation cgp = new CupsGetPrintersOperation();
    List<CupsPrinter> result = cgp.getPrinters(host, port, creds);
    return result;
  }

  /**
   * Returns the printer for the provided URL
   * 
   * @param printerURL
   *          an URL like http://localhost:631/printers/printername
   * @return printer
   * @throws Exception
   */
  public CupsPrinter getPrinter(URL printerURL) throws Exception {
    List<CupsPrinter> printers = getPrinters();
    for (CupsPrinter printer : printers) {
      if (printer.getPrinterURL().toString().equals(printerURL.toString()))
        return printer;
    }
    return null;
  }

  /**
   * Returns the printer for the provided name
   *
   * @param printerName
   *          the printer name
   * @return printer
   * @throws Exception
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
   * @throws Exception
   */
  public CupsPrinter getDefaultPrinter() throws Exception {
    return new CupsGetDefaultOperation().getDefaultPrinter(host, port, creds);
  }

  /**
   * Returns the printer for the provided URL on the current host
   *
   * @param printerURL
   *          a URL like /printers/printername
   * @return printer
   * @throws Exception
   */
  public CupsPrinter getPrinterOnCurrentHost(String printerURL) throws Exception {
    return getPrinter(new URL("http://" + host + ":" + port + printerURL));
  }

  /**
   * Returns job attributes for the job associated with the provided jobID
   * 
   * @param jobID
   * @return Job attributes
   * @throws Exception
   */
  public PrintJobAttributes getJobAttributes(int jobID) throws Exception {
    return getJobAttributes(host, user, jobID);
  }

  /**
   * Returns job attributes for the job associated with the provided jobID and
   * user name
   * 
   * @param userName
   * @param jobID
   * @return Job attributes
   * @throws Exception
   */
  public PrintJobAttributes getJobAttributes(String userName, int jobID) throws Exception {
    return getJobAttributes(host, userName, jobID);
  }

  /**
   * Returns job attributes for the job associated with the provided jobID on
   * provided host and port
   * 
   * @param hostname
   * @param jobID
   * @return Job attributes
   * @throws Exception
   */
  private PrintJobAttributes getJobAttributes(String hostname, String userName, int jobID) throws Exception {
    if (userName == null || "".equals(userName)) {
      userName = DEFAULT_USER;
    }
    if (hostname == null || "".equals(hostname)) {
      hostname = DEFAULT_HOST;
    }

    return new IppGetJobAttributesOperation(port).getPrintJobAttributes(hostname, userName, port, jobID, creds);
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
   * @throws Exception
   */
  public List<PrintJobAttributes> getJobs(CupsPrinter printer, WhichJobsEnum whichJobs, String userName, boolean myJobs)
      throws Exception {
    return new IppGetJobsOperation(port).getPrintJobs(printer, whichJobs, userName, myJobs, creds);
  }

  /**
   * Cancel the job with the provided jobID on the current host wit current user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception
   */
  public boolean cancelJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppCancelJobOperation(port).cancelJob(host, user, jobID, printer, creds);
  }

  /**
   * Hold the job with the provided jobID on the current host wit current set
   * user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception
   */
  public boolean holdJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppHoldJobOperation(port).holdJob(host, user, jobID, printer, creds);
  }

  /**
   * Release the held job with the provided jobID on the current host wit
   * current set user
   * 
   * @param jobID
   * @return boolean success
   * @throws Exception
   */
  public boolean releaseJob(CupsPrinter printer, int jobID) throws Exception {
    return new IppReleaseJobOperation(port).releaseJob(host, user, jobID, printer, creds);
  }

  /**
   * Moves the print job with job ID jobID from currentPrinter to targetPrinter
   * 
   * @param jobID
   * @param userName
   * @param currentPrinter
   * @param targetPrinter
   * @return boolean successs
   * @throws Exception
   */
  public boolean moveJob(int jobID, String userName, CupsPrinter currentPrinter, CupsPrinter targetPrinter)
      throws Exception {
    String currentHost = currentPrinter.getPrinterURL().getHost();

    return new CupsMoveJobOperation(port).moveJob(currentPrinter, currentHost, userName, jobID,
        targetPrinter.getPrinterURL(), creds);
  }

}

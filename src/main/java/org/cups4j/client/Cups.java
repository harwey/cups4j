package org.cups4j.client;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.WhichJobsEnum;

public class Cups {

  public static void main(String[] args) throws FileNotFoundException {

    String host = "localhost";

    String printerName = null;
    boolean print = false;
    boolean getPrinters = false;
    boolean getJobs = false;
    boolean getPrinterAttributes = false;
    boolean duplex = false;
    String fileName = null;
    String userName = null;
    String attributes = null;
    int copies = 1;
    String pages = null;

    try {
      if (args.length == 0) {
        usage();
      }
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-h")) {
          host = args[++i];
        } else if (args[i].equals("getPrinters")) {
          getPrinters = true;
        } else if (args[i].equals("printFile")) {
          print = true;
          fileName = args[++i];
        } else if (args[i].equals("getJobs")) {
          getJobs = true;
        } else if (args[i].equals("getPrinterAttributes")) {
          getPrinterAttributes = true;
        } else if (args[i].equals("-u")) {
          userName = args[++i];
        } else if (args[i].equals("-c")) {
          copies = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-p")) {
          pages = args[++i].trim();
        } else if (args[i].equals("-P")) {
          printerName = args[++i];
        } else if (args[i].equals("-duplex")) {
          duplex = true;
        } else if (args[i].equals("-job-attributes")) {
          attributes = args[++i];
        } else if (args[i].equals("-help")) {
          usage();
        }
      }

      if (getPrinters) {
        listPrintersOnHost(host);
      }
      if (print) {
        print(host, printerName, fileName, copies, pages, duplex, attributes);
      }
      if (getJobs) {
        getJobs(host, userName, printerName);
      }
      if (getPrinterAttributes) {
        getPrinterAttributes(host, userName, printerName);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void getPrinterAttributes(String host, String userName, String printerName) throws Exception {
    if (host == null) {
      host = CupsClient.DEFAULT_HOST;
    }

    if (userName == null) {
      userName = CupsClient.DEFAULT_USER;
    }
    if (printerName == null) {
      CupsClient cupsClient = new CupsClient(host, CupsClient.DEFAULT_PORT, userName);
      printerName = cupsClient.getDefaultPrinter().getName();
    }

    CupsPrinter printer = new CupsPrinter(new URL("http://" + host + "/printers/" + printerName), printerName, false);
    System.out.println("Media supported:");
    for (String media : printer.getMediaSupported()) {
      System.out.println(" Media: " + media);
    }

  }

  private static void getJobs(String host, String userName, String printerName) throws Exception {
    if (host == null) {
      host = CupsClient.DEFAULT_HOST;
    }

    if (userName == null) {
      userName = CupsClient.DEFAULT_USER;
    }
    if (printerName == null) {
      CupsClient cupsClient = new CupsClient(host, CupsClient.DEFAULT_PORT, userName);
      printerName = cupsClient.getDefaultPrinter().getName();
    }

    CupsClient cupsClient = new CupsClient();

    // if user provided - get only jobs from this user.
    boolean myJobs = true;
    if (userName.equals(CupsClient.DEFAULT_USER)) {
      myJobs = false;
    }
    List<PrintJobAttributes> jobs = cupsClient.getJobs(
        new CupsPrinter(new URL("http://" + host + "/printers/" + printerName), printerName, false), WhichJobsEnum.ALL,
        userName, myJobs);

    for (PrintJobAttributes a : jobs) {
      System.out.println("job: " + a.getJobID() + " " + a.getJobName() + " " + a.getJobState() + " " + a.getPrinterURL()
          + " " + a.getUserName());
    }
  }

  private static void print(String host, String printerName, String fileName, int copies, String pages, boolean duplex,
      String attributes) throws Exception {
    FileInputStream fileInputStream = new FileInputStream(fileName);

    CupsPrinter printer = null;
    CupsClient cupsClient = new CupsClient(host, CupsClient.DEFAULT_PORT);
    if (printerName == null) {

      printer = cupsClient.getDefaultPrinter();
    } else {
      printer = new CupsPrinter(new URL("http://" + host + ":" + CupsClient.DEFAULT_PORT + "/printers/" + printerName),
          printerName, false);
    }

    HashMap<String, String> attributeMap = new HashMap<String, String>();
    if (attributes != null) {
      attributeMap.put("job-attributes", attributes.replace("+", "#"));
    }

    PrintJob printJob = new PrintJob.Builder(fileInputStream).jobName("testJobName").userName("harald").copies(copies)
        .pageRanges(pages).duplex(duplex).attributes(attributeMap).build();

    PrintRequestResult printRequestResult = printer.print(printJob);
    if (printRequestResult.isSuccessfulResult()) {
      int jobID = printRequestResult.getJobId();

      System.out.println("file sent to " + printer.getPrinterURL() + " jobID: " + jobID);
      System.out.println("... current status = " + printer.getJobStatus(jobID));
      Thread.sleep(1000);
      System.out.println("... status after 1 sec. = " + printer.getJobStatus(jobID));

      System.out.println("Get last Printjob");
      PrintJobAttributes job = cupsClient.getJobAttributes(host, jobID);
      System.out.println("ID: " + job.getJobID() + " user: " + job.getUserName() + " url: " + job.getJobURL()
          + " status: " + job.getJobState());
    } else {
      // you might throw an exception or try to retry printing the job
      throw new Exception("print error! status code: " + printRequestResult.getResultCode() + " status description: "
          + printRequestResult.getResultDescription());

    }

  }

  private static void listPrintersOnHost(String hostname) throws Exception {

    System.out.println("List printers on " + hostname + ":");
    List<CupsPrinter> printers = null;
    long timeoutTime = System.currentTimeMillis() + 10000;
    while (System.currentTimeMillis() < timeoutTime && printers == null) {
      try {
        CupsClient cupsClient = new CupsClient(hostname, CupsClient.DEFAULT_PORT);
        printers = cupsClient.getPrinters();
      } catch (Exception e) {
        System.out.println("could not get printers... retrying");
      }
    }

    if (printers == null || printers.size() == 0) {
      throw new Exception("Error! Could not find any printers - check CUPS log files please.");
    }

    for (CupsPrinter p : printers) {
      System.out.println(p.toString());
    }
    System.out.println("----\n");
  }

  private static void usage() {
    System.out.println(
        "CupsTest [-h <hostname>] [getPrinters][getJobs [-u <userName>][-P <printer name>]][printFile <file name> [-P <printer name>] [-c <copies>][-p <pages>][-duplex][-job-attributes <attributes>]] -help ");
    System.out.println("  <hostname>      - CUPS host name or ip adress (default: localhost)");
    System.out.println("  getPrinters     - list all printers from <hostname>");
    System.out.println("  getJobs         - list Jobs for given printer and user name on given host.");
    System.out.println(
        "                    defaults are: <hostname>=localhost, printer=default on <hostname>, user=anonymous");
    System.out.println("  printFile       - print the file provided in following parameter");
    System.out.println("  <filename>      - postscript file to print");
    System.out.println("  <printer name>  - printer name on <hostname>");
    System.out
        .println("  <copies>        - number of copies (default: 1 wich means the document will be printed once)");
    System.out.println("  <pages>         - ranges of pages to print in the following syntax: ");
    System.out.println("                    1-2,4,6,10-12 - single ranges need to be in ascending order");
    System.out.println("  -duplex         - turns on double sided printing");
    System.out.println("  <attributes>    - this is a list of additional print-job-attributes separated by '+' like:\n"
        + "                    print-quality:enum:3+job-collation-type:enum:2");

    System.out.println("  -help           - shows this text");
  }

}

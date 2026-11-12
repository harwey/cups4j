/*
 * Copyright (c) 2026 by Oliver Boehm
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
package org.cups4j.operations.cups;

import ch.ethz.vppserver.ippclient.IppResult;
import ch.ethz.vppserver.ippclient.IppTag;
import org.cups4j.CupsAuthentication;
import org.cups4j.PrintRequestResult;
import org.cups4j.operations.IppOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the CUPS specific operation <a
 * href="https://www.cups.org/doc/spec-ipp.html#CUPS_ADD_MODIFY_PRINTER">CUPS-Add-Modify-Printer</a>
 * (operation id {@code 0x4003}) to add a new printer or to modify an existing printer on a CUPS
 * server.
 * <p>
 * A typical use case is adding a network printer:
 * </p>
 *
 * <pre>{@code
 * CupsAddModifyPrinterOperation operation = new CupsAddModifyPrinterOperation(631);
 * operation.addModifyPrinter("cups.example.com", "my-printer",
 *         URI.create("ipp://192.168.1.50:631/ipp/print"), null, creds);
 * }</pre>
 *
 * @author oboehm
 * @since 0.8.4
 */
public class CupsAddModifyPrinterOperation extends IppOperation {

  private static final Logger LOG = LoggerFactory.getLogger(CupsAddModifyPrinterOperation.class);

  public CupsAddModifyPrinterOperation() {
    operationID = 0x4003;
    bufferSize = 8192;
  }

  public CupsAddModifyPrinterOperation(int port) {
    this();
    this.ippPort = port;
  }

  /**
   * Creates the IPP header for the CUPS-Add-Modify-Printer request.
   * <p>
   * The {@code printer-uri} is taken from the given attribute map if present. Otherwise it is
   * derived from the given URI. This is necessary because the request itself is sent to the
   * CUPS admin resource ({@code /admin/}) while the {@code printer-uri} has to point to the
   * printer resource ({@code /printers/<name>}).
   * </p>
   *
   * @param uri CUPS URI, usually the admin URI
   * @param map operation and printer attributes
   * @return IPP header
   * @throws UnsupportedEncodingException if encoding is not supported
   */
  @Override
  public ByteBuffer getIppHeader(URI uri, Map<String, String> map) throws UnsupportedEncodingException {
    if (uri == null) {
      LOG.error("CupsAddModifyPrinterOperation.getIppHeader(): uri is null");
      return null;
    }

    ByteBuffer ippBuf = ByteBuffer.allocateDirect(bufferSize);
    ippBuf = IppTag.getOperation(ippBuf, operationID);

    String printerUri = (map == null) ? null : map.get("printer-uri");
    if (printerUri == null) {
      printerUri = stripPortNumber(uri);
    }
    ippBuf = IppTag.getUri(ippBuf, "printer-uri", printerUri);

    if (map == null) {
      ippBuf = IppTag.getEnd(ippBuf);
      ippBuf.flip();
      return ippBuf;
    }

    if (map.get("requesting-user-name") != null) {
      ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "requesting-user-name",
          map.get("requesting-user-name"));
    }

    if (hasPrinterAttributes(map)) {
      ippBuf = IppTag.getPrinterAttributesTag(ippBuf);
      ippBuf = addPrinterAttributes(ippBuf, map);
    }

    ippBuf = IppTag.getEnd(ippBuf);
    ippBuf.flip();
    return ippBuf;
  }

  /**
   * Adds or modifies a printer on the CUPS server.
   *
   * @param hostname          host name of the CUPS server
   * @param printerName       name of the printer to add or modify
   * @param deviceURI         device URI of the printer, e.g. {@code ipp://192.168.1.50:631/ipp/print}
   * @param printerAttributes optional printer attributes, may be {@code null}
   * @param creds             credentials of a CUPS administrator, may be {@code null}
   * @return {@code true} if the operation was successful
   * @throws IOException in case of I/O problems
   */
  public boolean addModifyPrinter(String hostname, String printerName, URI deviceURI,
                                  Map<String, String> printerAttributes,
                                  CupsAuthentication creds) throws IOException {
    return addModifyPrinter(createURI(hostname, "/admin/"), printerName, deviceURI,
        printerAttributes, null, creds);
  }

  /**
   * Adds or modifies a printer on the CUPS server.
   *
   * @param cupsURI           base URI of the CUPS server, e.g. {@code http://cups.example.com:631}
   * @param printerName       name of the printer to add or modify
   * @param deviceURI         device URI of the printer, e.g. {@code ipp://192.168.1.50:631/ipp/print}
   * @param printerAttributes optional printer attributes, may be {@code null}
   * @param creds             credentials of a CUPS administrator, may be {@code null}
   * @return {@code true} if the operation was successful
   * @throws IOException in case of I/O problems
   */
  public boolean addModifyPrinter(URI cupsURI, String printerName, URI deviceURI,
                                  Map<String, String> printerAttributes,
                                  CupsAuthentication creds) throws IOException {
    return addModifyPrinter(cupsURI, printerName, deviceURI, printerAttributes, null, creds);
  }

  /**
   * Adds or modifies a printer on the CUPS server.
   *
   * @param cupsURI           base URI of the CUPS server, e.g. {@code http://cups.example.com:631}
   * @param printerName       name of the printer to add or modify
   * @param deviceURI         device URI of the printer, e.g. {@code ipp://192.168.1.50:631/ipp/print}
   * @param printerAttributes optional printer attributes, may be {@code null}
   * @param ppd               optional PPD file to upload, may be {@code null}
   * @param creds             credentials of a CUPS administrator, may be {@code null}
   * @return {@code true} if the operation was successful
   * @throws IOException in case of I/O problems
   */
  public boolean addModifyPrinter(URI cupsURI, String printerName, URI deviceURI,
                                  Map<String, String> printerAttributes, InputStream ppd,
                                  CupsAuthentication creds) throws IOException {
    if (cupsURI == null) {
      throw new IllegalArgumentException("CUPS URI must not be null");
    }
    if (printerName == null || printerName.isEmpty()) {
      throw new IllegalArgumentException("printer name must not be empty");
    }
    if (deviceURI == null) {
      throw new IllegalArgumentException("device URI must not be null");
    }

    Map<String, String> attributes = new HashMap<>();
    if (printerAttributes != null) {
      attributes.putAll(printerAttributes);
    }
    attributes.put("printer-uri", toPrinterURI(cupsURI, printerName).toString());
    attributes.put("device-uri", deviceURI.toString());
    attributes.putIfAbsent("requesting-user-name", getUserName(creds));

    IppResult result = request(null, toAdminURI(cupsURI), attributes, ppd, creds);
    if (result == null) {
      return false;
    }
    if (result.getHttpStatusCode() >= 300) {
      throw new IOException("HTTP error! Status code: " + result.getHttpStatusResponse());
    }
    return new PrintRequestResult(result).isSuccessfulResult();
  }

  private static ByteBuffer addPrinterAttributes(ByteBuffer ippBuf, Map<String, String> map)
      throws UnsupportedEncodingException {
    if (map.get("device-uri") != null) {
      ippBuf = IppTag.getUri(ippBuf, "device-uri", map.get("device-uri"));
    }
    if (map.get("printer-info") != null) {
      ippBuf = IppTag.getTextWithoutLanguage(ippBuf, "printer-info", map.get("printer-info"));
    }
    if (map.get("printer-location") != null) {
      ippBuf = IppTag.getTextWithoutLanguage(ippBuf, "printer-location", map.get("printer-location"));
    }
    if (map.get("printer-more-info") != null) {
      ippBuf = IppTag.getUri(ippBuf, "printer-more-info", map.get("printer-more-info"));
    }
    if (map.get("printer-is-accepting-jobs") != null) {
      ippBuf = IppTag.getBoolean(ippBuf, "printer-is-accepting-jobs",
          Boolean.parseBoolean(map.get("printer-is-accepting-jobs")));
    }
    if (map.get("printer-is-shared") != null) {
      ippBuf = IppTag.getBoolean(ippBuf, "printer-is-shared",
          Boolean.parseBoolean(map.get("printer-is-shared")));
    }
    if (map.get("ppd-name") != null) {
      ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "ppd-name", map.get("ppd-name"));
    }
    if (map.get("printer-state") != null) {
      ippBuf = IppTag.getEnum(ippBuf, "printer-state", Integer.parseInt(map.get("printer-state")));
    }
    if (map.get("printer-state-message") != null) {
      ippBuf = IppTag.getTextWithoutLanguage(ippBuf, "printer-state-message",
          map.get("printer-state-message"));
    }
    return ippBuf;
  }

  private static boolean hasPrinterAttributes(Map<String, String> map) {
    return map.containsKey("device-uri") || map.containsKey("printer-info")
        || map.containsKey("printer-location") || map.containsKey("printer-more-info")
        || map.containsKey("printer-is-accepting-jobs") || map.containsKey("printer-is-shared")
        || map.containsKey("ppd-name") || map.containsKey("printer-state")
        || map.containsKey("printer-state-message");
  }

  private static URI toAdminURI(URI cupsURI) {
    String scheme = (cupsURI.getScheme() == null) ? "http" : cupsURI.getScheme();
    return URI.create(scheme + "://" + getAuthority(cupsURI) + "/admin/");
  }

  private static URI toPrinterURI(URI cupsURI, String printerName) {
    String scheme = "https".equalsIgnoreCase(cupsURI.getScheme()) ? "ipps" : "ipp";
    return URI.create(scheme + "://" + getAuthority(cupsURI) + "/printers/" + printerName);
  }

  private static String getAuthority(URI uri) {
    String host = uri.getHost();
    if (host == null || host.isEmpty()) {
      throw new IllegalArgumentException("CUPS URI has no host: " + uri);
    }
    int port = uri.getPort();
    return (port < 0) ? host : host + ":" + port;
  }

  private static String getUserName(CupsAuthentication creds) {
    if (creds != null && creds.getUserid() != null) {
      return creds.getUserid();
    }
    return System.getProperty("user.name", "anonymous");
  }

}

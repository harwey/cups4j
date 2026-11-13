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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the CUPS specific operation <a
 * href="https://www.cups.org/doc/spec-ipp.html#CUPS_DELETE_PRINTER">CUPS-Delete-Printer</a>
 * (operation id {@code 0x4004}) to remove an existing printer from a CUPS server.
 *
 * <pre>{@code
 * CupsDeletePrinterOperation operation = new CupsDeletePrinterOperation(631);
 * operation.deletePrinter("cups.example.com", "my-printer", creds);
 * }</pre>
 *
 * @author oboehm
 * @since 0.8.4
 */
public class CupsDeletePrinterOperation extends IppOperation {

  private static final Logger LOG = LoggerFactory.getLogger(CupsDeletePrinterOperation.class);

  public CupsDeletePrinterOperation() {
    operationID = 0x4004;
    bufferSize = 8192;
  }

  public CupsDeletePrinterOperation(int port) {
    this();
    this.ippPort = port;
  }

  /**
   * Creates the IPP header for the CUPS-Delete-Printer request.
   * <p>
   * The {@code printer-uri} is taken from the given attribute map if present. Otherwise it is
   * derived from the given URI. This is necessary because the request itself is sent to the
   * CUPS admin resource ({@code /admin/}) while the {@code printer-uri} has to point to the
   * printer resource ({@code /printers/<name>}).
   * </p>
   *
   * @param uri CUPS URI, usually the admin URI
   * @param map operation attributes
   * @return IPP header
   * @throws UnsupportedEncodingException if encoding is not supported
   */
  @Override
  public ByteBuffer getIppHeader(URI uri, Map<String, String> map) throws UnsupportedEncodingException {
    if (uri == null) {
      LOG.error("CupsDeletePrinterOperation.getIppHeader(): uri is null");
      return null;
    }

    ByteBuffer ippBuf = ByteBuffer.allocateDirect(bufferSize);
    ippBuf = IppTag.getOperation(ippBuf, operationID);

    String printerUri = (map == null) ? null : map.get("printer-uri");
    if (printerUri == null) {
      printerUri = stripPortNumber(uri);
    }
    ippBuf = IppTag.getUri(ippBuf, "printer-uri", printerUri);

    if (map != null && map.get("requesting-user-name") != null) {
      ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "requesting-user-name",
          map.get("requesting-user-name"));
    }

    ippBuf = IppTag.getEnd(ippBuf);
    ippBuf.flip();
    return ippBuf;
  }

  /**
   * Deletes a printer on the CUPS server.
   *
   * @param hostname    host name of the CUPS server
   * @param printerName name of the printer to delete
   * @param creds       credentials of a CUPS administrator, may be {@code null}
   * @return {@code true} if the operation was successful
   * @throws IOException in case of I/O problems
   */
  public boolean deletePrinter(String hostname, String printerName, CupsAuthentication creds)
      throws IOException {
    return deletePrinter(createURI(hostname, "/admin/"), printerName, creds);
  }

  /**
   * Deletes a printer on the CUPS server.
   *
   * @param cupsURI     base URI of the CUPS server, e.g. {@code http://cups.example.com:631}
   * @param printerName name of the printer to delete
   * @param creds       credentials of a CUPS administrator, may be {@code null}
   * @return {@code true} if the operation was successful
   * @throws IOException in case of I/O problems
   */
  public boolean deletePrinter(URI cupsURI, String printerName, CupsAuthentication creds)
      throws IOException {
    if (cupsURI == null) {
      throw new IllegalArgumentException("CUPS URI must not be null");
    }
    if (printerName == null || printerName.isEmpty()) {
      throw new IllegalArgumentException("printer name must not be empty");
    }

    Map<String, String> attributes = new HashMap<>();
    attributes.put("printer-uri", toPrinterURI(cupsURI, printerName).toString());
    attributes.put("requesting-user-name", getUserName(creds));

    IppResult result = request(null, toAdminURI(cupsURI), attributes, creds);
    return new PrintRequestResult(result).isSuccessfulResult();
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

package org.cups4j.operations.cups;

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

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.operations.IppOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class CupsGetDefaultOperation extends IppOperation {

  private static final Logger log = LoggerFactory.getLogger(CupsGetDefaultOperation.class);

  public CupsGetDefaultOperation() {
    operationID = 0x4001;
    bufferSize = 8192;
  }

  public CupsGetDefaultOperation(int port) {
    this();
    this.ippPort = port;
  }

  public CupsPrinter getDefaultPrinter(String hostname, int port, CupsAuthentication creds) throws IOException {
    return getDefaultPrinter(URI.create("http://" + hostname + ":" + port), creds);
  }

  public CupsPrinter getDefaultPrinter(URI uri, CupsAuthentication creds) throws IOException {
    CupsGetDefaultOperation command = new CupsGetDefaultOperation(uri.getPort());

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("requested-attributes", "printer-name printer-uri-supported printer-location");
    map.put("requesting-user-name", getUserName(creds));

    IppResult result = command.request(null, uri, map, creds);
    log.debug("{} received from request to {}.", result, uri);
    for (AttributeGroup group : result.getAttributeGroupList()) {
      log.debug("Handling {}...", group);
      if (group.getTagName().equals("printer-attributes-tag")) {
        if (group.getAttribute() != null) {
          for (Attribute attr : group.getAttribute()) {
            if (attr.getName().equals("printer-name") && attr.getAttributeValue() != null && !attr.getAttributeValue().isEmpty()) {
              String printerName = attr.getAttributeValue().get(0).getValue();
              log.info("Default printer '{}' found with {}.", printerName, attr);
              CupsPrinter defaultPrinter = new CupsClient(uri).getPrinter(printerName);
              defaultPrinter.setDefault(true);
              return defaultPrinter;
            }
          }
        }
      }
    }
    log.info("No default printer found in {}.", result);
    return null;
  }

  private static String getUserName(CupsAuthentication creds) {
    if (creds != null && creds.getUserid() != null) {
      return creds.getUserid();
    } else {
      return System.getProperty("user.name", "anonymous");
    }
  }

}

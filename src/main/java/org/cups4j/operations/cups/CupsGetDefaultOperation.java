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

import java.net.URI;
import java.util.HashMap;

public class CupsGetDefaultOperation extends IppOperation {
  public CupsGetDefaultOperation() {
    operationID = 0x4001;
    bufferSize = 8192;
  }

  public CupsGetDefaultOperation(int port) {
    this();
    this.ippPort = port;
  }

  public CupsPrinter getDefaultPrinter(String hostname, int port, CupsAuthentication creds) throws Exception {
    return getDefaultPrinter(URI.create("http://" + hostname + ":" + port), creds);
  }

  public CupsPrinter getDefaultPrinter(URI uri, CupsAuthentication creds) throws Exception {
    CupsPrinter defaultPrinter = null;
    CupsGetDefaultOperation command = new CupsGetDefaultOperation(uri.getPort());

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("requested-attributes", "printer-name printer-uri-supported printer-location");

    IppResult result = command.request(null, URI.create(uri + "/printers"), map, creds);
    for (AttributeGroup group : result.getAttributeGroupList()) {
      if (group.getTagName().equals("printer-attributes-tag")) {
        String printerName = null;
        for (Attribute attr : group.getAttribute()) {
          if (attr.getName().equals("printer-name")) {
            printerName = attr.getAttributeValue().get(0).getValue();
          }
        }
        defaultPrinter = new CupsClient(uri).getPrinter(printerName);
        defaultPrinter.setDefault(true);
      }
    }

    return defaultPrinter;
  }

}

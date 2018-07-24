package org.cups4j;

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
import org.cups4j.ipp.attributes.AttributeGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Result of a print request
 * 
 * 
 */
public class PrintRequestResult {
  private int jobId;
  private String resultCode = "";
  private String resultDescription = "";
  private final IppResult ippResult;

  public PrintRequestResult(IppResult ippResult) {
    this.ippResult = ippResult;
    if ((ippResult == null) || isNullOrEmpty(ippResult.getHttpStatusResponse())) {
      return;
    }
    initializeFromHttpStatusResponse(ippResult);
    if (ippResult.getIppStatusResponse() != null) {
      initializeFromIppStatusResponse(ippResult);
    }
  }

  private void initializeFromIppStatusResponse(IppResult ippResult) {
    Pattern p = Pattern.compile("Status Code:(0x\\d+)(.*)");
    Matcher m = p.matcher(ippResult.getIppStatusResponse());
    if (m.find()) {
      this.resultCode = m.group(1);
      this.resultDescription = m.group(2);
    }
  }

  private void initializeFromHttpStatusResponse(IppResult ippResult) {
    Pattern p = Pattern.compile("HTTP/1.0 (\\d+) (.*)");
    Matcher m = p.matcher(ippResult.getHttpStatusResponse());
    if (m.find()) {
      this.resultCode = m.group(1);
      this.resultDescription = m.group(2);
    }
  }

  private boolean isNullOrEmpty(String string) {
    return (string == null) || ("".equals(string.trim()));
  }

  public boolean isSuccessfulResult() {
    return (resultCode != null) && getResultCode().startsWith("0x00");
  }

  public String getResultCode() {
    if (ippResult.getHttpStatusCode() < 400) {
    return resultCode;
    } else {
      return "0x" + ippResult.getHttpStatusCode();
    }
  }

  public String getResultDescription() {
    return resultDescription;
  }

  public String getResultMessage() {
    if (ippResult.hasAttributeGroup("operation-attributes-tag")) {
      AttributeGroup attributeGroup = ippResult.getAttributeGroup("operation-attributes-tag");
      return attributeGroup.getAttribute("status-message").getValue();
    } else {
      return ippResult.getHttpStatusResponse();
    }
  }

  public int getJobId() {
    return jobId;
  }

  protected void setJobId(int jobId) {
    this.jobId = jobId;
  }

}

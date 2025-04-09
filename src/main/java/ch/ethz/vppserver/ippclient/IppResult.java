package ch.ethz.vppserver.ippclient;

import org.apache.commons.lang.StringUtils;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * program; if not, see <http://www.gnu.org/licenses/>.
 */
public class IppResult {
  private static final Logger log = LoggerFactory.getLogger(IppResult.class);
  private String httpStatusResponse = null;
  private String ippStatusResponse = null;
  private List<AttributeGroup> attributeGroupList = new ArrayList<AttributeGroup>();
  private int httpStatusCode;

  public IppResult() {
  }

  /**
   * 
   * @return
   */
  public String getHttpStatusResponse() {
    return httpStatusResponse;
  }

  /**
   * 
   * @param statusResponse
   */
  public void setHttpStatusResponse(String statusResponse) {
    httpStatusResponse = statusResponse;
  }

  /**
   * 
   * @return
   */
  public String getIppStatusResponse() {
    return ippStatusResponse;
  }

  /**
   * 
   * @param statusResponse
   */
  public void setIppStatusResponse(String statusResponse) {
    ippStatusResponse = statusResponse;
  }

  /**
   * 
   * @return
   */
  public List<AttributeGroup> getAttributeGroupList() {
    return attributeGroupList;
  }

  public AttributeGroup getAttributeGroup(String tagName) {
    for (AttributeGroup group : attributeGroupList) {
      if (tagName.equalsIgnoreCase(group.getTagName())) {
        return group;
      }
    }
    throw new IllegalArgumentException("tag '" + tagName + "' not found in " + attributeGroupList
    		+ "; Http status response: " + httpStatusResponse + ", Ipp status response: " + ippStatusResponse);
  }

  public boolean hasAttributeGroup(String tagName) {
    for (AttributeGroup group : attributeGroupList) {
      if (tagName.equalsIgnoreCase(group.getTagName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param group
   */
  public void setAttributeGroupList(List<AttributeGroup> group) {
    attributeGroupList = group;
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(int httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  /**
   * Extracts the IPP status code from the response.
   *
   * @return a number like {@link #getHttpStatusCode()}
   * @since 0.8 (09-Apr-2025, Oli B.)
   */
  public int getIppStatusCode() {
        String code = StringUtils.substringAfter(ippStatusResponse, "Status Code:");
        if (StringUtils.isBlank(code)) {
            log.debug("No status code found in ipp response '{}'.", ippStatusResponse);
            return 0;
        }
        code = StringUtils.substringBefore(code, "(").trim();
        if (code.startsWith("0x")) {
            code = code.substring(2);
        }
        return Integer.parseInt(code);
    }

    /**
     * Combines the HTTP and IPP status code. The resulting status code is
     * the max value of both.
     *
     * @return a number like {@link #getHttpStatusCode()}
     * @since 0.8 (09-Apr-2025, Oli B.)
     */
    public int getStatusCode() {
        return Math.max(httpStatusCode, getIppStatusCode());
    }

	public boolean isPrintQueueUnavailable() {
		return ippStatusResponse != null && ippStatusResponse.contains("client-error-not-possible");
	}

}

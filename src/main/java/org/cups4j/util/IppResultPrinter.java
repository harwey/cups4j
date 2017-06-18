package org.cups4j.util;

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
import java.util.List;

import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.ipp.attributes.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.vppserver.ippclient.IppResult;

public class IppResultPrinter {

  private static final Logger LOG = LoggerFactory.getLogger(IppResultPrinter.class);

  public static void print(IppResult result) {
    LOG.info(result.getHttpStatusResponse());
    LOG.info(result.getIppStatusResponse());
    List<AttributeGroup> attributeGroupList = result.getAttributeGroupList();
    printAttributeGroupList(attributeGroupList);
  }

  public static void print(IppResult result, boolean nurHeader) {
    if (nurHeader) {
      LOG.info(result.getHttpStatusResponse());
      LOG.info(result.getIppStatusResponse());
    } else {
      print(result);
    }
  }

  private static void printAttributeGroupList(List<AttributeGroup> list) {
    if (list == null) {
      return;
    }
    int l = list.size();
    for (int i = 0; i < l; i++) {
      AttributeGroup attributeGroup = list.get(i);
      printAttributeGroup(attributeGroup);
    }
  }

  private static void printAttributeGroup(AttributeGroup attributeGroup) {
    if (attributeGroup == null) {
      return;
    }
    LOG.info("\r\nAttribute Group: " + attributeGroup.getTagName());
    List<Attribute> attributeList = attributeGroup.getAttribute();
    printAttributeList(attributeList);
  }

  private static void printAttributeList(List<Attribute> list) {
    if (list == null) {
      return;
    }
    int l = list.size();
    for (int i = 0; i < l; i++) {
      Attribute attr = list.get(i);
      printAttribute(attr);
    }
  }

  private static void printAttribute(Attribute attr) {
    if (attr == null) {
      return;
    }
    LOG.info("\tAttribute Name: " + attr.getName());
    List<AttributeValue> attributeValueList = attr.getAttributeValue();
    printAttributeValueList(attributeValueList);
  }

  private static void printAttributeValueList(List<AttributeValue> list) {
    if (list == null) {
      return;
    }
    int l = list.size();
    for (int i = 0; i < l; i++) {
      AttributeValue attrValue = list.get(i);
      LOG.info(
          "\t\tAttribute Value: (" + attrValue.getTagName() + "[" + attrValue.getTag() + "] " + attrValue.getValue());
    }
  }

}

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

import ch.ethz.vppserver.ippclient.IppResult;

public class IppResultPrinter {
  /**
   * Print IPP response to standard output stream
   * 
   * @param IppResult
   */
  public static void print(IppResult result) {
    System.out.println(result.getHttpStatusResponse());
    System.out.println(result.getIppStatusResponse());
    List<AttributeGroup> attributeGroupList = result.getAttributeGroupList();
    printAttributeGroupList(attributeGroupList);
  }

  public static void print(IppResult result, boolean nurHeader) {
    if (nurHeader) {
      System.out.println(result.getHttpStatusResponse());
      System.out.println(result.getIppStatusResponse());
    } else {
      print(result);
    }
  }

  /**
   * 
   * @param list
   */
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

  /**
   * 
   * @param attributeGroup
   */
  private static void printAttributeGroup(AttributeGroup attributeGroup) {
    if (attributeGroup == null) {
      return;
    }
    System.out.println("\r\nAttribute Group: " + attributeGroup.getTagName());
    List<Attribute> attributeList = attributeGroup.getAttribute();
    printAttributeList(attributeList);
  }

  /**
   * 
   * @param list
   */
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

  /**
   * 
   * @param attr
   */
  private static void printAttribute(Attribute attr) {
    if (attr == null) {
      return;
    }
    System.out.println("\tAttribute Name: " + attr.getName());
    List<AttributeValue> attributeValueList = attr.getAttributeValue();
    printAttributeValueList(attributeValueList);
  }

  /**
   * 
   * @param list
   */
  private static void printAttributeValueList(List<AttributeValue> list) {
    if (list == null) {
      return;
    }
    int l = list.size();
    for (int i = 0; i < l; i++) {
      AttributeValue attrValue = list.get(i);
      System.out.println("\t\tAttribute Value: (" + attrValue.getTagName() + "[" + attrValue.getTag() + "] "
          + attrValue.getValue());
    }
  }

}

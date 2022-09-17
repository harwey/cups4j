package ch.ethz.vppserver.ippclient;

/**
 * Copyright (C) 2012 Harald Weyhing
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

import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.ipp.attributes.AttributeList;
import org.cups4j.ipp.attributes.Tag;
import org.cups4j.ipp.attributes.TagList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IppAttributeProvider implements IIppAttributeProvider {
  private static final IppAttributeProvider INSTANCE = new IppAttributeProvider();
  private final List<Tag> tagList = new ArrayList<>();
  private final List<AttributeGroup> attributeGroupList = new ArrayList<>();
  
  public static IppAttributeProvider getInstance() {
    return INSTANCE;
  }

  private IppAttributeProvider() {
    ClassLoader classLoader = IppAttributeProvider.class.getClassLoader();
    try {
      JAXBContext tagListContext  = JAXBContext.newInstance(TagList.class);
      Unmarshaller tagListUnmarshaller = tagListContext.createUnmarshaller();
    
      InputStream tagListStream = classLoader.getResourceAsStream(TAG_LIST_FILENAME);
      TagList tList = (TagList) tagListUnmarshaller.unmarshal(tagListStream);
      this.tagList.addAll(tList.getTag());
    } catch (Exception e) {
      throw new RuntimeException("Error unmarshalling tag list", e);
    }
    
    try {
      JAXBContext attListContext = JAXBContext.newInstance(AttributeList.class);
      Unmarshaller attListUnmarshaller = attListContext.createUnmarshaller();
      InputStream attListStream = classLoader.getResourceAsStream(ATTRIBUTE_LIST_FILENAME);
      AttributeList aList = (AttributeList) attListUnmarshaller.unmarshal(attListStream);
      List<AttributeGroup> attributeGroups = aList.getAttributeGroup();
      this.attributeGroupList.addAll(attributeGroups);
    } catch (Exception e) {
      throw new RuntimeException("Error unmarshalling attribute groups", e);
    }
  }
  
  /**
   * @return tag list
   */
  public List<Tag> getTagList() {
    return tagList;
  }

  /**
   * @return attribute groups
   */
  public List<AttributeGroup> getAttributeGroupList() {
    return attributeGroupList;
  }
}

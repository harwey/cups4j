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
import java.util.List;

import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.ipp.attributes.Tag;

public interface IIppAttributeProvider {
  public final static String TAG_LIST_FILENAME = "config/ippclient/ipp-list-of-tag.xml";
  public final static String ATTRIBUTE_LIST_FILENAME = "config/ippclient/ipp-list-of-attributes.xml";
  public final static String CONTEXT = "ch.ethz.vppserver.schema.ippclient";

  public List<Tag> getTagList();

  public List<AttributeGroup> getAttributeGroupList();
}

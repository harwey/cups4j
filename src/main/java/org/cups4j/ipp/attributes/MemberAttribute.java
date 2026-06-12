/*
 * Copyright (c) 2026 by Oli B.
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
package org.cups4j.ipp.attributes;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "member-attribute")
public class MemberAttribute {

    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String tag;
    @XmlAttribute(name = "tag-name", required = true)
    protected String tagName;
    @XmlAttribute
    protected String value;
    @XmlAttribute
    protected String description;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String value) {
        this.tag = value;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String value) {
        this.tagName = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
}

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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CollectionAttributeTest {

    @Test
    public void testGetMemberAttribute() {
        CollectionAttribute collectionAttribute = new CollectionAttribute();
        collectionAttribute.setName("media-col");
        collectionAttribute.memberAttribute = new ArrayList<>();
        collectionAttribute.memberAttribute.add(createMemberAttribute("media-type", "0x44", "keyword", "stationery"));

        assertEquals(1, collectionAttribute.getMemberAttribute().size());
        assertEquals("media-col", collectionAttribute.getName());

        MemberAttribute member = collectionAttribute.getMemberAttribute().get(0);
        assertEquals("media-type", member.getName());
        assertEquals("0x44", member.getTag());
        assertEquals("keyword", member.getTagName());
        assertEquals("stationery", member.getValue());
    }

    @Test
    public void testEmptyMemberAttribute() {
        CollectionAttribute collectionAttribute = new CollectionAttribute();
        collectionAttribute.setName("media-col");
        assertEquals(0, collectionAttribute.getMemberAttribute().size());
    }

    @Test
    public void testCollectionAttributeInAttributeValue() {
        MemberAttribute member = new MemberAttribute();
        member.setName("media-type");
        member.setTag("0x44");
        member.setTagName("keyword");
        member.setValue("stationery");

        CollectionAttribute collectionAttribute = new CollectionAttribute();
        collectionAttribute.setName("media-col");
        collectionAttribute.getMemberAttribute().add(member);

        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setTag("0x34");
        attributeValue.setTagName("begCollection");
        attributeValue.setCollectionAttribute(collectionAttribute);

        assertEquals("0x34", attributeValue.getTag());
        assertEquals("begCollection", attributeValue.getTagName());
        assertEquals(collectionAttribute, attributeValue.getCollectionAttribute());
        assertEquals(1, attributeValue.getCollectionAttribute().getMemberAttribute().size());
    }

    @Test
    public void testCollectionAttributeWithMultipleMembers() {
        CollectionAttribute collectionAttribute = new CollectionAttribute();
        collectionAttribute.setName("media-col");

        collectionAttribute.getMemberAttribute().add(createMemberAttribute("media-type", "0x44", "keyword", "stationery"));
        collectionAttribute.getMemberAttribute().add(createMemberAttribute("media-source", "0x44", "keyword", "main"));
        collectionAttribute.getMemberAttribute().add(createMemberAttribute("media-color", "0x22", "boolean", "true"));

        assertEquals(3, collectionAttribute.getMemberAttribute().size());

        assertEquals("media-color", collectionAttribute.getMemberAttribute().get(2).getName());
        assertEquals("0x22", collectionAttribute.getMemberAttribute().get(2).getTag());
        assertEquals("true", collectionAttribute.getMemberAttribute().get(2).getValue());
    }

    private static MemberAttribute createMemberAttribute(String name, String tag, String tagName, String value) {
        MemberAttribute member = new MemberAttribute();
        member.setName(name);
        member.setTag(tag);
        member.setTagName(tagName);
        member.setValue(value);
        return member;
    }

}

package org.cups4j.ipp.attributes;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;


public final class AttributeGroupTest {

    private final AttributeGroup attributeGroup = new AttributeGroup();

    @Test
    public void testGetAttribute() {
        attributeGroup.attribute = new ArrayList<Attribute>();
        attributeGroup.attribute.add(createAttribute("hello"));
        attributeGroup.attribute.add(createAttribute("world"));
        assertEquals(2, attributeGroup.getAttribute().size());
    }

    private static Attribute createAttribute(String name) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setDescription("test of " + name);
        return attribute;
    }

}
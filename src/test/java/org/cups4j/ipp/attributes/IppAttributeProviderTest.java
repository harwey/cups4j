package org.cups4j.ipp.attributes;

import ch.ethz.vppserver.ippclient.IppAttributeProvider;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IppAttributeProviderTest {
	
	@Test
	public void testAttributeGroups() {
		List<AttributeGroup> attributeGroups = IppAttributeProvider.getInstance().getAttributeGroupList();
		assertEquals(5, attributeGroups.size());
		for (AttributeGroup attributeGroup : attributeGroups) {
			testAttributeGroup(attributeGroup);
		}
	}
	
	private void testAttributeGroup(AttributeGroup group) {
		assertNotNull(group.getTag());
		assertFalse(group.getTag().isEmpty());
		assertNotNull(group.getTagName());
		assertFalse(group.getTagName().isEmpty());
		
		for (Attribute attribute : group.getAttribute()) {
			testAttribute(attribute);
		}
	}
	
	private void testAttribute(Attribute attribute) {
		assertNotNull(attribute.getName());
		
		for (AttributeValue attributeValue : attribute.getAttributeValue()) {
			assertNotNull(attributeValue.getTag());
			assertFalse(attributeValue.getTag().isEmpty());
			assertNotNull(attributeValue.getTagName());
			assertFalse(attributeValue.getTagName().isEmpty());
			
			SetOfKeyword setOfKeyword = attributeValue.getSetOfKeyword();
			if (setOfKeyword != null) {
				for (Keyword keyword : setOfKeyword.getKeyword()) {
					assertNotNull(keyword.getValue());
					assertFalse(keyword.getValue().isEmpty());
				}
			}
		}
	}
	
	@Test
	public void testTagList() {
		List<Tag> tagList = IppAttributeProvider.getInstance().getTagList();
		assertEquals(37, tagList.size());
		for (Tag tag : tagList) {
			assertNotNull(tag.getName());
			assertFalse(tag.getName().isEmpty());
			assertNotNull(tag.getValue());
			assertFalse(tag.getValue().isEmpty());
		}
	}
}

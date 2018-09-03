package ch.ethz.vppserver.ippclient;

import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IppResultTest {

    @Test
    public void testGetAttributeGroupList() {
        IppResult ippResult = new IppResult();
        List<AttributeGroup> attributeGroupList = ippResult.getAttributeGroupList();
        assertNotNull(attributeGroupList);
        assertTrue(attributeGroupList.isEmpty());
    }

}

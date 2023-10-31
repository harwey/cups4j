package ch.ethz.vppserver.ippclient;

import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class IppResultTest {

    @Test
    public void testGetAttributeGroupList() {
        IppResult ippResult = new IppResult();
        List<AttributeGroup> attributeGroupList = ippResult.getAttributeGroupList();
        assertNotNull(attributeGroupList);
        assertTrue(attributeGroupList.isEmpty());
    }

}

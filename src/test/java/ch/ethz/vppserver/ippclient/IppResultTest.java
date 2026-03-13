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

    @Test
    public void testGetStatusCode() {
        IppResult ippResult = new IppResult();
        ippResult.setHttpStatusCode(200);
        ippResult.setIppStatusResponse("Major Version:0x01 Minor Version:0x01 Request Id:4\n" +
                "Status Code:0x0400(client-error-bad-request)");
        assertEquals(400, ippResult.getIppStatusCode());
        assertEquals(400, ippResult.getStatusCode());
    }

}

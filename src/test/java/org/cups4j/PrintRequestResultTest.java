package org.cups4j;

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Unit tests for class {@link PrintRequestResult}.
 *
 * @author oliver (boehm@javatux.de)
 */
public class PrintRequestResultTest {

    @Test
    public void testGetResultCode() {
        IppResult ippResult = createIppResult401();
        PrintRequestResult result = new PrintRequestResult(ippResult);
        assertEquals("0x401", result.getResultCode());
    }

    private static IppResult createIppResult401() {
        IppResult ippResult = new IppResult();
        ippResult.setHttpStatusResponse("HTTP/1.1 401 Nicht autorisiert");
        ippResult.setIppStatusResponse("Major Version:0x3c Minor Version:0x21 Request Id:1129601360\n" +
                "Status Code:0x444f(enum name not found in IANA list: 17487)");
        ippResult.setHttpStatusCode(401);
        ippResult.setAttributeGroupList(Collections.<AttributeGroup>emptyList());
        return ippResult;
    }

}
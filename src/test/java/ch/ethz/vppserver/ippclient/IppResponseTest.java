package ch.ethz.vppserver.ippclient;

import org.apache.commons.io.FileUtils;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for class {@link IppResponse}.
 *
 * @author oliver (boehm@javatux.de)
 */
public class IppResponseTest {

    private final IppResponse ippResponse = new IppResponse();

    @Test
    public void testGetResponse() throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File("src/test/resources/ipp/IppResponse400.bin"));
        IppResult ippResult = ippResponse.getResponse(ByteBuffer.wrap(data));
        String statusResponse = ippResult.getIppStatusResponse();
        assertThat(statusResponse, containsString("client-error-bad-request"));
        AttributeGroup attributeGroup =
                ippResult.getAttributeGroup("operation-attributes-tag");
        Attribute attr = attributeGroup.getAttribute("status-message");
        assertEquals("Got a printer-uri attribute but no job-id.", attr.getAttributeValue().get(0).getValue());
    }

}
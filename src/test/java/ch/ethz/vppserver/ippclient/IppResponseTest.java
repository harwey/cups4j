package ch.ethz.vppserver.ippclient;

import org.apache.commons.io.FileUtils;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Unit tests for class {@link IppResponse}.
 *
 * @author oliver (boehm@javatux.de)
 */
public class IppResponseTest {

    private final IppResponse ippResponse = new IppResponse();

    @Test
    public void testGetResponse() throws IOException {
        IppResult ippResult = readIppResponse("IppResponse400.bin");
        assertTrue(ippResult.getIppStatusResponse().contains("client-error-bad-request"), "No client-error-bad-request returned.");

        AttributeGroup attributeGroup =
                ippResult.getAttributeGroup("operation-attributes-tag");
        Attribute attr = attributeGroup.getAttribute("status-message");
        assertEquals("Got a printer-uri attribute but no job-id.", attr.getAttributeValue().get(0).getValue());
    }

    /**
     * The recorded response is a response with "Unauthorized" from a CUPS
     * server on a Mac with a HTTP status code 401. It should be translated
     * into a "client-error-forbidden" (0x0401) or "client-error-not-authenticated"
     * (0x0403).
     *
     * @throws IOException in case of read errors
     */
    @Test
    public void testGetResponseUnauthorized() throws IOException {
        IppResult ippResult = readIppResponse("error401.html");
        assertTrue(ippResult.getIppStatusResponse().contains("client-error-"), "No client-error- returned.");
    }

    private IppResult readIppResponse(String filename) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File("src/test/resources/ipp", filename));
        return ippResponse.getResponse(ByteBuffer.wrap(data));
    }

}
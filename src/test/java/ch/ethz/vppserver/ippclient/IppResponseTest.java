package ch.ethz.vppserver.ippclient;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;

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
        String statusResponse = ippResult.getIppStatusResponse();
        assertThat(statusResponse, containsString("client-error-bad-request"));
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
        assertThat(ippResult.getIppStatusResponse(), containsString("client-error-"));
    }

    @Test
    public void testGetResponseDecodesUtf8TextAttributes() throws IOException {
        IppResult ippResult = ippResponse.getResponse(createIppResponse("utf-8", "打印机就绪", StandardCharsets.UTF_8));
        AttributeGroup attributeGroup = ippResult.getAttributeGroup("operation-attributes-tag");
        Attribute attr = attributeGroup.getAttribute("status-message");
        assertEquals("打印机就绪", attr.getAttributeValue().get(0).getValue());
    }

    @Test
    public void testGetResponseUsesDeclaredAttributesCharset() throws IOException {
        Charset latin1 = Charset.forName("ISO-8859-1");
        IppResult ippResult = ippResponse.getResponse(createIppResponse("iso-8859-1", "Jörg", latin1));
        AttributeGroup attributeGroup = ippResult.getAttributeGroup("operation-attributes-tag");
        Attribute attr = attributeGroup.getAttribute("status-message");
        assertEquals("Jörg", attr.getAttributeValue().get(0).getValue());
    }

    private IppResult readIppResponse(String filename) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File("src/test/resources/ipp", filename));
        return ippResponse.getResponse(ByteBuffer.wrap(data));
    }

    private ByteBuffer createIppResponse(String charsetName, String message, Charset messageCharset) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeByte(0x01);
        out.writeByte(0x01);
        out.writeShort(0x0000);
        out.writeInt(1);
        out.writeByte(0x01);
        writeTextAttribute(out, 0x47, "attributes-charset", charsetName, StandardCharsets.US_ASCII);
        writeTextAttribute(out, 0x48, "attributes-natural-language", "zh-cn", StandardCharsets.US_ASCII);
        writeTextAttribute(out, 0x41, "status-message", message, messageCharset);
        out.writeByte(0x03);
        out.flush();
        return ByteBuffer.wrap(bytes.toByteArray());
    }

    private void writeTextAttribute(DataOutputStream out, int tag, String name, String value, Charset charset)
            throws IOException {
        byte[] nameBytes = name.getBytes(StandardCharsets.US_ASCII);
        byte[] valueBytes = value.getBytes(charset);
        out.writeByte(tag);
        out.writeShort(nameBytes.length);
        out.write(nameBytes);
        out.writeShort(valueBytes.length);
        out.write(valueBytes);
    }

}

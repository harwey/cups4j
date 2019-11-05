package org.cups4j.operations.ipp;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.apache.commons.io.FileUtils;
import org.cups4j.CupsPrinter;
import org.cups4j.CupsPrinterTest;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link IppSendDocumentOperation}-Klasse.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppSendDocumentOperationTest extends AbstractIppOperationTest {

    private static final Logger LOG = LoggerFactory.getLogger(IppSendDocumentOperationTest.class);
    private final IppSendDocumentOperation operation = new IppSendDocumentOperation(4711);

    /**
     * This is only a basic test to see if the operation tag is set coorect.
     *
     * @throws UnsupportedEncodingException in case of encoding problemss
     */
    @Test
    public void testGetIppHeader() throws UnsupportedEncodingException {
        ByteBuffer buffer = getIppHeader(operation);
        assertEquals(6, buffer.get(3));
        checkAttribute(buffer, "printer-uri", "http://localhost:631/printers/test-printer");
    }

    /**
     * For the send-document command it is important, that the header contains
     * the job-id. This is tested here.
     *
     * @throws IOException in case of encoding or other problemss
     */
    @Test
    public void testGetIppHeaderWithJobId() throws IOException {
        URL printerURL = createURL("http://localhost:631/test-printer");
        Map<String, String> attributes = setUpAttributes();
        ByteBuffer buffer = operation.getIppHeader(printerURL, attributes);
        byte[] header = toByteArray(buffer);
        assertThat(new String(header), containsString("job-id"));
        checkIppRequest(header);
        checkIppRequestAttributes(header);
    }

    /**
     * The last Send-Document request for a given Job includes a
     * "last-document" operation attribute set to 'true' indicating that
     * this is the last request.
     *
     * @throws IOException in case of encoding or other problemss
     */
    @Test
    public void testGetIppHeaderOfLastDocument() throws IOException {
        IppSendDocumentOperation op = new IppSendDocumentOperation(6631, 4711, true);
        Map<String, String> attributes = setUpAttributes();
        ByteBuffer buffer = op.getIppHeader(createURL("http://localhost:6631/test-printer"), attributes);
        byte[] header = toByteArray(buffer);
        IppResult ippResult = new IppResponse().getResponse(ByteBuffer.wrap(header));
        AttributeGroup group = ippResult.getAttributeGroupList().get(0);
        Attribute attribute = group.getAttribute("last-document");
        assertNotNull(attribute);
        assertEquals("true", attribute.getValue());
    }

    private static void checkIppRequest(byte[] header) throws IOException {
        IppResult ippResult = new IppResponse().getResponse(ByteBuffer.wrap(header));
        Set<String> groupTagNames = new HashSet<String>();
        for (AttributeGroup group : ippResult.getAttributeGroupList()) {
            String tagName = group.getTagName();
            assertThat("duplicate tag name", groupTagNames, not(hasItem(tagName)));
            groupTagNames.add(tagName);
        }
    }

    private static void checkIppRequestAttributes(byte[] header) throws IOException {
        IppResponse ippResponse = new IppResponse();
        IppResult ippResult = new IppResponse().getResponse(ByteBuffer.wrap(header));
        IppResult ref = ippResponse.getResponse(
                ByteBuffer.wrap(FileUtils.readFileToByteArray(new File("src/test/resources/ipp/Send-Document.ipp"))));
        for (AttributeGroup group : ref.getAttributeGroupList()) {
            checkAttributeGroupList(group, ippResult.getAttributeGroup(group.getTagName()));
        }
    }

    private static void checkAttributeGroupList(AttributeGroup ref, AttributeGroup attributeGroup) {
        Set<String> attributeNames = new HashSet<String>();
        for (Attribute attr : attributeGroup.getAttribute()) {
            attributeNames.add(attr.getName());
        }
        for (Attribute attr : ref.getAttribute()) {
            if (!attributeNames.contains(attr.getName())) {
                fail("attribute '" + attr.getName() + "' is missing in " + attributeGroup);
            }
        }
    }

    /**
     * We should see the login user in the header. Otherwise we may get a
     * 401-response (forbidden).
     *
     * @throws UnsupportedEncodingException in case of encoding problemss
     */
    @Test
    public void testGetIppHeaderWithUser() throws UnsupportedEncodingException {
        URL printerURL = createURL("http://localhost:631/test-printer");
        Map<String, String> attributes = setUpAttributes();
        ByteBuffer buffer = operation.getIppHeader(printerURL, attributes);
        byte[] header = toByteArray(buffer);
        String user = System.getProperty("user.name", "anonymous");
        assertThat(new String(header), containsString(user));
    }

    private static byte[] toByteArray(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    @Test
    @Ignore
    public void testRequest() throws Exception {
        CupsPrinter printer = CupsPrinterTest.getPrinter();
        if (printer == null) {
            LOG.info("You must set system property 'printer' to activate this test!");
            LOG.info("testRequest() is SKIPPED.");
        } else {
            checkRequest(printer, printer.getPrinterURL());
        }
    }

    private void checkRequest(CupsPrinter printer, URL printerURL) throws Exception {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome");
        attributes.put("job-name", "testosteron");
        attributes.put("requesting-user-name", "oboehm");
        ByteArrayInputStream document = new ByteArrayInputStream("Hello World!\n".getBytes());
        IppResult ippResult = operation.request(printer, printerURL, attributes, document, null);
        assertEquals(200, ippResult.getHttpStatusCode());
    }

}

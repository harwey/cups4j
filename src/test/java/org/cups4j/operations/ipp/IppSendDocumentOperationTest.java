package org.cups4j.operations.ipp;

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.CupsPrinter;
import org.cups4j.CupsPrinterTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
    }

    /**
     * For the send-document command it is important, that the header contains
     * the job-id. This is tested here.
     *
     * @throws UnsupportedEncodingException in case of encoding problemss
     */
    @Test
    public void testGetIppHeaderWithJobId() throws UnsupportedEncodingException {
        URL printerURL = createURL("http://localhost:631/test-printer");
        Map<String, String> attributes = setUpAttributes();
        attributes.put("operation-attributes", "job-id:integer:40#last-document:boolean:false");
        ByteBuffer buffer = operation.getIppHeader(printerURL, attributes);
        byte[] header = toByteArray(buffer);
        assertThat(new String(header), containsString("job-id"));
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
    public void testRequest() throws Exception {
        CupsPrinter printer = CupsPrinterTest.getPrinter();
        if (printer == null) {
            LOG.info("You must set system property 'printer' to activate this test!");
            LOG.info("testRequest() is SKIPPED.");
        } else {
            checkRequest(printer.getPrinterURL());
        }
    }

    private void checkRequest(URL printerURL) throws Exception {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome");
        attributes.put("job-name", "testosteron");
        attributes.put("requesting-user-name", "oboehm");
        ByteArrayInputStream document = new ByteArrayInputStream("Hello World!\n".getBytes());
        IppResult ippResult = operation.request(printerURL, attributes, document);
        assertEquals(200, ippResult.getHttpStatusCode());
    }

}

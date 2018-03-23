package org.cups4j.operations.ipp;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit-tests for {@link IppCreateJobOperation} class.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppCreateJobOperationTest {
    
    private final IppCreateJobOperation operation = new IppCreateJobOperation();

    @Test
    public void testGetIppHeader() throws MalformedURLException, UnsupportedEncodingException {
        URL printerURL = new URL("http://localhost:631/test-printer");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("requested-attributes", "copies-supported page-ranges-supported printer-name " +
                "printer-info printer-location printer-make-and-model printer-uri-supported media-supported " +
                "media-default sides-supported sides-default orientation-requested-supported " +
                "printer-resolution-supported printer printer-resolution-default number-up-default " +
                "number-up-supported document-format-supported print-color-mode-supported print-color-mode-default " +
                "multiple-operation-time-out multiple-document-jobs-supported multiple-document-handling " +
                "multiple-document-handling-supported");
        attributes.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome");
        attributes.put("job-name", "testJUCW5V");
        attributes.put("requesting-user-name", "anonymous");
        ByteBuffer buffer = operation.getIppHeader(printerURL, attributes);
        assertEquals(5, buffer.get(3));
    }

}

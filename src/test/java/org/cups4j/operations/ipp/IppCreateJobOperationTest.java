package org.cups4j.operations.ipp;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.CupsPrinter;
import org.cups4j.CupsPrinterTest;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit-tests for {@link IppCreateJobOperation} class.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppCreateJobOperationTest extends AbstractIppOperationTest {

    private static final Logger LOG = LoggerFactory.getLogger(IppCreateJobOperationTest.class);
    private final IppCreateJobOperation operation = new IppCreateJobOperation();

    @Test
    public void testGetIppHeaderMap() throws UnsupportedEncodingException {
        ByteBuffer buffer = getIppHeader(operation);
        assertEquals(5, buffer.get(3));
    }
    
    @Test
    public void testGetIppHeader() throws UnsupportedEncodingException {
        URL printerURL = createURL("http://localhost:631/test-printer");
        ByteBuffer buffer = operation.getIppHeader(printerURL);
        checkAttribute(buffer, "printer-uri");
        checkAttribute(buffer, "requesting-user-name");
    }

    private static void checkAttribute(ByteBuffer buffer, String name) {
        IppResponse ippResponse = new IppResponse();
        try {
            buffer.rewind();
            IppResult ippResult = ippResponse.getResponse(buffer);
            for (AttributeGroup group : ippResult.getAttributeGroupList()) {
                for (Attribute attr : group.getAttribute()) {
                    if (name.equals(attr.getName())) {
                        String value = attr.getValue();
                        assertNotEquals("attribute '" + name + "' is empty", 0, value.length());
                        return;
                    }
                }
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("invalid ByteBuffer " + buffer, ioe);
        }
        fail("Attribute '" + name + "' not found.");
    }

    private static byte[] toByteArray(ByteBuffer buffer) {
        byte[] array = new byte[buffer.limit()];
        buffer.get(array);
        return array;
    }
    
    @Test
    public void testRequest() {
        CupsPrinter cupsPrinter = CupsPrinterTest.getPrinter();
        IppResult ippResult = operation.request(cupsPrinter.getPrinterURL());
        assertNotNull(ippResult);
        checkAttribute(ippResult, "job-uri");
        checkAttribute(ippResult, "job-id");
        checkAttribute(ippResult, "job-state");
        checkAttribute(ippResult, "job-reasons");
    }
    
    private static void checkAttribute(IppResult ippResult, String name) {
        for (AttributeGroup attributeGroup : ippResult.getAttributeGroupList()) {
            if (hasAttribute(attributeGroup.getAttribute(), name)) {
                LOG.info("Attribute '{}' was found in {}.", name, ippResult.getAttributeGroupList());
                return;
            }
        }
        fail("Attribute '" + name + "' not found in " + ippResult.getAttributeGroupList());
    }

    private static boolean hasAttribute(List<Attribute> attributes, String name) {
        for (Attribute attr : attributes) {
            if (name.equals(attr.getName())) {
                LOG.info("{} = {}", name, attr.getAttributeValue());
                return true;
            }
        }
        return false;
    }

}

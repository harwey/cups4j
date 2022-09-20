package org.cups4j.operations.ipp;

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.CupsPrinter;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.operations.AbstractIppOperationTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
  public void testOperationId() throws UnsupportedEncodingException {
    ByteBuffer buffer = this.getIppHeader(operation);
    assertEquals(5, buffer.get(3));
  }

  @Test
  public void testGetIppHeader() throws UnsupportedEncodingException {
    URL printerURL = getPrinterURL();
    ByteBuffer buffer = operation.getIppHeader(printerURL);
    checkAttribute(buffer, "printer-uri", printerURL.toString());
    checkAttribute(buffer, "requesting-user-name", System.getProperty("user.name", "anonymous"));
  }

  @Test
  public void testGetIppHeaderWithJobName() throws UnsupportedEncodingException {
    Map<String, String> map = new HashMap<>();
    map.put("job-name", "Test-Job");
    ByteBuffer buffer = operation.getIppHeader(getPrinterURL(), map);
    checkAttribute(buffer, "job-name", "Test-Job");
  }
  
  @Test
  public void testRequest() {
    CupsPrinter cupsPrinter = this.getPrinter();
    IppResult ippResult = operation.request(cupsPrinter, cupsPrinter.getPrinterURL(), null);
    assertNotNull(ippResult);
    checkAttribute(ippResult, "job-uri");
    checkAttribute(ippResult, "job-id");
    checkAttribute(ippResult, "job-state");
    checkAttribute(ippResult, "job-state-reasons");
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

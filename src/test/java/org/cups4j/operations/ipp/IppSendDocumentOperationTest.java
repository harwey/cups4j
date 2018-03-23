package org.cups4j.operations.ipp;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link IppSendDocumentOperation}-Klasse.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppSendDocumentOperationTest extends AbstractIppOperationTest {

    private final IppSendDocumentOperation operation = new IppSendDocumentOperation();

    @Test
    public void testGetIppHeader() throws MalformedURLException, UnsupportedEncodingException {
        ByteBuffer buffer = getIppHeader(operation);
        assertEquals(6, buffer.get(3));
    }
    
}

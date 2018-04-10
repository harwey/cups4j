package org.cups4j.operations.ipp;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Unit-tests for {@link IppCreateJobOperation} class.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppCreateJobOperationTest extends AbstractIppOperationTest {
    
    private final IppCreateJobOperation operation = new IppCreateJobOperation();

    @Test
    public void testGetIppHeader() throws UnsupportedEncodingException {
        ByteBuffer buffer = getIppHeader(operation);
        assertEquals(5, buffer.get(3));
    }

}

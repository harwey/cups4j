package org.cups4j;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for class {@link PrintRequestResult}.
 *
 * @author oliver (boehm@javatux.de)
 */
public class PrintRequestResultTest {

    @Test
    public void testGetResultCode() {
        IppResult ippResult = createIppResult401();
        PrintRequestResult result = new PrintRequestResult(ippResult);
        assertEquals("0x401", result.getResultCode());
    }

    private static IppResult createIppResult401() {
        IppResult ippResult = readIppResponse("error401.html");
        ippResult.setHttpStatusResponse("HTTP/1.1 401 Nicht autorisiert");
        ippResult.setHttpStatusCode(401);
        return ippResult;
    }

    private static IppResult readIppResponse(String filename) {
        try {
            byte[] data = FileUtils.readFileToByteArray(new File("src/test/resources/ipp", filename));
            return new IppResponse().getResponse(ByteBuffer.wrap(data));
        } catch (IOException ioe) {
            throw new IllegalArgumentException("cannot load '" + filename + "'", ioe);
        }
    }

}

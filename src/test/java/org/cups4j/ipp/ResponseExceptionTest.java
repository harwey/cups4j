package org.cups4j.ipp;

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.operations.IppOperation;
import org.cups4j.operations.ipp.IppCreateJobOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link ResponseException}.
 *
 * @author: oboehm
 */
public final class ResponseExceptionTest {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseExceptionTest.class);

    @Test
    public void testGetMessage() {
        IppOperation op = new IppCreateJobOperation();
        IppResult result = createIppResult(400, "Bad Request");
        ResponseException ex = new ResponseException(op, result);
        assertThat(ex.getMessage(), containsString("Bad Request"));
        LOG.info("msg = \"{}\"", ex.getMessage());
    }

    private static IppResult createIppResult(int sc, String response) {
        IppResult result = new IppResult();
        result.setHttpStatusCode(sc);
        result.setHttpStatusResponse(response);
        result.setIppStatusResponse("Major Version:0x02 Minor Version:0x00 Request Id:3\n" +
                "Status Code:0x0400(client-error-bad-request)");
        return result;
    }

}

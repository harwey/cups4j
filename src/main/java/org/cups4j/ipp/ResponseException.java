package org.cups4j.ipp;

import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.operations.IppOperation;

/**
 * The class ResponseException will be thrown if we will get an unexpected
 * response.
 *
 * @author oboehm
 * @since 0.7.6 (13.12.2018)
 */
public final class ResponseException extends RuntimeException {

    /**
     * Constructs a new response exception with the specified detail message
     * from the given operation and response.
     *
     * @param op        the operation
     * @param ippResult the cause of the exception
     */
    public ResponseException(IppOperation op, IppResult ippResult) {
        super(op + " failed with " + ippResult);
    }

}

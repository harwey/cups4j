/*
 * Copyright (c) 2018 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 23.03.2018 by oboehm (ob@oasd.de)
 */
package org.cups4j.operations.ipp;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.cups4j.operations.IppOperation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasse AbstractIppOperationTest.
 *
 * @author oboehm
 * @since x.x (23.03.2018)
 */
public abstract class AbstractIppOperationTest {

    protected static ByteBuffer getIppHeader(IppOperation operation) throws UnsupportedEncodingException {
        URL printerURL = createURL("http://localhost:631/printers/test-printer");
        Map<String, String> attributes = setUpAttributes();
        return operation.getIppHeader(printerURL, attributes);
    }

    protected static Map<String, String> setUpAttributes() {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("requested-attributes", "copies-supported page-ranges-supported printer-name " +
                "printer-info printer-location printer-make-and-model printer-uri-supported media-supported " +
                "media-default sides-supported sides-default orientation-requested-supported " +
                "printer-resolution-supported printer printer-resolution-default number-up-default " +
                "number-up-supported document-format-supported print-color-mode-supported print-color-mode-default " +
                "multiple-operation-time-out multiple-document-jobs-supported multiple-document-handling " +
                "multiple-document-handling-supported");
        attributes.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome#" +
                "job-state-reasons:keyword:incoming#job-id:integer:815");
        attributes.put("job-name", "testJUCW5V");
        attributes.put("document-name", "test-document");
        return attributes;
    }

    protected static URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("not a URL: " + url, ex);
        }
    }

    protected static void checkAttribute(ByteBuffer buffer, String name, String expectedValue) {
        IppResponse ippResponse = new IppResponse();
        try {
            buffer.rewind();
            IppResult ippResult = ippResponse.getResponse(buffer);
            for (AttributeGroup group : ippResult.getAttributeGroupList()) {
                for (Attribute attr : group.getAttribute()) {
                    if (name.equals(attr.getName())) {
                        String value = attr.getValue();
                        assertEquals(expectedValue, value);
                        return;
                    }
                }
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("invalid ByteBuffer " + buffer, ioe);
        }
        fail("Attribute '" + name + "' not found.");
    }


}

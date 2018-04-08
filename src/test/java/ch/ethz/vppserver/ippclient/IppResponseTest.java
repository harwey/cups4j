package ch.ethz.vppserver.ippclient;
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 08.04.18 by oliver (ob@oasd.de)
 */

import org.apache.commons.io.FileUtils;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for class {@link IppResponse}.
 *
 * @author oliver (boehm@javatux.de)
 */
public class IppResponseTest {

    private final IppResponse ippResponse = new IppResponse();

    @Test
    public void testGetResponse() throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File("src/test/resources/ipp/IppResponse400.bin"));
        IppResult ippResult = ippResponse.getResponse(ByteBuffer.wrap(data));
        String statusResponse = ippResult.getIppStatusResponse();
        assertThat(statusResponse, containsString("client-error-bad-request"));
        AttributeGroup attributeGroup =
                ippResult.getAttributeGroup("operation-attributes-tag");
        assertThat(statusResponse, containsString("Got a printer-uri attribute but no job-id"));
    }

}
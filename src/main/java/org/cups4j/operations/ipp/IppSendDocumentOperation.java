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

import ch.ethz.vppserver.ippclient.IppResult;
import ch.ethz.vppserver.ippclient.IppTag;
import org.cups4j.CupsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * The class IppSendDocumentOperation represents the operation for sending
 * a document.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppSendDocumentOperation extends IppPrintJobOperation {

    private static final Logger LOG = LoggerFactory.getLogger(IppSendDocumentOperation.class);

    public IppSendDocumentOperation() {
        this(CupsClient.DEFAULT_PORT);
    }
    
    public IppSendDocumentOperation(int port) {
        super(port);
        this.operationID = 0x0006;
    }

    public IppResult request(URL url, Map<String, String> map, InputStream document) throws Exception {
        return sendRequest(url.toURI(), getIppHeader(url, map), document);
    }

    /**
     * Creates the IPP header with the IPP tags.
     *
     * @param url printer-uri
     * @param map attributes map
     * @return IPP header
     * @throws UnsupportedEncodingException in case of unsupported encoding
     */
    @Override
    public ByteBuffer getIppHeader(URL url, Map<String, String> map) throws UnsupportedEncodingException {
        if (url == null) {
            LOG.error("IppPrintJobOperation.getIppHeader(): uri is null");
            return null;
        }

        ByteBuffer ippBuf = ByteBuffer.allocateDirect(bufferSize);
        ippBuf = IppTag.getOperation(ippBuf, operationID);
        ippBuf = IppTag.getUri(ippBuf, "printer-uri", stripPortNumber(url));

        if (map == null) {
            ippBuf = IppTag.getEnd(ippBuf);
            ippBuf.flip();
            return ippBuf;
        }

        String userName = map.get("requesting-user-name");
        if (userName == null) {
            userName = System.getProperty("user.name", CupsClient.DEFAULT_USER);
        }
        ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "requesting-user-name", userName);

        if (map.get("job-name") != null) {
            ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "job-name", map.get("job-name"));
        }

        if (map.get("ipp-attribute-fidelity") != null) {
            boolean value = false;
            if (map.get("ipp-attribute-fidelity").equals("true")) {
                value = true;
            }
            ippBuf = IppTag.getBoolean(ippBuf, "ipp-attribute-fidelity", value);
        }

        if (map.get("document-name") != null) {
            ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "document-name", map.get("document-name"));
        }

        if (map.get("compression") != null) {
            ippBuf = IppTag.getKeyword(ippBuf, "compression", map.get("compression"));
        }

        if (map.get("document-format") != null) {
            ippBuf = IppTag.getMimeMediaType(ippBuf, "document-format", map.get("document-format"));
        }

        if (map.get("document-natural-language") != null) {
            ippBuf = IppTag.getNaturalLanguage(ippBuf, "document-natural-language", map.get("document-natural-language"));
        }

        if (map.get("job-k-octets") != null) {
            int value = Integer.parseInt(map.get("job-k-octets"));
            ippBuf = IppTag.getInteger(ippBuf, "job-k-octets", value);
        }

        if (map.get("job-impressions") != null) {
            int value = Integer.parseInt(map.get("job-impressions"));
            ippBuf = IppTag.getInteger(ippBuf, "job-impressions", value);
        }

        if (map.get("job-media-sheets") != null) {
            int value = Integer.parseInt(map.get("job-media-sheets"));
            ippBuf = IppTag.getInteger(ippBuf, "job-media-sheets", value);
        }

        if (map.get("operation-attributes") != null) {
            String[] attributeBlocks = map.get("operation-attributes").split("#");
            ippBuf = getOperationAttributes(ippBuf, attributeBlocks);
        }

        if (map.get("job-attributes") != null) {
            String[] attributeBlocks = map.get("job-attributes").split("#");
            ippBuf = getJobAttributes(ippBuf, attributeBlocks);
        }

        ippBuf = IppTag.getEnd(ippBuf);
        ippBuf.flip();
        return ippBuf;
    }
    
    private static ByteBuffer getOperationAttributes(ByteBuffer ippBuf, String[] attributeBlocks)
            throws UnsupportedEncodingException {
        if (ippBuf == null) {
            LOG.error("IppPrintJobOperation.getOperationAttributes(): ippBuf is null");
            return null;
        }
        if (attributeBlocks == null) {
            return ippBuf;
        }

        ippBuf = IppTag.getOperationAttributesTag(ippBuf);

        int l = attributeBlocks.length;
        for (int i = 0; i < l; i++) {
            String[] attr = attributeBlocks[i].split(":");
            if ((attr == null) || (attr.length != 3)) {
                throw new IllegalArgumentException(attributeBlocks[i] + ": 'name:type:value' expected as attribute");
            }
            String name = attr[0];
            String tagName = attr[1];
            String value = attr[2];

            if (tagName.equals("boolean")) {
                if (value.equals("true")) {
                    ippBuf = IppTag.getBoolean(ippBuf, name, true);
                } else {
                    ippBuf = IppTag.getBoolean(ippBuf, name, false);
                }
            } else if (tagName.equals("integer")) {
                ippBuf = IppTag.getInteger(ippBuf, name, Integer.parseInt(value));
            } else if (tagName.equals("rangeOfInteger")) {
                String[] range = value.split("-");
                int low = Integer.parseInt(range[0]);
                int high = Integer.parseInt(range[1]);
                ippBuf = IppTag.getRangeOfInteger(ippBuf, name, low, high);
            } else if (tagName.equals("setOfRangeOfInteger")) {
                String ranges[] = value.split(",");

                for (String range : ranges) {
                    range = range.trim();
                    String[] values = range.split("-");

                    int value1 = Integer.parseInt(values[0]);
                    int value2 = value1;
                    // two values provided?
                    if (values.length == 2) {
                        value2 = Integer.parseInt(values[1]);
                    }

                    // first attribute value needs name, additional values need to get the
                    // "null" name
                    ippBuf = IppTag.getRangeOfInteger(ippBuf, name, value1, value2);
                    name = null;
                }
            } else if (tagName.equals("keyword")) {
                ippBuf = IppTag.getKeyword(ippBuf, name, value);
            } else if (tagName.equals("name")) {
                ippBuf = IppTag.getNameWithoutLanguage(ippBuf, name, value);
            } else if (tagName.equals("enum")) {
                ippBuf = IppTag.getEnum(ippBuf, name, Integer.parseInt(value));
            } else if (tagName.equals("resolution")) {
                String[] resolution = value.split(",");
                int value1 = Integer.parseInt(resolution[0]);
                int value2 = Integer.parseInt(resolution[1]);
                byte value3 = Byte.valueOf(resolution[2]);
                ippBuf = IppTag.getResolution(ippBuf, name, value1, value2, value3);
            }
        }
        return ippBuf;
    }

}

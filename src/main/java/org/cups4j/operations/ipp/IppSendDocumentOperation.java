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

import ch.ethz.vppserver.ippclient.IppTag;
import org.cups4j.CupsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * The class IppCreateJobOperation represents  he 
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
            LOG.error("IppGetJObsOperation.getIppHeader(): uri is null");
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

        ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "requesting-user-name", map.get("requesting-user-name"));

        if (map.get("limit") != null) {
            int value = Integer.parseInt(map.get("limit"));
            ippBuf = IppTag.getInteger(ippBuf, "limit", value);
        }

        if (map.get("requested-attributes") != null) {
            String[] sta = map.get("requested-attributes").split(" ");
            if (sta != null) {
                ippBuf = IppTag.getKeyword(ippBuf, "requested-attributes", sta[0]);
                int l = sta.length;
                for (int i = 1; i < l; i++) {
                    ippBuf = IppTag.getKeyword(ippBuf, null, sta[i]);
                }
            }
        }

        ippBuf = IppTag.getEnd(ippBuf);
        ippBuf.flip();
        return ippBuf;
    }

}

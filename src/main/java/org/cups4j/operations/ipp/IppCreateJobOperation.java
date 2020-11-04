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
import ch.ethz.vppserver.ippclient.IppTag;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.operations.IppHttp;
import org.cups4j.operations.IppOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * The class IppCreateJobOperation represents the create-job operation.
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppCreateJobOperation extends IppOperation {

    public IppCreateJobOperation() {
        operationID = 0x0005;
    }

    public IppCreateJobOperation(int port) {
        this();
        this.ippPort = port;
    }

    /**
     * Gets the IPP header with requesting-user-name.
     *
     * @param url where to send the request
     * @return IPP header
     * @throws UnsupportedEncodingException if encoding is not supported.
     */
    @Override
    public ByteBuffer getIppHeader(URL url) throws UnsupportedEncodingException {
        return getIppHeader(url, createAttributeMap());
    }

    private static Map<String, String> createAttributeMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("requesting-user-name", CupsClient.DEFAULT_USER);
        return map;
    }

    /**
     * Gets the IPP header with requesting-user-name.
     *
     * @param url where to send the request
     * @param map attributes
     * @return IPP header
     * @throws UnsupportedEncodingException if encoding is not supported.
     */
    @Override
    public ByteBuffer getIppHeader(URL url, Map<String, String> map) throws UnsupportedEncodingException {
        ByteBuffer ippBuf = ByteBuffer.allocateDirect(bufferSize);
        ippBuf = IppTag.getOperation(ippBuf, operationID);
        ippBuf = IppTag.getUri(ippBuf, "printer-uri", url.toString());
        ippBuf = IppTag.getNameWithoutLanguage(ippBuf, "requesting-user-name", 
                map.get("requesting-user-name"));

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

        if( map.get("job-name") != null) {
            ippBuf = IppTag. getNameWithoutLanguage (ippBuf, "job-name", map.get("job-name"));
        }

        ippBuf = IppTag.getEnd(ippBuf);
        ippBuf.flip();
        return ippBuf;
    }

    public IppResult request(CupsPrinter printer, URL url, CupsAuthentication creds) {
        return request(printer, url, createAttributeMap(), creds);
    }

    public IppResult request(CupsPrinter printer, URL url, Map<String, String> map, CupsAuthentication creds) {
        try {
            return sendRequest(printer, url.toURI(), getIppHeader(url, map), creds);
        } catch (IOException ex) {
            throw new IllegalStateException("cannot request " + url, ex);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("not a valid URI: " + url, ex);
        }
    }

    private IppResult sendRequest(CupsPrinter printer, URI uri, ByteBuffer ippBuf,
    		CupsAuthentication creds) throws IOException {
        CloseableHttpClient client = IppHttp.createHttpClient();

        HttpPost httpPost = new HttpPost(uri);
        IppHttp.setHttpHeaders(httpPost, printer, creds, uri.getHost(), ippPort);

        byte[] bytes = new byte[ippBuf.limit()];
        ippBuf.get(bytes);

        ByteArrayInputStream headerStream = new ByteArrayInputStream(bytes);

        // set length to -1 to advice the entity to read until EOF
        InputStreamEntity requestEntity = new InputStreamEntity(headerStream, -1);

        requestEntity.setContentType(IPP_MIME_TYPE);
        httpPost.setEntity(requestEntity);
        CloseableHttpResponse httpResponse = client.execute(httpPost);
        
        //System.out.println("Response body");
        //System.out.println(Base64.getEncoder().encodeToString(IOUtils.toString(httpResponse.getEntity().getContent()).getBytes()));
        return toIppResult(httpResponse);
    }
    
    private static IppResult toIppResult(CloseableHttpResponse httpResponse) throws IOException {
    	try {
	        IppResponse ippResponse = new IppResponse();
	        IppResult ippResult = ippResponse.getResponse(read(httpResponse.getEntity()));
	        StatusLine statusLine = httpResponse.getStatusLine();
	        ippResult.setHttpStatusResponse(statusLine.getReasonPhrase());
	        ippResult.setHttpStatusCode(statusLine.getStatusCode());
	        return ippResult;
    	} finally {
    		try {
    			httpResponse.close();
    		} catch (IOException e) {}
    	}
    }
    
    private static ByteBuffer read(HttpEntity entity) throws IOException {
        byte[] bytes = IOUtils.toByteArray(entity.getContent());
        return ByteBuffer.wrap(bytes);
    }

}

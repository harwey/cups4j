package org.cups4j.operations;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import ch.ethz.vppserver.ippclient.IppTag;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.ipp.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;

public abstract class IppOperation {
  protected short operationID = -1; // IPP operation ID
  protected short bufferSize = 8192; // BufferSize for this operation
  protected int ippPort = CupsClient.DEFAULT_PORT;

  protected final static String IPP_MIME_TYPE = "application/ipp";

  private static final Logger LOG = LoggerFactory.getLogger(IppOperation.class);

  /**
   * Gets the IPP header
   * 
   * @param url
   * 
   * @return IPP header
   * 
   * @throws UnsupportedEncodingException
   */
  public ByteBuffer getIppHeader(URL url) throws UnsupportedEncodingException {
    return getIppHeader(url, null);
  }

  /**
   * Requests the given printer.
   *
   * @param printer printer
   * @param url     printer URL
   * @param map     printer attributes
   * @param creds   credentials
   * @return IPP result
   * @throws IOException in case of I/O problems
   * @deprecated use {@link #request(CupsPrinter, URI, Map, CupsAuthentication)}
   */
  @Deprecated
  public IppResult request(CupsPrinter printer, URL url, Map<String, String> map,
                           CupsAuthentication creds) throws IOException {
    return request(printer, URI.create(url.toString()), map, creds);
  }

  /**
   * Requests the given printer.
   *
   * @param printer printer
   * @param url     printer URL
   * @param map     printer attributes
   * @param creds   credentials
   * @return IPP result
   * @throws IOException in case of I/O problems
   * since 0.8 (oboehm)
   */
  public IppResult request(CupsPrinter printer, URI url, Map<String, String> map,
                           CupsAuthentication creds) throws IOException {
    return sendRequest(printer, url, getIppHeader(url, map), creds);
  }

  /**
   * Requests the given printer.
   *
   * @param printer  printer
   * @param url      printer URL
   * @param map      printer attributes
   * @param document document stream
   * @param creds    credentials
   * @return IPP result
   * @throws IOException in case of I/O problems
   * @deprecated use {@link #request(CupsPrinter, URI, Map, InputStream, CupsAuthentication)}
   */
  @Deprecated
  public IppResult request(CupsPrinter printer, URL url, Map<String, String> map, InputStream document,
		  CupsAuthentication creds) throws IOException {
    return request(printer, URI.create(url.toString()), map, document, creds);
  }

  /**
   * Requests the given printer.
   *
   * @param printer  printer
   * @param uri      printer URL
   * @param map      printer attributes
   * @param document document stream
   * @param creds    credentials
   * @return IPP result
   * @throws IOException in case of I/O problems
   * sinde 0.8
   */
  public IppResult request(CupsPrinter printer, URI uri, Map<String, String> map, InputStream document,
                           CupsAuthentication creds) throws IOException {
    return sendRequest(printer, uri, getIppHeader(uri, map), document, creds);
  }

  /**
   * Gets the IPP header
   * 
   * @param url
   * @param map
   * 
   * @return IPP header
   *
   * @throws UnsupportedEncodingException if encoding is not supported.
   * @deprecated use {@link #getIppHeader(URI, Map)}
   */
  @Deprecated
  public ByteBuffer getIppHeader(URL url, Map<String, String> map) throws UnsupportedEncodingException {
    return getIppHeader(URI.create(url.toString()), map);
  }

  /**
   * Creates the IPP header with the IPP tags.
   *
   * @param url URI beginning with "ipp://..." or "ipps://..."
   * @param map attribute map
   * @return IPP header
   * @throws UnsupportedEncodingException
   * @since 0.8
   */
  public ByteBuffer getIppHeader(URI url, Map<String, String> map) throws UnsupportedEncodingException {
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

    ippBuf = IppTag.getEnd(ippBuf);
    ippBuf.flip();
    return ippBuf;
  }

  /**
   * Sends a request to the provided URL.
   *
   * @param url    printer URI
   * @param ippBuf IPP buffer
   * @return result
   * @throws IOException in case of I/O problems
   * @since 0.8
   */
  private IppResult sendRequest(CupsPrinter printer, URI url, ByteBuffer ippBuf,
                                CupsAuthentication creds) throws IOException  {
    IppResult result = sendRequest(printer, url, ippBuf, null, creds);
    if (result.getHttpStatusCode() >= 300) {
      throw new IOException(
          "HTTP error! Status code:  " + result.getHttpStatusResponse());
    }
    return result;
  }

  /**
   * Sends a request to the provided URI.
   *
   * @param url            an URI beginning with "ipp://..." or "ipps://..."
   * @param ippBuf         IPP buffer
   * @param documentStream document stream
   * @return result        IPP result
   * @throws IOException in case of error
   * @since 0.8
   */
  private IppResult sendRequest(CupsPrinter printer, URI url, ByteBuffer ippBuf, InputStream documentStream, CupsAuthentication creds)
          throws IOException {
    IppResult ippResult = null;
    if (ippBuf == null) {
      return null;
    }

    if (url == null) {
      return null;
    }

    CloseableHttpClient client = IppHttp.createHttpClient();

    HttpPost httpPost = new HttpPost(url.toString());
    IppHttp.setHttpHeaders(httpPost, printer, creds);

    byte[] bytes = new byte[ippBuf.limit()];
    ippBuf.get(bytes);

    ByteArrayInputStream headerStream = new ByteArrayInputStream(bytes);

    // If we need to send a document, concatenate InputStreams
    InputStream inputStream = headerStream;
    if (documentStream != null) {
      inputStream = new SequenceInputStream(headerStream, documentStream);
    }

    // set length to -1 to advice the entity to read until EOF
    InputStreamEntity requestEntity = new InputStreamEntity(inputStream, -1,
        ContentType.create(IPP_MIME_TYPE));

    httpPost.setEntity(requestEntity);

    final IppHttpResult ippHttpResult = new IppHttpResult();
    ippHttpResult.setStatusCode(-1);

    HttpClientResponseHandler<byte[]> handler =
        new HttpClientResponseHandler<byte[]>() {
          @Override
          public byte[] handleResponse(ClassicHttpResponse response)
              throws HttpException, IOException {
            HttpEntity entity = response.getEntity();
            StatusLine line = new StatusLine(response);

            ippHttpResult.setStatusLine(line.toString());
            ippHttpResult.setStatusCode(response.getCode());

            return (entity != null) ? EntityUtils.toByteArray(entity) : null;
          }
        };

    byte[] result = client.execute(httpPost, handler);

    IppResponse ippResponse = new IppResponse();

    ippResult = ippResponse.getResponse(ByteBuffer.wrap(result));
    ippResult.setHttpStatusResponse(ippHttpResult.getStatusLine());
    ippResult.setHttpStatusCode(ippHttpResult.getStatusCode());

    return ippResult;
  }

  /**
   * Removes the port number in the submitted URL
   *
   * @param url
   * 
   * @return url without port number
   */
  protected String stripPortNumber(URL url) {
    return stripPortNumber(URI.create(url.toString()));
  }

  /**
   * Removes the port number in the submitted URI
   *
   * @param url an URI beginning with "ipp(s)://..." or "http(s)://..."
   * @return URL without port number
   * @since 0.8
   */
  protected String stripPortNumber(URI url) {
    String protocol = url.getScheme();
    if ("ipp".equals(protocol)) {
      protocol = "http";
    } else if ("ipps".equals(protocol)) {
      protocol = "https";
    }
    return protocol + "://" + url.getHost() + url.getPath();
  }

  protected String getAttributeValue(Attribute attr) {
    return attr.getAttributeValue().get(0).getValue();
  }

  protected String getScheme() {
    return ippPort == 443 ? "https" : "http";
  }

}

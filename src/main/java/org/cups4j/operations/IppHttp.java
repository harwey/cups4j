package org.cups4j.operations;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;

public final class IppHttp {

	private static final int MAX_CONNECTION_BUFFER = 20;

	private static final int CUPSTIMEOUT = Integer.parseInt(System.getProperty("cups4j.timeout", "10000"));

	private static final RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(CUPSTIMEOUT).setConnectTimeout(CUPSTIMEOUT)
			.build();

	private static final CloseableHttpClient client = HttpClientBuilder.create()
			.disableCookieManagement()
			.disableRedirectHandling()
			.evictExpiredConnections()
			.setMaxConnPerRoute(MAX_CONNECTION_BUFFER)
			.setMaxConnTotal(MAX_CONNECTION_BUFFER)
			.setRetryHandler(new DefaultHttpRequestRetryHandler())
			.build();

	private IppHttp() {
	}

	public static CloseableHttpClient createHttpClient() {
		return client;
	}

	public static void setHttpHeaders(HttpPost httpPost, CupsPrinter targetPrinter,
			CupsAuthentication creds) {
		 if (targetPrinter == null) {
			 httpPost.addHeader("target-group", "local");
		 } else {
		 	 httpPost.addHeader("target-group", targetPrinter.getName());
		 }
	   httpPost.setConfig(requestConfig);

	   if (creds != null && StringUtils.isNotBlank(creds.getUserid())
	    		&& StringUtils.isNotBlank(creds.getPassword())) {
		    String auth = creds.getUserid() + ":" + creds.getPassword();
		    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		    String authHeader = "Basic " + new String(encodedAuth);
		    httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
	   }
	}

}

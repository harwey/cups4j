package org.cups4j.operations;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.util.Timeout;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;

public final class IppHttp {

	private static final int MAX_CONNECTION_BUFFER = 20;

	private static final int CUPSTIMEOUT =
			Integer.parseInt(System.getProperty("cups4j.timeout", "10000"));

	private static final CloseableHttpClient client = HttpClients.custom()
			.disableCookieManagement()
			.disableRedirectHandling()
			.evictExpiredConnections()
			.setRetryStrategy(new DefaultHttpRequestRetryStrategy())
			.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
					.setMaxConnTotal(MAX_CONNECTION_BUFFER)
					.setMaxConnPerRoute(MAX_CONNECTION_BUFFER)
					.setDefaultConnectionConfig(ConnectionConfig.custom()
							.setConnectTimeout(Timeout.ofMilliseconds(CUPSTIMEOUT))
							.build())
					.build())
			.build();

	private IppHttp() {}

	public static CloseableHttpClient createHttpClient() {
		return client;
	}

	public static void setHttpHeaders(
			HttpPost httpPost,
			CupsPrinter targetPrinter,
			CupsAuthentication creds) {
		httpPost.addHeader("target-group",
				(targetPrinter == null) ? "local" : targetPrinter.getName());

		if (creds != null && StringUtils.isNotBlank(creds.getUserid())
				&& StringUtils.isNotBlank(creds.getPassword())) {
			String auth = creds.getUserid() + ":" + creds.getPassword();
			byte[] encodedAuth =
					Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
			String authHeader = "Basic " + new String(encodedAuth);
			httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		}
	}

}

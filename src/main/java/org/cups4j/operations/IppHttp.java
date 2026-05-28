package org.cups4j.operations;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;
import org.cups4j.http.IppClient;
import org.cups4j.http.IppRequest;
import org.cups4j.http.JdkIppClient;
import org.cups4j.http.JdkIppRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class IppHttp {

	private static final int CUPSTIMEOUT =
			Integer.parseInt(System.getProperty("cups4j.timeout", "10000"));

	private static final IppClient client = new JdkIppClient(HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NEVER)
			.connectTimeout(Duration.ofMillis(CUPSTIMEOUT))
			.build());

	private IppHttp() {}

	public static IppClient createHttpClient() {
		return client;
	}

	public static IppRequest createRequest(URI uri) {
		return JdkIppRequest.post(uri);
	}

	public static void setHttpHeaders(
			IppRequest httpPost,
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
			httpPost.setHeader("Authorization", authHeader);
		}
	}

}

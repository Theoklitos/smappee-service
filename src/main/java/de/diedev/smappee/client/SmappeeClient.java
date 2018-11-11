package de.diedev.smappee.client;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.grants.ResourceOwnerPasswordGrant;
import org.dmfs.oauth2.client.grants.TokenRefreshGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import de.diedev.smappee.service.exception.FetchTokenError;

@SuppressWarnings("unused")
@Service
public class SmappeeClient {

	private final static Logger logger = LoggerFactory.getLogger(SmappeeClient.class);

	@Value("${smappee.security.oauth2.clientid}")
	private String clientId;

	@Value("${smappee.security.oauth2.secret}")
	private String secret;

	@Value("${smappee.security.username}")
	private String username;

	@Value("${smappee.security.password}")
	private String password;

	private OAuth2AccessToken token;
	private OAuth2Client oauth2client;
	private HttpRequestExecutor executor;
	private CloseableHttpClient httpClient;

	private Header createAuthHeader() {
		try {
			return new BasicHeader("Authorization", String.format("Bearer %s", token.accessToken()));
		} catch (final ProtocolException e) {
			throw new Error(e); // TODO use proper exception
		}
	}

	private JSONObject doGetRequest(final String url) {
		logger.info("Calling " + url);
		try {
			final HttpGet httpget = new HttpGet(url);
			httpget.setHeader(createAuthHeader());
			final JSONObject response = parseJsonFromResponse(httpClient.execute(httpget));
			return response;
		} catch (final IOException e) {
			throw new Error(e); // TODO use proper exception, httpclient.execute throws this
		}
	}

	private void fetchToken() {
		logger.info("Fetching token...");
		try {
			token = new ResourceOwnerPasswordGrant(oauth2client, new BasicScope(), username, password).accessToken(executor);
		} catch (IOException | ProtocolError | ProtocolException e) {
			throw new FetchTokenError(e);
		}
		logger.info("Got it.");
	}

	public JSONObject getConsumtion(final String serviceLocationId, final int aggregation, final Long from, final Long to) {
		return doGetRequest("https://app1pub.smappee.net/dev/v2/servicelocation/" + serviceLocationId + "/consumption?aggregation=" + aggregation + "&from="
				+ from + "&to=" + to);
	}

	public JSONObject getCostAnalysis(final String serviceLocationId, final int aggregation, final Long from, final Long to) {
		return doGetRequest("https://app1pub.smappee.net/dev/v2/servicelocation/" + serviceLocationId + "/costanalysis?aggregation=" + aggregation + "&from="
				+ from + "&to=" + to);
	}

	public JSONObject getEvents(final String serviceLocationId, final Integer applianceId, final Long from, final Long to, final Integer maxNumber) {
		if (applianceId == 0) { // show all applicances
			return doGetRequest("https://app1pub.smappee.net/dev/v1/servicelocation/" + serviceLocationId + "/events?from=" + from + "&to=" + to + "&maxNumber="
					+ maxNumber);
		} else {
			return doGetRequest("https://app1pub.smappee.net/dev/v1/servicelocation/" + serviceLocationId + "/events?applianceId=" + applianceId + "&from="
					+ from + "&to=" + to + "&maxNumber=" + maxNumber);
		}
	}

	public JSONObject getServiceLocationInfo(final String serviceLocationId) {
		return doGetRequest("https://app1pub.smappee.net/dev/v1/servicelocation/" + serviceLocationId + "/info");
	}

	public JSONObject getServiceLocations() {
		return doGetRequest("https://app1pub.smappee.net/dev/v1/servicelocation");
	}

	@PostConstruct
	public void init() {
		logger.info("Connecting to smappee with user '" + username + "'");
		httpClient = HttpClients.createDefault();
		final OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(clientId, secret);
		final OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(URI.create("https://app1pub.smappee.net/dev/v1/oauth2/token"),
				URI.create("https://app1pub.smappee.net/dev/v1/oauth2/token"), new Duration(1, 0, 3600));
		oauth2client = new BasicOAuth2Client(provider, credentials, new LazyUri(new Precoded("http://localhost")));
		executor = new HttpUrlConnectionExecutor();
		fetchToken();
	}

	private JSONObject parseJsonFromResponse(final CloseableHttpResponse response) {
		try {
			final String rawString = StreamUtils.copyToString(response.getEntity().getContent(), Charset.defaultCharset());
			JSONObject jsonResponse = new JSONObject();
			if (StringUtils.isEmpty(rawString)) {
				return jsonResponse;
			}

			try {
				jsonResponse = new JSONObject(rawString);
			} catch (final JSONException e) { // maybe its an array? improve the handling
				jsonResponse.put("results", new JSONArray(rawString));
			}
			response.close();

			return jsonResponse;
		} catch (UnsupportedOperationException | IOException e) {
			throw new Error(e); // TODO use proper exception
		}
	}

	private void refreshToken() {
		logger.info("Refreshing token...");
		try {
			token = new TokenRefreshGrant(oauth2client, token).accessToken(executor);
		} catch (IOException | ProtocolError | ProtocolException e) {
			throw new FetchTokenError(e);
		}
		logger.info("Got it.");
	}
}

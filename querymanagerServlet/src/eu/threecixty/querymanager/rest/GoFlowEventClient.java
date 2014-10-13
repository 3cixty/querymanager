/**
 *  Common client class for both publisher and subscriber stub in charge of setting up the connection to the RMQ server and for sending http request to the server for login and setup.
 *  
 *   Author : Pierre-Guillaume Raverdy, Copyright Ambientic 2014
 */
package eu.threecixty.querymanager.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GoFlowEventClient {

	protected static final String REGISTER_USER = "user/registerUser";
	protected static final String UNREGISTER_USER = "user/unregisterUser";
	protected static final String LOGIN = "user/login";
	protected static final String LOGOUT = "user/logout";

	protected static final String PUBLISHER_START = "event/publisherStart";
	protected static final String PUBLISHER_STOP = "event/publisherStop";

	protected static final String SUBSCRIBER_REGISTER = "event/subscriberRegister";
	protected static final String SUBSCRIBER_UNREGISTER = "event/subscriberUnregister";

	protected static final String REGISTER_APP = "event/registerApp";
	protected static final String UNREGISTER_APP = "event/unregisterApp";
	protected static final String MONITOR_APP_STOP = "event/monitorAppStop";
	protected static final String MONITOR_APP_START = "event/monitorAppStart";

//	protected String appId = "";
//	protected String user = "";
//	protected String pwd = "";

	protected static final String TOPIC_EXCHANGE_TYPE = "topic";

	protected Gson gson = new Gson();

	protected String goFlowServerUrl;

	private String sessionCookie = null;

	//
	//
	static TrustManager[] trustAllCerts = null;
	static HostnameVerifier nameVerifier = null;
	static SSLContext sslContext = null;

	/**
	 * 
	 * @param sourceId
	 * @param rmqServerUrl
	 * @param goFlowServerUrl
	 * @throws IOException
	 */
	public GoFlowEventClient(String goFlowServerUrl) throws IOException {
		this.goFlowServerUrl = goFlowServerUrl;

		if (trustAllCerts == null) {

			// Create a trust manager that does not validate certificate chains
			trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} };

			// Ignore differences between given hostname and certificate hostname
			if (nameVerifier == null) {
				nameVerifier = new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
			}

			// Install the all-trusting trust manager
			try {
				sslContext = SSLContext.getInstance("SSL"); // or TLS
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(nameVerifier);
			} catch (Exception e) {
				System.out.println("Unable to set HTTPS configuration");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param nickname
	 * @param password
	 * @param appId
	 * @throws IOException
	 */
	public void loginUser(String nickname, String password, String appId) throws IOException {
		String res;
		
		//
		if (appId == null || appId.equals("") || !validId(appId)) {
			throw new IOException("Invalid parameter");
		}
		if (nickname == null || nickname.equals("")) {
			throw new IOException("Invalid parameter");
		}
		if (password == null || password.equals("")) {
			throw new IOException("Invalid parameter");
		}


		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("nickname", nickname);
		jsonParam.addProperty("password", password);
		jsonParam.addProperty("appId", appId);
		res = sendJsonPostRequest(LOGIN, jsonParam.toString());

		// XXX PGR
		if (res == null) {
			throw new IOException("Unable to login");
		}
		return;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void logoutUser() throws IOException {
		String requestStr = "";
		String res = sendGetRequest(LOGOUT, requestStr);
		return;
	}


	//
	//
	//
	//
	//

	/**
	 * Check that only alphanumeric characters are user, also _ supported
	 * 
	 * @param appId2
	 * @return
	 */
	protected boolean validId(String id) {
		String pattern = "^[a-z_A-Z0-9:\\-]*$";
		if (id.matches(pattern)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param method
	 * @param request
	 * @return
	 */
	protected String sendGetRequest(String method, String request) {
		String requestUrl = "";
		// get has both method name and parameters
		if (goFlowServerUrl.endsWith("/")) {
			requestUrl = goFlowServerUrl + method + request;
		} else {
			requestUrl = goFlowServerUrl + "/" + method + request;
		}

		BufferedReader rd;
		String line;
		String result = "";

		try {
			URL url = new URL(requestUrl);

			// convert as http, although it can be https (not using any http specific
			// method) below
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// must be disabled for android
			// connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");

			if (sessionCookie != null && !sessionCookie.equals("")) {
				// connection.setRequestProperty("Set-Cookie", "" +
				// sessionCookie);
				connection.setRequestProperty("Cookie", "" + sessionCookie);
			}
			connection.setUseCaches(false);
			connection.connect();

			int HttpResult = connection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {

				rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
			} else {
				return null;
			}
		} catch (Exception e) {
			// System.out.println("Network error when sending json post : " +
			// e.getMessage());
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @param method
	 * @param requestContent
	 * @return
	 */
	protected String sendJsonPostRequest(String method, String requestContent) {
		String requestUrl = "";
		// post only has method name
		if (goFlowServerUrl.endsWith("/")) {
			requestUrl = goFlowServerUrl + method;
		} else {
			requestUrl = goFlowServerUrl + "/" + method;
		}

		BufferedReader rd;
		String line;
		String result = "";

		try {
			URL url = new URL(requestUrl);

			// convert as http, although it can be https (not using any http specific
			// method) below
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(requestContent.getBytes().length));
			if (sessionCookie != null && !sessionCookie.equals("")) {
				// connection.setRequestProperty("Set-Cookie", "" +
				// sessionCookie);
				connection.setRequestProperty("Cookie", "" + sessionCookie);
			}
			connection.setUseCaches(false);
			connection.connect();

			// Create I/O stream
			DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
			// Send request
			outStream.writeBytes(requestContent);
			outStream.flush();
			outStream.close();

			int HttpResult = connection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				//
				String tmpCookie = getCookieFromHeaders(connection);
				if (tmpCookie != null && !tmpCookie.equals("")) {
					sessionCookie = tmpCookie;
				}
				// Get Response
				rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
			} else {
				// for amm error codes, return null (500, 405, ...)
				// XXX TODO decide if error should be reported to app thru
				// callback
				return null;
			}
			connection.disconnect();
		} catch (Exception e) {
			// e.printStackTrace();
			// System.out.println("Network error when sending json post : " +
			// e.getMessage());
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @param wsConnection
	 * @return
	 */
	private String getCookieFromHeaders(HttpURLConnection wsConnection) {
		// debug code - display all the returned headers
		String headerName;
		String headerValue = null;
		for (int i = 0;; i++) {
			headerName = wsConnection.getHeaderFieldKey(i);
			if (headerName != null && headerName.equals("Set-Cookie")) {
				// found the Set-Cookie header (code assumes only one cookie is
				// being set)
				headerValue = wsConnection.getHeaderField(i);
				break;
			}
			if (i > 0 && (headerName == null || headerName.equals(""))) {
				break;
			}
		}
		// return the header value (null for not found)
		return headerValue;
	}

}

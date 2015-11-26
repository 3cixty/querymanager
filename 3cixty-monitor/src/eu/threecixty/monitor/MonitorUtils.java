package eu.threecixty.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class MonitorUtils {

	private static final String UTF8 = "UTF-8";
	
	public static void check(String endpoint,
			Map <String, Object> headerParams, Map <String, Object> params,
			String httpMethod, StringBuilder response) {
		if ("GET".equalsIgnoreCase(httpMethod)) {
			checkForGet(endpoint, headerParams, params, response);
		} else {
			checkForPost(endpoint, headerParams, params, response);
		}
	}

	private static void checkForGet(String endpoint,
			Map <String, Object> headerParams, Map <String, Object> params, StringBuilder response) {
		try {
			StringBuilder sb = null;
			if (params != null && params.size() > 0) {
				sb = new StringBuilder();
				for (String paramKey: params.keySet()) {
					if (sb.length() > 0) sb.append("&");
					sb.append(paramKey).append("=").append(URLEncoder.encode(params.get(paramKey).toString(), UTF8));
				}
			}
			URL url = new URL(sb == null ? endpoint : endpoint + "?" + sb.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (headerParams != null && headerParams.size() > 0) {
				for (String key: headerParams.keySet()) {
				    conn.setRequestProperty(key, headerParams.get(key).toString());
				}
			}

			int statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				EmailUtils.send("Http status code " + statusCode, "The endpoint " + endpoint + " got HTTP status code " + statusCode + " while trying to connect");
				return;
			}
			InputStream input = conn.getInputStream();
			if (input != null && response != null) {
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					response.append(new String(b, 0, readBytes, UTF8));
				}
			}
			if (input != null) input.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			EmailUtils.send("Invalid endpoint", "The endpoint " + endpoint + " is invalid. Are you sure this is valid?");
		} catch (IOException e) {
			e.printStackTrace();
			EmailUtils.send("Exception", "Exception while connecting to " + endpoint + ".");
		}
	}
	
	private static void checkForPost(String endpoint,
			Map <String, Object> headerParams, Map <String, Object> params, StringBuilder response) {
		try {
			StringBuilder sb = null;
			if (params != null && params.size() > 0) {
				sb = new StringBuilder();
				for (String paramKey: params.keySet()) {
					if (sb.length() > 0) sb.append("&");
					sb.append(paramKey).append("=").append(params.get(paramKey).toString());
				}
			}
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (headerParams != null && headerParams.size() > 0) {
				for (String key: headerParams.keySet()) {
				    conn.setRequestProperty(key, headerParams.get(key).toString());
				}
			}
			if (params != null && params.size() > 0) {
				conn.setDoOutput(true);
				OutputStream output = conn.getOutputStream();
				output.write(sb.toString().getBytes(UTF8));
				output.close();
			}

			int statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				EmailUtils.send("Http status code " + statusCode, "The endpoint " + endpoint + " got HTTP status code " + statusCode + " while trying to connect");
				return;
			}
			InputStream input = conn.getInputStream();
			if (input != null && response != null) {
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					response.append(new String(b, 0, readBytes, UTF8));
				}
			}
			if (input != null) input.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			EmailUtils.send("Invalid endpoint", "The endpoint " + endpoint + " is invalid. Are you sure this is valid?");
		} catch (IOException e) {
			e.printStackTrace();
			EmailUtils.send("Exception", "Exception while connecting to " + endpoint + ".");
		}
	}
	
	private MonitorUtils() {
	}
}

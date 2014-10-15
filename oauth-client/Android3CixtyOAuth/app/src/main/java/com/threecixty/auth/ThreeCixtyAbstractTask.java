package com.threecixty.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;

import android.os.AsyncTask;

public abstract class ThreeCixtyAbstractTask extends AsyncTask<Void, Void, Void> {

	protected static final String SERVER = "https://api.3cixty.com";

	protected static final String OAUTH_ANDROID_SERVICE = SERVER + "/v2/getAccessToken";
	protected static final String REFRESH_OAUTH_SERVICE = SERVER + "/v2/token";

	protected URLConnection getUrlConnection(String urlStr) {
		URLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			if (urlStr.startsWith("https")) {
				trustAllHosts();
				HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
				conn = httpsConn;
			} else {
				conn = url.openConnection();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	protected TokenInfo parse(InputStream input) throws IOException, JSONException {
		if (input != null) {
			String content = getContent(input);
			input.close();

			return TokenInfo.parse(content);
		}
		return null;
	}
	
	protected String getContent(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int readBytes = 0;
		StringBuilder builder = new StringBuilder();
		while ((readBytes = input.read(bytes)) >= 0) {
			builder.append(new String(bytes, 0, readBytes));
		}
		return builder.toString();
	}

	protected String encode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	   private static void trustAllHosts()
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
	        {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers()
	            {
	                return new java.security.cert.X509Certificate[] {};
	            }

	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	            {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	            {
	            }
	        } };

	        // Install the all-trusting trust manager
	        try
	        {
	            SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
}

package com.threecixty.auth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;

public abstract class ThreeCixtyAbstractTask extends AsyncTask<Void, Void, Void> {

	protected static final String SERVER = "https://api.3cixty.com";

	protected static final String OAUTH_ANDROID_SERVICE = SERVER + "/v2/getAccessToken";
	protected static final String REFRESH_OAUTH_SERVICE = SERVER + "/v2/token";

    protected static final String GET_KEY_INFO_SERVICE_FROM_APPKEY = SERVER + "/v2/retrieveKeyInfo";
    protected static final String GET_KEY_INFO_SERVICE_FROM_ACCESS_TOKEN = SERVER + "/v2/retrieveKeyInfoFromAccessToken";

	protected static URLConnection getUrlConnection(String urlStr, Context context) {

        URLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			if (urlStr.startsWith("https")) {

                // Load CAs from an InputStream
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = new BufferedInputStream(
                        context.getResources().openRawResource(R.raw.threecixty_com));
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

				HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
                httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());

                conn = httpsConn;
			} else {
				conn = url.openConnection();
			}
		} catch (Exception e) {
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
		int readBytes;
		StringBuilder builder = new StringBuilder();
		while ((readBytes = input.read(bytes)) >= 0) {
			builder.append(new String(bytes, 0, readBytes));
		}
		return builder.toString();
	}

	protected String encode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
}

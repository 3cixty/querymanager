package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;
import org.junit.Assert;

public class HTTPCall {
	
	private static final String USER_AGENT = "Mozilla/5.0";
	
	//protected static final String KEY = "687eedc0-17a4-4835-bee6-43ac9394cd04"; // for dev server
	protected static final String KEY = "fe67f4e4-b5fa-40c8-838b-5f88360eec6b"; // for dev server, test db
	
	protected static final String SERVER = "https://dev.3cixty.com/v2-test-1/";
	//protected static final String SERVER = "http://localhost:8080/v2/";
	
	protected static final int THREADS_10 = 10;
	protected static final int THREADS_100 = 100;
	protected static final int THREADS_200 = 200;
//	protected static final int THREADS_800 = 800;
//	protected static final int THREADS_1000 = 1000;
//	protected static final int THREADS_5000 = 5000;
//	protected static final int THREADS_10000 = 10000;

	protected void sendPost(String url, String params) throws Exception {
		sendPost(url, params, null);
	}
	
	protected void sendPost(String url, String params, StringBuffer response) throws Exception {
		 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		
		Assert.assertEquals(responseCode, 200);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
	}
	
	protected void sendGET(String url, String params) throws Exception {
		sendGET(url, params, null);
	}

	protected void sendGET(String url, String params, StringBuffer response) throws Exception {
		 
		URL obj = new URL(url + "/" + params);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		int responseCode = con.getResponseCode();
		
		Assert.assertEquals(responseCode, 200);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
	}
	
   protected String getAccessToken(String googleAccessToken) throws Exception {
		
		HttpURLConnection conn = createConnection(SERVER + "getAccessToken", "GET",
				new String[] {"google_access_token", "key", "scope"},
				new String[] {googleAccessToken, KEY, "Profile,Wishlist"});
		
		conn.setDoOutput(false);
		
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if (responseCode != 200) Assert.fail();
		
		String content = getContent(conn);
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (jsonObj.has("access_token")) {
			return jsonObj.getString("access_token");
		}
		return null;
	}
	
	protected String getContent(HttpURLConnection conn) throws IOException {
		return getContent(conn.getInputStream());
	}
	
	protected String getContent(InputStream input) throws IOException {
		StringBuffer response = new StringBuffer();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(input));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
		input.close();
		return response.toString();
	}
	
	protected HttpURLConnection createConnection(String urlStr, String httpMethod,
			String [] paramNames, String[] paramValues) throws Exception {
		HttpURLConnection conn = null;
		if (urlStr.startsWith("https")) {
			conn = createConnection(new URL(urlStr));
		} else {
			conn =  (HttpURLConnection)(new URL(urlStr)).openConnection();
		}
		conn.setRequestMethod(httpMethod.equalsIgnoreCase("POST") ? "POST" : "GET");
		if (paramNames != null) {
		    for (int i = 0; i < paramNames.length; i++) {
		    	conn.setRequestProperty(paramNames[i], paramValues[i]);
		    }
		}
		return conn;
	}
	
	protected HttpsURLConnection createConnection(URL url) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        
        return conn;
	}
	
	protected static void initSSL() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = PerformanceTests.class.getResourceAsStream("/3cixty.com.crt");
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
        SSLContext.setDefault(sslContext);
	}
	
	
}

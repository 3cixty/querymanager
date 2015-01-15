package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class PerformanceTests {

	private static final String GOOGLE_ACCESS_TOKEN = ""; // your Google access token
	
	private static final String KEY = "687eedc0-17a4-4835-bee6-43ac9394cd04"; // for dev server
	
	private static final String SERVER = "https://dev.3cixty.com/v2/";
	
	@BeforeClass
	public static void setup() {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
	}
	
	
	@Test
	public void testMeasureGet3cixtyAccessToken() throws Exception {
		
		String accessToken = getAccessToken();
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		boolean ok = revokeAccessToken(accessToken);
		if (!ok) Assert.fail();
		
		long startTime = System.currentTimeMillis();
		
		accessToken = getAccessToken();
		
		long endTime = System.currentTimeMillis();
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		System.out.println("Time to get a new 3cixty access token: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureRevoke3cixtyAccessToken() throws Exception {
		
		String accessToken = getAccessToken();
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		long startTime = System.currentTimeMillis();
		boolean ok = revokeAccessToken(accessToken);
		if (!ok) Assert.fail();
		
		long endTime = System.currentTimeMillis();
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		System.out.println("Time to revoke a 3cixty access token: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureSPOQueryWithoutAugmentation() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT * WHERE { ?s ?p ?o . }";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to get all data: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureSPOQueryForPoIsWithoutAugmentation() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT ?venue ?title  \n WHERE { ?venue rdf:type dul:Place .\n ?venue schema:name ?title . }";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to get all PoIs: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasure20ResultsOfSPOQueryForPoIsWithoutAugmentation() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT ?venue ?title  \n WHERE { ?venue rdf:type dul:Place .\n ?venue schema:name ?title . } LIMIT 20 ";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to 20 PoIs: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureSPOQueryForPoIsWithAugmentationBasedOnMyExprience() throws Exception {
		String accessToken = getAccessToken();
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		long startTime = System.currentTimeMillis();
		String query = "SELECT ?venue ?title  \n WHERE { ?venue rdf:type dul:Place .\n ?venue schema:name ?title . }";
		String strUrl = SERVER + "augmentAndExecute?format=json&query=" + URLEncoder.encode(query, "UTF-8") + "&filter=enteredRating";
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"access_token"}, new String[] {accessToken});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to make a SPO query for PoIs with augmentation based on my own experience: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureSPOQueryForPoIsWithAugmentationBasedOnMyFriends() throws Exception {
		String accessToken = getAccessToken();
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		long startTime = System.currentTimeMillis();
		String query = "SELECT ?venue ?title  \n WHERE { ?venue rdf:type dul:Place .\n ?venue schema:name ?title . }";
		String strUrl = SERVER + "augmentAndExecute?format=json&query=" + URLEncoder.encode(query, "UTF-8") + "&filter=friends";
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"access_token"}, new String[] {accessToken});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to make a SPO query for PoIs with augmentation based on my friends' experience: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureSPOQueryForAllEvents() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT * WHERE { ?event a lode:Event. OPTIONAL{?event dc:title ?title.} OPTIONAL{?event dc:description ?description.} }";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to get all events: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureGetOneEvent() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT * WHERE { ?event a lode:Event. OPTIONAL{?event dc:title ?title.} OPTIONAL{?event dc:description ?description.}  FILTER (?event = <http://data.linkedevents.org/event/d1c2a576-dbb5-4b54-8856-1c18d5c8051a>) }";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to get an event: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureGetOnePoI() throws Exception {
		long startTime = System.currentTimeMillis();
		String query = "SELECT ?venue ?title  \n WHERE { ?venue rdf:type dul:Place .\n ?venue schema:name ?title . FILTER(?venue = <http://data.linkedevents.org/location/05ce54a1-c356-413a-a0ac-6e18b9f21166>)  }  ";
		String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"key"}, new String[] {KEY});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		long endTime = System.currentTimeMillis();
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (!jsonObj.has("results")) Assert.fail();
		
		System.out.println("Time to get one PoI: " + (endTime - startTime) + " ms");
	}

	
	private boolean revokeAccessToken(String accessToken) throws Exception {
		
		HttpURLConnection conn = createConnection(SERVER + "revoke", "POST",
				new String[]{"access_token"}, new String[] {accessToken});
		 
		
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		JSONObject jsonObj = new JSONObject(content);

		String responseKey = "response";
		if (!jsonObj.has(responseKey)) return false;
		return jsonObj.getString(responseKey).equals("successful");
	}
	

	private String getAccessToken() throws Exception {
		
		HttpURLConnection conn = createConnection(SERVER + "getAccessToken", "GET",
				new String[] {"google_access_token", "key", "scope"},
				new String[] {GOOGLE_ACCESS_TOKEN, KEY, "Profile,Wishlist"});
		
		conn.setDoOutput(false);
		
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();
		
		String content = getContent(conn);
		
		JSONObject jsonObj = new JSONObject(content);
		
		if (jsonObj.has("access_token")) {
			return jsonObj.getString("access_token");
		}
		return null;
	}
	
	private String getContent(HttpURLConnection conn) throws IOException {
		return getContent(conn.getInputStream());
	}
	
	private String getContent(InputStream input) throws IOException {
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
	
	private HttpURLConnection createConnection(String urlStr, String httpMethod,
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
	
	private HttpsURLConnection createConnection(URL url) throws Exception {
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
        
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        return conn;
	}
}

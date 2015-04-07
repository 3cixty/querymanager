package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;



public class SparqlEndPointUtils {
	
	private static final String LOCN_PREFIX = "PREFIX locn: <http://www.w3.org/ns/locn#> ";
	private static final String CE_MILANO_PREFIX = "PREFIX ce: <http://data.linkedevents.org/cell/milano/> ";
	private static final String SPARQL_ENDPOINT_URL = ProfileManagerImpl.SPARQL_ENDPOINT_URL;
	
	//private static final String SPARQL_ENDPOINT_URL = Configuration.getVirtuosoServer() + "/sparql";

	public static void executeQueryViaSPARQL(String query, String format, StringBuilder result) throws IOException {
		String urlStr = SPARQL_ENDPOINT_URL + URLEncoder.encode(LOCN_PREFIX + query, "UTF-8")
				+ URLEncoder.encode(CE_MILANO_PREFIX + query, "UTF-8");
		urlStr += "&format=" + URLEncoder.encode(format, "UTF-8");

		URL url = new URL(urlStr);

		InputStream input = url.openStream();
		byte [] b = new byte[1024];
		int readBytes = 0;
		while ((readBytes = input.read(b)) >= 0) {
			result.append(new String(b, 0, readBytes, "UTF-8"));
		}
		input.close();
	}
	
	
	/*
	public static void executeQueryViaSPARQL(String query, String format, StringBuilder result) throws IOException {
		// http://3cixty.eurecom.fr/sparql
		//URL url = new URL(SPARQL_ENDPOINT_URL);
		HttpURLConnection.setFollowRedirects(true);
		URL url = new URL("http://3cixty.eurecom.fr/sparql");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
		
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setAllowUserInteraction(true);
		StringBuilder queryBuilder = new StringBuilder("query=").append(LOCN_PREFIX + query);
		queryBuilder.append("&format=").append(format);
		OutputStream output = conn.getOutputStream();
		output.write(queryBuilder.toString().getBytes("UTF-8"));
		System.out.println(queryBuilder.toString());
		output.close();
		InputStream input = conn.getInputStream();
		byte [] b = new byte[1024];
		int readBytes = 0;
		while ((readBytes = input.read(b)) >= 0) {
			result.append(new String(b, 0, readBytes, "UTF-8"));
		}
		input.close();
	}
	*/
	
	public static String cleanResultReceivedFromVirtuoso(String result) {
		return result.replace("\\U", "\\u");
	}
	
	private SparqlEndPointUtils() {
	}
}

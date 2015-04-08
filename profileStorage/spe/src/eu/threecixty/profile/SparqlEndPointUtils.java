package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import eu.threecixty.Configuration;




public class SparqlEndPointUtils {
	
	
	private static final String LOCN_PREFIX = "PREFIX locn: <http://www.w3.org/ns/locn#> ";
	private static final String CE_MILANO_PREFIX = "PREFIX ce: <http://data.linkedevents.org/cell/milano/> ";
	
	private static final String SPARQL_ENDPOINT_URL = Configuration.getVirtuosoServer() + "/sparql";
	private static final String UTF8 = "UTF-8";
	
	public static void executeQueryViaSPARQL(String query, String format,
			StringBuilder result) throws IOException {
		HttpURLConnection.setFollowRedirects(true);
		URL url = new URL(SPARQL_ENDPOINT_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setAllowUserInteraction(true);
	
		StringBuilder queryBuilder = new StringBuilder("query=").append(
				URLEncoder.encode(CE_MILANO_PREFIX + LOCN_PREFIX + query, UTF8));
		queryBuilder.append("&format=").append(URLEncoder.encode(format, UTF8));
		
		OutputStream output = conn.getOutputStream();
		output.write(queryBuilder.toString().getBytes(UTF8));

		output.close();
		InputStream input = conn.getInputStream();
		byte [] b = new byte[1024];
		int readBytes = 0;
		while ((readBytes = input.read(b)) >= 0) {
			result.append(new String(b, 0, readBytes, UTF8));
		}
		input.close();
	}
		
	public static String cleanResultReceivedFromVirtuoso(String result) {
		return result.replace("\\U", "\\u");
	}
	
	private SparqlEndPointUtils() {
	}
}

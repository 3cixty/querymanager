package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;


public class SparqlEndPointUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 SparqlEndPointUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final String SPARQL_ENDPOINT_URL = Configuration.getVirtuosoServer() + "/sparql";
	private static final String UTF8 = "UTF-8";
	
	private static final String SPARQL_ENDPOINT_URL_GET = ProfileManagerImpl.SPARQL_ENDPOINT_URL;
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	
	
	public static void executeQueryViaSPARQL(String query, String format,
			String httpMethod, StringBuilder result) throws IOException {
		long startTime = System.currentTimeMillis();
		if (HTTP_GET.equals(httpMethod)) executeQueryViaSPARQL_GET(query, format, result);
		else executeQueryViaSPARQL_POST(query, format, result);
		long endTime = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Query sent to KB: " + query);
		if (DEBUG_MOD) LOGGER.info("Time to make query from server to KB without processing: "
		        + (endTime - startTime) + " ms");
	}

	private static void executeQueryViaSPARQL_GET(String query, String format, StringBuilder result) throws IOException {
		String urlStr = SPARQL_ENDPOINT_URL_GET + URLEncoder.encode(query, "UTF-8");
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
	
	private static void executeQueryViaSPARQL_POST(String query, String format,
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
				URLEncoder.encode(query, UTF8));
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

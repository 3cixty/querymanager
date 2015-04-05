package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class SparqlEndPointUtils {
	
	private static final String LOCN_PREFIX = "PREFIX locn: <http://www.w3.org/ns/locn#> ";
	private static final String SPARQL_ENDPOINT_URL = ProfileManagerImpl.SPARQL_ENDPOINT_URL;

	public static void executeQueryViaSPARQL(String query, String format, StringBuilder result) throws IOException {
		String urlStr = SPARQL_ENDPOINT_URL + URLEncoder.encode(LOCN_PREFIX + query, "UTF-8");
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
	
	public static String cleanResultReceivedFromVirtuoso(String result) {
		return result.replace("\\U", "\\u");
	}
	
	private SparqlEndPointUtils() {
	}
}

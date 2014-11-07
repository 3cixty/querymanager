package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;


import virtuoso.jena.driver.VirtGraph;


public class VirtuosoManager {

	private static final String SPARQL_ENDPOINT_URL = ProfileManagerImpl.SPARQL_ENDPOINT_URL;
	
	private static final Object _sync = new Object();
	
	private static final String RESULT_JSON_FORMAT = "application%2Fsparql-results%2Bjson";// application/sparql-results+json

	private static VirtuosoManager instance;

	public static VirtuosoManager getInstance() {
		if (instance == null) {
			synchronized (_sync) {
				if (instance == null) instance = new VirtuosoManager();
			}
		}
		return instance;
	}
	
	

	public VirtGraph getVirtGraph() {
		// TODO: fixme
		VirtGraph graph = new VirtGraph ("jdbc:virtuoso://localhost:1111", "dba", "dba");
		//VirtGraph graph = new VirtGraph ("jdbc:virtuoso://dev.3cixty.com:8890", "dba", "dba");
		return graph;
	}
	
	
	/**
	 * Executes a given query through the SPARQL end point.
	 * @param queryStr
	 * @return
	 */
	public JSONObject executeQuery(String queryStr) {
		try {
			String urlStr = SPARQL_ENDPOINT_URL + URLEncoder.encode(queryStr, "UTF-8")
					+ "&format=" + RESULT_JSON_FORMAT ;

			URL url = new URL(urlStr);

			StringBuilder sb = new StringBuilder();
			
			InputStream input = url.openStream();
			byte [] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				sb.append(new String(b, 0, readBytes));
			}
			input.close();
			return new JSONObject(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private VirtuosoManager() {
	}
}

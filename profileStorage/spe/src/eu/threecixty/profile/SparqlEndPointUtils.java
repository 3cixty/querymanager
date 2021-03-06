/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

/**
 * 
 * This class is used to send SPARQL queries to Virtuoso and receive results.
 *
 */
public class SparqlEndPointUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 SparqlEndPointUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final String UTF8 = "UTF-8";
	
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	
	/**
	 * This method sends the given query to KB and receives data, then store the data in the string builder.
	 *
	 * @param query
	 * @param format
	 * @param httpMethod
	 * 				The HTTP method. The value can be either <code>GET</code> or <code>POST</code>.
	 * @param endPointUrl
	 * 				The SPARQL endpoint.
	 * @param result
	 * 				The string builder to contain data received from KB by executing the query.
	 * @throws IOException
	 */
	public static void executeQueryViaSPARQL(String query, String format,
			String httpMethod, String endPointUrl, StringBuilder result) throws IOException {
		long startTime = System.currentTimeMillis();
//		String key = query + format + httpMethod + endPointUrl;
//		String content = CacheManager.getInstance().getCacheData(key);
//		if (content != null) {
//			if (DEBUG_MOD) LOGGER.info("Result of the query " + query + " was cached");
//			result.append(content);
//		} else {
			if (HTTP_GET.equals(httpMethod)) executeQueryViaSPARQL_GET(query, format, endPointUrl, result);
			else executeQueryViaSPARQL_POST(query, format, endPointUrl, result);
//			CacheManager.getInstance().putCacheData(key, result.toString());
			long endTime = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Query sent to KB: " + query);
			if (DEBUG_MOD) LOGGER.info("Time to make query from server to KB without processing: "
					+ (endTime - startTime) + " ms");
//		}
	}

	/**
	 * Sends the SPARQL queries to KB via HTTP GET method, and store result in the string builder.
	 * @param query
	 * @param format
	 * @param endPointUrl
	 * @param result
	 * @throws IOException
	 */
	private static void executeQueryViaSPARQL_GET(String query, String format, String endPointUrl, StringBuilder result) throws IOException {
		String urlStr = endPointUrl + "?debug=on&default-graph-uri=&query=" + URLEncoder.encode(query, "UTF-8");
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
	
	/**
	 * Sends the SPARQL queries to KB via HTTP POST method, and store result in the string builder.
	 * @param query
	 * @param format
	 * @param endPointUrl
	 * @param result
	 * @throws IOException
	 */
	private static void executeQueryViaSPARQL_POST(String query, String format, String endPointUrl,
			StringBuilder result) throws IOException {
		HttpURLConnection.setFollowRedirects(true);
		URL url = new URL(endPointUrl);
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

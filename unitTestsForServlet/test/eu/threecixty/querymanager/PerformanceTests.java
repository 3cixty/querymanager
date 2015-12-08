/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager;

import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class PerformanceTests extends HTTPCall {

	private static final String GOOGLE_ACCESS_TOKEN = ""; // your Google access token
	
	@BeforeClass
	public static void setup() {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
	}
	
	
	@Test
	public void testMeasureGet3cixtyAccessToken() throws Exception {
		
		String accessToken = getAccessToken(GOOGLE_ACCESS_TOKEN);
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		boolean ok = revokeAccessToken(accessToken);
		if (!ok) Assert.fail();
		
		long startTime = System.currentTimeMillis();
		
		accessToken = getAccessToken(GOOGLE_ACCESS_TOKEN);
		
		long endTime = System.currentTimeMillis();
		
		if (accessToken == null || accessToken.equals("")) Assert.fail();
		
		System.out.println("Time to get a new 3cixty access token: " + (endTime - startTime) + " ms");
	}
	
	@Test
	public void testMeasureRevoke3cixtyAccessToken() throws Exception {
		
		String accessToken = getAccessToken(GOOGLE_ACCESS_TOKEN);
		
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
		String accessToken = getAccessToken(GOOGLE_ACCESS_TOKEN);
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
		String accessToken = getAccessToken(GOOGLE_ACCESS_TOKEN);
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
}

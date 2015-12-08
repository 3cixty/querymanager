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

import java.net.URL;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;


public class TrayJsonRpcTests {


	private static final String END_POINT_JSON_RPC = "http://localhost:8080/qm/v1/json-nospring/";
	private static final String ADD_TRAY_ELEMENT = "add_tray_element";
	private static final String APP_KEY = "MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwOTA0MDYwNDk3NmBwbF9hcXh4YGdiIQlm";
	
	@Test
	public void testAdd() {
		long time = System.currentTimeMillis();
		try {
			createTray("element_id" + time, "event", "my title", "my url", "token " + time, "exploration");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testList() {
		try {
			long currentTime = System.currentTimeMillis();
			
			createTray("element_id" + currentTime + "001", "event", "my title 001", "my url 1", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "002", "event", "my title 002", "my url 2", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "003", "event", "my title 002", "my url 3", "token " + currentTime, "exploration");
			
			String response = getTrays("token " + currentTime);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("element_id").equals("element_id" + currentTime + "001"));
			Assert.assertTrue(arr.getJSONObject(1).getString("element_id").equals("element_id" + currentTime + "002"));
			Assert.assertTrue(arr.getJSONObject(2).getString("element_id").equals("element_id" + currentTime + "003"));
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdate() {
		try {
			long currentTime = System.currentTimeMillis();
			
			createTray("element_id" + currentTime + "001", "event", "my title 001", "my url 1", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "002", "event", "my title 002", "my url 2", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "003", "event", "my title 002", "my url 3", "token " + currentTime, "exploration");
			
			String response = getTrays("token " + currentTime);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("element_id").equals("element_id" + currentTime + "001"));
			Assert.assertTrue(arr.getJSONObject(1).getString("element_id").equals("element_id" + currentTime + "002"));
			Assert.assertTrue(arr.getJSONObject(2).getString("element_id").equals("element_id" + currentTime + "003"));
			
			// delete a tray
			JsonRpcHttpClient client = new JsonRpcHttpClient(
				    new URL(END_POINT_JSON_RPC));
			client.invoke("update_tray_element", new Object[] {APP_KEY, "element_id" + currentTime + "001", "event", "token " + currentTime, "exploration", true, false, "", -1}, String.class);
			
			response = getTrays("token " + currentTime);
			
			arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmpty() {
		try {
			long currentTime = System.currentTimeMillis();
			
			createTray("element_id" + currentTime + "001", "event", "my title 001", "my url 1", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "002", "event", "my title 002", "my url 2", "token " + currentTime, "exploration");
			
			createTray("element_id" + currentTime + "003", "event", "my title 002", "my url 3", "token " + currentTime, "exploration");
			
			String response = getTrays("token " + currentTime);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("element_id").equals("element_id" + currentTime + "001"));
			Assert.assertTrue(arr.getJSONObject(1).getString("element_id").equals("element_id" + currentTime + "002"));
			Assert.assertTrue(arr.getJSONObject(2).getString("element_id").equals("element_id" + currentTime + "003"));
			
			// empty trays
			JsonRpcHttpClient client = new JsonRpcHttpClient(
				    new URL(END_POINT_JSON_RPC));
			client.invoke("empty_tray", new Object[] {APP_KEY, "token " + currentTime}, void.class);

			response = getTrays("token " + currentTime);
			arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private String getTrays(String token) throws Throwable {
		JsonRpcHttpClient client = new JsonRpcHttpClient(
			    new URL(END_POINT_JSON_RPC));
		return client.invoke("get_tray_elements", new Object[] {APP_KEY, token}, String.class);
	}

	private void createTray(String element_id, String element_type, String element_title,
			String image_url, String token, String source) throws Throwable {
		JsonRpcHttpClient client = new JsonRpcHttpClient(
			    new URL(END_POINT_JSON_RPC));
		client.invoke(ADD_TRAY_ELEMENT, new Object[] {APP_KEY, element_id, element_type, element_title, image_url, token, source}, void.class);
	}
}

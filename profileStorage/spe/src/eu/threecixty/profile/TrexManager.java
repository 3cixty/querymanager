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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.threecixty.Configuration;

/**
 * 
 * The class is used to publish WishList actions to Trex server.
 *
 */
public class TrexManager {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 TrexManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	private static final TrexManager SINGLETON = new TrexManager();
	
	/**The attribute which creates a thread pool of two threads to publish WishList action to Trex server*/
	private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
	
	public static TrexManager getInstance() {
		return SINGLETON;
	}
	
	public void publish(String id, String title, String image) {
		if (isNullOrEmpty(id) || isNullOrEmpty(title) || isNullOrEmpty(image)) return;
		final JSONObject json = new JSONObject();
		json.put("evtType", 1111);
		json.put("timeStamp", 0);
		JSONObject attrJson = new JSONObject();
		attrJson.put("image", image);
		attrJson.put("item_id", id);
		attrJson.put("title", title);
		json.put("attr", attrJson);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				sendData(json);
			}
		};
		executorService.execute(runnable);
	}
	
	private void sendData(JSONObject json) {
		OutputStream output = null;
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(Configuration.getTrexServer());
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			output = conn.getOutputStream();
			if (output == null) return;
			output.write(json.toString().getBytes("UTF-8"));
			output.flush();
			int responseCode = conn.getResponseCode();
			if (DEBUG_MOD) LOGGER.info("response code = " + responseCode);
		} catch (IOException e) {
		} finally {
			try {
				if (output != null) output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if (conn != null) conn.disconnect();
		}
	}

	private boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) return true;
		return false;
	}
	
	private TrexManager() {
	}
}

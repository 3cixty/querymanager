/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.monitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class AccessTokenChecker {

	private String username;
	private String password;
	private String key;
	private String endPoint;
	private Timer timer;
	
	private long period;
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public void start() {
		if (timer == null) return;
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				check();
			}
		};
		timer.scheduleAtFixedRate(task, 1000 * 60, period);
	}
	
	public void loadProperties(String filename) {
		if (filename == null) return;
		InputStream input = null;
		try {
			input = new FileInputStream(filename);
			Properties props = new Properties();
			props.load(input);
			username = props.getProperty("USERNAME");
			password = props.getProperty("PASSWORD");
			key = props.getProperty("KEY");
			period = Long.parseLong(props.getProperty("PERIOD"));
			endPoint = props.getProperty("ENDPOINT");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void check() {
		if (key == null) return;
		
		Map <String, Object> headers = new HashMap<String, Object>();
		headers.put("key", key);
		headers.put("email", username);
		headers.put("password", password);
		headers.put("scopes", "Profile,WishList");
		
		StringBuilder response = new StringBuilder();
		
		MonitorUtils.check(endPoint, headers, null, "GET", response);
		if (response.length() == 0) return; // already notify errors
		try {
		    JSONObject json = new JSONObject(response.toString());
		    if (!json.has("access_token")) {
		    	EmailUtils.send("Incorrect results", "Results do not contain access_token key/value: " + response.toString());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			EmailUtils.send("Invalid results", "Results are not in JSON: " + response.toString());
		}
	}
}

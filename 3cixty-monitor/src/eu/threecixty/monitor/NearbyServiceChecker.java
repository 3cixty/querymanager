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

import org.json.JSONArray;


public class NearbyServiceChecker {

	private double lon;
	private double lat;
	private double distance;
	private int numberOfResultsExpected;
	private String key;
	private String language;
	private String endPoint;
	private String city;
	private int limit;
	
	private long period; // in millisecond
	private Timer timer;
	
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
	
	private void check() {
		if (key == null) return;
		Map <String, Object> params = new HashMap<String, Object>();
		params.put("lat", lat);
		params.put("lon", lon);
		params.put("distance", distance);
		params.put("limit", limit);
		params.put("city", city);
		
		Map <String, Object> headers = new HashMap<String, Object>();
		headers.put("key", key);
		headers.put("Accept-Language", language);
		
		StringBuilder response = new StringBuilder();
		
		MonitorUtils.check(endPoint, headers, params, "GET", response);
		if (response.length() == 0) return; // already notify errors
		try {
		    JSONArray arr = new JSONArray(response.toString());
		    if (arr.length() != numberOfResultsExpected) {
		    	EmailUtils.send("Incorrect results",
		    			"Number of results: " + arr.length()+ "is different with expected: "+ numberOfResultsExpected);
		    }
		} catch (Exception e) {
			e.printStackTrace();
			EmailUtils.send("Invalid results", "Results are not in JSON array: " + response.toString());
		}
	}
	
	public void loadProperties(String filename) {
		if (filename == null) return;
		InputStream input = null;
		try {
			input = new FileInputStream(filename);
			Properties props = new Properties();
			props.load(input);
			lon = Double.parseDouble(props.getProperty("LON"));
			lat = Double.parseDouble(props.getProperty("LAT"));
			distance = Double.parseDouble(props.getProperty("DISTANCE"));
			numberOfResultsExpected = Integer.parseInt(props.getProperty("EXPECTED"));
			limit = Integer.parseInt(props.getProperty("LIMIT"));
			key = props.getProperty("KEY");
			language = props.getProperty("LANGUAGE");
			endPoint = props.getProperty("ENDPOINT");
			city = props.getProperty("CITY");
			period = Long.parseLong(props.getProperty("PERIOD"));
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
}

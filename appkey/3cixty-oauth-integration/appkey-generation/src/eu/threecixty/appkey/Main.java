/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.appkey;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.json.JSONObject;

public class Main {
	
	private static final String GOOGLE_ACCESS_TOKEN_KEY = "google_access_token";
	private static final String APP_ID_KEY = "appid";
	private static final String DESCRIPTION_KEY = "description";
	private static final String CATEGORY_KEY = "category";
	private static final String REDIRECT_URI_KEY = "redirect_uri";
	private static final String APP_NAME_KEY = "appname";
	private static final String THUMB_NAIL_URL = "thumbNailUrl";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream input = Main.class.getResourceAsStream("/appkeygeneration.properties");
		if (input == null) {
			System.out.println("Please check the appkeygeneration.properties in the resources folder");
		} else {
			Properties props = new Properties();
			try {
				props.load(input);
				String googleAccessToken = props.getProperty(GOOGLE_ACCESS_TOKEN_KEY);
				String appid = props.getProperty(APP_ID_KEY);
				String appname = props.getProperty(APP_NAME_KEY);
				String desc = props.getProperty(DESCRIPTION_KEY);
				String cat = props.getProperty(CATEGORY_KEY);
				String redirect_uri = props.getProperty(REDIRECT_URI_KEY);
				String thumbNailUrl = props.getProperty(THUMB_NAIL_URL);
				
				// XXX:
				//    'redirect_uri' and 'thumbNailUrl' are optional, don't provide 
				//    these information if you don't have, for example:
				/*
				 * http://dev.3cixty.com:8080/v2/getAppKey?google_access_token=" + encode(googleAccessToken)
						+ "&appid=" + encode(appid)
						+ "&description=" + encode(desc)
						+ "&category=" + encode(cat)
				        + "&appname=" + encode(appname)
				 */
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("http://dev.3cixty.com:8080/v2/getAppKey?google_access_token=" + encode(googleAccessToken));
				append(buffer, "appid", encode(appid));
				append(buffer, "description", encode(desc));
				append(buffer, "category", encode(cat));
				append(buffer, "appname", encode(appname));
				append(buffer, "redirect_uri", encode(redirect_uri));
				append(buffer, "thumbNailUrl", encode(thumbNailUrl));
				
				URL url = new URL(buffer.toString());
				//
				String content = getContent(url);
				if (content == null) {
					System.out.println("Please check your server");
				} else {
					JSONObject json = new JSONObject(content);
					if (json.has("key")) {
						System.out.println("Your app secret is: " + json.getString("key"));
					} else {
						System.out.println("Failed to get app key: " + json);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void append(StringBuffer buffer, String paramKey, String paramVal) {
		if (paramVal == null || paramVal.equals("")) return;
		buffer.append("&").append(paramKey).append("=").append(paramVal);
	}

	private static String getContent(URL url) {
		try {
			InputStream input = url.openConnection().getInputStream();

			StringBuffer buffer = new StringBuffer();
			byte[] b = new byte[1024];
			int readBytes = 0;
			while (true) {
				readBytes = input.read(b);
				if (readBytes < 0) break;
				buffer.append(new String(b, 0, readBytes));
			}
			input.close();
			return (buffer.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String encode(String info) {
		if (info == null) return null;
		try {
			return URLEncoder.encode(info, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}

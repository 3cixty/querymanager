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
	
	private static final String APP_KEY = "key";
	private static final String DESCRIPTION_KEY = "description";
	private static final String CATEGORY_KEY = "category";
	private static final String SCOPE_KEY = "scopeName";
	private static final String REDIRECT_URI_KEY = "redirect_uri";
	private static final String APP_NAME_KEY = "appname";
	private static final String THUMB_NAIL_URL = "thumbNailUrl";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream input = Main.class.getResourceAsStream("/appkeyupdate.properties");
		if (input == null) {
			System.out.println("Please check the appkeyupdate.properties in the resources folder");
		} else {
			Properties props = new Properties();
			try {
				props.load(input);
				String key = props.getProperty(APP_KEY);
				String appname = props.getProperty(APP_NAME_KEY);
				String desc = props.getProperty(DESCRIPTION_KEY);
				String cat = props.getProperty(CATEGORY_KEY);
				String scopeName = props.getProperty(SCOPE_KEY);
				String redirect_uri = props.getProperty(REDIRECT_URI_KEY);
				String thumbNailUrl = props.getProperty(THUMB_NAIL_URL);
				
				// TODO: note that 'redirect_uri' and 'thumbNailUrl' are optional, don't provide 
				// these information if you don't have, for example:
				/*
				 * http://localhost:8080/v2/getAppKey?google_access_token=" + encode(googleAccessToken)
						+ "&appid=" + encode(appid)
						+ "&description=" + encode(desc)
						+ "&category=" + encode(cat)
				        + "&scopeName=" + encode(scopeName)
				        + "&appname=" + encode(appname)
				 */
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("http://localhost:8080/v2/updateAppKey?key=" + encode(key));
				append(buffer, "description", encode(desc));
				append(buffer, "category", encode(cat));
				append(buffer, "scopeName", encode(scopeName));
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
					if (json.has("response")) {
						if (json.getString("response").equals("successful")) {
						    System.out.println("Successful to update appkey's info");
						} else System.out.println("Failed to update app key: " + json);
					} else {
						System.out.println("Failed to update app key: " + json);
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

package eu.threecixty.appkey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

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
		InputStream input = Main.class.getResourceAsStream("/appkeyupdate.properties");
		if (input == null) {
			System.out.println("Please check the appkeyupdate.properties in the resources folder");
		} else {
			Properties props = new Properties();
			try {
				props.load(input);
				String googleToken = props.getProperty(GOOGLE_ACCESS_TOKEN_KEY);
				String appid = props.getProperty(APP_ID_KEY);
				String appname = props.getProperty(APP_NAME_KEY);
				String desc = props.getProperty(DESCRIPTION_KEY);
				String cat = props.getProperty(CATEGORY_KEY);
				String redirect_uri = props.getProperty(REDIRECT_URI_KEY);
				String thumbNailUrl = props.getProperty(THUMB_NAIL_URL);
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("google_access_token=" + encode(googleToken));
				append(buffer, "appid", encode(appid));
				append(buffer, "description", encode(desc));
				append(buffer, "category", encode(cat));
				append(buffer, "appname", encode(appname));
				append(buffer, "redirect_uri", encode(redirect_uri));
				append(buffer, "thumbNailUrl", encode(thumbNailUrl));
				
				URL url = new URL("https://api.3cixty.com/v2/updateAppKey");
				//
				String content = getContent(url, buffer.toString());
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

	private static String getContent(URL url, String formParams) {
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			
			OutputStream output = conn.getOutputStream();
			output.write(formParams.getBytes());
			output.close();
			
			InputStream input = conn.getInputStream();

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

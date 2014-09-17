package eu.threecixty.oauth.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceServerUtils {
	
	private static final Object _sync = new Object();

	private static Properties props;

	public static String getResourceServerKey() {
		return getPropVal("SERVER_KEY");
	}

	public static String getResourceServerSecret() {
		return getPropVal("SERVER_SECRET");
	}

	public static String getResourceServerThumbNailUrl() {
		return getPropVal("THUMB_NAIL_URL");
	}

	private static String getPropVal(String key) {
		if (props == null) load();
		if (props == null) return null;
		return props.getProperty(key);
	}

	private static void load() {
		if (props != null) return;
		synchronized (_sync) {
			if (props == null) {
				InputStream input = ResourceServerUtils.class.getResourceAsStream("/resourceserver_3cixty.properties");
				if (input == null) return;
				props = new Properties();
				try {
					props.load(input);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private ResourceServerUtils() {
	}
}

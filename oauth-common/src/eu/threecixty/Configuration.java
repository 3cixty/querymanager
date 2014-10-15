package eu.threecixty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static final String HTTP_SERVER_DEFAULT = "https://api.3cixty.com/";

	private static String path;
	private static Properties props;

	private static String version;


	public static void setPath(String path) {
		Configuration.path = path;
	}
	
	public static void setVersion(String version) {
		Configuration.version = version;
	}

	public static String getVersion() {
		return version;
	}

	public static String getHttpServer() {
		if (props == null) load();
		if (props == null) return HTTP_SERVER_DEFAULT;
		return props.getProperty("HTTP_SERVER", HTTP_SERVER_DEFAULT);
	}

	public static String get3CixtyRoot() {
		return getHttpServer() + getVersion();
	}

	private static void load() {
		try {
			InputStream input = new FileInputStream(path + File.separatorChar
					+ "WEB-INF" + File.separatorChar + "3cixty.properties");
			if (input != null) {
				props = new Properties();
				props.load(input);
				input.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Configuration() {
	}
}

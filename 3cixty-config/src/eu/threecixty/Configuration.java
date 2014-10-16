package eu.threecixty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

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
		return getProperty("HTTP_SERVER");
	}
	
	public static String getVirtuosoServer() {
		return getProperty("VIRTUOSO_SERVER");
	}

	public static String get3CixtyRoot() {
		return getHttpServer() + getVersion();
	}
	
	public static String getGoogleClientId() {
		return getProperty("CLIENT_ID");
	}
	
	private static String getProperty(String key) {
		if (props == null) load();
		String purpose = props.getProperty("PURPOSE");
		if (purpose == null) return null;
		else if (purpose.equals("localhost")) return props.getProperty(key + "_LOCAL");
		else if (purpose.equals("prod")) return props.getProperty(key + "_PROD");
		else return props.getProperty(key + "_DEV");
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

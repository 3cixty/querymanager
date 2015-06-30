package eu.threecixty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	public static String path;
	private static Properties props;

	private static String version;
	
	private static String target; // DEV or PROD
	
	public static final String PROFILE_GRAPH = "http://3cixty.com";
	public static final String SCHEMA_URI = "http://schema.org/";
	//public static final String PROFILE_GRAPH = "http://3cixty.com/fakeprofile";
	public static final String PROFILE_PREFIX = "prefix profile:	<http://3cixty.com/ontology/profile/> ";
	public static final String PREFIXES = "prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+"prefix rdfs:	<http://www.w3.org/2000/01/rdf-schema#> "
			+"prefix foaf:	<http://xmlns.com/foaf/0.1/> "
			+"prefix schema:	<" + SCHEMA_URI + "> "
			+"prefix xsd:	<http://www.w3.org/2001/XMLSchema#> "
			+ PROFILE_PREFIX
			+"prefix frap:	<http://purl.org/frap#> "
			+"prefix dc:	<http://purl.org/dc/elements/1.1/> "
			+ "prefix fn: <http://www.w3.org/2005/xpath-functions#> "
			+ "PREFIX locn: <http://www.w3.org/ns/locn#>";
	
	public static final String PROFILE_URI = "http://data.linkedevents.org/person/";

	private static String http_virtuoso_server = null;

	public synchronized static void setPath(String path) {
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
		if (http_virtuoso_server == null) {
			http_virtuoso_server = getProperty("VIRTUOSO_SERVER");
		}
		if (http_virtuoso_server != null) return http_virtuoso_server;
		return getProperty("VIRTUOSO_SERVER");
	}

	public static synchronized void setVirtuosoServer(String virtuoso_server) {
		if (virtuoso_server != null && !"".equals(virtuoso_server)) {
			http_virtuoso_server = virtuoso_server;
		}
	}
	
	public static synchronized void resetVirtuosoServerByDefault() {
		http_virtuoso_server = getProperty("VIRTUOSO_SERVER");
	}

	public static String get3CixtyRoot() {
		return getHttpServer() + getVersion();
	}
	
	public static String getGoogleClientId() {
		return getProperty("CLIENT_ID");
	}
	
	public static String getVirtuosoJDBC() {
		return getProperty("VIRTUOSO_JDBC");
	}
	
	public static String getFacebookAppID() {
		return getProperty("FB_APP_ID");
	}
	
	public static boolean isForProdTarget() {
		if (target == null) {
			if (props == null) load();
			if (props == null) return false;
			target = props.getProperty("PURPOSE");
		}
		return "prod".equalsIgnoreCase(target);
	}
	
	private static String getProperty(String key) {
		if (props == null) load();
		if (props == null) return null;
		target = props.getProperty("PURPOSE");
		if (target == null) return null;
		else if (target.equals("localhost")) return props.getProperty(key + "_LOCAL");
		else if (target.equals("prod")) return props.getProperty(key + "_PROD");
		else return props.getProperty(key + "_DEV");
	}

	private synchronized static void load() {
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

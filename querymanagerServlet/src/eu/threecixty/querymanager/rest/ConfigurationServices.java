package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;



@Path("/" + Constants.VERSION_2)
public class ConfigurationServices {

	private static final String USER_KEY = "USERNAME";
	private static final String PASS_KEY = "PWD";
	
	 private static final Logger LOGGER = Logger.getLogger(
			 ConfigurationServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static String username = null;
	private static String password = null;
	
	@POST
	@Path("/setKB")
	public Response setKB(@FormParam("username") String username,
			@FormParam("password") String password, 
			@FormParam("virtuosoServer") String vituosoServer) {
		if (isNullOrEmpty(ConfigurationServices.username) || isNullOrEmpty(ConfigurationServices.password)) {
			try {
				loadProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isNullOrEmpty(ConfigurationServices.username) || isNullOrEmpty(ConfigurationServices.password)) {
			return Response.status(500).entity("Incorrect configuration for KB switch service").build();
		}
		if (!ConfigurationServices.username.equals(username) || !ConfigurationServices.password.equals(password)) {
			return Response.status(400).entity("Invalid username or password").build();
		}
		if (isNullOrEmpty(vituosoServer)) {
			return Response.status(400).entity("Virtuoso server is invalid").build();
		}
		Configuration.setVirtuosoServer(vituosoServer);
		return Response.status(200).entity("Successful to set " + vituosoServer + " to the backend!" ).build();
	}
	
	@POST
	@Path("/resetKB")
	public Response resetKB(@FormParam("username") String username,
			@FormParam("password") String password) {
		if (isNullOrEmpty(ConfigurationServices.username) || isNullOrEmpty(ConfigurationServices.password)) {
			try {
				loadProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isNullOrEmpty(ConfigurationServices.username) || isNullOrEmpty(ConfigurationServices.password)) {
			return Response.status(500).entity("Incorrect configuration for KB switch service").build();
		}
		if (!ConfigurationServices.username.equals(username) || !ConfigurationServices.password.equals(password)) {
			return Response.status(400).entity("Invalid username or password").build();
		}
		Configuration.resetVirtuosoServerByDefault();
		return Response.status(200).entity("Successful to change KB endpoint to default!" ).build();
	}
	
	@GET
	@Path("/kb")
	public Response getKBInfo() {
		String virtuosoServer = Configuration.getVirtuosoServer().toLowerCase();
		if (DEBUG_MOD) LOGGER.info("Virtuoso endpoint: " + virtuosoServer);
		boolean eurecomKB = virtuosoServer.contains("3cixty.eurecom.fr");
		boolean hostEuropeKB = virtuosoServer.contains("91.250.81.138");
		if (eurecomKB) Response.ok("Eurecom").build();
		if (hostEuropeKB) Response.ok("HostEurope").build();
		boolean apiProxy = virtuosoServer.contains("api.3cixty.com");
		if (apiProxy) return getKbInfoFromApiProxy();
		return getKbInfoFromDevProxy();
	}
	
	private Response getKbInfoFromDevProxy() {
		try {
			URL url = new URL("http://91.250.81.138:8890/sparql");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == 200) return Response.ok("HostEurope").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok("Eurecom").build();
	}

	private Response getKbInfoFromApiProxy() {
		try {
			URL url = new URL("http://3cixty.eurecom.fr/sparql");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == 200) return Response.ok("Eurecom").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok("HostEurope").build();
	}

	private synchronized void loadProperties() throws IOException {
		InputStream input = new FileInputStream(Configuration.path + File.separatorChar + "WEB-INF"
	            + File.separatorChar + "kbswitch.properties");
		Properties props = new Properties();
		props.load(input);
		input.close();
		username = props.getProperty(USER_KEY);
		password = props.getProperty(PASS_KEY);
	}
	
	private boolean isNullOrEmpty(String str) {
		return str == null || "".equals(str);
	}
}

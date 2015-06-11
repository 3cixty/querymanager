package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;



@Path("/" + Constants.VERSION_2)
public class ConfigurationServices {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 ConfigurationServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
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
}

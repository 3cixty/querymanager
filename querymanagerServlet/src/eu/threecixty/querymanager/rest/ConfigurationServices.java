package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import eu.threecixty.Configuration;



@Path("/" + Constants.VERSION_2)
public class ConfigurationServices {

	private static final String USER_KEY = "USERNAME";
	private static final String PASS_KEY = "PWD";
	
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
		return Response.status(200).entity("Successful to set " + vituosoServer + " to the backend" ).build();
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
		return Response.status(200).entity("Successful to change KB endpoint to default" ).build();
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

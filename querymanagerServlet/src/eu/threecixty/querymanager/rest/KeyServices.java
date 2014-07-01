package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;


import eu.threecixty.keys.KeyManager;

@Path("/key")
public class KeyServices {

	@GET
	@Path("/validate")
	@Produces("text/plain")
	public String getProfile(@QueryParam("key") String key) {
		if (KeyManager.getInstance().checkAppKey(key)) {
			return "ok";
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
}

package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import eu.threecixty.keys.KeyManager;

@Path("/key")
public class KeyServices {

	@GET
	@Path("/validate")
	@Produces("text/plain")
	public String validate(@QueryParam("key") String key) {
		if (KeyManager.getInstance().checkAppKey(key)) {
			return "ok";
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
}

package eu.threecixty.querymanager.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.threecixty.profile.VirtuosoManager;

@Path("/" + Constants.PREFIX_NAME)
public class VirtuosoServices {

	@GET
	@Path("/getAvailablePermits")
	public Response getAvailablePermits() {
		return Response.ok("{ \"availablePermits\": \"" + VirtuosoManager.getInstance().getAvailablePermits() + "\" } ", MediaType.APPLICATION_JSON_TYPE).build();
	}
}

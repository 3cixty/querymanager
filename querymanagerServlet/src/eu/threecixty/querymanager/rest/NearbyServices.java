package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.nearby.NearbyElement;
import eu.threecixty.profile.nearby.NearbyUtils;

@Path("/" + Constants.PREFIX_NAME)
public class NearbyServices {

	@GET
	@Path("/getNearbyLocations")
	public Response getNearbyLocations(@QueryParam("locationId") String locationId,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("key") String key) {
		
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		String tmpCat;
		if (category == null || category.equals("")) tmpCat = null;
		else tmpCat = category;
		try {
			List <NearbyElement> nearbyElements = NearbyUtils.getNearbyLocationElements(locationId, tmpCat, distance, offset, limit);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server is too busy at the moment. Please try it later").build();
		}
	}
}

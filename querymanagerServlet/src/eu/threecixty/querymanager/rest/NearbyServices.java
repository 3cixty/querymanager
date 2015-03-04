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
import eu.threecixty.profile.ElementDetails;
import eu.threecixty.profile.NearbyUtils;

@Path("/" + Constants.PREFIX_NAME)
public class NearbyServices {

	@GET
	@Path("/getNearbyPoIs")
	public Response getNearbyPoIs(@QueryParam("poi") String poiID,
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
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyPoIElements(poiID, tmpCat, distance, offset, limit);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server is too busy at the moment. Please try it later").build();
		}
	}
	
	@GET
	@Path("/getNearbyPoIsBasedOnGPS")
	public Response getNearbyPoIsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
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
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyPoIElements(lat, lon, tmpCat, distance, offset, limit);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server is too busy at the moment. Please try it later").build();
		}
	}
	
	@GET
	@Path("/getNearbyEvents")
	public Response getNearbyEvents(@QueryParam("id") String id,
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
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyEvents(id, tmpCat, distance, offset, limit);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server is too busy at the moment. Please try it later").build();
		}
	}
	
	@GET
	@Path("/getNearbyEventsBasedOnGPS")
	public Response getNearbyEventsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
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
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyEvents(lat, lon, tmpCat, distance, offset, limit, null);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server is too busy at the moment. Please try it later").build();
		}
	}
}

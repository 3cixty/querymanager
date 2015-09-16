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

import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.SocialWishListUtils;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.elements.ElementDetails;
import eu.threecixty.profile.elements.LanguageUtils;
import eu.threecixty.profile.elements.NearbyUtils;


@Path("/" + Constants.PREFIX_NAME)
public class NearbyServices {

	 private static final Logger LOGGER = Logger.getLogger(
			 NearbyServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	@GET
	@Path("/getNearbyPoIs")
	public Response getNearbyPoIs(@QueryParam("poi") String poiID,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("") @QueryParam("topCategories") String topCategories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("key") String key, @HeaderParam("Accept-Language") String languages) {
		
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		String [] tmpCats, tmpTopCats;
		if (categories == null || categories.equals("")) tmpCats = null;
		else tmpCats = categories.split(",");
		if (topCategories == null || topCategories.equals("")) tmpTopCats = null;
		else tmpTopCats = topCategories.split(",");
		try {
			long time1 = System.currentTimeMillis();
			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyPoIElements(
					SparqlChooser.getEndPointUrl(key), SparqlChooser.getPoIGraph(key),
					poiID, tmpCats, tmpTopCats,
					tmpLanguages, distance, offset, limit);
			long time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
			CallLoggingManager.getInstance().save(key, time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/getNearbyPoIsBasedOnGPS")
	public Response getNearbyPoIsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("") @QueryParam("topCategories") String topCategories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("key") String key, @HeaderParam("Accept-Language") String languages) {
		
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		String [] tmpCats, tmpTopCats;
		if (categories == null || categories.equals("")) tmpCats = null;
		else tmpCats = categories.split(",");
		if (topCategories == null || topCategories.equals("")) tmpTopCats = null;
		else tmpTopCats = topCategories.split(",");
		try {
			long time1 = System.currentTimeMillis();
			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyPoIElements(SparqlChooser.getEndPointUrl(key),
					SparqlChooser.getPoIGraph(key), lat, lon, tmpCats, tmpTopCats,
					tmpLanguages, distance > 10 ? 2 : distance, offset, limit, null);
			long time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
			CallLoggingManager.getInstance().save(key, time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/getNearbyEvents")
	public Response getNearbyEvents(@QueryParam("id") String id,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("key") String key, @HeaderParam("Accept-Language") String languages) {
		
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		String [] tmpCats;
		if (categories == null || categories.equals("")) tmpCats = null;
		else tmpCats = categories.split("");
		try {
			long time1 = System.currentTimeMillis();
			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyEvents(SparqlChooser.getEndPointUrl(key),
					SparqlChooser.getEventGraph(key), id, tmpCats,
					tmpLanguages, distance, offset, limit);
			long time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
			CallLoggingManager.getInstance().save(key, time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/getNearbyEventsBasedOnGPS")
	public Response getNearbyEventsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("key") String key, @HeaderParam("Accept-Language") String languages) {
		
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		String [] tmpCats;
		if (categories == null || categories.equals("")) tmpCats = null;
		else tmpCats = categories.split("");
		try {
			long time1 = System.currentTimeMillis();
			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyEvents(SparqlChooser.getEndPointUrl(key),
					SparqlChooser.getEventGraph(key), lat, lon, tmpCats,
					tmpLanguages, distance > 10 ? 2 : distance, offset, limit, null, null);
			long time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
			CallLoggingManager.getInstance().save(key, time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	
	@GET
	@Path("/getAugmentedNearbyPoIsBasedOnGPS")
	public Response getAugmentedNearbyPoIsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("") @QueryParam("topCategories") String topCategories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("access_token") String accessToken, @HeaderParam("Accept-Language") String languages) {
		AccessToken at = OAuthWrappers.findAccessTokenFromDB(accessToken);
		if (at != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
			String [] tmpCats, tmpTopCats;
			if (categories == null || categories.equals("")) tmpCats = null;
			else tmpCats = categories.split(",");
			if (topCategories == null || topCategories.equals("")) tmpTopCats = null;
			else tmpTopCats = topCategories.split(",");
			try {
				long time1 = System.currentTimeMillis();
				String [] tmpLanguages = LanguageUtils.getLanguages(languages);
				List<String> listPoIsFromFriendsWishList = null;
				try {
					listPoIsFromFriendsWishList = SocialWishListUtils.getPoIsFromFriendsWishList(at.getUid());
				} catch (TooManyConnections e) {
					e.printStackTrace();
				}
				List <ElementDetails> nearbyElements = NearbyUtils.getNearbyPoIElements(SparqlChooser.getEndPointUrl(at.getAppkey()),
						SparqlChooser.getPoIGraph(at.getAppkey()), lat, lon, tmpCats, tmpTopCats,
						tmpLanguages, distance > 10 ? 2 : distance, offset, limit, listPoIsFromFriendsWishList);
				long time2 = System.currentTimeMillis();
				if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
				CallLoggingManager.getInstance().save(at.getAppkey(), time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).entity("Invalid access token").build();
	}
	
	@GET
	@Path("/getAugmentedNearbyEventsBasedOnGPS")
	public Response getAugmentedNearbyEventsBasedOnGPS(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("categories") String categories,
			@DefaultValue("-1") @QueryParam("distance") double distance,
			@HeaderParam("access_token") String accessToken, @HeaderParam("Accept-Language") String languages) {
		AccessToken at = OAuthWrappers.findAccessTokenFromDB(accessToken);
		if (at != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
		String [] tmpCats;
		if (categories == null || categories.equals("")) tmpCats = null;
		else tmpCats = categories.split("");
		try {
			long time1 = System.currentTimeMillis();
			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			List<String> listEventsFromFriendsWishList = null;
			try {
				listEventsFromFriendsWishList = SocialWishListUtils.getEventsFromFriendsWishList(at.getUid());
			} catch (TooManyConnections e) {
				e.printStackTrace();
			}
			List <ElementDetails> nearbyElements = NearbyUtils.getNearbyEvents(SparqlChooser.getEndPointUrl(at.getAppkey()),
					SparqlChooser.getEventGraph(at.getAppkey()), lat, lon, tmpCats,
					tmpLanguages, distance > 10 ? 2 : distance, offset, limit, null, listEventsFromFriendsWishList);
			long time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to make nearby query: " + (time2 - time1) + " ms");
			CallLoggingManager.getInstance().save(at.getAppkey(), time1, CallLoggingConstants.NEARBY_SERVICES, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(JSONObject.wrap(nearbyElements).toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid access token").build();
		}
	}
}

package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.UserAccessToken;
import eu.threecixty.profile.ProfileInformation;
import eu.threecixty.profile.ProfileInformationStorage;

/**
 * The class is an end point for Rest ProfileAPI to expose to other components.
 * @author Cong-Kinh Nguyen
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class SPEServices {
	
	@Context 
	private HttpServletRequest httpRequest;
	
	/**
	 * Gets profile information in JSON format from a given 3cixt access token.
	 * @param accessToken
	 * @return a string in JSON format which represents the class ProfileInformation. Please check
	 *         the document at https://docs.google.com/document/d/1RPlZJaCWbb6G9Ilf-nTMavU_AAkzIj8fKSDwSNvpXtg/edit
	 *         for more information.
	 */
	@GET
	@Path("/getProfile")
	@Produces("application/json")
	public String getProfile(@HeaderParam("accessToken") String accessToken) {
		try {
			UserAccessToken userAccessToken = OAuthWrappers.retrieveUserAccessToken(accessToken);
			long starttime = System.currentTimeMillis();
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
				String uid = null;
				HttpSession session = httpRequest.getSession();
				uid = userAccessToken.getUser().getUid();
				session.setAttribute("uid", uid);
				String key = userAccessToken.getApp().getKey();
				ProfileInformation profile = ProfileInformationStorage.loadProfile(uid);
				if (profile == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.FAILED);
					throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity("There is no information of your profile in the KB")
					        .type(MediaType.TEXT_PLAIN)
					        .build());
				}
				Gson gson = new Gson();
				String ret = gson.toJson(profile);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
				return ret;
			} else {
				CallLoggingManager.getInstance().save(accessToken, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + accessToken);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("The access token is invalid '" + accessToken + "'")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity(e.getMessage())
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Saves profile information to the KB.
	 * @param accessToken
	 * @param profileStr
	 * @param key
	 * @return If successful, the message <code>{"save": "true"}</code> will be returned. Otherwise,
	 *         the message <code>{"save": "false"}</code> will be returned.
	 */
	@POST
	@Path("/saveProfile")
	@Produces("application/json")
	public String saveProfile(@HeaderParam("accessToken") String accessToken, @FormParam("profile") String profileStr) {
		long starttime = System.currentTimeMillis();
		UserAccessToken userAccessToken = OAuthWrappers.retrieveUserAccessToken(accessToken);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
			HttpSession session = httpRequest.getSession();
			String uid = userAccessToken.getUser().getUid();
			session.setAttribute("uid", uid);
			String key = userAccessToken.getApp().getKey();
			if (profileStr == null || profileStr.equals("")) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Invalid profile in JSON format: '" + profileStr + "'")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
			Gson gson = new Gson();
			try {
				ProfileInformation profile = gson.fromJson(profileStr, ProfileInformation.class);
				if (profile == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.FAILED);
					throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity("There is no information of your profile in the KB")
					        .type(MediaType.TEXT_PLAIN)
					        .build());
				}
				profile.setUid(uid);
				String ret =  "{\"save\":\"" + ProfileInformationStorage.saveProfile(profile) + "\"}";
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.SUCCESSFUL);
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
		} else {
			CallLoggingManager.getInstance().save(accessToken, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + accessToken);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + accessToken + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Gets Google UID from a Google access token and an App key.
	 * @param accessToken
	 * @param key
	 * @return If a given access token is valid, a message <code>{"uid": "103918130978226832690"}</code> for example will be returned. Otherwise,
	 *         the message <code>{"uid": ""}</code> will be returned.
	 */
	@POST
	@Path("/getUID")
	@Produces("application/json")
	public String getUID(@HeaderParam("accessToken") String accessToken) {
		long starttime = System.currentTimeMillis();
		UserAccessToken userAccessToken = OAuthWrappers.retrieveUserAccessToken(accessToken);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
			CallLoggingManager.getInstance().save(userAccessToken.getApp().getKey(), starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.SUCCESSFUL);
			return "{\"uid\":\"" + userAccessToken.getUser().getUid() + "\"}";
		} else {
			CallLoggingManager.getInstance().save(accessToken, starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + accessToken);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + accessToken + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}	
}

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
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
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
	 * @param access_token
	 * @return a string in JSON format which represents the class ProfileInformation. Please check
	 *         the document at https://docs.google.com/document/d/1RPlZJaCWbb6G9Ilf-nTMavU_AAkzIj8fKSDwSNvpXtg/edit
	 *         for more information.
	 */
	@GET
	@Path("/getProfile")
	@Produces("application/json")
	public String getProfile(@HeaderParam("access_token") String access_token) {
		try {
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			long starttime = System.currentTimeMillis();
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
				String uid = null;
				HttpSession session = httpRequest.getSession();
				uid = userAccessToken.getUid();
				session.setAttribute("uid", uid);
				String key = userAccessToken.getAppkey();
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
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("The access token is invalid '" + access_token + "'")
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
	 * @param access_token
	 * @param profileStr
	 * @param key
	 * @return If successful, the message <code>{"save": "true"}</code> will be returned. Otherwise,
	 *         the message <code>{"save": "false"}</code> will be returned.
	 */
	@POST
	@Path("/saveProfile")
	@Produces("application/json")
	public String saveProfile(@HeaderParam("access_token") String access_token, @FormParam("profile") String profileStr) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			HttpSession session = httpRequest.getSession();
			String uid = userAccessToken.getUid();
			session.setAttribute("uid", uid);
			String key = userAccessToken.getAppkey();
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
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Gets Google UID from a Google access token and an App key.
	 * @param access_token
	 * @param key
	 * @return If a given access token is valid, a message <code>{"uid": "103918130978226832690"}</code> for example will be returned. Otherwise,
	 *         the message <code>{"uid": ""}</code> will be returned.
	 */
	@POST
	@Path("/getUID")
	@Produces("application/json")
	public String getUID(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.SUCCESSFUL);
			return "{\"uid\":\"" + userAccessToken.getUid() + "\"}";
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}	
}

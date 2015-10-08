package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.Configuration;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserRelatedInformation;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.oldmodels.UserInteractionMode;

/**
 * This class is to store settings information into UserProfile.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class SettingsServices {

	
	private static final String ACCESS_TOKEN_PARAM = "accessToken";

	private static final String PROFILE_URI = Configuration.PROFILE_URI;
	
	@Context 
	private HttpServletRequest httpRequest;

	@GET
	@Path("/viewSettings")
	public void view(@HeaderParam("access_token") String access_token, @Context HttpServletResponse response,
            @Context HttpServletRequest request) {
		try {
			PrintWriter writer = response.getWriter();

			long starttime = System.currentTimeMillis();
			HttpSession session = httpRequest.getSession();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
				try {
					checkPermission(userAccessToken);
				} catch (ThreeCixtyPermissionException e1) {
					CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime,
							CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.FAILED);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					writer.write(e1.getMessage());
					writer.close();
					return;
				}
				String uid =  userAccessToken.getUid();
				String key = userAccessToken.getAppkey();
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE,
						CallLoggingConstants.SUCCESSFUL);

				ThreeCixtySettings settings = SettingsStorage.load(uid);
				session.setAttribute("settings", settings);
				session.setAttribute(ACCESS_TOKEN_PARAM, access_token);

				try {
					request.getRequestDispatcher(Constants.OFFSET_LINK_TO_SETTINGS_PAGE + "settings.jsp").forward(request, response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE,
						CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write("Your access token '" + access_token + "' is invalid.");
				writer.close();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (TooManyConnections e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@POST
	@Path("/saveSettings")
	public void save(@DefaultValue("")@FormParam("firstName") String firstName,
			@DefaultValue("")@FormParam("lastName") String lastName,
			@DefaultValue("")@FormParam("townName") String townName,
			@DefaultValue("")@FormParam("countryName") String countryName,
			@DefaultValue("")@FormParam("lat") String latStr,
			@DefaultValue("")@FormParam("lon") String lonStr,
			@DefaultValue("")@FormParam("pi_sources") List<String> sources,
			@DefaultValue("")@FormParam("pi_ids") List<String> pi_ids,
			@DefaultValue("")@FormParam("pi_ats") List<String> pi_ats,
			@FormParam("access_token") String access_token,
			@Context HttpServletResponse response,
            @Context HttpServletRequest request) {
		try {
			PrintWriter writer = response.getWriter();
			long starttime = System.currentTimeMillis();

			HttpSession session = httpRequest.getSession();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
				try {
					checkPermission(userAccessToken);
				} catch (ThreeCixtyPermissionException e1) {
					CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime,
							CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.FAILED);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					writer.write(e1.getMessage());
					writer.close();
					return;
				}
				String uid =  userAccessToken.getUid();
				String key = userAccessToken.getAppkey();
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_SAVE_SERVICE, CallLoggingConstants.SUCCESSFUL);
				ThreeCixtySettings settings = (ThreeCixtySettings) session.getAttribute("settings");
				if (settings == null) {
					settings = SettingsStorage.load(uid);
					settings.setUid(uid);
				}

				if (isNotNullOrEmpty(firstName)) settings.setFirstName(firstName);

				if (isNotNullOrEmpty(lastName)) settings.setLastName(lastName);

				if (isNotNullOrEmpty(townName)) settings.setTownName(townName);

				if (isNotNullOrEmpty(countryName)) settings.setCountryName(countryName);

				if (isNotNullOrEmpty(latStr)) settings.setCurrentLatitude(Double.parseDouble(latStr));

				if (isNotNullOrEmpty(lonStr)) settings.setCurrentLongitude(Double.parseDouble(lonStr));

				if (sources != null && sources.size() > 0 && pi_ids != null
						&& pi_ids.size() > 0 && pi_ats != null && pi_ats.size() > 0
						) {

					if (settings.getIdentities() != null) settings.getIdentities().clear();
					
					for (int i = 0; i < sources.size(); i++) {
						addProfileIdentities(sources.get(i), pi_ids.get(i), pi_ats.get(i), settings);
					}
				}

				SettingsStorage.save(settings);

				session.setAttribute("settings", settings);
				session.setAttribute(ACCESS_TOKEN_PARAM, access_token);

				session.setAttribute("successful", true);

				try {
					request.getRequestDispatcher(Constants.OFFSET_LINK_TO_SETTINGS_PAGE + "settings.jsp").forward(request, response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_SAVE_SERVICE,
						CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write("Your access token '" + access_token + "' is invalid.");
				writer.close();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (TooManyConnections e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	@GET
	@Path("/viewPrivacySettings")
	public void viewPrivacySettings(@QueryParam("access_token") String access_token, @Context HttpServletResponse response,
            @Context HttpServletRequest request) {
		try {
			long starttime = System.currentTimeMillis();
			HttpSession session = httpRequest.getSession();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

				String key = userAccessToken.getAppkey();
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PRIVACY_SETTINGS_VIEW_SERVICE,
						CallLoggingConstants.SUCCESSFUL);

				session.setAttribute(ACCESS_TOKEN_PARAM, access_token);

				try {
					request.getRequestDispatcher(Constants.OFFSET_LINK_TO_SETTINGS_PAGE + "privacySettings.jsp").forward(request, response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PRIVACY_SETTINGS_VIEW_SERVICE,
						CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
				PrintWriter writer = response.getWriter();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write("Your access token '" + access_token + "' is invalid.");
				writer.close();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	@POST
	@Path("/removeFriend")
	public Response removeFriendByUser(@FormParam("access_token") String access_token,
			@FormParam("friendUid") String friendUid,
			@Context HttpServletResponse response) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

			if (!isNotNullOrEmpty(friendUid)) return Response.status(400).entity("Empty friend UID").build();
			
			String key = userAccessToken.getAppkey();
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_REMOVE_FRIEND_BY_USER,
					CallLoggingConstants.SUCCESSFUL);
			
			boolean ok = ProfileManagerImpl.getInstance().getForgottenUserManager()
					.add(userAccessToken.getUid(), friendUid);
			
			if (ok) return Response.ok().build();
			return Response.status(400).entity("Failed to remove the friend " + friendUid + " from your profile").build();
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_REMOVE_FRIEND_BY_USER,
					CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return Response.status(400).entity("Your access token '" + access_token + "' is invalid.").build();
		}
	}
	
	@POST
	@Path("/removeFriends")
	public Response removeFriendsByUser(@FormParam("access_token") String access_token,
			@FormParam("friendUids") String friendUids,
			@Context HttpServletResponse response) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

			if (!isNotNullOrEmpty(friendUids)) return Response.status(400).entity("Empty friend UID").build();
			
			String key = userAccessToken.getAppkey();
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_REMOVE_FRIENDS_BY_USER,
					CallLoggingConstants.SUCCESSFUL);
			
			Set <String> setOfFriendUIDs = new HashSet<String>();
			String[] tmpFriends = friendUids.split(",");
			for (String tmpFriend: tmpFriends) {
				setOfFriendUIDs.add(tmpFriend.trim());
			}
			
			boolean ok = ProfileManagerImpl.getInstance().getForgottenUserManager()
					.add(userAccessToken.getUid(), setOfFriendUIDs);
			
			if (ok) return Response.ok().build();
			return Response.status(400).entity("Failed to remove friends " + friendUids + " from your profile").build();
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_REMOVE_FRIENDS_BY_USER,
					CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return Response.status(400).entity("Your access token '" + access_token + "' is invalid.").build();
		}
	}
	
	@GET
	@Path("/getAllUserRelatedInfoByUser")
	public Response getAllUserRelatedInfoByUser(@HeaderParam("access_token") String access_token,
			@DefaultValue("en") @QueryParam("language") String language,
			@Context HttpServletResponse response) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

			try {
				UserRelatedInformation uri = SPEServices.getUserRelatedInfo(
						userAccessToken.getUid(), language, userAccessToken.getAppkey());
				return Response.ok().entity(((JSONObject) JSONObject.wrap(uri)).toString(4)).build();
			} catch (TooManyConnections e) {
				e.printStackTrace();
				return Response.serverError().build();
			}
			
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_REMOVE_FRIEND_BY_USER,
					CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return Response.status(400).entity("Your access token '" + access_token + "' is invalid.").build();
		}
	}
	
	/**
	 * Adds profile identities composed by a given source, a given accountId, and a given access token
	 * to a given settings instance.
	 * @param source
	 * @param accountId
	 * @param accessToken
	 * @param settings
	 */
	private void addProfileIdentities(String source, String accountId,
			String accessToken, ThreeCixtySettings settings) {
		if (!isNotNullOrEmpty(source) || !isNotNullOrEmpty(accountId)) return;
		List <ProfileIdentities> profileIdentities = settings.getIdentities();
		if (profileIdentities == null) profileIdentities = new ArrayList <ProfileIdentities>();
		ProfileIdentities tmpProfile = new ProfileIdentities();
		tmpProfile.setHasUserInteractionMode(UserInteractionMode.Active);
		tmpProfile.setHasUserAccountID(accountId);
		tmpProfile.setHasSourceCarrier(source);
		tmpProfile.setHasProfileIdentitiesURI(PROFILE_URI+ settings.getUid() + "/Account/"+tmpProfile.getHasSourceCarrier());
		// TODO: update private data from accessToken ?
		profileIdentities.add(tmpProfile);
		settings.setIdentities(profileIdentities);
	}


	/**
	 * Checks whether or not a given string is not null or empty.
	 * @param str
	 * @return
	 */
	private boolean isNotNullOrEmpty(String str) {
		if (str == null || str.equals("")) return false;
		return true;
	}

	private void checkPermission(AccessToken accessToken) throws ThreeCixtyPermissionException {
		SPEServices.checkPermission(accessToken);
	}
}

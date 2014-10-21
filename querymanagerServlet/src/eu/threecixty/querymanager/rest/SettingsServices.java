package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class is to store settings information into UserProfile.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class SettingsServices {

	
	private static final String ACCESS_TOKEN_PARAM = "accessToken";

	private static final String PROFILE_SCOPE_NAME = "Profile";
	
	@Context 
	private HttpServletRequest httpRequest;

	@GET
	@Path("/viewSettings")
	@Produces("text/plain")
	public Response view(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		HttpSession session = httpRequest.getSession();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			try {
				checkPermission(access_token);
			} catch (ThreeCixtyPermissionException e1) {
				CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime,
						CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.FAILED);
				return Response.status(Response.Status.UNAUTHORIZED).entity(e1.getMessage()).build();
			}
			String uid =  userAccessToken.getUid();
			String key = userAccessToken.getAppkey();
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.SUCCESSFUL);
			session.setAttribute("uid", uid);

			ThreeCixtySettings settings = SettingsStorage.load(uid);
			session.setAttribute("settings", settings);
			session.setAttribute(ACCESS_TOKEN_PARAM, access_token);

			try {
				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_SETTINGS_PAGE + "settings.jsp")).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				    .entity("Your access token '" + access_token + "' is invalid.")
				    .type(MediaType.TEXT_PLAIN)
				    .build();
		}
		
		return null;
	}

	@POST
	@Path("/saveSettings")
	@Produces("text/plain")
	public Response save(@DefaultValue("")@FormParam("firstName") String firstName,
			@DefaultValue("")@FormParam("lastName") String lastName,
			@DefaultValue("")@FormParam("townName") String townName,
			@DefaultValue("")@FormParam("countryName") String countryName,
			@DefaultValue("")@FormParam("lat") String latStr,
			@DefaultValue("")@FormParam("lon") String lonStr,
			@DefaultValue("")@FormParam("pi_sources") List<String> sources,
			@DefaultValue("")@FormParam("pi_ids") List<String> pi_ids,
			@DefaultValue("")@FormParam("pi_ats") List<String> pi_ats) {
		long starttime = System.currentTimeMillis();
		
		HttpSession session = httpRequest.getSession();
		String accessToken = (String) session.getAttribute(ACCESS_TOKEN_PARAM);
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(accessToken);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
			try {
				checkPermission(accessToken);
			} catch (ThreeCixtyPermissionException e1) {
				CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime,
						CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.FAILED);
				return Response.status(Response.Status.UNAUTHORIZED).entity(e1.getMessage()).build();
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

				for (int i = 0; i < sources.size(); i++) {
					addProfileIdentities(sources.get(i), pi_ids.get(i), pi_ats.get(i), settings);
				}
			}

			SettingsStorage.save(settings);

			session.setAttribute("settings", settings);

			session.setAttribute("successful", true);

			try {
				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_SETTINGS_PAGE + "settings.jsp")).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			CallLoggingManager.getInstance().save(accessToken, starttime, CallLoggingConstants.SETTINGS_SAVE_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + accessToken);
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				    .entity("Your Access token '" + accessToken + "' is invalid")
				    .type(MediaType.TEXT_PLAIN)
				    .build();
		}
		
		return null;
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
		tmpProfile.setHasSource(source);
		tmpProfile.setHasUserAccountID(accountId);
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

	private void checkPermission(String token) throws ThreeCixtyPermissionException {
		AccessToken accessToken = OAuthWrappers.findAccessTokenFromDB(token);
		if (!accessToken.getScopeNames().contains(PROFILE_SCOPE_NAME)) {
		    throw new ThreeCixtyPermissionException("{\"error\": \"no permission\"}");
		}
	}
}

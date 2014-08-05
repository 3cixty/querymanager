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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import eu.threecixty.keys.KeyManager;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class is to store settings information into UserProfile.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/settings")
public class SettingsServices {

	
	private static final String ACCESS_TOKEN_PARAM = "accessToken";
	
	@Context 
	private HttpServletRequest httpRequest;

	@GET
	@Path("/view")
	@Produces("text/plain")
	public Response view(@QueryParam("accessToken") String accessToken, @QueryParam("key") String key) {
		long starttime = System.currentTimeMillis();
		String uid = null;
		HttpSession session = httpRequest.getSession();
		if (KeyManager.getInstance().checkAppKey(key)) {
			if (session.getAttribute(ACCESS_TOKEN_PARAM) != null) {
				uid = (String) session.getAttribute("uid");
			} else {
				uid = GoogleAccountUtils.getUID(accessToken);
				session.setMaxInactiveInterval(GoogleAccountUtils.getValidationTime(accessToken));
			}
			if (uid == null || uid.equals("")) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.UNAUTHORIZED);
				return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
						.entity("Your access token is incorrect or expired")
						.type(MediaType.TEXT_PLAIN)
						.build();
			} else {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.SUCCESSFUL);
				session.setAttribute("uid", uid);

				ThreeCixtySettings settings = SettingsStorage.load(uid);
				session.setAttribute("settings", settings);
				session.setAttribute("key", key);
				session.setAttribute(ACCESS_TOKEN_PARAM, accessToken);

				try {
					return Response.temporaryRedirect(new URI("../settings.jsp")).build();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				    .entity("Your AppKey '" + key + "' is invalid. Please get a new key")
				    .type(MediaType.TEXT_PLAIN)
				    .build();
		}
		
		return null;
	}

	@POST
	@Path("/save")
	@Produces("text/plain")
	public Response save(@DefaultValue("")@FormParam("accessToken") String accessToken,
			@DefaultValue("")@FormParam("key") String key,
			@DefaultValue("")@FormParam("firstName") String firstName,
			@DefaultValue("")@FormParam("lastName") String lastName,
			@DefaultValue("")@FormParam("townName") String townName,
			@DefaultValue("")@FormParam("countryName") String countryName,
			@DefaultValue("")@FormParam("lat") String latStr,
			@DefaultValue("")@FormParam("lon") String lonStr,
			@DefaultValue("")@FormParam("pi_sources") List<String> sources,
			@DefaultValue("")@FormParam("pi_ids") List<String> pi_ids,
			@DefaultValue("")@FormParam("pi_ats") List<String> pi_ats) {
		long starttime = System.currentTimeMillis();
		String uid = null;
		HttpSession session = httpRequest.getSession();
		if (key == null || key.equals("")) key = (String) session.getAttribute("key");
		if (KeyManager.getInstance().checkAppKey(key)) {
			if (session.getAttribute(ACCESS_TOKEN_PARAM) != null) {
				uid = (String) session.getAttribute("uid");
			} else {
				uid = GoogleAccountUtils.getUID(accessToken);
				session.setMaxInactiveInterval(GoogleAccountUtils.getValidationTime(accessToken));
			}
			if (uid == null || uid.equals("")) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_SAVE_SERVICE, CallLoggingConstants.UNAUTHORIZED);
				return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
						.entity("Your access token is incorrect or expired")
						.type(MediaType.TEXT_PLAIN)
						.build();
			} else {
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
					return Response.temporaryRedirect(new URI("../settings.jsp")).build();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_SAVE_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				    .entity("Your AppKey '" + key + "' is invalid. Please get a new key")
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
}

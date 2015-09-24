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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import eu.threecixty.Configuration;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.FaceBookAccountUtils;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.InvalidTrayElement;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.SPEConstants;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.UserProfile;
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
	public void view(@QueryParam("access_token") String access_token, @Context HttpServletResponse response,
            @Context HttpServletRequest request) {
		try {

			long starttime = System.currentTimeMillis();
			HttpSession session = httpRequest.getSession();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

				String uid =  userAccessToken.getUid();
				String key = userAccessToken.getAppkey();
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.SETTINGS_VIEW_SERVICE,
						CallLoggingConstants.SUCCESSFUL);

				session.setAttribute(ACCESS_TOKEN_PARAM, access_token);
				session.setAttribute("uid", uid);

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
				PrintWriter writer = response.getWriter();
				writer.write("Your access token '" + access_token + "' is invalid.");
				writer.close();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	@POST
	@Path("/linkAccounts")
	public Response linkAccounts(@FormParam("access_token") String accessToken,
			@DefaultValue("") @FormParam("googleAccessToken") String googleAccessToken,
			@DefaultValue("") @FormParam("fbAccessToken") String fbAccessToken) {
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(accessToken);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(accessToken)) {
			if (!isNotNullOrEmpty(googleAccessToken) || !isNotNullOrEmpty(fbAccessToken))
				return Response.status(400).entity("Both Google and Facebook access tokens are empty").build();
			String uidDerivedFromGoogle = GoogleAccountUtils.getUID(googleAccessToken);
			if (!isNotNullOrEmpty(uidDerivedFromGoogle)) return Response.status(400).entity(
					"Google access token " + googleAccessToken + " is invalid").build();
			String uidDerivedFromFacebook = FaceBookAccountUtils.getUID(fbAccessToken, 50, 50);
			if (!isNotNullOrEmpty(uidDerivedFromFacebook)) return Response.status(400).entity(
					"Facebook access token " + fbAccessToken + " is invalid").build();
			try {
				List <Tray> traysDerivedFromGoogle = ProfileManagerImpl.getInstance().getTrayManager().getTrays(uidDerivedFromGoogle);
				List <Tray> traysDerivedFromFacebook = ProfileManagerImpl.getInstance().getTrayManager().getTrays(uidDerivedFromFacebook);
				List <Tray> traysDerivedFrom3cixtyAccount = ProfileManagerImpl.getInstance().getTrayManager().getTrays(userAccessToken.getUid());
				if (conflict(traysDerivedFromGoogle, traysDerivedFromFacebook)) return Response.status(400).entity(
						"There are at least two WishList items under your Google and Facebook account have the same identity").build();
				if (conflict(traysDerivedFromGoogle, traysDerivedFrom3cixtyAccount)) return Response.status(400).entity(
						"There are at least two WishList items under your Google and 3cixty dedicated account have the same identity").build();
				if (conflict(traysDerivedFromFacebook, traysDerivedFrom3cixtyAccount)) return Response.status(400).entity(
						"There are at least two WishList items under your Facebook and 3cixty dedicated account have the same identity").build();
				ProfileManagerImpl.getInstance().getTrayManager().replaceUID(uidDerivedFromGoogle, userAccessToken.getUid());
				ProfileManagerImpl.getInstance().getTrayManager().replaceUID(uidDerivedFromFacebook, userAccessToken.getUid());
				
				Set <String> googleKnows = ProfileManagerImpl.getInstance().getProfile(uidDerivedFromGoogle, null).getKnows();
				Set <String> fbKnows = ProfileManagerImpl.getInstance().getProfile(uidDerivedFromFacebook, null).getKnows();
				
				UserProfile profile = ProfileManagerImpl.getInstance().getProfile(userAccessToken.getUid(), null);
				Set <String> knows = profile.getKnows();
				if (knows == null) {
					knows = new HashSet<String>();
					profile.setKnows(knows);
				}
				if (googleKnows != null && googleKnows.size() > 0) {
					for (String uid: googleKnows) {
						if (knows.contains(uid)) continue;
						knows.add(uid);
					}
				}
				if (fbKnows != null && fbKnows.size() > 0) {
					for (String uid: fbKnows) {
						if (knows.contains(uid)) continue;
						knows.add(uid);
					}
				}
				Set <ProfileIdentities> pis = profile.getHasProfileIdenties();
				if (pis == null) {
					pis = new HashSet<ProfileIdentities>();
					profile.setHasProfileIdenties(pis);
				}
				String g_user_id = ProfileManagerImpl.getInstance().findAccountId(ProfileManagerImpl.getInstance().getProfile(uidDerivedFromGoogle, null), SPEConstants.GOOGLE_SOURCE);
				eu.threecixty.profile.Utils.setProfileIdentities(profile.getHasUID(), g_user_id, SPEConstants.GOOGLE_SOURCE, pis);
				
				String fb_user_id = ProfileManagerImpl.getInstance().findAccountId(ProfileManagerImpl.getInstance().getProfile(uidDerivedFromFacebook, null), SPEConstants.FACEBOOK_SOURCE);
				eu.threecixty.profile.Utils.setProfileIdentities(profile.getHasUID(), fb_user_id, SPEConstants.FACEBOOK_SOURCE, pis);
				
				ProfileManagerImpl.getInstance().getForgottenUserManager().deleteUserProfile(uidDerivedFromGoogle);
				ProfileManagerImpl.getInstance().getForgottenUserManager().deleteUserProfile(uidDerivedFromFacebook);
				
				ProfileManagerImpl.getInstance().saveProfile(profile, null);
				
			} catch (InvalidTrayElement e) {
				e.printStackTrace();
				return Response.status(400).entity(e.getMessage()).build();
			} catch (TooManyConnections e) {
				e.printStackTrace();
				return Response.serverError().build();
			}
		}
		return Response.status(400).entity("The token " + accessToken + " is invalid").build();
	}
	

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
	
	private boolean conflict(List <Tray> trays1, List <Tray> trays2) {
		if (trays1 == null || trays1.size() == 0) return false;
		if (trays2 == null || trays2.size() == 0) return false;
		boolean found = false;
		for (Tray tray1: trays1) {
			if (exist(tray1.getElement_id(), trays2)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	private boolean exist(String itemId, List <Tray> inTrays) {
		for (Tray tray: inTrays) {
			if (itemId.equals(tray.getElement_id())) return true;
		}
		return false;
	}
}

package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.profile.PartnerAccountUtils;
import eu.threecixty.profile.ProfileManager;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

@Path("/" + Constants.VERSION_2)
public class MobidotServices {
	
	private static final String MOBIDOT_SOURCE = "Mobidot";

	@GET
	@Path("/getMobidotAccount")
	public Response getAccount(@HeaderParam("access_token") String access_token) {
		try {
			long starttime = System.currentTimeMillis();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

				PartnerAccount account = PartnerAccountUtils.retrieveOrAddMobidotUser(userAccessToken.getUid(), userAccessToken.getUid());
				CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.MOBIDOT_GET_USER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				if (account != null) {
					setMobidotAccountToUserProfile(userAccessToken.getUid(), account);
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("username", account.getUsername());
					jsonObj.put("password", account.getPassword());
					return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
				} else {
					return Response.ok("{}", MediaType.APPLICATION_JSON_TYPE).build();
				}
			} else {
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.MOBIDOT_GET_USER_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("The access token is invalid '" + access_token + "'")
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (TooManyConnections e) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity(e.getMessage())
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	private void setMobidotAccountToUserProfile(String uid,
			PartnerAccount account) throws TooManyConnections {
		
		Map <String, Boolean> attrs = getAttributesForProfileIdentities();
		
		UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, attrs);
		Set <ProfileIdentities> profileIdentities = null;
		if (profile.getHasProfileIdenties() == null) {
			profileIdentities = new HashSet <ProfileIdentities>();
			profile.setHasProfileIdenties(profileIdentities);
		} else profileIdentities = profile.getHasProfileIdenties();
		Utils.setProfileIdentities(uid, account.getUser_id(), MOBIDOT_SOURCE, profileIdentities);
		
		ProfileManagerImpl.getInstance().saveProfile(profile, attrs);
	}
	
	private Map <String, Boolean> getAttributesForProfileIdentities() {
		Map <String, Boolean> attrs = new HashMap <String, Boolean>();
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IDENTITIES, true);
		return attrs;
	}
}

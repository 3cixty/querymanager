package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.profile.ProfileManager;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.partners.PartnerAccountUtils;

@Path("/" + Constants.VERSION_2)
public class MobidotServices {
	
	private static final String MOBIDOT_SOURCE = "Mobidot";
	private static final String MOBIDOT_KEY = "SRjHX5yHgqqpZyiYaHSXVqhlFWzIEoxUBmbFcSxiZn58Go02rqB9gKwFqsGx5dks";
	private static final String MOBIDOT_ENDPOINT = "https://www.movesmarter.nl/external/identitymanager/user/onetimetoken?";
	
	private static final Logger LOGGER = Logger.getLogger(
			MobidotServices.class.getName());

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
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage())
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build();
		}
	}
	
	@GET
	@Path("/getMobidotToken")
	public Response getMobidotToken(@HeaderParam("access_token") String access_token) {
		try {
			long starttime = System.currentTimeMillis();
			AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {

				PartnerAccount account = PartnerAccountUtils.retrieveOrAddMobidotUser(userAccessToken.getUid(), userAccessToken.getUid());
				CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.MOBIDOT_GET_USER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				if (account != null) {
					if (account.getId() == null) {
					    setMobidotAccountToUserProfile(userAccessToken.getUid(), account);
					}
					String content = getContent(account.getUsername(), account.getPassword());
					if (content == null) return Response.status(Response.Status.BAD_REQUEST)
							.entity("Could not get token from Mobidot server")
							.type(MediaType.TEXT_PLAIN_TYPE)
							.build();
					return Response.ok(content, MediaType.APPLICATION_JSON_TYPE).build();
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
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(e.getMessage())
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(e.getMessage())
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build();
		}
	}

	private String getContent(String username, String password) {
		try {
			URL url = new URL(MOBIDOT_ENDPOINT + "userName=" + username + "&password=" + URLEncoder.encode(password, "UTF-8") + "&key=" + MOBIDOT_KEY);
			InputStream input = url.openStream();
			StringBuilder builder = new StringBuilder();
			byte [] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				builder.append(new String(b, 0, readBytes, "UTF-8"));
			}
			input.close();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
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

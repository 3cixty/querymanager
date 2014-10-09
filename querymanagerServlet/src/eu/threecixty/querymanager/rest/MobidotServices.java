package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.MobidotUser;
import eu.threecixty.profile.MobidotUser.MobidotAccount;
import eu.threecixty.profile.ProfileManagerImpl;

@Path("/" + Constants.VERSION_2)
public class MobidotServices {

	@GET
	@Path("/existMobidotAccount")
	public Response exist(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String key = userAccessToken.getAppkey();
			if (ProfileManagerImpl.getInstance().getMobidot().exist(userAccessToken.getUid())) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.MOBIDOT_EXIST_USER_SERVICE, CallLoggingConstants.SUCCESSFUL);
			    return Response.status(Response.Status.OK).entity(
					"{ \"response\": true}").type(MediaType.APPLICATION_JSON_TYPE).build();
			} else {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.MOBIDOT_EXIST_USER_SERVICE, CallLoggingConstants.FAILED);
				return Response.status(Response.Status.OK).entity(
						"{ \"response\": false}").type(MediaType.APPLICATION_JSON_TYPE).build();
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	@GET
	@Path("/getMobidotAccount")
	public Response getAccount(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			MobidotUser mobidotUser = ProfileManagerImpl.getInstance().getMobidot().getUser(userAccessToken.getUid());
			MobidotAccount account = ProfileManagerImpl.getInstance().getMobidot().findAccount(mobidotUser, userAccessToken.getAppkey());
			if (account != null) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", account.getUsername());
				jsonObj.put("password", account.getPassword());
			    return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
			} else {
				return Response.ok("{}", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	@POST
	@Path("/addMobidotAccount")
	public Response addAccount(@HeaderParam("access_token") String access_token,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)
				&& isNotNullOrEmpty(username) && isNotNullOrEmpty(password)) {
			MobidotUser mobidotUser = ProfileManagerImpl.getInstance().getMobidot().getUser(userAccessToken.getUid());
			MobidotAccount account = ProfileManagerImpl.getInstance().getMobidot().findAccount(mobidotUser, userAccessToken.getAppkey());
			if (account != null) { // there exists an account for this given access token
			    return Response.status(Response.Status.OK).entity(
					"{ \"response\": false, \"reason\": \"This user already had an account\"}").type(MediaType.APPLICATION_JSON_TYPE).build();
			} else {
				if (mobidotUser == null) {
					mobidotUser = new MobidotUser();
				}
				if (mobidotUser.getMobidotAccounts() == null) mobidotUser.setMobidotAccounts(new ArrayList <MobidotAccount>());
				account = new MobidotAccount(username, password, userAccessToken.getAppkey());
				mobidotUser.getMobidotAccounts().add(account);
				if (ProfileManagerImpl.getInstance().getMobidot().updateUser(mobidotUser)) {
				    return Response.ok("{ \"response\": true }", MediaType.APPLICATION_JSON_TYPE).build();
				} else {
					return Response.ok("{ \"response\": false, \"reason\": \"internal errors\"}", MediaType.APPLICATION_JSON_TYPE).build();
				}
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "' or username and password are empty")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	private boolean isNotNullOrEmpty(String str) {
		return str != null && !str.equals("");
	}
}
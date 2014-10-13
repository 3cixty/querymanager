package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.App;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.PartnerUser;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.PartnerUser.PartnerAccount;

@Path("/" + Constants.VERSION_2)
public class GoFlowServices {

	private static final String END_USER_ROLE = "User";
	private static final String DEVELOPER_ROLE = "Developer";
	
	/**
	 * This API checks whether or not a given 3Cixty access_token corresponds with an account at GoFlow server.
	 * If there is no account existed, then the method goes to create one.
	 *
	 * @param access_token
	 * @return
	 */
	@GET
	@Path("/createOrRetrieveGoFlowUser")
	public Response getUser(@HeaderParam("access_token") String access_token) {
		return getAccount(access_token, END_USER_ROLE);
	}

	@GET
	@Path("/createOrRetrieveGoFlowDeveloper")
	public Response getDeveloper(@HeaderParam("access_token") String access_token) {
		return getAccount(access_token, DEVELOPER_ROLE);
	}

	@POST
	@Path("/registerGoFlowApp")
	public Response registerApp(@HeaderParam("key") String appkey, @HeaderParam("google_access_token") String g_access_token) {
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": false, \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		App app = OAuthWrappers.retrieveApp(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": false, \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		boolean ok = GoFlowServer.getInstance().registerNewApp(app.getAppNameSpace(), app.getAppName(), app.getDescription());
		if (!ok) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": false, \"reason\": \"Cannot register your App on GoFlow server\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		getAccountFromUID(uid, app.getAppNameSpace(), DEVELOPER_ROLE);
		return Response.ok("{ \"response\": true }", MediaType.APPLICATION_JSON_TYPE).build();
	}

	private Response getAccount(String access_token, String role) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);

		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.GOFLOW_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			String appid = OAuthWrappers.retrieveApp(userAccessToken.getAppkey()).getAppNameSpace();
			return getAccountFromUID(userAccessToken.getUid(), appid, role);
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.GOFLOW_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	private Response getAccountFromUID(String uid, String appid, String role) {
		// check if there is no account existed at GoFlow server, then go to create an account
		PartnerUser goflowUser = ProfileManagerImpl.getInstance().getGoFlow().getUser(uid);
		PartnerAccount account = ProfileManagerImpl.getInstance().getGoFlow().findAccount(goflowUser, appid, role);
		JSONObject jsonObj = new JSONObject();
		String username, pwd;
		boolean ok = true;
		if (account != null) {
			username = account.getUsername();
			pwd = account.getPassword();
		} else {
			username = uid;
			if (END_USER_ROLE.equals(role)) { // create end user
			    pwd = GoFlowServer.getInstance().createEndUser(appid, uid);
			} else { // create developer
				pwd = GoFlowServer.getInstance().createDeveloper(appid, uid);
			}
			
			if (goflowUser == null) goflowUser = new PartnerUser(uid);
			if (goflowUser.getAccounts() == null) goflowUser.setPartnerAccounts(
					new ArrayList <PartnerAccount>());
			
			if (pwd != null) {
				account = new PartnerAccount(username, pwd, appid, role);
				goflowUser.getAccounts().add(account);
				ok = ProfileManagerImpl.getInstance().getGoFlow().updateUser(goflowUser);
			}
		}
		if (!ok) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{ \"response\": false, \"reason\": \"internal errors\"}").type(MediaType.APPLICATION_JSON_TYPE).build();
		}
		if (pwd != null) {
		    jsonObj.put("username", username);
		    jsonObj.put("password", pwd);
		    return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("{ \"response\": false, \"reason\": \"Cannot create user at GoFlow\"}").type(MediaType.APPLICATION_JSON_TYPE).build();
		}
	}
}

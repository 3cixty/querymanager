/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.partners.GoFlowServer;
import eu.threecixty.profile.partners.PartnerAccountUtils;

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
	@Path("/getGoflowAccount")
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
		boolean ok = registerAppFromUID(uid, appkey);
		
		if (ok) {
		    return Response.ok("{ \"response\": true }", MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST)
	        .entity(" {\"response\": false, \"reason\": \"Cannot register your App on GoFlow server or your AppKey is invalid\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		
	}

	public static boolean registerAppFromUID(String uid, String appkey) {
		AppCache app = TokenCacheManager.getInstance().getAppCache(appkey);
		if (app == null) return false;
		
		boolean ok = GoFlowServer.getInstance().registerNewApp(app.getAppNameSpace(), app.getAppName(), app.getDescription());
		if (!ok) return false;
		
		getAccountFromUID(uid, app.getAppNameSpace(), DEVELOPER_ROLE);
		return true;
	}

	private Response getAccount(String access_token, String role) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);

		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.GOFLOW_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			String appid = TokenCacheManager.getInstance().getAppCache(userAccessToken.getAppkey()).getAppNameSpace();
			return getAccountFromUID(userAccessToken.getUid(), appid, role);
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.GOFLOW_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	private static Response getAccountFromUID(String uid, String appid, String role) {
		// check if there is no account existed at GoFlow server, then go to create an account
		PartnerAccount account = PartnerAccountUtils.retrieveOrAddGoflowUser(uid, appid);
		if (account == null) { // can not create Goflow account
			return Response.status(Response.Status.BAD_REQUEST).entity("{ \"response\": false, \"reason\": \"Cannot create user at GoFlow\"}").type(MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("username", account.getUsername());
			jsonObj.put("password", account.getPassword());
			jsonObj.put("appid", appid);
			return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		}
	}
}

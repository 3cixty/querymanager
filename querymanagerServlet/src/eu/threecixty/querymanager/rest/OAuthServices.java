package eu.threecixty.querymanager.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.utils.ScopeUtils;
import eu.threecixty.profile.GoogleAccountUtils;

@Path("/" + Constants.VERSION_2)
public class OAuthServices {
	
	public static final String APP_KEY = "appObj";
	public static final String UID_KEY = "uid";
	
	private static final String V2_ROOT = OAuthWrappers.ROOT_SERVER + "v2/";
	
	public static final String GOOGLE_CALLBACK = V2_ROOT + "googlecallback.jsp";
	public static final String THREECIXTY_CALLBACK = V2_ROOT + "3cixtycallback.jsp";
	public static final String REDIRECT_URI = V2_ROOT + "redirect_uri";
	
	public static final String REDIRECT_URI_CLIENT = V2_ROOT + "redirect_uri_client";
	public static final String ONLY_GOOGLE_ACCESS_TOKEN = "only_google_access_token";

	@Context 
	private HttpServletRequest httpRequest;

	@GET
	@Path("/validateAccessToken")
	public Response validateAccessToken(@HeaderParam("access_token") String access_token,
			@HeaderParam("key") String key) {
		if (OAuthWrappers.validateAppKey(key)) {
			if (OAuthWrappers.validateUserAccessToken(access_token)) {
				// TODO: add callLog
				return Response.status(Response.Status.OK)
						.entity(" {\"response\": \"ok\"} ")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.build();
			}
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(" {\"response\": \"failed\"} ")
					.type(MediaType.APPLICATION_JSON_TYPE)
					.build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid: " + key + "\"} ")
					.type(MediaType.APPLICATION_JSON_TYPE)
					.build();
		}
	}

	@GET
	@Path("/getAccessToken")
	public Response getAccessToken(@HeaderParam("google_access_token") String g_access_token, @HeaderParam("key") String appkey,
			@DefaultValue("") @HeaderParam("scope") String scope) {
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		App app = OAuthWrappers.retrieveApp(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		AccessToken accessToken = OAuthWrappers.createAccessTokenForMobileApp(app, scope);
		if (accessToken == null) {
			return Response.status(Response.Status.BAD_REQUEST)
	        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		// scope can be a 'null' string as its result is found in 3cixtycallback
		if (!OAuthWrappers.storeAccessTokenWithUID(uid, accessToken.getAccess_token(), accessToken.getRefresh_token(), scope, app)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		Gson gson = new Gson();
		return Response.status(Response.Status.OK).entity(
				gson.toJson(accessToken)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Path("/getAppKey")
	public Response getAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid,
			@QueryParam("appname") String appname,
			@DefaultValue("") @QueryParam("description") String desc, @QueryParam("category") String cat,
			@QueryParam("scopeName") List<String> scopeNames,
			@DefaultValue("")@QueryParam("redirect_uri") String redirect_uri,
			@DefaultValue("")@QueryParam("thumbNailUrl") String thumbNailUrl) {
		//thumbNailUrl
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		// TODO: there is only one scope by default
		//String appKey = OAuthWrappers.getAppKey(appid, appname, desc, cat, uid, scopeNames, redirect_uri, thumbNailUrl);
		String appKey = OAuthWrappers.getAppKey(appid, appname, desc, cat, uid, ScopeUtils.getScopeNames(), redirect_uri, thumbNailUrl);
		if (appKey != null && !appKey.equals("")) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"key\": \"" + appKey + "\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"appId already existed or scopeName doesn't exist\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@POST
	@Path("/updateAppKey")
	public Response updateAppKey(@QueryParam("key") String key, 
			@DefaultValue("") @QueryParam("appname") String appname,
			@DefaultValue("") @QueryParam("description") String desc, @DefaultValue("") @QueryParam("category") String cat,
			@DefaultValue("") @QueryParam("scopeName") List<String> scopeNames, @DefaultValue("") @QueryParam("redirect_uri") String redirect_uri,
			@DefaultValue("")@QueryParam("thumbNailUrl") String thumbNailUrl) {
		// TODO: there is only one scope by default
		//boolean ok = OAuthWrappers.updateAppKey(key, appname, desc, cat, scopeNames, redirect_uri, thumbNailUrl);
		boolean ok = OAuthWrappers.updateAppKey(key, appname, desc, cat, ScopeUtils.getScopeNames(), redirect_uri, thumbNailUrl);
		if (ok) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"successful\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"app key or scope name is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@GET
	@Path("/retrieveAppKey")
	public Response retrieveAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid) {
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String appKey = OAuthWrappers.retrieveAppKey(appid, uid);
		if (appKey != null && !appKey.equals("")) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"key\": \"" + appKey + "\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"appid doesn't exist\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@GET
	@Path("/getScopes")
	public Response getScopes() {
		List <Scope> scopes = OAuthWrappers.getScopes();
		List <DeveloperScope> retScopes = new ArrayList <DeveloperScope>();
		for (Scope scope: scopes) {
			DeveloperScope ds = new DeveloperScope(scope.getScopeName(), scope.getDescription());
			retScopes.add(ds);
		}
		Gson gson = new Gson();
		return Response.status(Response.Status.OK)
				.entity(gson.toJson(retScopes))
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	@GET
	@Path("/auth")
	public Response auth(@QueryParam("key") String appkey) {
		App app = OAuthWrappers.retrieveApp(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		HttpSession session = httpRequest.getSession();
		session.setAttribute(APP_KEY, app);
		try {
			//return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_AUTH_PAGE + "auth.jsp")).build();
			return Response.temporaryRedirect(new URI("https://accounts.google.com/o/oauth2/auth?scope=email%20profile&state=%2Fprofile&redirect_uri="
			+ OAuthServices.GOOGLE_CALLBACK
			+"&response_type=token&client_id=239679915676-j58smonkigkh26rugnbsja3pon7bkvbv.apps.googleusercontent.com"))
			.header("Access-Control-Allow-Origin", "*").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Path("/getGoogleAccessToken")
	public Response getGoogleAccessToken() {
		HttpSession session = httpRequest.getSession();
		session.setAttribute(ONLY_GOOGLE_ACCESS_TOKEN, true);
		try {
			//return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_AUTH_PAGE + "auth.jsp")).build();
			return Response.temporaryRedirect(new URI("https://accounts.google.com/o/oauth2/auth?scope=email%20profile&state=%2Fprofile&redirect_uri="
			+ OAuthServices.GOOGLE_CALLBACK
			+"&response_type=token&client_id=239679915676-j58smonkigkh26rugnbsja3pon7bkvbv.apps.googleusercontent.com"))
			.header("Access-Control-Allow-Origin", "*").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Path("/redirect_uri")
	public Response redirect_uri(@QueryParam("google_access_token") String google_access_token) {
		HttpSession session = httpRequest.getSession();
		App app = (App) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		String uid = GoogleAccountUtils.getUID(google_access_token);
		
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		session.setAttribute(UID_KEY, uid);
		
		AccessToken accessToken = OAuthWrappers.findAccessToken(uid, app);
		if (accessToken != null) {
			return redirect_uri_client2(accessToken, accessToken.getExpires_in(), app);
		}
		
		try {
			return Response.temporaryRedirect(new URI(
					OAuthWrappers.ENDPOINT_AUTHORIZATION + "?response_type=token&scope="
			+ join(app.getScopes()) + "&client_id="
			+ app.getClientId() + "&redirect_uri="
		    + THREECIXTY_CALLBACK)).header(OAuthWrappers.AUTHORIZATION,
		    		OAuthWrappers.getBasicAuth()).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/redirect_uri_client")
	public Response redirect_uri_client(@QueryParam("access_token") String accessToken,
			@QueryParam("refresh_token") String refreshToken,
			@QueryParam("expires_in") int expires_in, @QueryParam("scope") String scope) {
		HttpSession session = httpRequest.getSession();
		App app = (App) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String uid = (String) session.getAttribute(UID_KEY);
		// scope can be a 'null' string as its result is found in 3cixtycallback
		if (!OAuthWrappers.storeAccessTokenWithUID(uid, accessToken, refreshToken, scope, app)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		AccessToken tokenInfo = new AccessToken();
		tokenInfo.setExpires_in(expires_in);
		tokenInfo.setAccess_token(accessToken);
		tokenInfo.setRefresh_token(refreshToken);
		if (scope != null && !scope.equals("") && !scope.equals("null")) {
			if (scope.indexOf(',') > 0) { // more than one scope
				String[] tmpScopeNames = scope.split(",");
				for (String tmpScopeName: tmpScopeNames) tokenInfo.getScopeNames().add(tmpScopeName.trim());
			} else tokenInfo.getScopeNames().add(scope.trim());
		}
		return redirect_uri_client2(tokenInfo, expires_in, app);
	}
	
	// TODO
	@GET
	@Path("/token")
	public Response token(@HeaderParam("refresh_token") String refresh_token) {
		AccessToken refreshedToken = OAuthWrappers.refreshAccessToken(refresh_token);
		if (refreshedToken == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"refresh_token is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();

		Gson gson = new Gson();
		
		return Response.status(Response.Status.OK).entity(
				gson.toJson(refreshedToken)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	@POST
	@Path("/revoke")
	public Response revoke(@HeaderParam("access_token") String access_token) {
		boolean ok = OAuthWrappers.revokeAccessToken(access_token);
		if (!ok) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"an invalid access token or internal errors\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		return Response.status(Response.Status.OK).entity(
				" {\"response\": \"successful\" }").type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	private Response redirect_uri_client2(AccessToken accessToken, int expires_in, App app) {
		try {
			return Response.temporaryRedirect(new URI(app.getRedirectUri()
					+ "#access_token=" + accessToken.getAccess_token()
					+ "&refresh_token=" + accessToken.getRefresh_token()
					+ "&expires_in=" + expires_in
					+ "&scope=" + join(accessToken.getScopeNames(), ",") )).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String join(List<String> scopeNames, String strJoined) {
		if (scopeNames.size() == 0) return "null";
		StringBuilder builder = new StringBuilder();
		for (String scopeName: scopeNames) {
			if (builder.length() == 0) builder.append(scopeName);
			else {
				builder.append(strJoined).append(scopeName);
			}
		}
		return builder.toString();
	}

	private String join(Set<Scope> scopes) {
		StringBuilder builder = new StringBuilder();
		for (Scope scope: scopes) {
			if (builder.length() == 0) builder.append(scope.getScopeName());
			else {
				builder.append(',').append(scope.getScopeName());
			}
		}
		return builder.toString();
	}

	private class DeveloperScope {
		
		public DeveloperScope(String scopeName, String desc) {
			this.scopeName = scopeName;
			this.description = desc;
		}
		// used by gson
		@SuppressWarnings("unused")
		private String scopeName;
		@SuppressWarnings("unused")
		private String description;
	}
}

package eu.threecixty.querymanager.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import eu.threecixty.Configuration;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.utils.ScopeUtils;
import eu.threecixty.profile.FaceBookAccountUtils;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.querymanager.filter.DynamicCORSFilter;

@Path("/" + Constants.VERSION_2)
public class OAuthServices {
	
	private static final String ID_PATTERN = "^[a-z_A-Z0-9:\\-]*$";
	
	public static final String APP_KEY = "appObj";
	public static final String UID_KEY = "uid";
	
	/**
	 * Set the server configuration in WebContent/WEB-INF/3cixty.properties
	 */
	private static final String V2_ROOT = Configuration.get3CixtyRoot() + "/";
	private static final String GOOGLE_CLIENT_ID = Configuration.getGoogleClientId();
	
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
		App app = OAuthWrappers.retrieveApp(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String _3cixtyUid = GoogleAccountUtils.getUID(g_access_token, app.getAppNameSpace());
		if (_3cixtyUid == null || _3cixtyUid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();

		return getAccessTokenFromUid(_3cixtyUid, app, scope);
	}
	
	@GET
	@Path("/getAccessTokenForFB")
	public Response getAccessTokenForFB(@HeaderParam("fb_access_token") String fb_access_token, @HeaderParam("key") String appkey,
			@DefaultValue("") @HeaderParam("scope") String scope) {
		App app = OAuthWrappers.retrieveApp(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String _3cixtyUid = FaceBookAccountUtils.getUID(fb_access_token, app.getAppNameSpace());
		if (_3cixtyUid == null || _3cixtyUid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Facebook access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();

		return getAccessTokenFromUid(_3cixtyUid, app, scope);
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
		if (!validId(appid) || appid == null || appid.equals("")) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"appId only contains characters in the following patterns ^[a-z_A-Z0-9:\\-]*$\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		String uid = GoogleAccountUtils.getUID(g_access_token, null);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		// TODO: there is only one scope by default
		//String appKey = OAuthWrappers.getAppKey(appid, appname, desc, cat, uid, scopeNames, redirect_uri, thumbNailUrl);
		String appKey = OAuthWrappers.getAppKey(appid, appname, desc, cat, uid, ScopeUtils.getScopeNames(), redirect_uri, thumbNailUrl);
		if (appKey != null && !appKey.equals("")) {
			boolean ok = GoFlowServices.registerAppFromUID(uid, appKey);
			if (ok) {
				// add CORS configuration if existed
				if (redirect_uri != null && !redirect_uri.trim().equals("")) {
					DynamicCORSFilter currentFilter = DynamicCORSFilter.getCurrentFilter();
					if (currentFilter != null) {
						String rootRedirectUri = getRootRedirectUri(redirect_uri.trim());
						if (rootRedirectUri != null) currentFilter.addConfiguration(rootRedirectUri);
					}
				}
			    return Response.ok(" {\"key\": \"" + appKey + "\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			} else {
				return Response.ok(" {\"response\": \"Cannot register App on GoFlow server\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"appId already existed or scopeName doesn't exist\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@POST
	@Path("/updateAppInfo")
	public Response updateAppKey(@FormParam("google_access_token") String googleAccessToken,
			@FormParam("appid") String appid, 
			@DefaultValue("") @FormParam("appname") String appname,
			@DefaultValue("") @FormParam("description") String desc, @DefaultValue("") @FormParam("category") String cat,
			@DefaultValue("") @FormParam("scopeName") List<String> scopeNames, @DefaultValue("") @FormParam("redirect_uri") String redirect_uri,
			@DefaultValue("")@FormParam("thumbNailUrl") String thumbNailUrl) {
		if (!validId(appid) || appid == null || appid.equals("")) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"appId only contains characters in the following patterns ^[a-z_A-Z0-9:\\-]*$\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		String uid = GoogleAccountUtils.getUID(googleAccessToken, null);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		boolean ok = OAuthWrappers.updateAppKey(uid, appid, appname, desc, cat, ScopeUtils.getScopeNames(), redirect_uri, thumbNailUrl);
		if (ok) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"successful\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\" } ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@GET
	@Path("/getApps")
	public Response getApps(@QueryParam("google_access_token") String g_access_token, @DefaultValue("json") @QueryParam("format") String format) {
		try {
			String uid = GoogleAccountUtils.getUID(g_access_token, null);
			if (uid == null || uid.equals(""))
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.build();
			List <App> apps = OAuthWrappers.getApps(uid);
			JSONArray root = new JSONArray();
			for (App app: apps) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("key", app.getKey());
				jsonObj.put("id", app.getAppNameSpace());
				jsonObj.put("name", app.getAppName() == null ? "" : app.getAppName());
				jsonObj.put("description", app.getDescription() == null ? "" : app.getDescription());
				Set <Scope> scopes = OAuthWrappers.getScopes(app);
				List <String> strScopes = new ArrayList <String>();
				for (Scope scope: scopes) {
					strScopes.add(scope.getScopeName());
				}
				jsonObj.put("scopes", strScopes);
				jsonObj.put("redirect_uri", app.getRedirectUri() == null ? "" : app.getRedirectUri());
				jsonObj.put("logo", app.getThumbnail() == null ? "" : app.getThumbnail());
				root.put(jsonObj);
			}
			if (format.equalsIgnoreCase("html")){
				String jsonOutput = root.toString(4);
				String htmlout = "<html><body><pre>" +jsonOutput +  "</pre></body></html>";
				return Response.ok(htmlout, MediaType.TEXT_HTML).build();
			}
			else
				return Response.ok(root.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	@GET
	@Path("/retrieveAppKey")
	public Response retrieveAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid) {
		String uid = GoogleAccountUtils.getUID(g_access_token, null);
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
	@Path("/retrieveKeyInfo")
	public Response retrieveKeyInfo(@HeaderParam("key") String key) {
		try {
			App app = OAuthWrappers.retrieveApp(key);
			if (app == null) {
				return Response.status(Response.Status.OK)
						.entity(" {\"response\": \"not found\"} ")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.build();
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("response", "found");
			jsonObj.put("appid", app.getAppNameSpace());
			jsonObj.put("appname", app.getAppName());
			return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	@GET
	@Path("/retrieveKeyInfoFromAccessToken")
	public Response retrieveKeyInfoFromAccessToken(@HeaderParam("access_token") String access_token) {
		try {
			AccessToken tokenInfo = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (tokenInfo == null) {
				return Response.ok(" {\"response\": \"not found\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			}
			App app = OAuthWrappers.retrieveApp(tokenInfo.getAppkey());
			if (app == null) {
				return Response.ok(" {\"response\": \"not found\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("response", "found");
			jsonObj.put("appid", app.getAppNameSpace());
			jsonObj.put("appname", app.getAppName());
			return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
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
		HttpSession session = httpRequest.getSession();
		App app = (App) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		session.setAttribute(APP_KEY, app);
		try {
			//return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_AUTH_PAGE + "auth.jsp")).build();
			return Response.temporaryRedirect(new URI("https://accounts.google.com/o/oauth2/auth?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fplus.login&state=%2Fprofile&redirect_uri="
			+ OAuthServices.GOOGLE_CALLBACK
			+"&response_type=token&client_id=" + GOOGLE_CLIENT_ID))
			.header("Access-Control-Allow-Origin", "*")
            .cacheControl(cacheControlNoStore())
            .header("Pragma", "no-cache")
			.build();
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
			return Response.temporaryRedirect(new URI("https://accounts.google.com/o/oauth2/auth?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fplus.login&state=%2Fprofile&redirect_uri="
			+ OAuthServices.GOOGLE_CALLBACK
			+"&response_type=token&client_id=" + GOOGLE_CLIENT_ID))
			.header("Access-Control-Allow-Origin", "*").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Path("/redirect_uri")
	public Response redirect_uri(@QueryParam("access_token_outside") String accessTokenFromOutside,
			@DefaultValue("Google") @QueryParam("source") String source) {
		HttpSession session = httpRequest.getSession();
		App app = (App) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		String uid = "Google".equals(source) ? GoogleAccountUtils.getUID(accessTokenFromOutside, app.getAppNameSpace())
				: FaceBookAccountUtils.getUID(accessTokenFromOutside, app.getAppNameSpace());
		
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google or Facebook access token is invalid or expired\"} ")
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
			+ join(OAuthWrappers.getScopes(app)) + "&client_id="
			+ app.getClientId() + "&redirect_uri="
		    + THREECIXTY_CALLBACK)).header(OAuthWrappers.AUTHORIZATION,
		    		OAuthWrappers.getBasicAuth(app.getClientId(), app.getPassword()))
		    		.header("Access-Control-Allow-Origin", "*")
                    .cacheControl(cacheControlNoStore())
                    .header("Pragma", "no-cache")
		    		.build();
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
		if (!OAuthWrappers.storeAccessTokenWithUID(uid, accessToken, refreshToken, scope, app) || uid == null) {
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
		HttpSession session = httpRequest.getSession();
		session.removeAttribute(APP_KEY);
		try {
			return Response.temporaryRedirect(new URI(app.getRedirectUri()
					+ "#access_token=" + accessToken.getAccess_token()
					+ "&refresh_token=" + accessToken.getRefresh_token()
					+ "&expires_in=" + expires_in
					+ "&scope=" + join(accessToken.getScopeNames(), ",") ))
					.header("Access-Control-Allow-Origin", "*").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private Response getAccessTokenFromUid(String _3cixtyUID, App app, String scope) {
		if (!checkValidScope(scope)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Scope is invalid\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		AccessToken accessToken = OAuthWrappers.createAccessTokenForMobileApp(app, scope);
		if (accessToken == null) {
			return Response.status(Response.Status.BAD_REQUEST)
	        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		// scope can be a 'null' string as its result is found in 3cixtycallback
		if (!OAuthWrappers.storeAccessTokenWithUID(_3cixtyUID, accessToken.getAccess_token(), accessToken.getRefresh_token(), scope, app)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		Gson gson = new Gson();
		return Response.status(Response.Status.OK).entity(
				gson.toJson(accessToken)).type(MediaType.APPLICATION_JSON_TYPE).build();
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

	protected boolean validId(String id) {
		if (id.matches(ID_PATTERN)) {
			return true;
		}
		return false;
	}
	
	private boolean checkValidScope(String scope) {
		if (scope != null && !scope.equals("")) {
			String[] primitiveScopes = scope.split(",");
			for (String primitiveScope: primitiveScopes) {
				String tmp = primitiveScope.trim();
				if ((!tmp.equalsIgnoreCase(Constants.WISH_LIST_SCOPE_NAME)
						&& (!tmp.equalsIgnoreCase(Constants.PROFILE_SCOPE_NAME)))) return false;
			}
		}
		return true;
	}
	
	private CacheControl cacheControlNoStore() {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoStore(true);
		return cacheControl;
	}
	
	private String getRootRedirectUri(String redirect_uri) {
		int index = redirect_uri.lastIndexOf("/"); // redirect_uri must contain protocol, scheme (http://, https://)
		if (index == 7 || index == 9) return redirect_uri;
		if (index < 9) return null;
		return redirect_uri.substring(0, index);
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

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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.Configuration;
import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.utils.ScopeUtils;
import eu.threecixty.profile.FaceBookAccountUtils;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.SPEConstants;
import eu.threecixty.querymanager.AuthorizationBypassManager;
import eu.threecixty.querymanager.filter.DynamicCORSFilter;

/**
 * 
 * This class provides RESTful APIs to bridge 3cixty with OAuth server.
 * The class provides APIs to get 3cixty token, validate token, revoke token, and
 * refresh token.
 *
 */
@Path("/" + Constants.VERSION_2)
public class OAuthServices {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 OAuthServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
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
	public static final String SCOPES = "Profile,WishList";
	
	private static final String OUTSIDE_TOKEN = "outsideToken";
	private static final String SOURCE = "source";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";

	@Context 
	private HttpServletRequest httpRequest;

	/**
	 * This API is used to check whether or not a given 3cixty access token is still valid.
	 *
	 * @param access_token
	 * @param key
	 * @return
	 */
	@GET
	@Path("/validateAccessToken")
	public Response validateAccessToken(@HeaderParam("access_token") String access_token,
			@HeaderParam("key") String key) {
		if (OAuthWrappers.validateAppKey(key)) {
			if (OAuthWrappers.validateUserAccessToken(access_token)) {
				return Response.status(Response.Status.OK)
						.entity(" {\"response\": \"ok\"} ")
						.type(MediaType.APPLICATION_JSON_TYPE)
						.build();
			}
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(" {\"response\": \"failed\", \"reason\": \"Access token is invalid\"} ")
					.type(MediaType.APPLICATION_JSON_TYPE)
					.build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid: " + key + "\"} ")
					.type(MediaType.APPLICATION_JSON_TYPE)
					.build();
		}
	}

	/**
	 * This API is used to get 3cixty access token from a Google token.
	 * <br>
	 * The API also gets the list of friends extracted from Google token which the user gives permission to 3cixty.
	 * The friends list is stored in the <code>knows</code> property of user profile.
	 * <br>
	 * All actions done inside this API are performed in the {@link GoogleAccountUtils.getUID}. Please have a look
	 * at this class for more detail.
	 *
	 * @param g_access_token
	 * 				Google access token
	 * @param appkey
	 * 				3cixty application key
	 * @param scope
	 * 				3cixty scope (Profile,WishList)
	 * @return
	 */
	@GET
	@Path("/getAccessToken")
	public Response getAccessToken(@HeaderParam("google_access_token") String g_access_token, @HeaderParam("key") String appkey,
			@DefaultValue("") @HeaderParam("scope") String scope) {
		long startTime = System.currentTimeMillis();
		if (!AuthorizationBypassManager.getInstance().isFound(appkey))
			return Response.status(Response.Status.UNAUTHORIZED).entity(
					" {\"response\": \"failed\", \"reason\": \"App key is not allowed to get access token\"} ").type(
							MediaType.APPLICATION_JSON_TYPE).build();
		AppCache app = TokenCacheManager.getInstance().getAppCache(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String _3cixtyUid = GoogleAccountUtils.getUID(g_access_token);
		long time1 = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Time to extract Google info: " + (time1 - startTime) + " ms");
		if (_3cixtyUid == null || _3cixtyUid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();

		Response response = getAccessTokenFromUid(_3cixtyUid, app, scope);
		long endTime = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Total time generate 3cixty access token: " + (endTime - startTime) + " ms");
		return response;
	}
	
	/**
	 * This API is used to get 3cixty access token from a Facebook token.
	 * <br>
	 * The API also gets the list of friends extracted from Facebook token which the user gives permission to 3cixty.
	 * The friends list is stored in the <code>knows</code> property of user profile.
	 * <br>
	 * All actions done inside this API are performed in the {@link FaceBookAccountUtils.getUID}. Please have a look
	 * at this class for more detail.
	 *
	 * @param fb_access_token
	 * 				Facebook access token
	 * @param appkey
	 * 				Application key
	 * @param scope
	 * 				3cixty scope (Profile,WishList).
	 * @param width
	 * 				The width for profile image
	 * @param height
	 * 				The height for profile image
	 * @return
	 */
	@GET
	@Path("/getAccessTokenForFB")
	public Response getAccessTokenForFB(@HeaderParam("fb_access_token") String fb_access_token,
			@HeaderParam("key") String appkey,
			@DefaultValue("") @HeaderParam("scope") String scope,
			@DefaultValue("50") @QueryParam("width") int width,
			@DefaultValue("50") @QueryParam("height") int height) {
		long startTime = System.currentTimeMillis();
		if (!AuthorizationBypassManager.getInstance().isFound(appkey))
			return Response.status(Response.Status.UNAUTHORIZED).entity(
					" {\"response\": \"failed\", \"reason\": \"App key is not allowed to get access token\"} ").type(
							MediaType.APPLICATION_JSON_TYPE).build();
		AppCache app = TokenCacheManager.getInstance().getAppCache(appkey);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String _3cixtyUid = FaceBookAccountUtils.getUID(fb_access_token, width, height);
		if (_3cixtyUid == null || _3cixtyUid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Facebook access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		long time1 = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Time to extract Facebook info: " + (time1 - startTime) + " ms");

		Response response = getAccessTokenFromUid(_3cixtyUid, app, scope);
		long endTime = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Total time generate 3cixty access token: " + (endTime - startTime) + " ms");
		return response;

	}

	/**
	 * This API is to create a 3cixty application key.
	 * <br>
	 * Currently, creating an application key is only available with Google token. Each application needs
	 * to have unique application ID which is provided by developer. Developers are able to change
	 * information about the application key at any moment, but the application ID provided by developers
	 * needs to be constant. To change information about the application, please use {@link updateAppInfo}.
	 *
	 * @param g_access_token
	 * 				Google token
	 * @param appid
	 * 				Application ID
	 * @param appname
	 * 				Application name
	 * @param desc
	 * 				Application description
	 * @param cat
	 * 				Application category
	 * @param scopeNames
	 * 				3cixty scope (Profile,WishList)
	 * @param redirect_uri
	 * 				The redirect URI which is to return 3cixty access token to the application.
	 * 				This parameter is required for Web application.
	 * @param thumbNailUrl
	 * 				The link to application logo. The logo is shown during authorization to ask
	 * 				the user to give permission to the application.
	 * @return
	 */
	@GET
	@Path("/getAppKey")
	public Response getAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid,
			@QueryParam("appname") String appname,
			@DefaultValue("") @QueryParam("description") String desc, @QueryParam("category") String cat,
			@DefaultValue("") @QueryParam("scopeName") List<String> scopeNames,
			@DefaultValue("")@QueryParam("redirect_uri") String redirect_uri,
			@DefaultValue("")@QueryParam("thumbNailUrl") String thumbNailUrl) {
		//thumbNailUrl
		if (!validId(appid) || appid == null || appid.equals("")) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"appId only contains characters in the following patterns ^[a-z_A-Z0-9:\\-]*$\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
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
				return Response.ok(" {\"response\": \"failed\", \"reason\": \"Cannot register App on GoFlow server\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"appId already existed or scopeName doesn't exist\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	/**
	 * This API is used to update information about a given application key.
	 *
	 * @param googleAccessToken
	 * 				The Google token of the user which created the application key.
	 * @param appid
	 * 				The application ID which needs to be immutable.
	 * @param appname
	 * 				The application name.
	 * @param desc
	 * 				The application description.
	 * @param cat
	 * 				The application category.
	 * @param scopeNames
	 * 				The scopes (no longer used) as the API always gets the list of scopes by default.
	 * @param redirect_uri
	 * 				The redirect URI which is used to return 3cixty access token to application.
	 * @param thumbNailUrl
	 * 				The link to application logo. The logo is shown during authorization to ask
	 * 				the user to give permission to the application.
	 * @return
	 */
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
		String uid = GoogleAccountUtils.getUID(googleAccessToken);
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

	/**
	 * This API is to list all application information which have been created by the corresponding
	 * user of a given Google access token.
	 * 
	 * @param g_access_token
	 * 				Google token.
	 * @param format
	 * 				The data format, either <code>html</code> or <code>json</code>.
	 * @return
	 */
	@GET
	@Path("/getApps")
	public Response getApps(@QueryParam("google_access_token") String g_access_token, @DefaultValue("json") @QueryParam("format") String format) {
		try {
			String uid = GoogleAccountUtils.getUID(g_access_token);
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

	/**
	 * This API is used to retrieve the application key.
	 *
	 * @param g_access_token
	 * 				Google token of the corresponding user who created the application key.
	 * @param appid
	 * 				The application ID.
	 * @return
	 */
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
	
	/**
	 * This API is used to retrieve application information of a given application key.
	 *
	 * @param key
	 * 				The applciation key.
	 * @return
	 */
	@GET
	@Path("/retrieveKeyInfo")
	public Response retrieveKeyInfo(@HeaderParam("key") String key) {
		try {
			AppCache app = TokenCacheManager.getInstance().getAppCache(key);
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

	/**
	 * This API is used to retrieve application ID and name from a given 3cixty access token.
	 *
	 * @param access_token
	 * 				The 3cixty access token.
	 * @return
	 */
	@GET
	@Path("/retrieveKeyInfoFromAccessToken")
	public Response retrieveKeyInfoFromAccessToken(@HeaderParam("access_token") String access_token) {
		try {
			AccessToken tokenInfo = OAuthWrappers.findAccessTokenFromDB(access_token);
			if (tokenInfo == null) {
				return Response.ok(" {\"response\": \"not found\"} ", MediaType.APPLICATION_JSON_TYPE).build();
			}
			AppCache app = TokenCacheManager.getInstance().getAppCache(tokenInfo.getAppkey());
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

	/**
	 * This API is used to get all scopes. Currently, there are only two scopes (Profile, WishList).
	 *
	 * @return
	 */
	@GET
	@Path("/getScopes")
	public Response getScopes() {
		List <Scope> scopes = OAuthWrappers.getScopes();
		List <DeveloperScope> retScopes = new ArrayList <DeveloperScope>();
		for (Scope scope: scopes) {
			DeveloperScope ds = new DeveloperScope(scope.getScopeName(), scope.getDescription());
			retScopes.add(ds);
		}
		return Response.status(Response.Status.OK)
				.entity(JSONObject.wrap(retScopes).toString())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	/**
	 * This API is used to sign in with Google account, then returns 3cixty access token,
	 * refresh token and expiration time in millisecond after its creation.
	 * <br>
	 * The API first extracts user information such as first name, last name, and Google UID.
	 * Then, the API extracts friends list from Google Plus if you gave any permission
	 * to 3cixty to access to them. All those information are persisted in database.
	 * <br>
	 * Using this API, during 3cixty authorization, there will appear a dialog box to ask you to
	 * give permission to the application represented by a given app key.
	 * 
	 * @param appkey
	 * @return
	 */
	@GET
	@Path("/auth")
	public Response auth(@QueryParam("key") String appkey) {
		HttpSession session = httpRequest.getSession();
		AppCache app = (AppCache) session.getAttribute(APP_KEY);
		if (app == null) {
			app = TokenCacheManager.getInstance().getAppCache(appkey);
		}
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
	
	/**
	 * This API is used to get Google access token;
	 *
	 * @return
	 */
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
	
	/**
	 * This API is used to sign in 3cixty from a given external token (Google or Facebook).
	 * <br>
	 * The API first extracts user information such as first name, last name, and Google UID
	 * or Facebook UID. Then, the API extracts friends list from Google Plus / Facebook if you
	 * gave any permission to 3cixty to access to them. All those information are persisted in
	 * database.
	 * <br>
	 * Using this API, during 3cixty authorization, there will appear a dialog box to ask you to
	 * give permission to the application represented by a given app key.
	 * <br>
	 * Note that it's only possible to extract friends list from Facebook if your friends already
	 * used 3cixty applications.
	 *
	 * @param accessTokenFromOutside
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 */
	@GET
	@Path("/redirect_uri")
	public Response redirect_uri(@QueryParam("access_token_outside") String accessTokenFromOutside,
			@DefaultValue("Google") @QueryParam("source") String source,
			@DefaultValue("50") @QueryParam("width") int width,
			@DefaultValue("50") @QueryParam("height") int height) {
		HttpSession session = httpRequest.getSession();
		AppCache app = (AppCache) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		boolean existed = SPEConstants.GOOGLE_SOURCE.equals(source) ?
				GoogleAccountUtils.existUserProfile(accessTokenFromOutside) : FaceBookAccountUtils.existUserProfile(accessTokenFromOutside);
		
		if (!existed) {
			session.setAttribute(OUTSIDE_TOKEN, accessTokenFromOutside);
			session.setAttribute(SOURCE, source);
			session.setAttribute(WIDTH, width);
			session.setAttribute(HEIGHT, height);
			try {
				return Response.seeOther(new URI(Configuration.get3CixtyRoot() + "/tnc.html")).build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return processOutsideToken(app, accessTokenFromOutside, source, width, height, session);
	}
	
	/**
	 * This API is used to check whether or not you agree with 3cixty Terms of Use and Privacy Policy.
	 * <br>
	 * Note that the API cannot be invoked directly from third party developers. The process to show
	 * and call it is performed through {@link auth} or {@link redirect_uri}. The check-box is shown
	 * just after receiving external token and before extracting information which can be found from
	 * the external token.
	 * 
	 * @param terms
	 * @return
	 */
	@POST
	@Path("/terms")
	public Response terms(@FormParam("terms") String terms) {
		if (!"on".equalsIgnoreCase(terms)) return Response.status(400).entity("Invalid request").build();

		HttpSession session = httpRequest.getSession();
		String accessTokenFromOutside = (String) session.getAttribute(OUTSIDE_TOKEN);
		String source = (String) session.getAttribute(SOURCE);
		int width = (Integer) session.getAttribute(WIDTH);
		int height = (Integer) session.getAttribute(HEIGHT);
		if (accessTokenFromOutside == null || source == null)
			return Response.status(400).entity("Invalid request").build();
		AppCache app = (AppCache) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		return processOutsideToken(app, accessTokenFromOutside, source, width, height, session);
	}

	/**
	 * This API is used to send information about 3cixty access token to 3cixty applications.
	 * <br>
	 * The API also persists 3cixty access token generated by OAuth server to 3cixty database.
	 * <br>
	 * Note that the API cannot directly be called from third party developers.
	 *
	 * @param accessToken
	 * @param refreshToken
	 * @param expires_in
	 * @param scope
	 * @return
	 */
	@GET
	@Path("/redirect_uri_client")
	public Response redirect_uri_client(@QueryParam("access_token") String accessToken,
			@QueryParam("refresh_token") String refreshToken,
			@QueryParam("expires_in") int expires_in, @QueryParam("scope") String scope) {
		HttpSession session = httpRequest.getSession();
		AppCache app = (AppCache) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String uid = (String) session.getAttribute(UID_KEY);
		// scope can be a 'null' string as its result is found in 3cixtycallback
		if (!OAuthWrappers.storeAccessTokenWithUID(uid, accessToken, refreshToken, scope, app, expires_in) || uid == null) {
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
	
	/**
	 * This API is used to refresh 3cixty token.
	 * <br>
	 * A given <code>refresh_token</code> is a one-time-use token. So, the <code>refresh_token</code>
	 * will be invalid just after using it.
	 * <br>
	 * The API also persists the new 3cixty token generated by OAuth server from a given <code>refresh_token</code>
	 * into database.
	 * <br>
	 * Note that for each 3cixty access token, there is only one corresponding refresh_token.
	 * 
	 * @param refresh_token
	 * @return
	 */
	@GET
	@Path("/token")
	public Response token(@HeaderParam("refresh_token") String refresh_token) {
		AccessToken refreshedToken = OAuthWrappers.refreshAccessToken(refresh_token);
		if (refreshedToken == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"refresh_token is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		return Response.status(Response.Status.OK).entity(
				JSONObject.wrap(refreshedToken).toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * This API is used to revoke a given 3cixty access token.
	 * <br>
	 * Note that invoking the API means deleting the given 3cixty token from database.
	 *
	 * @param access_token
	 * @return
	 */
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
	
	/**
	 * This method uses <code>redirect_uri</code> from application key to send back 3cixty access token to the application.
	 *
	 * @param accessToken
	 * @param expires_in
	 * @param app
	 * @return
	 */
	private Response redirect_uri_client2(AccessToken accessToken, int expires_in, AppCache app) {
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
	
	/**
	 * This method is used to create a new 3cixty access token, then persist it into database.
	 *
	 * @param _3cixtyUID
	 * @param app
	 * @param scope
	 * @return
	 */
	public static Response getAccessTokenFromUid(String _3cixtyUID, AppCache app, String scope) {
		if (!checkValidScope(scope)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Scope is invalid\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		long startTime = System.currentTimeMillis();
		AccessToken accessToken = OAuthWrappers.createAccessTokenForMobileApp(app, scope);
		long endTime = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Time to create access token on OAuth server: " + (endTime - startTime) + " ms");
		if (accessToken == null) {
			return Response.status(Response.Status.BAD_REQUEST)
	        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		// scope can be a 'null' string as its result is found in 3cixtycallback
		if (!OAuthWrappers.storeAccessTokenWithUID(_3cixtyUID, accessToken.getAccess_token(), accessToken.getRefresh_token(), scope, app, accessToken.getExpires_in())) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		return Response.status(Response.Status.OK).entity(
				JSONObject.wrap(accessToken).toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * This method is used to process a given external token including information extraction (firstName, lastName, profileImage, etc.),
	 * and creating a new 3cixty token based on extracted information. Then, the 3cixty token is persisted into database.
	 *
	 * @param app
	 * @param accessTokenFromOutside
	 * @param source
	 * @param width
	 * @param height
	 * @param session
	 * @return
	 */
	private Response processOutsideToken(AppCache app, String accessTokenFromOutside, String source, int width, int height, HttpSession session) {
		String uid = "Google".equals(source) ? GoogleAccountUtils.getUID(accessTokenFromOutside)
				: FaceBookAccountUtils.getUID(accessTokenFromOutside, width, height);
		
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google or Facebook access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		
		session.setAttribute(UID_KEY, uid);
		
		// bypass authorization for 3cixty's apps
		if (AuthorizationBypassManager.getInstance().isFound(app.getAppkey())) {
			AccessToken at = OAuthWrappers.createAccessTokenForMobileApp(app, SCOPES);
			if (at != null) {
				if (OAuthWrappers.storeAccessTokenWithUID(uid, at.getAccess_token(), at.getRefresh_token(), SCOPES, app, at.getExpires_in())) {
					return redirect_uri_client2(at, at.getExpires_in(), app);
				}
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
					" {\"response\": \"failed\" } ").type(MediaType.APPLICATION_JSON_TYPE).build();
		}
		
		try {
			
			return Response.temporaryRedirect(new URI(
					OAuthWrappers.ENDPOINT_AUTHORIZATION + "?response_type=token&scope="
			+ SCOPES + "&client_id="
			+ app.getAppClientKey() + "&redirect_uri="
		    + THREECIXTY_CALLBACK)).header(OAuthWrappers.AUTHORIZATION,
		    		OAuthWrappers.getBasicAuth(app.getAppClientKey(), app.getAppClientPwd()))
		    		.header("Access-Control-Allow-Origin", "*")
                    .cacheControl(cacheControlNoStore())
                    .header("Pragma", "no-cache")
		    		.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String join(List<String> scopeNames, String strJoined) {
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

//	private String join(Set<Scope> scopes) {
//		StringBuilder builder = new StringBuilder();
//		for (Scope scope: scopes) {
//			if (builder.length() == 0) builder.append(scope.getScopeName());
//			else {
//				builder.append(',').append(scope.getScopeName());
//			}
//		}
//		return builder.toString();
//	}

	protected boolean validId(String id) {
		if (id.matches(ID_PATTERN)) {
			return true;
		}
		return false;
	}
	
	private static boolean checkValidScope(String scope) {
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
	
	public static CacheControl cacheControlNoStore() {
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
		@SuppressWarnings("unused")
		private String scopeName;
		@SuppressWarnings("unused")
		private String description;
	}
}

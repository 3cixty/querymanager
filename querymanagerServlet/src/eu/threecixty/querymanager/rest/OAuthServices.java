package eu.threecixty.querymanager.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
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
	public Response validateAccessToken(@HeaderParam("accessToken") String accessToken,
			@HeaderParam("key") String key) {
		if (OAuthWrappers.validateAppKey(key)) {
			if (OAuthWrappers.validateUserAccessToken(accessToken)) {
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

//	@GET
//	@Path("/getAccessToken")
//	public Response getAccessToken(@QueryParam("google_access_token") String g_access_token, @HeaderParam("key") String appkey) {
//		String uid = GoogleAccountUtils.getUID(g_access_token);
//		if (uid == null || uid.equals(""))
//			return Response.status(Response.Status.BAD_REQUEST)
//		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
//		        .type(MediaType.APPLICATION_JSON_TYPE)
//		        .build();
//		String accessToken = OAuthWrappers.getAccessToken(uid, appkey);
//		if (accessToken != null && !accessToken.equals("")) {
//			return Response.status(Response.Status.OK)
//	        .entity(" {\"accessToken\": \"" + accessToken + "\"} ")
//	        .type(MediaType.APPLICATION_JSON_TYPE)
//	        .build();
//		}
//		return Response.status(Response.Status.BAD_REQUEST)
//		        .entity(" {\"response\": \"failed\"} ")
//		        .type(MediaType.APPLICATION_JSON_TYPE)
//		        .build();
//	}

	@GET
	@Path("/getAppKey")
	public Response getAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid,
			@QueryParam("appname") String appname,
			@DefaultValue("") @QueryParam("description") String desc, @QueryParam("category") String cat,
			@QueryParam("scopeName") String scopeName,
			@QueryParam("redirect_uri") String redirect_uri,
			@QueryParam("thumbNailUrl") String thumbNailUrl) {
		//thumbNailUrl
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String appKey = OAuthWrappers.getAppKey(appid, appname, desc, cat, uid, scopeName, redirect_uri, thumbNailUrl);
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

	@GET
	@Path("/updateAppKey")
	public Response updateAppKey(@QueryParam("key") String key, 
			@DefaultValue("") @QueryParam("description") String desc, @DefaultValue("") @QueryParam("category") String cat,
			@DefaultValue("") @QueryParam("scopeName") String scopeName, @DefaultValue("") @QueryParam("redirect_uri") String redirect_uri) {
		App app = OAuthWrappers.retrieveApp(key);
		if (app == null)
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		boolean ok = OAuthWrappers.updateAppKey(app, desc, cat, scopeName, redirect_uri);
		if (ok) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"successful\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"scope name is invalid\"} ")
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

	@POST
	@Path("/addScope")
	public Response addScope(@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("description") String desc, @QueryParam("scopeName") String scopeName, @QueryParam("scopeLevel") int scopeLevel) {
		if (!checkUserForScope(username, password))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"username or password is incorrect\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		boolean ok = OAuthWrappers.addScope(scopeName, desc, scopeLevel);
		if (ok) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"successful\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"scope name or scope level already existed\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@POST
	@Path("/deleteScope")
	public Response deleteScope(@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("scopeName") String scopeName) {
		if (!checkUserForScope(username, password))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"username or password is incorrect\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		boolean ok = OAuthWrappers.deleteScope(scopeName);
		if (ok) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"successful\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"scope name doesn't exist\"} ")
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
			+ app.getScope().getScopeName() + "&client_id="
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
			@QueryParam("expires_in") int expires_in) {
		HttpSession session = httpRequest.getSession();
		App app = (App) session.getAttribute(APP_KEY);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Session is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String uid = (String) session.getAttribute(UID_KEY);
		if (!OAuthWrappers.storeAccessTokenWithUID(uid, accessToken, refreshToken, app)) {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(" {\"response\": \"failed\", \"reason\": \"Internal errors\"} ")
			        .type(MediaType.APPLICATION_JSON_TYPE)
			        .build();
		}
		AccessToken infoToken = OAuthWrappers.findAccessToken(uid, app);
		return redirect_uri_client2(infoToken, expires_in, app);
	}
	
	// TODO
	@GET
	@Path("/token")
	public Response token(@QueryParam("refresh_token") String refresh_token) {
		AccessToken refreshedToken = OAuthWrappers.refreshAccessToken(refresh_token);
		if (refreshedToken == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"refresh_token is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();

		Gson gson = new Gson();
		
		return Response.status(Response.Status.OK).entity(
				gson.toJson(refreshedToken)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	private Response redirect_uri_client2(AccessToken accessToken, int expires_in, App app) {
		try {
			
			return Response.temporaryRedirect(new URI(app.getRedirectUri()
					+ "#access_token=" + accessToken.getAccess_token()
					+ "&refresh_token=" + accessToken.getRefresh_token()
					+ "&expires_in=" + expires_in)).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkUserForScope(String username, String password) {
		// TODO: FIXME 
		return ("3cixty".equals(username) && "3cixty".equals(password));
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

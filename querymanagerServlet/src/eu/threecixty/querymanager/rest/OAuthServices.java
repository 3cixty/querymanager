package eu.threecixty.querymanager.rest;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.profile.GoogleAccountUtils;

@Path("/" + Constants.VERSION_2)
public class OAuthServices {

	@GET
	@Path("/validateAccessToken")
	public Response validateAccessToken(@QueryParam("accessToken") String accessToken) {
		if (OAuthWrappers.validateUserAccessToken(accessToken)) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"response\": \"ok\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@GET
	@Path("/getAccessToken")
	public Response getAccessToken(@QueryParam("google_access_token") String g_access_token, @HeaderParam("key") String appkey) {
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String accessToken = OAuthWrappers.getAccessToken(uid, appkey);
		if (accessToken != null && !accessToken.equals("")) {
			return Response.status(Response.Status.OK)
	        .entity(" {\"accessToken\": \"" + accessToken + "\"} ")
	        .type(MediaType.APPLICATION_JSON_TYPE)
	        .build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
	}

	@GET
	@Path("/getAppKey")
	public Response getAppKey(@QueryParam("google_access_token") String g_access_token, @QueryParam("appid") String appid,
			@DefaultValue("") @QueryParam("description") String desc, @QueryParam("category") String cat, @QueryParam("scopeName") String scopeName) {
		String uid = GoogleAccountUtils.getUID(g_access_token);
		if (uid == null || uid.equals(""))
			return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"Google access token is invalid or expired\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		String appKey = OAuthWrappers.getAppKey(appid, desc, cat, uid, scopeName);
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
		System.out.println(username);
		System.out.println(password);
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

	private boolean checkUserForScope(String username, String password) {
		// TODO: FIXME 
		return ("3cixty".equals(username) && "3cixty".equals(password));
	}

	private class DeveloperScope {
		
		public DeveloperScope(String scopeName, String desc) {
			this.scopeName = scopeName;
			this.description = desc;
		}
		private String scopeName;
		private String description;
	}
}

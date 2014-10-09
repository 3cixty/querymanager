package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;

@Path("/" + Constants.VERSION_2)
public class GoFlowServices {

	/**
	 * This API checks whether or not a given 3Cixty access_token corresponds with an account at GoFlow server.
	 * If there is no account existed, then the method goes to create one.
	 *
	 * @param access_token
	 * @return
	 */
	@GET
	@Path("/createOrRetrieveGoFlowAccount")
	public Response getAccount(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);

		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			
			String appid = OAuthWrappers.retrieveApp(userAccessToken.getAppkey()).getAppNameSpace();
			// check if there is no account existed at GoFlow server, then go to create an account
			String goflowPwd = GoFlowServer.getInstance().getEndUserPassword(appid, userAccessToken.getUid());
			if (goflowPwd == null) {
				goflowPwd = GoFlowServer.getInstance().createEndUser(appid, userAccessToken.getUid());
			}
			
			if (goflowPwd != null && !goflowPwd.equals("")) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", userAccessToken.getUid());
				jsonObj.put("password", goflowPwd);
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

}

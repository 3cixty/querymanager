package eu.threecixty.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Developer;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.model.User;
import eu.threecixty.oauth.model.UserAccessToken;

public class OAuthWrappers { 
	
	private static final String ROOT_LOCALHOST = "http://localhost:8080/";
	private static final String ROOT_3CIXTY = "http://3cixty.com:8080/";
	
	public static final String ROOT_SERVER = ROOT_LOCALHOST;
	
	private static final String OAUTH_SERVER_CONTEXT_NAME = "apis-authorization-server-war-1.3.5";

	public static final String ENDPOINT_AUTHORIZATION = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/authorize";
	
	public static final String ENDPOINT_TO_POST_ACCESS_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/token";
	private static final String ENDPOINT_TO_VALIDATE_ACCESS_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/v1/tokeninfo?access_token=";
	
	private static final String ENDPOINT_TO_CREATE_CLIENT_FOR_APP = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/3cixty/createClientIdForApp";
	
	private static final String ENDPOINT_TO_CREATE_CLIENT_FOR_ASKING_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/3cixty/createClientForAskingAccessToken";
	
	private static final String ACCES_TOKEN_KEY = "access_token";
	
	public static final String AUTHORIZATION = "Authorization";

	// TODO: client id and client secret to communicate with OAuth server
	// make sure that this user exists in the database (the client table)
	private static final String clientId = "cool_app_id1";
	private static final String clientSecret = "secret2";
	private static final String CLIENT_REDIRECT_URI = "http://localhost:8080/v2/3cixtycallback.jsp";
	
	private static boolean firstTimeForClientCoolApp = false;

	// TODO: resourceServer Name and secret to communicate with OAuth server
	// make sure that this user exists in the database (the resourceserver table)
	private static final String resourceServerKey = "3cixty-res-admin";
	private static final String resourceServerSecret = "(3cixty)InMiLano!+-:";
	
	/**
	 * Gets user access token.
	 * <br>
	 * When a given user does not have his access token, the method asks OAuth server to give him one ;
	 * otherwise, the method picks that information from 3cixty database.
	 * @param uid
	 * @param appkey
	 * @return
	 */
	public static String getAccessToken(String uid, String appkey) {
		User user = OAuthModelsUtils.getUser(uid);
		if (user == null) {
			// create user in database to map with access tokens created by oauth server
			if (!OAuthModelsUtils.addUser(uid)) return null;
			user = OAuthModelsUtils.getUser(uid);
			if (user == null) return null;
		}
		App app = OAuthModelsUtils.getApp(appkey);
		if (app == null) return null;
		UserAccessToken tmpUserAccessToken = null;
		for (UserAccessToken userAccessToken: user.getUserAccessTokens()) {
			if (userAccessToken.getApp().getId().intValue() == app.getId().intValue()) { // found in DB
				// check if this access token is still available on oauth server to delete
				if (!validateAccessToken(userAccessToken.getAccessToken())) {
					tmpUserAccessToken = userAccessToken;
				} else {
				    return userAccessToken.getAccessToken();
				}
			}
		}
		// delete UserAccessToken as this access token is not available on OAuth server
		if (tmpUserAccessToken != null) {
			OAuthModelsUtils.deleteUserAccessToken(tmpUserAccessToken);
		}
		// create a new access token
		String accessToken = createAccessTokenUsingOAuthServer();
		if (storeAccessTokenWithUID(uid, accessToken, app)) return accessToken;
		return null;
	}

	public static String findAccessToken(String uid, String appkey) {
		User user = OAuthModelsUtils.getUser(uid);
		if (user == null) {
			// create user in database to map with access tokens created by oauth server
			if (!OAuthModelsUtils.addUser(uid)) return null;
			user = OAuthModelsUtils.getUser(uid);
			if (user == null) return null;
		}
		App app = OAuthModelsUtils.getApp(appkey);
		if (app == null) return null;
		UserAccessToken tmpUserAccessToken = null;
		for (UserAccessToken userAccessToken: user.getUserAccessTokens()) {
			if (userAccessToken.getApp().getId().intValue() == app.getId().intValue()) { // found in DB
				// check if this access token is still available on oauth server to delete
				if (!validateAccessToken(userAccessToken.getAccessToken())) {
					tmpUserAccessToken = userAccessToken;
				} else {
				    return userAccessToken.getAccessToken();
				}
			}
		}
		// delete UserAccessToken as this access token is not available on OAuth server
		if (tmpUserAccessToken != null) {
			OAuthModelsUtils.deleteUserAccessToken(tmpUserAccessToken);
		}
		return null;
	}

	public static boolean storeAccessTokenWithUID(String uid, String accessToken, App app) {
		User user = OAuthModelsUtils.getUser(uid);
		if (user == null) {
			// create user in database to map with access tokens created by oauth server
			if (!OAuthModelsUtils.addUser(uid)) return false;
			user = OAuthModelsUtils.getUser(uid);
			if (user == null) return false;
		}
		if (accessToken == null || accessToken.equals("")) return false;
		return OAuthModelsUtils.addUserAccessToken(accessToken, user, app);
	}
	
	public static UserAccessToken retrieveUserAccessToken(String accessToken) {
		return OAuthModelsUtils.retrieveUserAccessToken(accessToken);
	}
	
	/**
	 * Add scope.
	 * @param scopeName
	 * @param description
	 * @param scopeLevel
	 * @return
	 */
	public static boolean addScope(String scopeName, String description, int scopeLevel) {
		return OAuthModelsUtils.addScope(scopeName, description, scopeLevel);
	}
	
	/**
	 * Deletes scope from a given scope name
	 * @param scopeName
	 * @return
	 */
	public static boolean deleteScope(String scopeName) {
		return OAuthModelsUtils.deleteScope(scopeName);
	}
	
	/**
	 * Gets all the scopes.
	 * @return
	 */
	public static List <Scope> getScopes() {
		return OAuthModelsUtils.listScopes();
	}

	/**
	 * Note that a developer can have more than one app key.
	 * @param appId
	 * @param description
	 * @param category
	 * @param uid
	 * @return
	 */
	public static String getAppKey(String appId, String appName, String description,
			String category, String uid, String scopeName, String redirect_uri, String thumbNailUrl) {
		String tmpAppId = (appId == null) ? "" : appId;
		String tmpCategory = (category == null) ? "" : category;
		if (uid == null) return null;
		Developer developer = OAuthModelsUtils.getDeveloper(uid);
		if (developer == null) {
			if (!OAuthModelsUtils.addDeveloper(uid)) return null;
			developer = OAuthModelsUtils.getDeveloper(uid);
			if (developer == null) return null;
		}
		Scope scope = OAuthModelsUtils.getScope(scopeName);
		if (scope == null) return null;
		String appkey = createAccessTokenUsingOAuthServer();
		if (appkey == null || appkey.equals("")) return null;
		String clientId = tmpAppId + System.currentTimeMillis();
		boolean ok = OAuthModelsUtils.addApp(appkey, tmpAppId, appName, clientId, description, tmpCategory,
				developer, scope, redirect_uri);
		if (!ok) return null;
		// create clientId in the client table
		if (!createClientIdForApp(clientId, appName, scopeName, thumbNailUrl)) {
			App app = OAuthModelsUtils.getApp(appkey);
			OAuthModelsUtils.deleteApp(app);
			return null;
		}
		return appkey;
	}

	public static boolean updateAppKey(App app,  String description,
			String category, String scopeName, String redirect_uri) {
		if (description != null && !description.equals("")) {
			app.setDescription(description);
		}
		if (category != null && !category.equals("")) {
			app.setCategory(category);
		}
		if (scopeName != null && !scopeName.equals("")) {
			Scope scope = OAuthModelsUtils.getScope(scopeName);
			if (scope == null) return false;
			app.setScope(scope);
		}
		if (redirect_uri != null && !redirect_uri.equals("")) {
			app.setRedirectUri(redirect_uri);
		}
		return OAuthModelsUtils.updateApp(app);
	}

	/**
	 * Retrieves app key from a given UID and appID.
	 * @param appid
	 * @param uid
	 * @return
	 */
	public static String retrieveAppKey(String appid, String uid) {
		App app = OAuthModelsUtils.retrieveApp(uid, appid);
		return app == null ? null : app.getKey();
	}

	/**
	 * Retrieves app from a given UID and appID.
	 * @param appkey
	 * @return
	 */
	public static App retrieveApp(String appkey) {
		return OAuthModelsUtils.getApp(appkey);
	}

	/**
	 * Validates a given app key.
	 * <br>
	 * The method checks whether or not the app key is found in the App table.
	 * If found, the method continues checking the app key by using OAuth server ;
	 * otherwise, the method returns false.
	 * @param appKey
	 * @return
	 */
	public static boolean validateAppKey(String appKey) {
		if (appKey == null || appKey.equals("")) return false;
		if (!OAuthModelsUtils.existApp(appKey)) return false;
		return validateAccessToken(appKey);
	}

	/**
	 * Validates a given user access token.
	 * @param accessToken
	 * @return
	 */
	public static boolean validateUserAccessToken(String accessToken) {
		if (accessToken == null || accessToken.equals("")) return false;
		return validateAccessToken(accessToken);
	}

	public static String getBasicAuth() {
		if (firstTimeForClientCoolApp) {
			try {
				Client client = Client.create();

				String auth = "Basic ".concat(new String(Base64.encodeBase64(resourceServerKey.concat(":")
						.concat(resourceServerSecret).getBytes())));
				Builder builder = client.resource(ENDPOINT_TO_CREATE_CLIENT_FOR_ASKING_TOKEN
						+ "?clientId=" + clientId
						+ "&clientSecret=" + URLEncoder.encode(clientSecret, "UTF-8")
						+ "&scope=Read&redirect_uri=" + URLEncoder.encode(CLIENT_REDIRECT_URI, "UTF-8"))
						.header(AUTHORIZATION, auth)
						.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
				ClientResponse clientResponse = builder.get(ClientResponse.class);

				String jsonStr = IOUtils.toString(clientResponse.getEntityInputStream());
				JSONObject jsonObj = new JSONObject(jsonStr);
				if (jsonObj.has("response")) {
					if (jsonObj.getString("response").equals("successful")) {
						firstTimeForClientCoolApp = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String basicAuth = "Basic ".concat(new String(Base64.encodeBase64(clientId.concat(":")
				.concat(clientSecret).getBytes())));
		return basicAuth;
	}

	/**
	 * Validates a given access token with OAuth server.
	 * @param accessToken
	 * @return
	 */
	private static boolean validateAccessToken(String accessToken) {
		Client client = Client.create();

	    String auth = "Basic ".concat(new String(Base64.encodeBase64(resourceServerKey.concat(":")
	            .concat(resourceServerSecret).getBytes())));
	    Builder builder = client.resource(ENDPOINT_TO_VALIDATE_ACCESS_TOKEN + accessToken).header(AUTHORIZATION, auth)
	            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
	    ClientResponse clientResponse = builder.get(ClientResponse.class);
	    try {
			String jsonStr = IOUtils.toString(clientResponse.getEntityInputStream());
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has("expires_in")) {
				if (jsonObj.getInt("expires_in") == 0) return true; // lifetime access token
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static String createAccessTokenUsingOAuthServer() {
		Client client = Client.create();
	    MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
	    formData.add("grant_type", "client_credentials");

	    String auth = getBasicAuth();
	    Builder builder = client.resource(ENDPOINT_TO_POST_ACCESS_TOKEN).header(AUTHORIZATION, auth)
	            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
	    ClientResponse clientResponse = builder.post(ClientResponse.class, formData);
	    try {
			String jsonStr = IOUtils.toString(clientResponse.getEntityInputStream());
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has(ACCES_TOKEN_KEY)) {
				return jsonObj.getString(ACCES_TOKEN_KEY);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean createClientIdForApp(String clientId,
			String app_name, String scopeName, String thumbNailUrl) {
		Client client = Client.create();
	    Builder builder;
		try {
			builder = client.resource(ENDPOINT_TO_CREATE_CLIENT_FOR_APP + "?clientId=" + clientId
					+ "&app_name=" + URLEncoder.encode(app_name, "UTF-8")
					+ "&scope=" + URLEncoder.encode(scopeName, "UTF-8")
					+ "&thumbNailUrl=" + URLEncoder.encode(thumbNailUrl, "UTF-8"))
					.header(AUTHORIZATION, getBasicAuth());
			
	        ClientResponse clientResponse = builder.get(ClientResponse.class);

			String jsonStr = IOUtils.toString(clientResponse.getEntityInputStream());
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has("response")) {
				String res = jsonObj.getString("response");
				return res.equalsIgnoreCase("successful");
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private OAuthWrappers() {
	}
}

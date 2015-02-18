package eu.threecixty.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.Configuration;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Developer;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.model.User;
import eu.threecixty.oauth.utils.ResourceServerUtils;
import eu.threecixty.oauth.utils.ScopeUtils;

public class OAuthWrappers { 
	
	private static final String ROOT_LOCALHOST = "http://localhost:8080/";
	//private static final String ROOT_3CIXTY = "http://dev.3cixty.com:8080/";
	
	private static final String ROOT_SERVER = ROOT_LOCALHOST;
	
	private static final String OAUTH_SERVER_CONTEXT_NAME = "apis-authorization-server-war-1.3.5";

	//public static final String ENDPOINT_AUTHORIZATION = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/authorize";
	public static final String ENDPOINT_AUTHORIZATION = 
			Configuration.getHttpServer() + "/" + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/authorize";
	
	public static final String ENDPOINT_TO_POST_ACCESS_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/token";
	private static final String ENDPOINT_TO_VALIDATE_ACCESS_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/v1/tokeninfo?access_token=";
	
	private static final String ENDPOINT_TO_CREATE_CLIENT_FOR_APP = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/3cixty/createClientIdForApp";
	
	private static final String ENDPOINT_TO_CREATE_CLIENT_FOR_ASKING_TOKEN = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/3cixty/createClientForAskingAccessToken";
	
	private static final String ACCES_TOKEN_KEY = "access_token";
	
	public static final String AUTHORIZATION = "Authorization";

	// TODO: client id and client secret to communicate with OAuth server
	// make sure that this user exists in the database (the client table)
	private static final String clientId = "cool_app_id2";
	private static final String clientSecret = "secret*+-!S3";
	private static final String CLIENT_REDIRECT_URI = Configuration.get3CixtyRoot() + "/3cixtycallback.jsp";
	
	private static boolean firstTimeForClientCoolApp = true;

	private static final String resourceServerKey = ResourceServerUtils.getResourceServerKey();
	private static final String resourceServerSecret = ResourceServerUtils.getResourceServerSecret();

	private static final String ENDPOINT_TO_UPDATE_CLIENT_FOR_APP = ROOT_SERVER + OAUTH_SERVER_CONTEXT_NAME + "/oauth2/3cixty/updateClientIdForApp";
	
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	
//	/**
//	 * Gets user access token.
//	 * <br>
//	 * When a given user does not have his access token, the method asks OAuth server to give him one ;
//	 * otherwise, the method picks that information from 3cixty database.
//	 * @param uid
//	 * @param appkey
//	 * @return
//	 */
//	public static String getAccessToken(String uid, String appkey) {
//		User user = OAuthModelsUtils.getUser(uid);
//		if (user == null) {
//			// create user in database to map with access tokens created by oauth server
//			if (!OAuthModelsUtils.addUser(uid)) return null;
//			user = OAuthModelsUtils.getUser(uid);
//			if (user == null) return null;
//		}
//		App app = OAuthModelsUtils.getApp(appkey);
//		if (app == null) return null;
//		UserAccessToken tmpUserAccessToken = null;
//		for (UserAccessToken userAccessToken: user.getUserAccessTokens()) {
//			if (userAccessToken.getApp().getId().intValue() == app.getId().intValue()) { // found in DB
//				// check if this access token is still available on oauth server to delete
//				if (!validateAccessToken(userAccessToken.getAccessToken())) {
//					tmpUserAccessToken = userAccessToken;
//				} else {
//				    return userAccessToken.getAccessToken();
//				}
//			}
//		}
//		// delete UserAccessToken as this access token is not available on OAuth server
//		if (tmpUserAccessToken != null) {
//			OAuthModelsUtils.deleteUserAccessToken(tmpUserAccessToken);
//		}
//		// create a new access token
//		String accessToken = createAccessTokenUsingOAuthServer();
//		if (storeAccessTokenWithUID(uid, accessToken, app)) return accessToken;
//		return null;
//	}

	/**
	 * Return access token with info about expiration.
	 * @param uid
	 * @param app
	 * @return
	 */
	public static AccessToken findAccessToken(String uid, App app) {
		User user = OAuthModelsUtils.getUser(uid);
		if (user == null) {
			// create user in database to map with access tokens created by oauth server
			if (!OAuthModelsUtils.addUser(uid)) return null;
			user = OAuthModelsUtils.getUser(uid);
			if (user == null) return null;
		}
		if (app == null) return null;
		AccessToken foundInDB = OAuthModelsUtils.findTokenInfoFromDB(user, app);
		if (foundInDB != null) {
			// check if this access token is still available on oauth server to delete
			AccessToken tokenInfo = tokenInfo(foundInDB.getAccess_token());
			if (tokenInfo != null) {
				// update expires_in
				foundInDB.setExpires_in(tokenInfo.getExpires_in());
			    return foundInDB;
			}
		}
		// delete UserAccessToken as this access token is not available on OAuth server
		if (foundInDB != null) {
			OAuthModelsUtils.deleteUserAccessToken(foundInDB.getAccess_token());
		}
		return null;
	}

	/**
	 * Before calling this method, make sure 'scope' only contains valid "scope". 
	 * @param uid
	 * @param accessToken
	 * @param refreshToken
	 * @param scope
	 * @param app
	 * @return
	 */
	public static boolean storeAccessTokenWithUID(String uid, String accessToken, String refreshToken,
			String scope, App app) {
		User user = OAuthModelsUtils.getUser(uid);
		if (user == null) {
			// create user in database to map with access tokens created by oauth server
			if (!OAuthModelsUtils.addUser(uid)) return false;
			user = OAuthModelsUtils.getUser(uid);
			if (user == null) return false;
		}
		if (accessToken == null || accessToken.equals("")) return false;
		return OAuthModelsUtils.addUserAccessToken(accessToken, refreshToken, scope, user, app);
	}
	
	/**
	 * Add scope.
	 * @param scopeName
	 * @param description
	 * @param scopeLevel
	 * @return
	 */
	public static boolean addScope(String scopeName, String description) {
		return OAuthModelsUtils.addScope(scopeName, description);
	}

	public static void addScopesByDefault() {
		try {
			List <String> scopeNames = ScopeUtils.getScopeNames();
			for (String scopeName: scopeNames) {
				if (!OAuthModelsUtils.existScope(scopeName)) {
		            OAuthModelsUtils.addScope(scopeName, "Description for " + scopeName + " scope");
				}
			}
		} catch (Exception e) {
			
		}
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
			String category, String uid, List<String> scopeNames, String redirect_uri, String thumbNailUrl) {
		String tmpAppId = (appId == null) ? "" : appId;
		String tmpCategory = (category == null) ? "" : category;
		if (uid == null) return null;
		Developer developer = OAuthModelsUtils.getDeveloper(uid);
		if (developer == null) {
			if (!OAuthModelsUtils.addDeveloper(uid)) return null;
			developer = OAuthModelsUtils.getDeveloper(uid);
			if (developer == null) return null;
		}
		String appkey = createAccessTokenUsingOAuthServer();
		if (appkey == null || appkey.equals("")) return null;
		String clientId = tmpAppId + System.currentTimeMillis();
		
		// create clientId in the client table
		String pwd = createClientIdForApp(clientId, appName, scopeNames, thumbNailUrl);
		if (pwd == null) {
			return null;
		}
		
		boolean ok = OAuthModelsUtils.addApp(appkey, tmpAppId, appName, clientId, pwd, description, tmpCategory,
				developer, scopeNames, redirect_uri);
		if (!ok) return null;

		return appkey;
	}

	public static List <App> getApps(String uid) {
		return OAuthModelsUtils.getApps(uid);
	}
	
	public static AccessToken refreshAccessToken(String lastRefreshToken) {
		AccessToken lastAccessToken = OAuthModelsUtils.findTokenInfoFromRefreshToken(lastRefreshToken);
		if (lastAccessToken == null) return null;
		AccessToken newAccessToken = refreshAccessTokenUsingOAuthServer(lastAccessToken);
		if (newAccessToken == null) return null;
		// update user access token as OAuth server already deleted old one
		if (!OAuthModelsUtils.saveOrUpdateUserAccessToken(lastAccessToken, newAccessToken)) return null;
		return newAccessToken;
	}

	public static AccessToken findAccessTokenFromDB(String accessToken) {
		return OAuthModelsUtils.findTokenInfoFromAccessToken(accessToken);
	}
	
	public static boolean revokeAccessToken(String accessToken) {
		if (accessToken == null || accessToken.equals("")) return false;
		return OAuthModelsUtils.deleteUserAccessToken(accessToken);
	}

	public static String findGoogleUIDFrom(String accessToken) {
		if (accessToken == null || accessToken.equals("")) return null;
		return OAuthModelsUtils.findGoogleUIDFromAccessToken(accessToken);
	}

	public static boolean updateAppKey(String uid, String appid, String appname, String description,
			String category, List<String> scopeNames, String redirect_uri, String thumbNailUrl) {
		App app = OAuthModelsUtils.retrieveApp(uid, appid);
		if (app == null) return false;
		boolean ok = updateClientIdForApp(app.getClientId(), appname, scopeNames, thumbNailUrl);
		if (!ok) return false;
		return OAuthModelsUtils.updateApp(uid, appid, appname, description, category, scopeNames, redirect_uri, thumbNailUrl);
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
	
	public static Set <Scope> getScopes(App app) {
		return OAuthModelsUtils.getScopes(app);
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
		return tokenInfo(appKey) != null;
	}

	/**
	 * Validates a given user access token.
	 * @param accessToken
	 * @return
	 */
	public static boolean validateUserAccessToken(String accessToken) {
		if (accessToken == null || accessToken.equals("")) return false;
		if (!OAuthModelsUtils.existUserAccessToken(accessToken)) return false;
		return tokenInfo(accessToken) != null;
	}

	public static String getBasicAuth() {
		if (firstTimeForClientCoolApp) {
			try {
				String auth = "Basic ".concat(new String(Base64.encodeBase64(resourceServerKey.concat(":")
						.concat(resourceServerSecret).getBytes())));
				
				StringBuilder sb = new StringBuilder();
				makeHttpCall(ENDPOINT_TO_CREATE_CLIENT_FOR_ASKING_TOKEN
						+ "?clientId=" + clientId
						+ "&clientSecret=" + URLEncoder.encode(clientSecret, "UTF-8")
						+ "&redirect_uri=" + URLEncoder.encode(CLIENT_REDIRECT_URI, "UTF-8"), auth, HTTP_GET, null, sb);

				String jsonStr = sb.toString();
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
	
	public static String getBasicAuth(String username, String pwd) {
		return "Basic ".concat(new String(Base64.encodeBase64(username.concat(":")
				.concat(pwd).getBytes())));
	}

	/**
	 * Validates a given access token with OAuth server.
	 * <br><br>
	 * This method only returns access token and expires_in.
	 * @param accessToken
	 * @return
	 */
	private static AccessToken tokenInfo(String accessToken) {

	    String auth = "Basic ".concat(new String(Base64.encodeBase64(resourceServerKey.concat(":")
	            .concat(resourceServerSecret).getBytes())));
	    StringBuilder sb = new StringBuilder();
	    makeHttpCall(ENDPOINT_TO_VALIDATE_ACCESS_TOKEN + accessToken, auth, HTTP_GET, null, sb);
	    try {
	    	String jsonStr = sb.toString();
	    	JSONObject jsonObj = new JSONObject(jsonStr);
	    	if (!jsonObj.has("expires_in")) {
	    		return null;
	    	}
	    	long currentTime = Calendar.getInstance().getTimeInMillis();
	    	long expiredTime = jsonObj.getLong("expires_in");
	    	if (expiredTime != 0) { // 0 means infinity
	    		if (currentTime > expiredTime) return null;
	    	}
	    	AccessToken tokenInfo = new AccessToken();
	    	tokenInfo.setExpires_in((int)((expiredTime - currentTime) / 1000));
	    	tokenInfo.setAccess_token(accessToken);
		    return tokenInfo;
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
	
	private static String createAccessTokenUsingOAuthServer() {

	    String postParams = "grant_type=client_credentials";
	    
	    String auth = getBasicAuth();
	    StringBuilder sb = new StringBuilder();
	    makeHttpCall(ENDPOINT_TO_POST_ACCESS_TOKEN, auth, HTTP_POST, postParams, sb);
	    
	    try {
			String jsonStr = sb.toString();
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has(ACCES_TOKEN_KEY)) {
				return jsonObj.getString(ACCES_TOKEN_KEY);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AccessToken createAccessTokenForMobileApp(App app, String scope) {
	    
	    String postParams = "grant_type=client_credentials&scope=" + scope;
	    
		String auth = "Basic ".concat(new String(Base64.encodeBase64(
				app.getClientId().concat(":")
				.concat(app.getPassword()).getBytes())));
		
	    StringBuilder sb = new StringBuilder();
	    makeHttpCall(ENDPOINT_TO_POST_ACCESS_TOKEN, auth, HTTP_POST, postParams, sb);
	    
	    try {
			String jsonStr = sb.toString();
			JSONObject jsonObj = new JSONObject(jsonStr);
			AccessToken newAccessToken = getAccessToken(jsonObj);
			if (newAccessToken == null) return null;
			addScopeNames(scope, newAccessToken.getScopeNames());
			return newAccessToken;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static AccessToken refreshAccessTokenUsingOAuthServer(AccessToken lastAccessToken) {
	    
	    String postParams = "grant_type=refresh_token&refresh_token" + lastAccessToken.getRefresh_token();
		String auth = "Basic ".concat(new String(Base64.encodeBase64(
				lastAccessToken.getAppClientKey().concat(":")
				.concat(lastAccessToken.getAppClientPwd()).getBytes())));
		
	    StringBuilder sb = new StringBuilder();
	    makeHttpCall(ENDPOINT_TO_POST_ACCESS_TOKEN, auth, HTTP_POST, postParams, sb);
	    
	    try {
			String jsonStr = sb.toString();
			JSONObject jsonObj = new JSONObject(jsonStr);
			AccessToken newAccessToken = getAccessToken(jsonObj);
			if (newAccessToken == null) return null;
			// keep the same scopes
			newAccessToken.getScopeNames().addAll(lastAccessToken.getScopeNames());
			return newAccessToken;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Only gets information from access token, expires_in and refresh_token.
	 * @param jsonObj
	 * @return
	 */
	private static AccessToken getAccessToken(JSONObject jsonObj) {
		if (!jsonObj.has(ACCES_TOKEN_KEY)) {
			return null;
		}
		AccessToken accessToken = new AccessToken();
		accessToken.setAccess_token(jsonObj.getString(ACCES_TOKEN_KEY));
		accessToken.setExpires_in(jsonObj.getInt("expires_in"));
		if (jsonObj.has("refresh_token")) {
			accessToken.setRefresh_token(jsonObj.getString("refresh_token"));
		}
		return accessToken;
	}

	private static String createClientIdForApp(String clientId,
			String app_name, List<String> scopeNames, String thumbNailUrl) {
		try {
			
		    StringBuilder sb = new StringBuilder();
		    makeHttpCall(ENDPOINT_TO_CREATE_CLIENT_FOR_APP + "?clientId=" + clientId
					+ "&app_name=" + URLEncoder.encode(app_name, "UTF-8")
					+ "&scope=" + URLEncoder.encode(join(scopeNames), "UTF-8")
					+ "&thumbNailUrl=" + URLEncoder.encode(thumbNailUrl, "UTF-8"),
					getBasicAuth(), HTTP_GET, null, sb);

			String jsonStr = sb.toString();
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has("response")) {
				String res = jsonObj.getString("response");
				if (res.equalsIgnoreCase("successful")) {
					return jsonObj.getString("password");
				}
				return null;
			} else {
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean updateClientIdForApp(String clientId,
			String app_name, List<String> scopeNames, String thumbNailUrl) {
		try {
			String postParams = "clientId=" + clientId
					+ "&app_name=" + URLEncoder.encode(app_name, "UTF-8")
					+ "&scope=" + URLEncoder.encode(join(scopeNames), "UTF-8")
					+ "&thumbNailUrl=" + URLEncoder.encode(thumbNailUrl, "UTF-8");
		    StringBuilder sb = new StringBuilder();
		    makeHttpCall(ENDPOINT_TO_UPDATE_CLIENT_FOR_APP, getBasicAuth(), HTTP_POST, postParams, sb);

			String jsonStr = sb.toString();
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (jsonObj.has("response")) {
				String res = jsonObj.getString("response");
				if (res.equalsIgnoreCase("successful")) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static void makeHttpCall(String urlStr, String authorizationValue, String httpMethod,
			String httpParams, StringBuilder result) {
		OutputStream output = null;
		InputStream input = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(AUTHORIZATION, authorizationValue);
			conn.setRequestMethod(httpMethod);
			if (HTTP_POST.equals(httpMethod) && (httpParams != null)) {
				conn.setDoOutput(true);
				output = conn.getOutputStream();
				output.write(httpParams.getBytes());
			}
			input = conn.getInputStream();
			byte[] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				result.append(new String(b, 0, readBytes));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static void addScopeNames(String scope, List<String> results) {
		if (scope != null && !scope.equalsIgnoreCase("null") && !scope.equals("")) {
			if (scope.indexOf(",") < 0) { // one scope
				results.add(scope);
			} else { // more than one scope
				String [] tmpScopes = scope.split(",");
				for (String tmpScope: tmpScopes) {
					results.add(tmpScope);
				}
			}
		}
	}

	private static String join(List<String> scopeNames) {
		StringBuilder builder = new StringBuilder();
		for (String scopeName: scopeNames) {
			if (builder.length() == 0) builder.append(scopeName);
			else {
				builder.append(',').append(scopeName);
			}
		}
		return builder.toString();
	}
	
	private OAuthWrappers() {
	}
}
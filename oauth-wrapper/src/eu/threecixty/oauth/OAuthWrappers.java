package eu.threecixty.oauth;

import java.io.IOException;

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
import eu.threecixty.oauth.model.User;
import eu.threecixty.oauth.model.UserAccessToken;

public class OAuthWrappers {

	private static final String ENDPOINT_TO_GET_ACCESS_TOKEN = "http://localhost:8080/oauth2/token";
	private static final String ENDPOINT_TO_VALIDATE_ACCESS_TOKEN = "http://localhost:8080/v1/tokeninfo?access_token=";
	
	private static final String ACCES_TOKEN_KEY = "access_token";
	
	private static final String AUTHORIZATION = "Authorization";

	// client id and client secret to communicate with OAuth server
	private static final String clientId = "";
	private static final String clientSecret = "";
	
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
		if (user == null) return null;
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
		if (accessToken == null || accessToken.equals("")) return null;
		boolean ok = OAuthModelsUtils.addUserAccessToken(accessToken, user, app);
		if (ok) return accessToken;
		return null;
	}

	/**
	 * Note that a developer can have more than one app key.
	 * @param title
	 * @param description
	 * @param category
	 * @param developer
	 * @return
	 */
	public static String getAppKey(String title, String description,
			String category, Developer developer) {
		String tmpTitle = (title == null) ? "" : title;
		String tmpCategory = (category == null) ? "" : category;
		if (developer == null) return null;
		String accessToken = createAccessTokenUsingOAuthServer();
		if (accessToken == null || accessToken.equals("")) return null;
		boolean ok = OAuthModelsUtils.addApp(accessToken, tmpTitle, description, tmpCategory, developer);
		if (ok) return accessToken;
		return null;
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
		if (!OAuthModelsUtils.existUserAccessToken(accessToken)) return false;
		return validateAccessToken(accessToken);
	}

	/**
	 * Validates a given access token with OAuth server.
	 * @param accessToken
	 * @return
	 */
	private static boolean validateAccessToken(String accessToken) {
		Client client = Client.create();

	    String auth = "Basic ".concat(new String(Base64.encodeBase64(clientId.concat(":")
	            .concat(clientSecret).getBytes())));
	    Builder builder = client.resource(ENDPOINT_TO_VALIDATE_ACCESS_TOKEN + accessToken).header(AUTHORIZATION, auth)
	            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
	    ClientResponse clientResponse = builder.post(ClientResponse.class);
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

	    String auth = "Basic ".concat(new String(Base64.encodeBase64(clientId.concat(":")
	            .concat(clientSecret).getBytes())));
	    Builder builder = client.resource(ENDPOINT_TO_GET_ACCESS_TOKEN).header(AUTHORIZATION, auth)
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

	private OAuthWrappers() {
	}
}

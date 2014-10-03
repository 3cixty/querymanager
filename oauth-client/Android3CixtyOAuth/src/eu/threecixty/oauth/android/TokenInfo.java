package eu.threecixty.oauth.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is to represent an obtained 3Cixty access token.
 * <br><br>
 * <code>access_token</code> is an access token given by 3Cixty OAuth server.
 * <br>
 * <code>refresh_token</code> is a token to get next access token without asking
 * the end user about permission if the end user doesn't revoke it.
 * <br>
 * <code>expires_in</code> is time in second for the expiration of an access token.
 * @author Cong-Kinh NGUYEN
 *
 */
public class TokenInfo {

	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String REFRESH_TOKEN_KEY = "refresh_token";
	private static final String EXPIRES_IN_KEY = "expires_in";
	private static final String CREATION_TIME_KEY = "creation_time";
	private static final String SCOPES_KEY = "scopeNames";
	
	private String access_token;
	private String refresh_token;
	private int expires_in; // in second
	private List <String> scopes = new ArrayList <String>();
	
	private long creationTime;
	
	
	public static TokenInfo parse(String strJson) throws JSONException {
		JSONObject jsonObj = new JSONObject(strJson);
		TokenInfo tokenInfo = new TokenInfo();
		if (jsonObj.has(ACCESS_TOKEN_KEY)) tokenInfo.setAccess_token(jsonObj.getString(ACCESS_TOKEN_KEY));
		if (jsonObj.has(REFRESH_TOKEN_KEY)) tokenInfo.setRefresh_token(jsonObj.getString(REFRESH_TOKEN_KEY));
		if (jsonObj.has(CREATION_TIME_KEY)) tokenInfo.setCreationTime(jsonObj.getLong(CREATION_TIME_KEY));
		if (jsonObj.has(EXPIRES_IN_KEY)) tokenInfo.setExpires_in(jsonObj.getInt(EXPIRES_IN_KEY));
		if (jsonObj.has(SCOPES_KEY)) {
			JSONArray jsonArrs = jsonObj.getJSONArray(SCOPES_KEY);
			for (int i = 0; i < jsonArrs.length(); i++) {
				tokenInfo.getScopes().add(jsonArrs.getString(i));
			}
		}
		return tokenInfo;
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public List <String> getScopes() {
		return scopes;
	}

	protected long getCreationTime() {
		return creationTime;
	}
	protected void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public long getExpires_inInMillis() {
		return expires_in * 1000;
	}

	public String toJson() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(ACCESS_TOKEN_KEY, access_token);
		jsonObj.put(REFRESH_TOKEN_KEY, refresh_token);
		jsonObj.put(CREATION_TIME_KEY, creationTime);
		jsonObj.put(EXPIRES_IN_KEY, expires_in);
		JSONArray arr = new JSONArray(this.scopes);
		jsonObj.put(SCOPES_KEY, arr);
		
		return jsonObj.toString();
	}
}

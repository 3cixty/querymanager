package eu.threecixty.oauth;

import java.util.ArrayList;
import java.util.List;

public class AccessToken {

	private String access_token;
	private String refresh_token;
	private int expires_in;
	private String token_type = "Bearer";
	private List <String> scopeNames = new ArrayList <String>();
	
	public AccessToken() {
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public List<String> getScopeNames() {
		return scopeNames;
	}

	public void setScopeNames(List<String> scopeNames) {
		this.scopeNames = scopeNames;
	}
}

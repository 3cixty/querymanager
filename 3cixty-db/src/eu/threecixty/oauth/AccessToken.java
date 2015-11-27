package eu.threecixty.oauth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * This class is to create a corresponding class for 3cixty access token information from database.
 * The class is used to store in memory.
 */
public class AccessToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5897771990001680958L;
	private String access_token;
	private String refresh_token;
	private int expires_in = -1;
	private String token_type = "Bearer";
	// This attribute is used to avoid making two queries to DB.
	private String appClientKey;
	private String appClientPwd;
	
	private List <String> scopeNames = new ArrayList <String>();
	
	private String uid; // Google UID
	private String appkey; // App Key
	
	private Integer appkeyId;
	private Boolean used;
	
	private Long creation; // might be null
	
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

	public String getAppClientKey() {
		return appClientKey;
	}

	public void setAppClientKey(String appClientKey) {
		this.appClientKey = appClientKey;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppClientPwd() {
		return appClientPwd;
	}

	public void setAppClientPwd(String appClientPwd) {
		this.appClientPwd = appClientPwd;
	}

	public Integer getAppkeyId() {
		return appkeyId;
	}

	public void setAppkeyId(Integer appkeyId) {
		this.appkeyId = appkeyId;
	}

	public Long getCreation() {
		return creation;
	}

	public void setCreation(Long creation) {
		this.creation = creation;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}
}

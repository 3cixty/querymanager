package eu.threecixty.cache;

import java.io.Serializable;

/**
 * 
 * This class is to create a corresponding class for appkey information from database.
 * The class is used to store in memory.
 *
 */
public class AppCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8640296419076568401L;
	private String appkey;
	private String appClientKey;
	private String appClientPwd;
	
	private String appNameSpace;
	private String description;
	private String appName;
	private String category;
	private String redirectUri;
	
	/**Attribute which is to be returned to developers, but the value where users see in the client table of OAuth server*/
	private String thumbnail;
	private Integer id;
	
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getAppClientKey() {
		return appClientKey;
	}
	public void setAppClientKey(String appClientKey) {
		this.appClientKey = appClientKey;
	}
	public String getAppClientPwd() {
		return appClientPwd;
	}
	public void setAppClientPwd(String appClientPwd) {
		this.appClientPwd = appClientPwd;
	}
	public String getAppNameSpace() {
		return appNameSpace;
	}
	public void setAppNameSpace(String appNameSpace) {
		this.appNameSpace = appNameSpace;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}

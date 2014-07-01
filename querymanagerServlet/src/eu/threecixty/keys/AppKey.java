package eu.threecixty.keys;

import java.io.Serializable;

public class AppKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**For example Exploration, Mobile, AppChallenge*/
	private String appName;
	private String value;
	private KeyOwner owner;


	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public KeyOwner getOwner() {
		return owner;
	}

	public void setOwner(KeyOwner owner) {
		this.owner = owner;
	}
}

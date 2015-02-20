package eu.threecixty.partners;

public class PartnerAccount {
	private String username;
	private String password;
	private String appid; // 3Cixty app id
	
	// for GoFlow
	private String role;
	
	public PartnerAccount(String username, String pwd, String appid, String role) {
		this.username = username;
		this.password = pwd;
		this.appid = appid;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppId() {
		return appid;
	}

	public void setAppId(String appid) {
		this.appid = appid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
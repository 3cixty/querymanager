package eu.threecixty.partners;

import java.util.List;

/**
 * This class is to represent a 3Cixty user with Mobidot account.
 * @author Cong-Kinh NGUYEN
 *
 */
public class PartnerUser {

	private String uid; // Google user_id
	
	private List <PartnerAccount> partnerAccounts;
	
	public PartnerUser(String uid) {
		this.uid = uid;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<PartnerAccount> getAccounts() {
		return partnerAccounts;
	}

	public void setPartnerAccounts(List<PartnerAccount> accounts) {
		this.partnerAccounts = accounts;
	}

	public static class PartnerAccount {
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
	
}

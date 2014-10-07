package eu.threecixty.profile;

import java.util.List;

/**
 * This class is to represent a 3Cixty user with Mobidot account.
 * @author Cong-Kinh NGUYEN
 *
 */
public class MobidotUser {

	private String uid; // Google user_id
	
	private List <MobidotAccount> mobidotAccounts;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<MobidotAccount> getMobidotAccounts() {
		return mobidotAccounts;
	}

	public void setMobidotAccounts(List<MobidotAccount> mobidotAccounts) {
		this.mobidotAccounts = mobidotAccounts;
	}

	public static class MobidotAccount {
		private String username;
		private String password;
		private String appkey; // 3Cixty app key
		
		public MobidotAccount(String username, String pwd, String appkey) {
			this.username = username;
			this.password = pwd;
			this.appkey = appkey;
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

		public String getAppkey() {
			return appkey;
		}

		public void setAppkey(String appkey) {
			this.appkey = appkey;
		}
	}
	
}

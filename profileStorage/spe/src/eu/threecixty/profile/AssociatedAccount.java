package eu.threecixty.profile;

/**
 * 
 * This class is to easily export associated account for user-related information.
 * The class is used in the SPEServices.
 *
 */
public class AssociatedAccount {

	private String accountId;
	private String source;
	private String password;
	private String mobidotUserId;

	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobidotUserId() {
		return mobidotUserId;
	}
	public void setMobidotUserId(String mobidotUserId) {
		this.mobidotUserId = mobidotUserId;
	}
}

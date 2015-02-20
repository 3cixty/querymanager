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
}

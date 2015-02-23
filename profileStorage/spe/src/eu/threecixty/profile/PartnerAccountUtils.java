package eu.threecixty.profile;

import java.util.UUID;

import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.partners.PartnerUser;

public class PartnerAccountUtils {
	protected static final String MOBIDOT_APP_ID = "MobidotAppID";
	
	
	/**
	 * Try to create a Mobidot user if it doesn't exist on Movesmarter server.
	 * @param _3cixty
	 * @param displayName
	 * @return Mobidot's account if existed
	 */
	public static PartnerAccount retrieveOrAddMobidotUser(String _3cixtyUID, String displayName) {
		Partner partner = ProfileManagerImpl.getInstance().getPartner();
    	PartnerUser mobidotUser = partner.getUser(_3cixtyUID);
		PartnerAccount account = partner.findAccount(mobidotUser, MOBIDOT_APP_ID, null);
		
		if (account != null) { // already exist in 3cixty's DB
			return account;
		}
		
		if (mobidotUser == null) {
			mobidotUser = new PartnerUser();
			mobidotUser.setUid(_3cixtyUID);
			partner.addUser(mobidotUser); // persist user in 3cixty's DB
		}
		String password = "3cixtyI$InExpo)!_" + UUID.randomUUID().toString();
		try {
		    String mobidotID = MobidotUserUtils.createMobidotUser(_3cixtyUID, displayName, password);
		    if (mobidotID == null || mobidotID.equals("")) return null;
		    account = new PartnerAccount();
			account.setAppId(MOBIDOT_APP_ID);
			account.setPassword(password);
			account.setUsername(mobidotID);
			account.setRole("User");
			account.setPartnerUser(mobidotUser);
			partner.addAccount(account); // persist account in 3cixty's DB
			return account;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PartnerAccount retrieveOrAddGoflowUser(String _3cixtyUID, String appId) {
		Partner partner = ProfileManagerImpl.getInstance().getPartner();
    	PartnerUser mobidotUser = partner.getUser(_3cixtyUID);
		PartnerAccount account = partner.findAccount(mobidotUser, appId, null);
		
		if (account != null) { // already exist in 3cixty's DB
			return account;
		}
		
		if (mobidotUser == null) {
			mobidotUser = new PartnerUser();
			mobidotUser.setUid(_3cixtyUID);
			partner.addUser(mobidotUser); // persist user in 3cixty's DB
		}
		String password = GoFlowServer.getInstance().createEndUser(appId, _3cixtyUID);
		if (password == null) {
			return null;
		}
		try {
		    account = new PartnerAccount();
			account.setAppId(appId);
			account.setPassword(password);
			account.setUsername(_3cixtyUID);
			account.setRole("User");
			account.setPartnerUser(mobidotUser);
			partner.addAccount(account); // persist account in 3cixty's DB
			return account;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private PartnerAccountUtils() {
	}
}

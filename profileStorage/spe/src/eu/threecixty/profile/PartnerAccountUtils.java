package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.partners.PartnerUser;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

public class PartnerAccountUtils {
	
	protected static void addMobidotID(String _3cixtyUID,String uid, eu.threecixty.profile.oldmodels.Name name, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities) throws InterruptedException {
		//check if partner ID exists in kb, if yes do not create it. it is implied that json has it.
		if (!existID("Mobidot", uid, profileIdentities)){
			// add it to user profile
		    try{
		    	String appid="3cixtyBackend";
		    	String mobidotID=null;
				Partner partner=ProfileManagerImpl.getInstance().getMobidot();
		    	PartnerUser mobidotUser = ProfileManagerImpl.getInstance().getMobidot().getUser(uid);
				PartnerAccount account = ProfileManagerImpl.getInstance().getMobidot().findAccount(mobidotUser, appid, null);
				
				mobidotID=MobidotUserUtils.getMobidotID(uid);
				String password = "3cixtyI$InExpo)!_"+UUID.randomUUID().toString();
				//call movesmarter platform to create users
				if (mobidotID == null) mobidotID=MobidotUserUtils.createMobidotUser(uid,name,password);					
				
				if (account==null) account = new PartnerAccount(uid, password, appid, "User");
				
				boolean ok= setAccountFromUID(uid, account, mobidotUser, partner);
				if (ok) Utils.addProfileIdentities(_3cixtyUID, mobidotID, "Mobidot", profileIdentities);

			}catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	protected static void addGoflowID(String _3cixtyUID, String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities) throws InterruptedException {
		//check if partner ID exists in kb, if yes do not create it it is implied that json has it.
		if (!existID("GoFlow", uid, profileIdentities)){
			// call goflow and add to json file. This also checks if the user exists or not
			// check if there is no account existed at GoFlow server, then go to create an account
			try{
				String appid="3cixtyBackend";
				Partner partner=ProfileManagerImpl.getInstance().getGoFlow();
				PartnerUser goflowUser = ProfileManagerImpl.getInstance().getGoFlow().getUser(uid);
				PartnerAccount account = ProfileManagerImpl.getInstance().getGoFlow().findAccount(goflowUser, appid, null);
				String password=null;
				
				if (account == null) {
					password=GoFlowServer.getInstance().createEndUser(appid, uid);
					if (password!=null) {
						account = new PartnerAccount(uid, password, appid, "User");
					}
				}
				boolean ok=setAccountFromUID(uid, account, goflowUser, partner);
				if (ok) Utils.addProfileIdentities(_3cixtyUID, uid, "GoFlow", profileIdentities);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private static boolean setAccountFromUID(String uid, PartnerAccount account, PartnerUser partnerUser, Partner partner) {
		try {
			
			boolean ok = false;

			if (partnerUser == null) partnerUser = new PartnerUser(uid);
			if (partnerUser.getAccounts() == null) partnerUser.setPartnerAccounts(
					new ArrayList <PartnerAccount>());

			if (account!=null){
				if (account.getPassword()!=null && account.getUsername()!=null && account.getAppId()!=null && account.getRole()!=null){
					if (account.getPassword()!="" && account.getUsername()!="" && account.getAppId()!="" && account.getRole()!=""){
						partnerUser.getAccounts().add(account);
						ok = partner.updateUser(partnerUser);
					}
				}
			}
			if (ok) return true;
			
			return false;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private static boolean existID(String partnerName, String uid, Set<ProfileIdentities> profileIdentities) {
		if (uid == null) return false;
		boolean found=false;
		for (ProfileIdentities pi: profileIdentities) {
			if (uid.equals(pi.getHasUserAccountID()) && partnerName.equals(pi.getHasSourceCarrier())) {
				found = true;
				break;
			}
		}
		if (found) return true;
		else return false;
	}
}

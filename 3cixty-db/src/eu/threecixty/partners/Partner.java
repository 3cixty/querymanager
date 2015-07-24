package eu.threecixty.partners;

import java.util.List;


public interface Partner {

	boolean addUser(PartnerUser user);
	boolean updateUser(PartnerUser user);
	boolean deleteUser(PartnerUser user);
	boolean exist(String uid);
	PartnerUser getUser(String uid);
	
	PartnerAccount findAccount(PartnerUser user, String appid, String role);
	
	List <PartnerAccount> getPartnerAccounts(String uid);
	
	boolean addAccount(PartnerAccount account);
}

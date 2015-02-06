package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.PartnerUser.PartnerAccount;

public interface Partner {

	boolean addUser(PartnerUser user);
	boolean updateUser(PartnerUser user);
	boolean deleteUser(PartnerUser user);
	boolean exist(String uid);
	PartnerUser getUser(String uid);
	List <PartnerUser> getUsers();
	
	PartnerAccount findAccount(PartnerUser user, String appid, String role);
}

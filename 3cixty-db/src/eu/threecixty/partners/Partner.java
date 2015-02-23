package eu.threecixty.partners;


public interface Partner {

	boolean addUser(PartnerUser user);
	boolean updateUser(PartnerUser user);
	boolean deleteUser(PartnerUser user);
	boolean exist(String uid);
	PartnerUser getUser(String uid);
	
	PartnerAccount findAccount(PartnerUser user, String appid, String role);
	
	boolean addAccount(PartnerAccount account);
}

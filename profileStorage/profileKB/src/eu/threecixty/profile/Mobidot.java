package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.MobidotUser.MobidotAccount;

public interface Mobidot {

	boolean addUser(MobidotUser user);
	boolean updateUser(MobidotUser user);
	boolean deleteUser(MobidotUser user);
	boolean exist(String uid);
	MobidotUser getUser(String uid);
	List <MobidotUser> getUsers();
	
	MobidotAccount findAccount(MobidotUser user, String appkey);
}

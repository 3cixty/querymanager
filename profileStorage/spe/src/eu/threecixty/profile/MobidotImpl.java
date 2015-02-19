package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerUser;
import eu.threecixty.partners.PartnerUser.PartnerAccount;



/**
 * This class is to store all the 3Cixty's mobidot users in a file. 
 * @author Cong-Kinh NGUYEN
 *
 */
public class MobidotImpl implements Partner {
	
	private static final String MOBIDOT_FILE = "mobidot.json";
	
	private static String path;
	
	private Partner partnerIntf;

	public static Partner getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static void setPath(String path) {
		MobidotImpl.path = path;
	}

	@Override
	public boolean addUser(PartnerUser user) {
		return partnerIntf.addUser(user);
	}

	@Override
	public boolean updateUser(PartnerUser user) {
		return partnerIntf.updateUser(user);
	}

	@Override
	public boolean deleteUser(PartnerUser user) {
		return partnerIntf.deleteUser(user);
	}

	@Override
	public boolean exist(String uid) {
		return partnerIntf.exist(uid);
	}

	@Override
	public PartnerUser getUser(String uid) {
		return partnerIntf.getUser(uid);
	}

	@Override
	public List<PartnerUser> getUsers() {
		return partnerIntf.getUsers();
	}

	@Override
	public PartnerAccount findAccount(PartnerUser user, String appkey, String role) {
		return partnerIntf.findAccount(user, appkey, role);
	}

	private MobidotImpl() {
		partnerIntf = new PartnerImpl(path, MOBIDOT_FILE);
	}
	
	/**Singleton holder*/
	private static class SingletonHolder {
		private static final Partner INSTANCE = new MobidotImpl();
	}
}

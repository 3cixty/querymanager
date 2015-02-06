package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.PartnerUser.PartnerAccount;

public class GoFlowImpl implements Partner {

	private static final String GOFLOW_FILE = "goflow.json";
	
	private static String path;
	
	private Partner partnerIntf;

	public static Partner getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static void setPath(String path) {
		GoFlowImpl.path = path;
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

	private GoFlowImpl() {
		partnerIntf = new PartnerImpl(path, GOFLOW_FILE);
	}
	
	/**Singleton holder*/
	private static class SingletonHolder {
		private static final Partner INSTANCE = new GoFlowImpl();
	}
}

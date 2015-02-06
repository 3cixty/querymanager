package eu.threecixty.profile;

import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.authen.simple.SimpleSessionManager;
import org.theresis.humanization.datastorage.ProfileManagerFactory;
import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;

public class ThalesProfileManagerAndSession {
	
	private String propertyFilePath;

	private String uid;
	protected org.theresis.humanization.datastorage.ProfileManager profileMgr;
	protected Session session;

	public ThalesProfileManagerAndSession(String uid) {
		this.uid = uid;

		ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
		try {
			profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			String username = getUsername();
			String pwd = getPassword();
			session = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, username, pwd, null) );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * TODO:
	 * @return
	 */
	private String getUsername() {
		return uid;
	}
	
	/**
	 * TODO:
	 * @return
	 */
	private String getPassword() {
		return uid;
	}
}

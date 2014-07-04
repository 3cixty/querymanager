package eu.threecixty.keys.management;

/**
 * This class is used to check whether or not a user can access to key management page.
 * @author Cong-Kinh Nguyen
 *
 */
public class AuthenticationManager {

	private static final Object _sync = new Object();

	private static AuthenticationManager singleton;

	public static AuthenticationManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new AuthenticationManager();
			}
		}
		return singleton;
	}

	public boolean hasPermission(String username, String password) {
		return false;
	}
	
	private AuthenticationManager() {
	}
}

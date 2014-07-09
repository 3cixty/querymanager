package eu.threecixty.keys.management;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * This class is used to check whether or not a user can access to key management page.
 * @author Cong-Kinh Nguyen
 *
 */
public class AuthenticationManager {

	private static final Object _sync = new Object();

	private static AuthenticationManager singleton;
	public static final String ADMIN_USER = "admin";
	private static final String ADMIN_PASS = "admin(3Cixty)";

	public static AuthenticationManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new AuthenticationManager();
			}
		}
		return singleton;
	}

	/**
	 * Checks whether or not a given username & password are associated with ADMIN.
	 * @param username
	 * @param password
	 * @return true | false
	 */
	public boolean isAdmin(String username, String password) {
		return ADMIN_USER.equals(username) && ADMIN_PASS.equals(password);
	}

	/**
	 * Create a user who can manage AppKey.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean createAppKeyAdmin(String username, String password, String firstName, String lastName) {
		if (isNullOrEmpty(username) || isNullOrEmpty(password)) return false;
		String encodedpwd = encode(password);
		// false means username exists or there is a problem for storing data
		return AppKeyAdminStorage.createAndSave(username, encodedpwd, firstName, lastName);
	}
	
	/**
	 * Set password to a given user. This should be called by admin.
	 * @param username
	 * @param newPwd
	 * @return
	 */
	public boolean setPassword(String username, String newPwd) {
		if (isNullOrEmpty(username) || isNullOrEmpty(newPwd)) return false;
		String encodedpwd = encode(newPwd);
		// false means username exists or there is a problem for storing data
		return AppKeyAdminStorage.updatePwd(username, encodedpwd);
	}

	/**
	 * Check if a given user has permission to manage AppKey.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean hasPermission(String username, String password) {
		if (isNullOrEmpty(username) || isNullOrEmpty(password)) return false;
		if (username.equals(ADMIN_USER)) {
			return password.equals(ADMIN_PASS);
		}
		String storedPwdEncoded = AppKeyAdminStorage.getEncodedPassword(username);
		if (isNullOrEmpty(storedPwdEncoded)) return false;
		String encodedPwd = encode(password);
		return storedPwdEncoded.equals(encodedPwd);
	}

	

	/**
	 * Encodes a given password 1000 times.
	 * @param password
	 * @return Encoded password.
	 */
	private String encode(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] input = password.getBytes();
			for (int i = 0; i < 1000; i++) {
				input = md.digest(input);
			}
			return Base64.encodeBase64String(input);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private AuthenticationManager() {
	}
}

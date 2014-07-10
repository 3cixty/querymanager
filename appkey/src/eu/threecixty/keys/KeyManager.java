package eu.threecixty.keys;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

/**
 * This class is to create an application key, check whether a key value is valid or not.
 * @author Cong-Kinh Nguyen
 *
 */
public class KeyManager {
	
	private static KeyManager singleton;

	public static synchronized KeyManager getInstance() {
		if (singleton == null) singleton = new KeyManager();
		return singleton;
	}

	/**
	 * Sets root path to folder contains app keys.
	 * @param rootPath
	 */
	public void setPath(String rootPath) {
	}

	/**
	 * Checks if a given key is valid.
	 * @param key
	 * @return
	 */
	public boolean checkAppKey(String key) {
		if (key == null || key.equals("")) return false;
		return AppKeyStorage.checkKeyValidated(key);
	}

	/**
	 * Checks whether a given email exists or not.
	 * @param email
	 * @return
	 */
	public boolean checkEmailExisted(String email) {
		return AppKeyStorage.checkEmailExisted(email);
	}

	/**
	 * Checks whether or not a given UID exists.
	 * @param uid
	 * @return
	 */
	public boolean checkUidExisted(String uid) {
		return AppKeyStorage.checkUIDExisted(uid);
	}

	/**
	 * Adds or Updates an App Key to system.
	 * @param appKey
	 * @return true if this method successfully adds a given key to the system. Otherwise, the method returns false.
	 */
	public boolean addOrUpdateAppKey(AppKey appKey) {
		if (appKey.getAppName() == null || appKey.getAppName().equals("")
				|| appKey.getValue() == null || appKey.getValue().equals("")
				|| appKey.getOwner() == null
				|| appKey.getOwner().getEmail() == null || appKey.getOwner().getEmail().equals("")
				|| appKey.getOwner().getUid() == null || appKey.getOwner().getUid().equals("")) return false;
		return AppKeyStorage.createAppKey(appKey);
	}

	/**
	 * Gets all the application keys.
	 * @return A collection of App keys
	 */
	public Collection<AppKey> getAppKeys() {
		return getAppKeysList();
	}
	
	/**
	 * Get a list of App keys
	 * @return a list of App keys
	 */
	public List <AppKey> getAppKeysList() {
		return AppKeyStorage.getAppKeys();
	}

	/**
	 * Gets an AppKey from a given UID.
	 * @param uid
	 * @return returns AppKey associated with a given UID, and null if there is no App key associated with the UID
	 */
	public AppKey getAppKeyFromUID(String uid) {
		if (uid == null || uid.equals("")) return null;
		return AppKeyStorage.getAppKey(uid);
	}

	/**
	 * Deletes a development key associated with a given UID.
	 * @param uid
	 * @return
	 */
	public boolean deleteAppKey(String uid) {
		return AppKeyStorage.deleteAppKey(uid);
	}
	
	private  char [] availChars = null;

	/**
	 * Generates an AppKey from a given UID.
	 * <br>
	 * An AppKey is composed of three elements: the first element is UID, the second one is time in millisecond
	 * when the key was generated, and last one is a random string. These information make sure that each user has
	 * a unique key.  
	 * @param uid
	 * @return
	 */
	public String generateKey(String uid) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(uid).append(System.currentTimeMillis());
		int len = 48 - buffer.length();
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		int availLen = availChars.length;
		for (int i = 0; i < len; i++) {
			int nextInt = random.nextInt(availLen);

			buffer.append(availChars[nextInt]);
		}
		return Base64.encodeBase64String(buffer.toString().getBytes());
	}
	
	private KeyManager() {

		availChars = new char[('z' - 'a') + ('Z' - 'A') + 2 + 10 + 9]; // 10 digits, 9 special chars
		for (byte i = 0; i < ('z' - 'a') + 1; i++) {
			availChars[i] = (char) ('a' + i);
		}
		for (byte i = 'z' - 'a' + 1; i < 2 *('z' - 'a') + 2; i++) {
			availChars[i] = (char) ('A' + i);
		}
		for (byte i = ('z' - 'a' + 1) * 2; i < 2 *('z' - 'a') + 2 + 10; i++) {
			availChars[i] = (char) (i - ('z' - 'a' + 1) * 2);
		}
		availChars[2 *('z' - 'a') + 2 + 10 + 0] = ',';
		availChars[2 *('z' - 'a') + 2 + 10 + 1] = ';';
		availChars[2 *('z' - 'a') + 2 + 10 + 2] = ':';
		availChars[2 *('z' - 'a') + 2 + 10 + 3] = '!';
		availChars[2 *('z' - 'a') + 2 + 10 + 4] = '+';
		availChars[2 *('z' - 'a') + 2 + 10 + 5] = '-';
		availChars[2 *('z' - 'a') + 2 + 10 + 6] = '.';
		availChars[2 *('z' - 'a') + 2 + 10 + 7] = '(';
		availChars[2 *('z' - 'a') + 2 + 10 + 8] = ')';
	}
}

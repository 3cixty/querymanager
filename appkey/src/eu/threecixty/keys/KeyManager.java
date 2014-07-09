package eu.threecixty.keys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;

/**
 * This class is to create an application key, check whether a key value is valid or not.
 * @author Cong-Kinh Nguyen
 *
 */
public class KeyManager {

	private static final String APPKEY_FILENAME = "appkey.json";
//	private static final int SHA_TIMES = 1000;
	
	private static KeyManager singleton;
	private String rootPath;
	private Map <String, AppKey> appKeys;

	public static synchronized KeyManager getInstance() {
		if (singleton == null) singleton = new KeyManager();
		return singleton;
	}

	/**
	 * Sets root path to folder contains app keys.
	 * @param rootPath
	 */
	public void setPath(String rootPath) {
		this.rootPath = rootPath;
		initAllAppKeys();
	}

	/**
	 * Checks if a given key is valid.
	 * @param key
	 * @return
	 */
	public synchronized boolean checkAppKey(String key) {
		if (key == null || key.equals("")) return false;
		if (rootPath == null || rootPath.equals("")) return false;
		for (AppKey appKey: appKeys.values()) {
			if (key.equals(appKey.getValue())) return true;
		}
		// initiate again as AppKeys can be copied from backup folder after deployment
		initAllAppKeys();
		// check again
		for (AppKey appKey: appKeys.values()) {
			if (key.equals(appKey.getValue())) return true;
		}
		return false;
	}

	/**
	 * Checks whether a given email exists or not.
	 * @param email
	 * @return
	 */
	public boolean checkEmailExisted(String email) {
		AppKey appKey = getAppKeyFromEmail(email);
		return appKey != null;
	}

	/**
	 * Checks whether or not a given UID exists.
	 * @param uid
	 * @return
	 */
	public boolean checkUidExisted(String uid) {
		return appKeys.containsKey(uid);
	}

	/**
	 * Adds or Updates an App Key to system.
	 * @param appKey
	 * @return true if this method successfully adds a given key to the system. Otherwise, the method returns false.
	 */
	public synchronized boolean addOrUpdateAppKey(AppKey appKey) {
		if (rootPath == null || rootPath.equals("")) return false;
		if (appKey.getAppName() == null || appKey.getAppName().equals("")
				|| appKey.getValue() == null || appKey.getValue().equals("")
				|| appKey.getOwner() == null
				|| appKey.getOwner().getEmail() == null || appKey.getOwner().getEmail().equals("")
				|| appKey.getOwner().getUid() == null || appKey.getOwner().getUid().equals("")) return false;
		File file = new File(rootPath + appKey.getAppName() + File.separatorChar + appKey.getOwner().getUid());
		if (file.exists()) {
			if (file.isDirectory()) {
			for (File subFile: file.listFiles()) {
				if (!subFile.delete()) return false;
			}
			} else {
				if (!file.delete()) return false;
			}
		} else {
		    if (!file.mkdirs()) return false;
		}
		try {
			// persist in FileSystem
			FileOutputStream appkeyOutput = new FileOutputStream(rootPath + appKey.getAppName() + File.separatorChar
					+ appKey.getOwner().getUid() + File.separatorChar + APPKEY_FILENAME);
			Gson gson = new Gson();
			String appkeyInJSON = gson.toJson(appKey);
			appkeyOutput.write(appkeyInJSON.getBytes());
			appkeyOutput.close();
			
			// also store in memory to improve performance
			appKeys.put(appKey.getOwner().getUid(), appKey);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Gets all the application keys.
	 * @return
	 */
	public Collection<AppKey> getAppKeys() {
		return appKeys.values();
	}

	/**
	 * Gets an AppKey from a given UID.
	 * @param uid
	 * @return
	 */
	public AppKey getAppKeyFromUID(String uid) {
		if (uid == null || uid.equals("")) return null;
		return appKeys.get(uid);
	}

	/**
	 * Gets an AppKey from a given user's email.
	 * @param email
	 * @return
	 */
	public AppKey getAppKeyFromEmail(String email) {
		if (email == null || email.equals("")) return null;
		for (AppKey appKey: appKeys.values()) {
			if (email.equals(appKey.getOwner().getEmail())) return appKey;
		}
		return null;
	}

	/**
	 * Deletes a development key associated with a given UID.
	 * @param uid
	 * @return
	 */
	public synchronized boolean deleteAppKey(String uid) {
		if (rootPath == null || rootPath.equals("")) return false;
		if (uid == null || uid.equals("")) return false;
		AppKey appKey = getAppKeyFromUID(uid);
		if (appKey == null) return false;
		File file = new File(rootPath + appKey.getAppName() + File.separatorChar
					+ appKey.getOwner().getUid() + File.separatorChar + APPKEY_FILENAME);
		if (!file.exists()) {
			appKeys.remove(appKey.getOwner().getUid());
			return true;
		} else {
			if (file.delete()) {
				appKeys.remove(appKey.getOwner().getUid());
				return true;
			}
		}
		return false;
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

//	/**
//	 * Make a hash for the input string.
//	 * @param keyValue
//	 * @return
//	 */
//	private String hash(String keyValue) {
//		try {
//			MessageDigest digest = MessageDigest.getInstance("SHA-256");
//			byte [] input = keyValue.getBytes("UTF-8");
//			byte [] output = null;
//			for (int i  = 0; i < SHA_TIMES; i++) {
//				output = digest.digest(input);
//				input = output;
//			}
//			if (output != null) {
//		        StringBuffer hexString = new StringBuffer();
//		    	for (int i = 0; i < output.length; i++) {
//		    		String hex = Integer.toHexString(0xff & output[i]);
//		   	     	if (hex.length() == 1) hexString.append('0');
//		   	     	hexString.append(hex);
//		    	}
//		    	return hexString.toString();
//			}
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}

	/**
	 * Loads all APPKEYS into the system.
	 */
	private void initAllAppKeys() {
		appKeys.clear();
		File file = new File(rootPath);
		if (!file.exists()) file.mkdirs();
		byte [] b = new byte[1024];
		int readBytes;
		Gson gson = new Gson();
		for (File domainFolder: file.listFiles()) {
			if (!domainFolder.isDirectory()) continue;
			for (File keyFolder: domainFolder.listFiles()) {
				if (appKeys.containsKey(keyFolder.getName())) continue;
				try {
					FileInputStream input = new FileInputStream(keyFolder.getAbsolutePath()
							+ File.separatorChar + APPKEY_FILENAME);
					StringBuffer buffer = new StringBuffer();
					while ((readBytes = input.read(b)) >= 0) {
						buffer.append(new String(b, 0, readBytes));
					}
					input.close();
					AppKey appKey = gson.fromJson(buffer.toString(), AppKey.class);
					if (appKey != null && appKey.getOwner() != null) appKeys.put(keyFolder.getName(), appKey);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private KeyManager() {
		appKeys = new HashMap <String, AppKey>();

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

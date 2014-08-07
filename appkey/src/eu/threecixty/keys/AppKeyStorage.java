package eu.threecixty.keys;

import java.util.List;

import eu.threecixty.db.AppKeyTable;
import eu.threecixty.db.ThreeCixyDBException;

/**
 * This class is to deal with manipulating AppKey with data storage (database).
 * @author Cong-Kinh Nguyen
 *
 */
public class AppKeyStorage {
	
	public static boolean createAppKey(AppKey appKey) {
		if (appKey == null) return false;
		try {
			return AppKeyTable.createAppKey(appKey);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static AppKey getAppKey(String uid) {
		try {
			return AppKeyTable.getAppKey(uid);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AppKey getAppKeyFromKey(String key) {
		try {
			return AppKeyTable.getAppKeyFromKey(key);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<AppKey> getAppKeys() {
		try {
			return AppKeyTable.getAppKeys();
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean deleteAppKey(String uid) {
		try {
			return AppKeyTable.deleteAppKey(uid);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkEmailExisted(String email) {
		try {
			return AppKeyTable.checkEmailExisted(email);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkUIDExisted(String uid) {
		try {
			return AppKeyTable.checkUIDExisted(uid);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkKeyValidated(String appkey) {
		try {
			return AppKeyTable.checkKeyValidated(appkey);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	private AppKeyStorage() {
	}
}

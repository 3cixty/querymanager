package eu.threecixty.keys.management;

import eu.threecixty.db.AppKeyAdminTable;
import eu.threecixty.db.ThreeCixyDBException;

/**
 * This class is to write and retrieve a user which is an admin for AppKey.
 * 
 * @author Cong-Kinh Nguyen
 *
 */
public class AppKeyAdminStorage {

	public static String getEncodedPassword(String username) {
		try {
			return AppKeyAdminTable.getPassword(username);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean createAndSave(String username, String encodedPwd,
			String firstName, String lastName) {
		try {
			return AppKeyAdminTable.createUser(username, encodedPwd,
					firstName == null ? "" : firstName, lastName == null ? "" : lastName);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean updatePwd(String username, String encodedPwd) {
		try {
			return AppKeyAdminTable.updatePassword(username, encodedPwd);
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private AppKeyAdminStorage() {
	}
}

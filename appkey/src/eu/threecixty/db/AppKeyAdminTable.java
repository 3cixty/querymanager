package eu.threecixty.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is to deal with 
 * @author Cong-Kinh Nguyen
 *
 */
public class AppKeyAdminTable {

	private static final String TABLE_NAME = "app_key_admin";
	
	private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
	
	private static boolean firstTime = true;
	
	private static boolean createAppKeyAdminTable() throws ThreeCixyDBException {
		Connection conn = DBConnection.getInstance().getConnection();

		try {
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS app_key_admin " + 
					" (username VARCHAR(128) NOT NULL PRIMARY KEY, " + 
					" password VARCHAR(128) NOT NULL, " +
					" firstName VARCHAR(128), " +
					" lastName VARCHAR(128)" +
					" )"; 

			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			throw new ThreeCixyDBException(e.getCause());
		}
	}

	/**
	 * Creates a user who can access to AppKey management. This user can indeed add a new AppKey.
	 * @param username
	 * @param password
	 * @return Returns <code>true</code> if the method is successful to create a user. Otherwise, returns <code>false</code>.
	 * @throws ThreeCixyDBException
	 */
	public static boolean createUser(String username, String password,
			String firstName, String lastName) throws ThreeCixyDBException {
		if (firstTime) {
			createAppKeyAdminTable();
			firstTime = false;
		}
		if (!validateUserName(username)) {
			return false;
		}
        String sql = "INSERT INTO " + TABLE_NAME +
	            " VALUES ('" + username + "', '" + password + "', '"
        		+ firstName +  "', '" + lastName + "')";
		return executeQuery(sql);
	}

	/**
	 * Gets password from DB. 
	 * @param username
	 * @return If a given username is existed in the DB, returns the password associated with the username. Otherwise, return an empty string.
	 */
	public static String getPassword(String username) throws ThreeCixyDBException {
		if (!validateUserName(username)) return "";
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username LIKE '" + username + "'";
		Connection conn = DBConnection.getInstance().getConnection();
		try {
			Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(sql);
		    if (!rs.next()) return "";
		    String pass = rs.getString("password");
		    rs.close();
		    stmt.close();
		    return pass;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Updates password from DB. 
	 * @param username
	 * @param newPassword
	 * @return True if a new password is updated, and false in the opposite case.
	 */
	public static boolean updatePassword(String username, String newPassword) throws ThreeCixyDBException {
		if (!validateUserName(username)) return false;
		String sql = "UPDATE " + TABLE_NAME + " SET password='" + newPassword + "' WHERE username = '" + username + "'";
		Connection conn = DBConnection.getInstance().getConnection();
		try {
			Statement stmt = conn.createStatement();
		    int code = stmt.executeUpdate(sql);
		    stmt.close();
		    return code > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * This method is to validate a username is valid. This is to avoid some kinds of attack such as SQLInjection.
	 * @param username
	 * @return
	 */
	private static boolean validateUserName(String username) {
		Pattern pattern = Pattern.compile(USERNAME_PATTERN);
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}

	private static boolean executeQuery(String query) throws ThreeCixyDBException {
		Connection conn = DBConnection.getInstance().getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		    stmt.executeUpdate(query);
		    stmt.close();
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			return false;
		}
	}
	
	private AppKeyAdminTable() {
	}
}

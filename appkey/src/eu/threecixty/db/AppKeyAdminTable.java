package eu.threecixty.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO " + TABLE_NAME + " (username, password, firstName, lastName) VALUES (?, ?, ?, ?)";
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			preparedStmt.setString(3, firstName);
			preparedStmt.setString(4, lastName);
		    preparedStmt.executeUpdate();
			conn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				DBConnection.getInstance().closeConnection();
			}
		} finally {
			try {
				if (preparedStmt != null) preparedStmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        
		return false;
	}

	/**
	 * Gets password from DB. 
	 * @param username
	 * @return If a given username is existed in the DB, returns the password associated with the username. Otherwise, return an empty string.
	 */
	public static String getPassword(String username) throws ThreeCixyDBException {
		if (!validateUserName(username)) return "";
		if (firstTime) {
			createAppKeyAdminTable();
			firstTime = false;
		}
		
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? ";
		String pwd = null;
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, username);
			ResultSet rs = preparedStmt.executeQuery();
			if (rs.next()) {
				pwd = rs.getString("password");
			}
			rs.close();
			preparedStmt.close();
			return pwd == null ? "" : pwd;
		} catch (SQLException e) {
			e.printStackTrace();
			if (preparedStmt != null)
				try {
					preparedStmt.close();
				} catch (SQLException e1) {
					throw new ThreeCixyDBException(e1);
				}
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
		
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE username = ? ";
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, newPassword);
			preparedStmt.setString(2, username);
		    preparedStmt.executeUpdate();
			conn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (preparedStmt != null) preparedStmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
	
	private AppKeyAdminTable() {
	}
}

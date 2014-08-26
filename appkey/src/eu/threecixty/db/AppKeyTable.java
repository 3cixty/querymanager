package eu.threecixty.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;

import eu.threecixty.keys.AppKey;
import eu.threecixty.keys.KeyOwner;

public class AppKeyTable {

	private static final String TABLE_NAME = "app_key";
	
	private static boolean firstTime = true;
	
	/**
	 * Creates an App key.
	 * @param appKey
	 * @return true if successful, and false if failed
	 * @throws ThreeCixyDBException
	 */
	public static boolean createAppKey(AppKey appKey) throws ThreeCixyDBException {
		createAppKeyTableWhenNecessary();
		if (!validateEmail(appKey.getOwner().getEmail())) return false;
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO " + TABLE_NAME + " (uid, email, firstName, lastName, appType, appkey) VALUES (?, ?, ?, ?, ?, ?)";
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, appKey.getOwner().getUid());
			preparedStmt.setString(2, appKey.getOwner().getEmail());
			preparedStmt.setString(3, appKey.getOwner().getFirstName());
			preparedStmt.setString(4, appKey.getOwner().getLastName());
			preparedStmt.setString(5, appKey.getAppName());
			preparedStmt.setString(6, appKey.getValue());
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
	 * Deletes an App key.
	 * @param uid
	 * @return true if successful, and false if failed
	 * @throws ThreeCixyDBException
	 */
	public static boolean deleteAppKey(String uid) throws ThreeCixyDBException {
		if (uid == null) return false;
		createAppKeyTableWhenNecessary();
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE uid = ? ";
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, uid);
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
	 * Gets app key of a given UID.
	 * @param uid
	 * @return App key associated with a given UID, and null if the corresponding App key doesn't exist in the database.
	 * @throws ThreeCixyDBException
	 */
	public static AppKey getAppKey(String uid) throws ThreeCixyDBException {
		if (uid == null || uid.equals("")) return null;
		createAppKeyTableWhenNecessary();
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE uid = ? ";
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, uid);
			ResultSet rs = preparedStmt.executeQuery();
			AppKey appKey = null;
			if (rs.next()) {
				appKey = createAppKeyFromCurrentRecord(rs);
			}
			rs.close();
			preparedStmt.close();
			return appKey;
		} catch (SQLException e) {
			e.printStackTrace();
			if (preparedStmt != null)
				try {
					preparedStmt.close();
				} catch (SQLException e1) {
					throw new ThreeCixyDBException(e1);
				}
		}
		return null;
	}

	public static AppKey getAppKeyFromKey(String key) throws ThreeCixyDBException {
		if (key == null || key.equals("")) return null;
		createAppKeyTableWhenNecessary();
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE appkey = ? ";
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, key);
			ResultSet rs = preparedStmt.executeQuery();
			AppKey appKey = null;
			if (rs.next()) {
				appKey = createAppKeyFromCurrentRecord(rs);
			}
			rs.close();
			preparedStmt.close();
			return appKey;
		} catch (SQLException e) {
			e.printStackTrace();
			if (preparedStmt != null)
				try {
					preparedStmt.close();
				} catch (SQLException e1) {
					throw new ThreeCixyDBException(e1);
				}
		}
		return null;
	}
	
	public static List<AppKey> getAppKeys() throws ThreeCixyDBException {
		createAppKeyTableWhenNecessary();
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		String sql = "SELECT * FROM " + TABLE_NAME;
		List <AppKey> rets = new ArrayList <AppKey>();
		try {
			preparedStmt = conn.prepareStatement(sql);
			ResultSet rs = preparedStmt.executeQuery();
			while (rs.next()) {
				AppKey appKey = createAppKeyFromCurrentRecord(rs);

				rets.add(appKey);
			}
			rs.close();
			preparedStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (preparedStmt != null)
				try {
					preparedStmt.close();
				} catch (SQLException e1) {
					throw new ThreeCixyDBException(e1);
				}
		}
		return rets;
	}
	
	/**
	 * Checks whether or not a given UID exists in the database.
	 * @param uid
	 * @return true if a given UID existed in the database, false in contrary.
	 * @throws ThreeCixyDBException
	 */
	public static boolean checkUIDExisted(String uid) throws ThreeCixyDBException {
		return hasRecords("uid", uid);
	}

	public static boolean checkEmailExisted(String email) throws ThreeCixyDBException {
		return hasRecords("email", email);
	}

	public static boolean checkKeyValidated(String key) throws ThreeCixyDBException {
		return hasRecords("appkey", key);
	}
	
	private static boolean hasRecords(String columnName, String value) throws ThreeCixyDBException {
		if (value == null || value.equals("")) return false;
		createAppKeyTableWhenNecessary();
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement  preparedStmt = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + columnName + " = ? ";
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, value);
			ResultSet rs = preparedStmt.executeQuery();
			boolean ok = rs.next();
			rs.close();
			rs = null;
			preparedStmt.close();
			return ok;
		} catch (SQLException e) {
			e.printStackTrace();
			if (preparedStmt != null)
				try {
					preparedStmt.close();
				} catch (SQLException e1) {
					throw new ThreeCixyDBException(e1);
				}
		}
		return false;
	}
	
	private static AppKey createAppKeyFromCurrentRecord(ResultSet rs) throws SQLException {
		AppKey appKey = new AppKey();
		appKey.setAppName(rs.getString("appType"));
		appKey.setValue(rs.getString("appkey"));
		KeyOwner owner = new KeyOwner();
		appKey.setOwner(owner);
		owner.setUid(rs.getString("uid"));
		owner.setEmail(rs.getString("email"));
		owner.setFirstName(rs.getString("firstName"));
		owner.setLastName(rs.getString("lastName"));
		return appKey;
	}
	
	private static boolean createAppKeyTableWhenNecessary() throws ThreeCixyDBException {
		if (!firstTime) return true;
		else {
			Connection conn = DBConnection.getInstance().getConnection();

			try {
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
						" (uid VARCHAR(128) NOT NULL PRIMARY KEY, " + 
						" email VARCHAR(128) NOT NULL, " +
						" firstName VARCHAR(128), " +
						" lastName VARCHAR(128), " +
						" appType VARCHAR(128), " +
						" appkey VARCHAR(128) NOT NULL" +
						" )";

				stmt.executeUpdate(sql);
				stmt.close();
				firstTime = false;
				return true;
			} catch (SQLException e) {
				throw new ThreeCixyDBException(e.getCause());
			}
		}
	}

	private static boolean validateEmail(String email) {
		if (email == null || email.equals("")) return false;
		return EmailValidator.getInstance().isValid(email);
	}
	
	
	private AppKeyTable() {
	}
}

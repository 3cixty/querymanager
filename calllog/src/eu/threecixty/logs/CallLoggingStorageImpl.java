package eu.threecixty.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import eu.threecixty.db.DBConnection;
import eu.threecixty.db.ThreeCixyDBException;
import eu.threecixty.keys.AppKey;
import eu.threecixty.keys.KeyManager;

/**
 * This class persists statistics information in MySQL database.
 * @author Cong-Kinh Nguyen
 *
 */
public class CallLoggingStorageImpl implements CallLoggingStorage {

	private static final String TABLE_NAME = "logcall";

	private boolean firstTime = true;
	
	public boolean save(CallLogging logging) {
		if (logging == null) return false;
		try {
			createTableWhenNecessary();

			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement  preparedStmt = null;
			try {
				conn.setAutoCommit(false);
				String sql = "INSERT INTO " + TABLE_NAME + " (appkey, starttime, timeConsumed, serviceName, description) VALUES (?, ?, ?, ?, ?)";
				preparedStmt = conn.prepareStatement(sql);
				preparedStmt.setString(1, logging.getAppKey() == null ? null : logging.getAppKey().getValue());
				preparedStmt.setTimestamp(2, new Timestamp(logging.getStartTime()));
				preparedStmt.setInt(3, logging.getTimeConsumed());
				preparedStmt.setString(4, logging.getServiceName());
				preparedStmt.setString(5, logging.getDescription());
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
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
			
		}
		
		return false;
	}

	public List<CallLogging> getStats(String appkey, long from, long to,
			int minTimeConsumed, int maxTimeConsumed) {
		List <CallLogging> loggings = new ArrayList<CallLogging>();
		try {
			createTableWhenNecessary();
			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement  preparedStmt = null;
			try {
				String sql = "SELECT * FROM " + TABLE_NAME + " WHERE appkey = ? AND "
				        + "starttime >= ? AND "
						+ "starttime <= ? AND "
						+ " timeConsumed >= ? AND "
						+ "timeConsumed <= ?";
				preparedStmt = conn.prepareStatement(sql);
				preparedStmt.setString(1, appkey);
				preparedStmt.setTimestamp(2, new Timestamp(from));
				preparedStmt.setTimestamp(3, new Timestamp(to));
				preparedStmt.setInt(4, minTimeConsumed);
				preparedStmt.setInt(5, maxTimeConsumed);
			    ResultSet rs = preparedStmt.executeQuery();
			    AppKey tmpAppKey = KeyManager.getInstance().getAppKeyFromKey(appkey);
			    while (rs.next()) {
			    	CallLogging logging = new CallLogging();
			    	logging.setAppKey(tmpAppKey);
			    	Timestamp timestamp = rs.getTimestamp("starttime");
			    	logging.setStartTime(timestamp.getTime());
			    	String serviceName = rs.getString("serviceName");
			    	logging.setServiceName(serviceName);
			    	int timeConsumed = rs.getInt("timeConsumed");
			    	logging.setTimeConsumed(timeConsumed);
			    	logging.setDescription(rs.getString("description"));
			    	loggings.add(logging);
			    }
			} catch (SQLException e) {
			} finally {
				try {
					if (preparedStmt != null) preparedStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (ThreeCixyDBException e) {
			e.printStackTrace();
		}
		return loggings;
	}

	/**
	 * Creates <code>stats</code> table when the table doesn't exist.
	 * @throws ThreeCixyDBException
	 */
	private void createTableWhenNecessary() throws ThreeCixyDBException {
		if (firstTime) {
			Connection conn = DBConnection.getInstance().getConnection();

			try {
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
						" (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " + 
						" appkey VARCHAR(128) NULL, " +
						" starttime TIMESTAMP, " +
						" timeConsumed INT, " +
						" serviceName VARCHAR(256), " +
						" description VARCHAR(256) " +
						" )";

				stmt.executeUpdate(sql);
				stmt.close();
				firstTime = false;
			} catch (SQLException e) {
				throw new ThreeCixyDBException(e.getCause());
			}
		}
	}

}

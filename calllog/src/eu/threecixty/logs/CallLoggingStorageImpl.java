package eu.threecixty.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.threecixty.db.DBConnection;
import eu.threecixty.db.ThreeCixyDBException;

/**
 * This class persists statistics information in MySQL database.
 * @author Cong-Kinh Nguyen
 *
 */
public class CallLoggingStorageImpl implements CallLoggingStorage {

	private static final String TABLE_NAME = "logcall";
	private static final String APPKEY_TABLE_NAME = "3cixty_app";

	 private static final Logger LOGGER = Logger.getLogger(
			 CallLoggingStorageImpl.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private boolean firstTime = true;
	
	public boolean save(CallLogging logging) {
		logInfo("Before logging call to DB");
		if (logging == null) return false;
		try {
			createTableWhenNecessary();

			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement  preparedStmt = null;
			try {
				conn.setAutoCommit(false);
				String sql = "INSERT INTO " + TABLE_NAME + " (appkey, starttime, timeConsumed, serviceName, description) VALUES (?, ?, ?, ?, ?)";
				preparedStmt = conn.prepareStatement(sql);
				preparedStmt.setString(1, logging.getKey() == null ? null : logging.getKey());
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
		logInfo("After logging call to DB");
		return false;
	}

	public List<CallLogging> getCalls(String appkey, long from, long to,
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
			    //AppKey tmpAppKey = KeyManager.getInstance().getAppKeyFromKey(appkey);
			    while (rs.next()) {
			    	CallLogging logging = new CallLogging();
			    	logging.setKey(appkey);
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
	
	public List<CallLoggingDisplay> getCallsWithCount(long from, long to,
			int minTimeConsumed, int maxTimeConsumed) {
		List <CallLoggingDisplay> loggings = new ArrayList<CallLoggingDisplay>();
		try {
			createTableWhenNecessary();
			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement  preparedStmt = null;
			try {
				String sql = "SELECT "+ APPKEY_TABLE_NAME+".app_name AS appName, " //TODO checktable name
						+ "DATE_FORMAT(DATE_SUB("+TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') AS starttime, "
						+ "COUNT("+APPKEY_TABLE_NAME +".app_name) AS numberOfCalls "
						+ "FROM " + TABLE_NAME +"," + APPKEY_TABLE_NAME
						+ " WHERE "+ TABLE_NAME+".appkey LIKE "+ APPKEY_TABLE_NAME+".app_key"// AND "
								/*+ TABLE_NAME +".starttime >=? AND "
								+ TABLE_NAME +".starttime <= ? AND "
								+ TABLE_NAME +".timeConsumed >= ? AND "
								+ TABLE_NAME +".timeConsumed <= ? "*/
						+ " GROUP BY " + APPKEY_TABLE_NAME +".app_name, DATE_FORMAT(DATE_SUB("+ TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') "
						+ "ORDER BY starttime";
				preparedStmt = conn.prepareStatement(sql);
				//preparedStmt.setTimestamp(1, new Timestamp(from));
				//preparedStmt.setTimestamp(2, new Timestamp(to));
				//preparedStmt.setInt(3, minTimeConsumed);
				//preparedStmt.setInt(4, maxTimeConsumed);
			    ResultSet rs = preparedStmt.executeQuery();

			    while (rs.next()) {
			    	CallLoggingDisplay loggingDisplay = new CallLoggingDisplay();
			    	CallLogging logging = new CallLogging();
			    	//AppKey tmpAppKey = KeyManager.getInstance().getAppKeyFromKey(rs.getString("appkey"));
			    	logging.setKey(rs.getString("appName"));
			    	loggingDisplay.setCallLogging(logging);
			    	loggingDisplay.setDateCall(rs.getString("starttime"));;
			    	loggingDisplay.setNumberOfCalls(rs.getInt("numberOfCalls"));
			    	loggings.add(loggingDisplay);
			    }
			} catch (SQLException e) { e.printStackTrace();
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

	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}	
}

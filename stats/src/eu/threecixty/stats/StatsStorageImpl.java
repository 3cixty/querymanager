package eu.threecixty.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import eu.threecixty.db.DBConnection;
import eu.threecixty.db.ThreeCixyDBException;

/**
 * This class persists statistics information in MySQL database.
 * @author Cong-Kinh Nguyen
 *
 */
public class StatsStorageImpl implements StatsStorage {

	private static final String TABLE_NAME = "stats";

	private boolean firstTime = true;
	
	public boolean save(Stats stats) {
		if (stats == null) return false;
		try {
			createTableWhenNecessary();

			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement  preparedStmt = null;
			try {
				conn.setAutoCommit(false);
				String sql = "INSERT INTO " + TABLE_NAME + " (appkey, starttime, timeConsumed, serviceName) VALUES (?, ?, ?, ?)";
				preparedStmt = conn.prepareStatement(sql);
				preparedStmt.setString(1, stats.getAppKey().getValue());
				preparedStmt.setTimestamp(2, new Timestamp(stats.getStartTime()));
				preparedStmt.setInt(3, stats.getTimeConsumed());
				preparedStmt.setString(4, stats.getServiceName());
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
						" appkey VARCHAR(128) NOT NULL, " +
						" starttime TIMESTAMP, " +
						" timeConsumed INT, " +
						" serviceName VARCHAR(256) " +
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

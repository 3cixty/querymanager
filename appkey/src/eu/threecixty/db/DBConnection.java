package eu.threecixty.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

	private static final Object _sync = new Object();
	private static DBConnection singleton;

	private Connection conn;
	
	public static DBConnection getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new DBConnection();
			}
		}
		return singleton;
	}

	public Connection getConnection() throws ThreeCixyDBException {
		if (conn != null) return conn;
		try{
			//STEP 2: Register JDBC driver
			Class.forName(Constants.JDBC_DRIVER);

			//STEP 3: Open a connection
			//System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PWD);
			//System.out.println("Connected database successfully...");
			return conn;
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			throw new ThreeCixyDBException(se.getCause());
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			throw new ThreeCixyDBException(e.getCause());
		}
	}

	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				conn = null;
			}
		}
	}
	
	private DBConnection() {
	}
}

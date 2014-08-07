package eu.threecixty.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class DBConnection {

	private static final Object _sync = new Object();
	private static DBConnection singleton;

	private Connection conn;
	
	private String dbPwd = null;
	private String pathToPwdPropertyFile = null;
	
	public static DBConnection getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new DBConnection();
			}
		}
		return singleton;
	}

	public void setPath(String pathToPwdPropertyFile) {
		this.pathToPwdPropertyFile = pathToPwdPropertyFile;
	}

	public Connection getConnection() throws ThreeCixyDBException {
		if (conn != null) return conn;
		try{
			//STEP 2: Register JDBC driver
			Class.forName(Constants.JDBC_DRIVER);

			//STEP 3: Open a connection
			//System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, getDbPassword());
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

	/**
	 * Gets DB password from property file located in querymanagerServlet/WebContent/WEB-INF folder. 
	 * @return
	 */
	private String getDbPassword() {
		if (pathToPwdPropertyFile == null) return null;
		if (dbPwd != null) return dbPwd;
		try {
			Scanner scanner = new Scanner(new File(pathToPwdPropertyFile + "password.property"));
			dbPwd = scanner.nextLine();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dbPwd;
	}
	
	private DBConnection() {
	}
}

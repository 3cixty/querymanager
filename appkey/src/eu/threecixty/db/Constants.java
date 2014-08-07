package eu.threecixty.db;

public class Constants {

	public static final String DB_NAME = "3cixty";
	public static final String DB_USER = "3cixty";
	//public static final String DB_PWD = "3cixtydatabase001";
	
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	public static final String DB_URL = "jdbc:mysql://localhost:3306/" + Constants.DB_NAME;
	
	private Constants() {
	}
}

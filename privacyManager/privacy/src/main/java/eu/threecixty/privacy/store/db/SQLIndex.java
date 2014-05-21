package eu.threecixty.privacy.store.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.threecixty.privacy.store.Value;
import eu.threecixty.privacy.store.User;
import eu.threecixty.privacy.store.StoreIndex;

/** StoreIndexer implemented by two SQL tables */
public class SQLIndex implements StoreIndex {

	private final String driver;

	private final String url;

	private String user;

	private String pass;
	
	private Connection conn;
	
	private final Schema schema = new SchemaV1();
	
	/**
	 * Create an object capable of creating or opening an index in the specified
	 * database.
	 * 
	 * <p>
	 * This constructor is not opening the database. It must be done by calling
	 * {@link #open()}. If credentials are mandatory to open the database then
	 * set then with {@link #setCredentials(String, String)} before.
	 * </p>
	 * 
	 * @param driver
	 *            JDBC driver name, ex: "com.mysql.jdbc.Driver"
	 * @param url
	 *            URL of the database to open, ex: "jdbc:mysql://localhost/"
	 */
	public SQLIndex(String driver, String url) {
		this.driver = driver;
		this.url = url;
		
		setCredentials("", null);
		this.conn = null;
	}
	
	/**
	 * Map a privacy SQL index on an opened database.
	 * @param conn opened connection to a database
	 */
	public SQLIndex(Connection conn) {
		// external connection, don't know about these
		this.driver = null;
		this.url = null;
		
		setCredentials("", null);
		this.conn = conn;
	}

	/**
	 * Set one time credentials used when opening the database on {@link #open()}.
	 * @param user database's user name
	 * @param pass database's user password
	 */
	public void setCredentials(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * Open the index database using the credentials set with
	 * {@link #setCredentials(String, String)}. These credentials are reset
	 * before returning and it will be necessary to specify them again for the
	 * next call to open().
	 * @throws SQLException 
	 */
	public void open() throws IOException {

		if (conn != null) return; // Already opened.
		
		// Register JDBC driver
		try {
			Class.forName(this.driver);
		} catch (ClassNotFoundException e) {
			throw new IOException("failed to register JDBC driver " + this.driver, e);
		}

		try {
			conn = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
		// Dispose of the one time credentials.
		setCredentials("", null);
	}

	public void close() {

		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public User addUser(String user, String authenticator) {
		try {
			return schema.addUser(conn, user, authenticator);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User findUser(String user) {
		try {
			return schema.getUser(conn, user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Value addResource(String user, String ontology,
			String resource, String provider) {
		try {
			return schema.addResource(conn, user, ontology, resource, provider);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Value getResourceByOntology(String ontology) {
		try {
			return schema.getResource(conn, ontology);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

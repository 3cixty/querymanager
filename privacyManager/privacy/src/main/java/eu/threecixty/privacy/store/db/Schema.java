package eu.threecixty.privacy.store.db;

import java.sql.Connection;
import java.sql.SQLException;

import eu.threecixty.privacy.store.Value;
import eu.threecixty.privacy.store.User;

/**
 * @author T0129842
 * 
 */
public interface Schema {

	/**
	 * @return the version number of this schema.
	 */
	public abstract int getSchemaVersion();

	/**
	 * @param conn
	 * @throws SQLException
	 */
	public abstract void createTables(Connection conn) throws SQLException;

	/**
	 * @param conn
	 * @throws SQLException
	 */
	public abstract void dropTables(Connection conn) throws SQLException;

	/**
	 * @param conn
	 * @param user
	 * @param authenticator
	 * @return
	 * @throws SQLException
	 */
	public abstract User addUser(Connection conn, String user,
			String authenticator) throws SQLException;

	/**
	 * @param conn
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public abstract User getUser(Connection conn, String user)
			throws SQLException;

	/**
	 * @param conn
	 * @param user
	 * @param ontology
	 * @param resource
	 * @param provider
	 * @return
	 * @throws SQLException
	 */
	public abstract Value addResource(Connection conn, String user,
			String ontology, String resource, String provider)
			throws SQLException;

	/**
	 * @param conn
	 * @param ontology
	 * @return
	 * @throws SQLException
	 */
	public abstract Value getResource(Connection conn, String ontology)
			throws SQLException;

}
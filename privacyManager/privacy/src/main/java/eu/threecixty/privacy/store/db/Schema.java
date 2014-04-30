package eu.threecixty.privacy.store.db;

import java.sql.Connection;
import java.sql.SQLException;

import eu.threecixty.privacy.store.Value;
import eu.threecixty.privacy.store.User;

public interface Schema {

	public abstract int getSchemaVersion();
	
	public abstract void createTables(Connection conn) throws SQLException;

	public abstract void dropTables(Connection conn) throws SQLException;

	public abstract User addUser(Connection conn, String user,
			String authenticator) throws SQLException;

	public abstract User getUserById(Connection conn, long userId) throws SQLException;

	public abstract User getUserByName(Connection conn, String user) throws SQLException;

	public abstract Value addResource(Connection conn, long userId,
			String ontology, String resource, String provider) throws SQLException;

	public abstract Value getResourceByOntology(Connection conn, String ontology) throws SQLException;

}
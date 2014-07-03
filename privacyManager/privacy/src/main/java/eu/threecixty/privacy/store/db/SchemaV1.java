package eu.threecixty.privacy.store.db;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import eu.threecixty.privacy.store.Value;
import eu.threecixty.privacy.store.ValueObject;
import eu.threecixty.privacy.store.User;
import eu.threecixty.privacy.store.UserObject;

final class SchemaV1 implements Schema {

	/** Schema version number */
	private static final int VERSION = 1;

	/**
	 * This table references the unified storage users. The table columns are
	 * {@link #COLUMN_USER} and {@link #COLUMN_AUTH}
	 */
	public static final String TABLE_USER = "PRIVACY_USER";

	/**
	 * A unique string identifying the user which can be of different kind:
	 * applications, system users, etc. It should also be the primary key for
	 * tables unless the database management system has its own definition for
	 * primary keys of tables (like android sets a column _id as primary key of
	 * every table)
	 */
	public static final String COLUMN_USER = "id";

	/**
	 * Some optional information that can used by a caller in order to
	 * authenticate a self proclaimed user.
	 */
	public static final String COLUMN_AUTH = "auth";

	private final static String[] ALL_USER_COLUMNS = { COLUMN_USER, COLUMN_AUTH };

	/**
	 * This table references the stored resources and their rightful owner. The
	 * table columns are: {@link #COLUMN_ONTOLOGY}, {@link #COLUMN_OWNER} which
	 * references the column {@link #COLUMN_USER} from {@link #TABLE_USER},
	 * {@link #COLUMN_RESOURCE} and {@link #COLUMN_PROVIDER}. Every column is
	 * mandatory.
	 */
	public static final String TABLE_ENTITY = "ENTITY";

	/** A unique URL string identifying a stored resource */
	public static final String COLUMN_ONTOLOGY = "ontology";

	/**
	 * ID of the user that owns this resource, as a foreign key referencing the
	 * column {@link #COLUMN_USER} from {@link #TABLE_USER}
	 */
	public static final String COLUMN_OWNER = "owner";

	/**
	 * The path to the resource according to the provider logic. It may be an
	 * URL string and shall hold some scheme version when multiple versions of
	 * the format are supported by the provider and the resource does not hold
	 * any version indicator in itself. The resource should always be meaningful
	 * to the provider.
	 */
	public static final String COLUMN_RESOURCE = "resource";

	/**
	 * Identifies a unique system able to access a resource.
	 */
	public static final String COLUMN_PROVIDER = "provider";

	private final static String[] ALL_ENTITY_COLUMNS = { COLUMN_ONTOLOGY,
			COLUMN_OWNER, COLUMN_RESOURCE, COLUMN_PROVIDER };

	public int getSchemaVersion() {
		return VERSION;
	}

	/**
	 * Creates tables having their primary keys set to model defined columns.
	 * This creation method is not compatible with android database.
	 */
	public void createTables(Connection conn) throws SQLException {

		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE " + TABLE_USER + " (" + COLUMN_USER
					+ " VARCHAR(255) PRIMARY KEY AUTOINCREMENT," + COLUMN_AUTH
					+ " VARCHAR(255)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE " + TABLE_ENTITY + " (" + COLUMN_ONTOLOGY
					+ " VARCHAR(255) PRIMARY KEY AUTOINCREMENT," + COLUMN_OWNER
					+ " INTEGER," + COLUMN_RESOURCE + " VARCHAR(255),"
					+ COLUMN_PROVIDER + " VARCHAR(255)," + " FOREIGN KEY ("
					+ COLUMN_OWNER + ") REFERENCES " + TABLE_USER + " ("
					+ COLUMN_USER + ")" + ");";
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.threecixty.privacy.store.sqlindex.ISchema#dropTables(java.sql.Connection
	 * )
	 */
	public void dropTables(Connection conn) throws SQLException {

		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_USER);
			stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_ENTITY);
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.threecixty.privacy.store.sqlindex.ISchema#addUser(java.sql.Connection,
	 * java.lang.String, java.lang.String)
	 */
	public User addUser(Connection conn, String user, String authenticator)
			throws SQLException {

		String[] columns = ALL_USER_COLUMNS;
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(getPreparedInsertSQL(TABLE_USER,
					columns));
			stmt.setString(getParameterIndex(columns, COLUMN_USER), user);
			stmt.setString(getParameterIndex(columns, COLUMN_AUTH),
					authenticator == null ? "" : authenticator);
			stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
		}

		// Return the record that should have been created now.
		return getUser(conn, user);
	}

	private int getParameterIndex(String[] columns, String col) {

		for (int i = 0; i < columns.length; i++)
			if (columns[i].equals(col))
				return (i + 1); // remember: 1 based index

		return -1;
	}

	public User getUser(Connection conn, String user) throws SQLException {

		Statement stmt = null;
		User storedUser = null;

		try {
			stmt = conn.createStatement();
			String selectTableSQL = getSelectAllSQL(TABLE_USER,
					ALL_USER_COLUMNS, COLUMN_USER + " = " + user);
			ResultSet rs = stmt.executeQuery(selectTableSQL);
			if (rs.next()) {
				storedUser = readStoredUser(rs);
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}

		return storedUser;
	}

	private User readStoredUser(ResultSet rs) throws SQLException {
		UserObject storedUser = new UserObject();
		storedUser.setId(rs.getString(COLUMN_USER));

		Blob blob = rs.getBlob(COLUMN_AUTH);
		storedUser.setAuthenticator(blob.getBytes(0, (int) blob.length()));
		return storedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.threecixty.privacy.store.sqlindex.ISchema#addResource(java.sql.Connection
	 * , long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Value addResource(Connection conn, String user, String ontology,
			String resource, String provider) throws SQLException {

		String[] columns = ALL_ENTITY_COLUMNS;
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(getPreparedInsertSQL(TABLE_ENTITY,
					columns));
			stmt.setString(getParameterIndex(columns, COLUMN_OWNER), user);
			stmt.setString(getParameterIndex(columns, COLUMN_ONTOLOGY),
					ontology);
			stmt.setString(getParameterIndex(columns, COLUMN_RESOURCE),
					resource);
			stmt.setString(getParameterIndex(columns, COLUMN_PROVIDER),
					provider);
			stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
		}

		// Return the record that should have been created now.
		return getResource(conn, ontology);
	}

	public Value getResource(Connection conn, String ontology)
			throws SQLException {

		Statement stmt = null;
		Value res = null;

		try {
			stmt = conn.createStatement();
			String selectTableSQL = getSelectAllSQL(TABLE_ENTITY,
					ALL_ENTITY_COLUMNS, COLUMN_ONTOLOGY + " = " + ontology);
			ResultSet rs = stmt.executeQuery(selectTableSQL);
			if (rs.next()) {
				res = readStoredResource(rs);
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}

		return res;
	}

	private Value readStoredResource(ResultSet rs) throws SQLException {
		ValueObject res = new ValueObject();

		res.setOntology(rs.getString(COLUMN_ONTOLOGY));
		res.setOwner(rs.getString(COLUMN_OWNER));
		res.setProvider(rs.getString(COLUMN_PROVIDER));
		res.setResource(rs.getString(COLUMN_RESOURCE));

		return res;
	}

	private String getPreparedInsertSQL(String table, String[] columns) {

		StringBuffer insertSQL = new StringBuffer();
		StringBuffer valuesSQL = new StringBuffer();

		insertSQL.append("INSERT INTO " + table + " (");

		boolean first = true;
		for (String col : columns) {
			if (!first) {
				insertSQL.append(',');
				valuesSQL.append(',');
				first = false;
			}

			insertSQL.append(col);
			valuesSQL.append('?');
		}

		insertSQL.append(") VALUES (");
		insertSQL.append(valuesSQL.toString());
		insertSQL.append(')');

		return insertSQL.toString();
	}

	private String getSelectAllSQL(String table, String[] columns, String where) {

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");

		boolean first = true;
		for (String col : columns) {
			if (!first) {
				sql.append(',');
				first = false;
			}

			sql.append(col);
		}

		sql.append(" from ");
		sql.append(table);

		if (where != null && where.length() > 0) {
			sql.append(" where ");
			sql.append(where);
		}

		return sql.toString();
	}

}

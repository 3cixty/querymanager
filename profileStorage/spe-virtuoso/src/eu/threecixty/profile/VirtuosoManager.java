package eu.threecixty.profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;


public class VirtuosoManager {

	
	private static final Object _sync = new Object();
	
//	private static final String PASSWORD_FIXED = "Mil(ano3Cix)ty!";
	
	private static final String PREFIX_EACH_USER_PROFILE_GRAPH = "http://3cixty.com/private/";
	
	private static boolean firstTime = true;
	private static List <String> publicGraphs;
	
	 private static final Logger LOGGER = Logger.getLogger(
			 VirtuosoManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	private static VirtuosoManager instance;
	
	private Connection spoolConn;
	

	public static VirtuosoManager getInstance() {
		if (instance == null) {
			synchronized (_sync) {
				if (instance == null) instance = new VirtuosoManager();
			}
		}
		return instance;
	}
	
	
	/**
	 * Creates an account per 3cixty user.
	 * @param uid
	 * @return
	 */
	public boolean createAccount(String uid) {
		Connection conn = getConnection();
		if (conn == null) return false;
		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			stmt.addBatch("DB.DBA.USER_CREATE ('"+ uid +"', '"+ generatePassword(uid) +"')");

			stmt.addBatch("DB.DBA.USER_GRANT_ROLE('" + uid + "', 'SPARQL_SELECT')");

			// firstly, no one has permission to access to the graph
			if (firstTime) {
				synchronized (_sync) {
					if (firstTime) { // double-checked
					    stmt.addBatch("DB.DBA.RDF_DEFAULT_USER_PERMS_SET ('nobody', 0)");
					    setReadAccessToNobody(stmt);
					    firstTime = false;
					}
				}
			}
			
        	// secondly, current user has no permission at all
			stmt.addBatch("DB.DBA.RDF_DEFAULT_USER_PERMS_SET ('" + uid + "', 0)");
        	
        	// thirdly, set READ access to current user
			stmt.addBatch("DB.DBA.RDF_GRAPH_USER_PERMS_SET ('" + getGraph(uid) + "', '" + uid + "', 1)");
			
			// finally, set READ access to all public graphs
			for (String graphUri: getPublicGraphs(stmt)) {
				stmt.addBatch("DB.DBA.RDF_GRAPH_USER_PERMS_SET ('" + graphUri + "', '" + uid + "', 1)");
			}
			
			stmt.executeBatch();
			conn.commit();
			stmt.close();
			
		} catch (SQLException e) {
			try {
				if (stmt != null) stmt.close();
				conn.rollback();
			} catch (SQLException e1) {
				LOGGER.error(e1.getMessage());
			}
			closeConnection();
			LOGGER.error(e.getMessage());
			return false;
		}
		return true;
	}


	/**
	 * Checks whether or not there is an account associated with a given UID.
	 * @param uid
	 * @return
	 */
	public boolean existsAccount(String uid) {
		if (uid == null || uid.equals("")) return false;
		try {
			VirtGraph virtGraph = getVirtGraph(uid);

			virtGraph.close();
			return true;
		} catch (Exception e) { // bad login
			LOGGER.error("Bad login info for uid = " + uid);
			return false;
		}
	}
	
	public JSONObject executeQuery(String queryStr, String uid) {
		if (uid == null || uid.equals("")) return null;
		JSONObject jsonObject = null;
		VirtGraph virtGraph = getVirtGraph(uid);
		if (virtGraph == null) return null;
		jsonObject = executeQuery(queryStr, virtGraph);
		virtGraph.close();
		return jsonObject;
	}
	
	public JSONObject executeQueryWithDBA(String queryStr) {
		JSONObject jsonObject = null;
		VirtGraph virtGraphDBA = getVirtGraph();
		if (virtGraphDBA == null) return null;
		jsonObject = executeQuery(queryStr, virtGraphDBA);
		virtGraphDBA.close();
		return jsonObject;
	}

	/**
	 * Gets password in Virtuoso for a given UID.
	 * @param uid
	 * @return
	 */
	public String getPassword(String uid) {
		if (uid == null) return null;
		// FIXME:
		//return PASSWORD_FIXED;
		return uid;
	}
	
	public String generatePassword(String uid) {
		if (uid == null) return null;
		// FIXME
		//return PASSWORD_FIXED;
		return uid;
	}
	
	/**
	 * Gets VirtGraph with DBA user.
	 * @return
	 */
	public VirtGraph getVirtGraph() {
		VirtGraph graph = new VirtGraph (VirtuosoConnection.DB_URL,
				VirtuosoConnection.USER, VirtuosoConnection.PASS);
		return graph;
	}
	
	public String getGraph(String uid) {
		if (uid == null) return null;
		return PREFIX_EACH_USER_PROFILE_GRAPH + uid;
	}
	
	/**
	 * Gets connection to Virtuoso.
	 * @return
	 */
	public Connection getConnection() {
		if (spoolConn != null) return spoolConn; 
		try {
			synchronized (_sync) {
				if (spoolConn == null) {
					Class.forName("virtuoso.jdbc4.Driver");
					spoolConn = DriverManager.getConnection(VirtuosoConnection.DB_URL + "charset=UTF-8/roundrobin=1",
							VirtuosoConnection.USER, VirtuosoConnection.PASS);
				}
			}
		}catch(ClassNotFoundException ex){
			LOGGER.error(ex.getMessage());
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
		return spoolConn;
	}
	
	public void closeConnection() {
		synchronized (_sync) {
			if (spoolConn != null) {
				try {
					spoolConn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				spoolConn = null;
			}
		}
	}
	
	private JSONObject executeQuery(String queryStr, VirtGraph virtGraph) {
		if (DEBUG_MOD) LOGGER.info("Query to be executed: " + queryStr);
		String jsonStr = null;
		Query query = QueryFactory.create(queryStr);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, virtGraph);
		ResultSet results = vqe.execSelect();
		try {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ResultSetFormatter.outputAsJSON(baos, results);
		    jsonStr = new String(baos.toByteArray());
		    baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		vqe.close();
		if (jsonStr != null) {
			try {
				return new JSONObject(jsonStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void setReadAccessToNobody(Statement stmt) {
		try {
			for (String graphUri: getPublicGraphs(stmt)) {
				stmt.addBatch("DB.DBA.RDF_GRAPH_USER_PERMS_SET ('" + graphUri + "', 'nobody', 1)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private List <String> getPublicGraphs(Statement stmt) {
		if (publicGraphs != null) return publicGraphs;
		synchronized (_sync) {
			if (publicGraphs == null) {
				publicGraphs = new ArrayList <String>();
				try {
					java.sql.ResultSet rs = stmt.executeQuery("DB.DBA.SPARQL_SELECT_KNOWN_GRAPHS()");
					for ( ; rs.next(); ) {
						String graphUri = rs.getString(1) ;
						if (!graphUri.startsWith(PREFIX_EACH_USER_PROFILE_GRAPH)) publicGraphs.add(graphUri);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return publicGraphs;
	}
	
	private VirtGraph getVirtGraph(String uid) {
		String password = getPassword(uid);
		VirtGraph graph = new VirtGraph (VirtuosoConnection.DB_URL, uid, password);
		return graph;
	}
	
	private VirtuosoManager() {
	}
}

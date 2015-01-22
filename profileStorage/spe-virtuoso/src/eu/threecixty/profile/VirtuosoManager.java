package eu.threecixty.profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

	
//	private static final String PASSWORD_FIXED = "Mil(ano3Cix)ty!";
	
	private static final String PREFIX_EACH_USER_PROFILE_GRAPH = "http://3cixty.com/private/";
	
	
	 private static final Logger LOGGER = Logger.getLogger(
			 VirtuosoManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	public static VirtuosoManager getInstance() {
		return SingletonHolder.INSTANCE;
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
	 * Gets VirtGraph with DBA user.
	 * @return
	 */
	public VirtGraph getVirtGraph() {
		VirtGraph graph = new VirtGraph (VirtuosoConnection.DB_URL,
				VirtuosoConnection.USER, VirtuosoConnection.PASS);
		return graph;
	}
	
	public String getGraph(String uid) {
		return PREFIX_EACH_USER_PROFILE_GRAPH;
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


	
	private VirtuosoManager() {
	}

	/**Singleton holder*/
	private static class SingletonHolder {
		private static final VirtuosoManager INSTANCE = new VirtuosoManager();
	}
}

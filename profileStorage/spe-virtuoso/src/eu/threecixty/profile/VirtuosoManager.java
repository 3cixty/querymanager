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
import com.hp.hpl.jena.shared.JenaException;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;


public class VirtuosoManager {
	
	private static final String PREFIX_EACH_USER_PROFILE_GRAPH = "http://3cixty.com/private/";
	
	
	 private static final Logger LOGGER = Logger.getLogger(
			 VirtuosoManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	 
	 //private static final Semaphore SEMAPHORE = new Semaphore(100, true); // TODO: to be put in a property file
	 
	 public static final String BUSY_EXCEPTION = "Server is too busy at the moment";
	 
	
	public static VirtuosoManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public JSONObject executeQueryWithDBA(String queryStr) throws InterruptedException {
		JSONObject jsonObject = null;
		VirtGraph virtGraphDBA = getVirtGraph();
		if (virtGraphDBA == null) return null;
		jsonObject = executeQuery(queryStr, virtGraphDBA);
		releaseVirtGraph(virtGraphDBA);
		return jsonObject;
	}

	/**
	 * Execute an Insert or Delete query in virtuoso KB
	 * @param type
	 * @param Query
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void executeUpdateQuery(String Query) throws IOException, InterruptedException {
		VirtuosoConnection.processConfigFile();
		VirtGraph virtGraph = getVirtGraph();

		VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
				.create(Query, virtGraph);
		vur.exec();
		
		releaseVirtGraph(virtGraph);
	}

	
	/**
	 * Query a graph
	 * @param query
	 * @throws InterruptedException 
	 */
	public QueryReturnClass query(String query) throws InterruptedException {
		try {
			VirtuosoConnection.processConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		VirtGraph virtGraph = getVirtGraph();

		try {
			Query sparql = QueryFactory
					.create(query);

			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
					sparql, virtGraph);

			QueryReturnClass qRC = new QueryReturnClass(virtGraph, vqe);
			qRC.setResultSelectVar(sparql.getResultVars());
			qRC.setReturnedResultSet(vqe.execSelect());
			qRC.setQuery(sparql);

			return qRC;
		} catch (JenaException je) {
			je.printStackTrace();
			releaseVirtGraph(virtGraph);
			LOGGER.error(je.getMessage());
			throw new InterruptedException();
		}
	}
	
	
	/**
	 * Gets VirtGraph with DBA user.
	 * @return
	 * @throws InterruptedException 
	 */
	public VirtGraph getVirtGraph() throws InterruptedException {
		//SEMAPHORE.acquire();
		try {
			VirtGraph graph = new VirtGraph (VirtuosoConnection.DB_URL,
					VirtuosoConnection.USER, VirtuosoConnection.PASS);
			return graph;
		} catch (JenaException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new InterruptedException();
		}
	}

	public void releaseVirtGraph(VirtGraph virtGraph) {
		virtGraph.close();
		//SEMAPHORE.release();
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


	public int getAvailablePermits() {
		//return SEMAPHORE.availablePermits();
		return 100;
	}
	
	private VirtuosoManager() {
	}

	/**Singleton holder*/
	private static class SingletonHolder {
		private static final VirtuosoManager INSTANCE = new VirtuosoManager();
	}
}

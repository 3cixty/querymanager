package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class VirtuosoConnection {
	// JDBC driver name and database URL
		static String DB_URL;

		// Database credentials
		static String USER;
		static String PASS;
		
		// Graph to query
		static String GRAPH;
		
		private static boolean firstTime = true;
		
		/**
		 * read config file (parameters for virtuoso)
		 * @throws IOException
		 */
		private static void processConfigFile() throws IOException {
			if (!firstTime) return;
			Properties prop = new Properties();
			String propfileName="conf.properties";
			InputStream instream=Thread.currentThread().getContextClassLoader().getResourceAsStream(propfileName);
			prop.load(instream);
			instream.close();

			if (prop.getProperty("virtuoso.address") != null) {
				VirtuosoConnection.DB_URL = prop.getProperty("virtuoso.address");
			} else {
				throw new IOException(
						"The property virtuoso.address doesn't exist");
			}

			if (prop.getProperty("virtuoso.user") != null) {
				VirtuosoConnection.USER = prop.getProperty("virtuoso.user");
			} else {
				throw new IOException("The property virtuoso.user doesn't exist");
			}

			if (prop.getProperty("virtuoso.pass") != null) {
				VirtuosoConnection.PASS = prop.getProperty("virtuoso.pass");
			} else {
				throw new IOException("The property virtuoso.pass doesn't exist");
			}
			
			if (prop.getProperty("virtuoso.graph") != null) {
				VirtuosoConnection.GRAPH = prop.getProperty("virtuoso.graph");
			} else {
				throw new IOException("The property virtuoso.graph doesn't exist");
			}
			try {
				Class.forName("virtuoso.jdbc4.Driver");
//				return DriverManager.getConnection(VirtuosoConnection.DB_URL,
//					VirtuosoConnection.USER, VirtuosoConnection.PASS);
				firstTime = false;
			}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}	
		}

		/**
		 * Insert and Delete into virtuoso KB
		 * @param type
		 * @param Query
		 * @throws IOException
		 */
		public static void insertDeleteQuery(String Query) throws IOException {
			processConfigFile();
			VirtGraph virtGraph = new VirtGraph(VirtuosoConnection.DB_URL,
					VirtuosoConnection.USER, VirtuosoConnection.PASS);
			/*System.out.println("\nexecute: "+ type +" GRAPH "+ virtuosoConnection.GRAPH
						+ " { <aa> <bb> 'cc' ."
						+ " <aa1> <bb1> <123> . "
						+ "<aa2> <bb2> 456. "
						+ "}");*/
			VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
					.create(Query, virtGraph);
			vur.exec();
			virtGraph.close();
		}
		/**
		 * Update a record
		 * @param deleteOldDataQuery
		 * @param insertNewDataQuery
		 * @throws IOException
		 */
		public static void updateQuery(String deleteOldDataQuery, String insertNewDataQuery) throws IOException {
			processConfigFile();
			VirtGraph virtGraph = new VirtGraph(VirtuosoConnection.DB_URL,
					VirtuosoConnection.USER, VirtuosoConnection.PASS);
			/*System.out.println("\nexecute: Delete From GRAPH "+ virtuosoConnection.GRAPH
						+ " { <aa> <bb> 'cc' ."
						+ " <aa1> <bb1> <123> . "
						+ "<aa2> <bb2> 456. "
						+ "}");
			System.out.println("\nexecute: Insert Into GRAPH "+ virtuosoConnection.GRAPH
					+ " { <aa3> <bb3> 'cc3' ."
					+ " <aa4> <bb4> <0123> . "
					+ "<aa5> <bb5> 0456. "
					+ "}");*/
			VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
					.create(deleteOldDataQuery, virtGraph);
			vur.exec();
			vur = VirtuosoUpdateFactory
					.create(insertNewDataQuery, virtGraph);
			vur.exec();
			virtGraph.close();
		}
		
		/**
		 * Query a graph
		 * @param query
		 */
		public static QueryReturnClass query(String query) {
			try {
				processConfigFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			VirtGraph virtGraph = new VirtGraph(VirtuosoConnection.DB_URL,
					VirtuosoConnection.USER, VirtuosoConnection.PASS);

			Query sparql = QueryFactory
					.create(query);
			
			sparql.addGraphURI(VirtuosoConnection.GRAPH);
			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
					sparql, virtGraph);
			
			QueryReturnClass qRC= new QueryReturnClass();
			qRC.setResultSelectVar(sparql.getResultVars());
			qRC.setReturnedResultSet(vqe.execSelect());
			qRC.setQuery(sparql);
			
			return qRC;
		}

}

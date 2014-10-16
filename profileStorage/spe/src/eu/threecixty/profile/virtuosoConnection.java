package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class virtuosoConnection {
	// JDBC driver name and database URL
		static String DB_URL;

		// Database credentials
		static String USER;
		static String PASS;
		
		// Graph to query
		static String GRAPH;
		
		/**
		 * read config file (parameters for virtuoso)
		 * @throws IOException
		 */
		public static Connection processConfigFile() throws IOException, SQLException {
			Properties prop = new Properties();
			String propfileName="conf.properties";
			InputStream instream=Thread.currentThread().getContextClassLoader().getResourceAsStream(propfileName);
			prop.load(instream);

			if (prop.getProperty("virtuoso.address") != null) {
				virtuosoConnection.DB_URL = prop.getProperty("virtuoso.address");
			} else {
				throw new IOException(
						"The property virtuoso.address doesn't exists");
			}

			if (prop.getProperty("virtuoso.user") != null) {
				virtuosoConnection.USER = prop.getProperty("virtuoso.user");
			} else {
				throw new IOException("The property virtuoso.user doesn't exists");
			}

			if (prop.getProperty("virtuoso.pass") != null) {
				virtuosoConnection.PASS = prop.getProperty("virtuoso.pass");
			} else {
				throw new IOException("The property virtuoso.pass doesn't exists");
			}
			
			if (prop.getProperty("virtuoso.graph") != null) {
				virtuosoConnection.GRAPH = prop.getProperty("virtuoso.graph");
			} else {
				throw new IOException("The property virtuoso.graph doesn't exists");
			}
			try {
				return DriverManager.getConnection(virtuosoConnection.DB_URL,
					virtuosoConnection.USER, virtuosoConnection.PASS);
			}catch(SQLException ex){
				throw new SQLException("Connction not possible. This Service not available.");
			}
			
		}

		/**
		 * Insert and Delete into virtuoso KB
		 * @param type
		 * @param Query
		 * @throws IOException
		 */
		public static void insertDeleteQuery(String Query) throws IOException {
			VirtGraph virtGraph = new VirtGraph(virtuosoConnection.DB_URL,
					virtuosoConnection.USER, virtuosoConnection.PASS);
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
			VirtGraph virtGraph = new VirtGraph(virtuosoConnection.DB_URL,
					virtuosoConnection.USER, virtuosoConnection.PASS);
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
		public static queryReturnClass query(String query) {
			VirtGraph virtGraph = new VirtGraph(virtuosoConnection.DB_URL,
					virtuosoConnection.USER, virtuosoConnection.PASS);

			Query sparql = QueryFactory
					.create(query);
			
			sparql.addGraphURI(virtuosoConnection.GRAPH);
			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
					sparql, virtGraph);
			
			queryReturnClass qRC= new queryReturnClass();
			qRC.setResultSelectVar(sparql.getResultVars());
			qRC.setReturnedResultSet(vqe.execSelect());
			qRC.setQuery(sparql);
			
			return qRC;
		}

}

package eu.threecixty.profile;

import java.util.List;

import virtuoso.jena.driver.VirtuosoQueryExecution;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;

public class QueryReturnClass {
	
	private List<String> resultSelectVar;
	private Query query;
	private ResultSet returnedResultSet;
	private VirtuosoQueryExecution vqe;
	
	public QueryReturnClass(VirtuosoQueryExecution vqe) {
		this.vqe = vqe;
	}
	
	public List<String> getResultSelectVar() {
		return resultSelectVar;
	}
	public void setResultSelectVar(List<String> resultSelectVar) {
		this.resultSelectVar = resultSelectVar;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public ResultSet getReturnedResultSet() {
		return returnedResultSet;
	}
	public void setReturnedResultSet(ResultSet returnedResultSet) {
		this.returnedResultSet = returnedResultSet;
	}
	
	public void closeConnection() {
		if (vqe != null) {
			vqe.close();
			vqe = null;
		}
	}
}

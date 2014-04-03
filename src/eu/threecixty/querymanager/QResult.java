package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * This class is to wrap results when executing a query.
 * @author Cong-Kinh NGUYEN
 *
 */
public class QResult {

	private ResultSet resultSet;
	private QueryExecution queryExecution;

	public QResult(ResultSet resultSet, QueryExecution queryExecution) {
		this.resultSet = resultSet;
		this.queryExecution = queryExecution;
	}

	/**
	 * Get a set of results.
	 * @return
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * Release all related resources.
	 * <br><br>
	 * Note that when this method is called before using {@link #getResultSet()},
	 * the method {@link #getResultSet()}'s return will always be empty.
	 */
	public void releaseBuffer() {
		if (queryExecution == null) return;
		queryExecution.close();
		queryExecution = null;
	}

}

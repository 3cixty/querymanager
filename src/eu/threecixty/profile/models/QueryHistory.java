package eu.threecixty.profile.models;

import java.util.Date;

import com.hp.hpl.jena.graph.query.Query;
import com.hp.hpl.jena.query.ResultSet;

import eu.threecixty.profile.annotations.Description;

/**
 * History of user searched queries 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class QueryHistory {
	@Description(hasText="user Input query")
	private Query query;
	@Description(hasText="Augmented query")
	private Query augmentedquery;
	@Description(hasText="Result of the augmented query")
	private ResultSet result;
	@Description(hasText="time at which the user queried the KB")
    private Date hasQueringTime;
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public Query getAugmentedquery() {
		return augmentedquery;
	}
	public void setAugmentedquery(Query augmentedquery) {
		this.augmentedquery = augmentedquery;
	}
	public ResultSet getResult() {
		return result;
	}
	public void setResult(ResultSet result) {
		this.result = result;
	}
	public Date getHasQueringTime() {
		return hasQueringTime;
	}
	public void setHasQueringTime(Date hasQueringTime) {
		this.hasQueringTime = hasQueringTime;
	}
	
	
}

package eu.threecixty.profile.models;

import java.util.Date;

import com.hp.hpl.jena.query.Query;
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
	@Description(hasText="time at which the user queried the KB")
    private Date hasQueringTime;
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public Date getHasQueringTime() {
		return hasQueringTime;
	}
	public void setHasQueringTime(Date hasQueringTime) {
		this.hasQueringTime = hasQueringTime;
	}
	
	
}

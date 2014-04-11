package eu.threecixty.querymanager;


import com.hp.hpl.jena.query.Query;

/**
 * Abstract class to represent a query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public abstract class ThreeCixtyQuery {
	
	protected Query query;

	/**
	 * Clones query.
	 * @return
	 */
	public abstract ThreeCixtyQuery cloneQuery();

	/**
	 * Gets Jena's query.
	 *
	 * @return
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Converts the query to string.
	 * @return
	 */
	public String convert2String() {
		return QueryUtils.convert2String(query);
	}

	/**
	 * Add preferences found a given object to the query.
	 * <br><br>
	 * The method checks if there is a configuration file associated with
	 * the class of a given object. If not, the method immediately returns. Otherwise,
	 * the method add filters by taking values in a given instance's attributes which were
	 * defined in the configuration file.
	 * 
	 * @param object
	 */
	protected void addPreference(Object object) {
		QueryUtils.addPreference(query, object);
	}
}

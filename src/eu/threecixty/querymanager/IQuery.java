package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

/**
 * Interface to represent a query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public interface IQuery {

	/**
	 * Gets Jena's query.
	 *
	 * @return
	 */
	Query getQuery();

	/**
	 * Converts the query to string.
	 * @return
	 */
	String convert2String();

	/**
	 * Clones query.
	 * @return
	 */
	IQuery cloneQuery();
}

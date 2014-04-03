package eu.threecixty.querymanager;

/**
 * Interface to represent an augmented query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public interface IAugmentedQuery {

	/**
	 * Get a query.
	 * @return
	 */
	IQuery getQuery();

	/**
	 * Convert the augmented query to string.
	 * @return
	 */
	String convert2String();
}

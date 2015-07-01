package eu.threecixty.querymanager;

/**
 * This is the interface to augment a query. This interface is just to create a new query which is augmented
 * from a given original query.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public interface QueryAugmenter {

	/**
	 * Creates a query augmented in string format.
	 * <br>
	 * Note that if the given filter is improper, the augmented query will be the same with the original one.
	 *
	 * @param original
	 * 			The original SPARQL query.
	 * @param filter
	 * 			The filter to augment the query.
	 * @param uid
	 * 			The 3cixty UID.
	 * @throws InvalidSparqlQuery
	 * @return
	 */
	String createQueryAugmented(String original, QueryAugmenterFilter filter,
			String uid) throws InvalidSparqlQuery;
}

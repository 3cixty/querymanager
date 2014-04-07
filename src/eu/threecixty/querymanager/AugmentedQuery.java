package eu.threecixty.querymanager;

/**
 * Wrapper for augmented query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class AugmentedQuery {

	private ThreeCixtyQuery query;
	
	public AugmentedQuery(ThreeCixtyQuery query) {
		this.query = query;
	}
	
	/**
	 * Get a query.
	 * @return a 3Cixty query.
	 */
	public ThreeCixtyQuery getQuery() {
		return query;
	}

	/**
	 * Convert the augmented query to string.
	 * @return a string which represents the query.
	 */
	public String convert2String() {
		if (query == null) return null;
		return QueryUtils.convert2String(query.getQuery());
	}
}

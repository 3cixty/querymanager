package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

/**
 * Utility class.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class QueryUtils {
	
	/**
	 * Converts a given query to string.
	 *
	 * @param query
	 * @return
	 */
	public static String convert2String(Query query) {
		if (query == null) return null;
		return query.toString();
	}
	
	private QueryUtils() {
	}
}

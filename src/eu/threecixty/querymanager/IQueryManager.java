package eu.threecixty.querymanager;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public interface IQueryManager {

	/**
	 * Check for both query and augmented Query
	 * 
	 * @param: Query
	 * @return: If the query is found then output in jena.Query.ResultSet format is produced
	 */
	public ResultSet checkInCacheAndReturnResult(Query inputQuery);

	/**
	 * Connect to KB
	 * 
	 * @param: ClassLoader
	 * @param: KB source
	 */
	public void connectToKnowledgebase(ClassLoader classLoader, String filenameOrURI);

	/**
	 * Extract user's social preferences
	 * 
	 * @param: UserID
	 * @return: User social preferences in jena.Query.ResultSet format.
	 **/
	public ResultSet extractPreferenceSocial(String UID);

	/**
	 * Extract user's mobility preferences
	 * note: Mobidot part
	 * 
	 * @param: UserID
	 * @return: User mobility preferences in jena.Query.ResultSet format
	 */
	public ResultSet extractPreferenceMobile(String UID);

	/**
	 * Aggregate query with the social and mobility preferences
	 * note: Call the function multiple times
	 * 
	 * @param: Augmented query
	 * @param: The preferences of the user in jena.Query.ResultSet format
	 */
	public void setAugmentedQuery(Query augmentedQuery, ResultSet preferences);

	/**
	 * Query the KB.
	 * 
	 * @param: The augmented query
	 * @param: KB connection
	 * @return: Result from the KB in jena.Query.ResultSet format
	 */
	public ResultSet getResultFromKB(Query augmentedQuery, Model connection);

	/**
	 * Store the result in the cache
	 * note: Original query and augmented query can serve the purpose of key
	 * 
	 * @param: Input Query
	 * @param: Augmented Query
	 * @param: Result in jena.Query.ResultSet format
	 */
	public void storeResultInCache(Query inputQuery, Query augmentedQuery,
			ResultSet result);
}

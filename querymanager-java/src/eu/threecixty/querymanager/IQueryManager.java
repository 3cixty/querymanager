package eu.threecixty.querymanager;
import java.io.InputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.IProfiler;

/**
 * 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public interface IQueryManager {
	
	/**
	 * This method asks EventMedia to execute the augmented query received by the
	 * {@linkplain #performAugmentingTask()} method.
	 * <br><br>
	 * 
	 * Details of the component EventMedia are listed in:
	 * https://docs.google.com/file/d/0Byc_j3CYjZgzcWVObzU4aGRKT1U/edit.
	 * 
	 * The input required to EventMedia is only the augmented Query. 
	 * The connection to the KB Virtuoso happens inside the EventMedia blackbox.
	 * 
	 * The output of EventMedia is an RDF result of the Query. 
	 * The method captures the results from the EventMedia.
	 * 
	 * Step 6 and 7 of the global architecture. 
	 * https://docs.google.com/drawings/d/1nf4fPRJDia2lOZoYuWikeJpWgpUWU7kzODDhovQC2rk/edit.
	 * 
	 * @param: Augmented Query
	 * @param format
	 * 			The returned string format
	 * @return: a RDF or JSON in string format. Return Null if any error occurs.
	 */
	public String askForExecutingAugmentedQueryAtEventMedia(AugmentedQuery augmentedQuery, EventMediaFormat format);
	
	/**
	 * Request preferences from Knowledge-Base of User Profiles.
	 * 
	 * Extract user's social, mobility and other related preferences.
	 * Required for Step 5 of the global architecture.
	 * https://docs.google.com/drawings/d/1nf4fPRJDia2lOZoYuWikeJpWgpUWU7kzODDhovQC2rk/edit.
	 * 
	 * @param profiler
	 **/
	public void requestPreferences(IProfiler profiler);


	/**
	 * Log: Store the result in the history.
	 * 
	 * Step 10 if the global architecture.
	 * https://docs.google.com/drawings/d/1nf4fPRJDia2lOZoYuWikeJpWgpUWU7kzODDhovQC2rk/edit.
	 *  
	 * @param: Input Query
	 * @param: Augmented Query
	 * @param: Update query
	 */
	public void storeResultInUserQueryProfile(Query inputQuery, Query augmentedQuery,String updateQueryString);

	/**
	 * Performs the augmenting task. This task performs an augmentation for query by taking
	 * into account preferences extracted from knowledgebase of user profiles. Be careful to
	 * call this method as it may take time to complete the task.
	 * Required by the step 5 in the global architecture.
	 */
	void performAugmentingTask();

	/**
	 * Get augmented query. This method returns the augmented query resulted by the task {@link #performAugmentingTask()}.
	 * Required by the step 5 in the global architecture.
	 * @return Current augmented query.
	 */
	AugmentedQuery getAugmentedQuery();

	/**
	 * Gets the model representing the knowledgebase of user profile.
	 * @return RDF model
	 */
	Model getModel();

	/**
	 * Set model.
	 * @param model
	 */
	void setModel(Model model);

	/**
	 * Set model.
	 * @param modelStream
	 */
	void setModel(InputStream modelStream);

	/**
	 * Set model from a given RDF content.
	 * @param rdfContent
	 */
	void setModel(String rdfContent);

	/**
	 * Set model from a given File name or URI.
	 * @param fileOrUri
	 */
	void setModelFromFileOrUri(String fileOrUri);

	/**
	 * Get userID.
	 * @return UserID/session key
	 */
	String getUID();

	/**
	 * Create a Query object from a given query string.
	 * @param queryStr
	 * @return Query
	 */
	Query createJenaQuery(String queryStr);

	/**
	 * Get current query.
	 * @return
	 */
	ThreeCixtyQuery getQuery();

	/**
	 * Set current query.
	 * @param query
	 */
	void setQuery(ThreeCixtyQuery query);

	/**
	 * Execute a given augmented query.
	 * @param query
	 * @return an instance of QResult which contains results by executing an augmented query.
	 */
	QResult executeQuery(AugmentedQuery query);

	/**
	 * Execute found augmented query. The augmented query was resulted by the {@link #performAugmentingTask()} method.
	 * @return an instance of QResult which contains results by executing an augmented query.
	 */
	QResult executeAugmentedQuery();
}

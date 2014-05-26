package eu.threecixty.profile;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;



/**
 * Utility class to find minimum values in UserProfile.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class PreferencesUtils {

	private static final int DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED = 1;
	private static final float DEFAULT_MINIMUM_SCORE_RATED = 3;

	/**
	 * Gets minimum number of times visited.
	 *
	 * @param profiler
	 * @return
	 */
	public static int getMinimumNumberOfTimesVisited(IProfiler profiler) {
		if (profiler == null) return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
		buffer.append("SELECT  DISTINCT  ?numberOfTimes \n");
		buffer.append("WHERE {\n\n");
		buffer.append("?root a owl:NamedIndividual . \n");
		buffer.append("?root profile:hasUID ?uid . \n");
		buffer.append("?root profile:hasPreference ?p1 . \n");
		buffer.append("?p1 profile:hasUserEnteredRatings ?u1 . \n");
		buffer.append("?u1 ?predicate ?s1 . \n");
		buffer.append("?s1 profile:hasNumberofTimesVisited ?numberOfTimes  . \n");
		buffer.append("FILTER (STR(?uid) = \"" + profiler.getUID() + "\") . \n\n");
		buffer.append("}");
		
		int minNumberOfTimes = (int) findMinimumValue(buffer.toString(), "numberOfTimes");
		
		if (minNumberOfTimes == Integer.MAX_VALUE) {
			return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
		}
		
		return minNumberOfTimes;
	}

	/**
	 * Gets minimum score rated.
	 *
	 * @param profiler
	 * @return
	 */
	public static float getMinimumScoreRated(IProfiler profiler) {
		if (profiler == null) return DEFAULT_MINIMUM_SCORE_RATED;
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
		buffer.append("SELECT  DISTINCT  ?scoreRated \n");
		buffer.append("WHERE {\n\n");
		buffer.append("?root a owl:NamedIndividual . \n");
		buffer.append("?root profile:hasUID ?uid . \n");
		buffer.append("?root profile:hasPreference ?p1 . \n");
		buffer.append("?p1 profile:hasUserEnteredRatings ?u1 . \n");
		buffer.append("?u1 ?predicate ?s1 . \n");
		buffer.append("?s1 profile:hasRating ?r1 .\n");
		buffer.append("?r1 profile:hasUserDefinedRating ?scoreRated .\n");
		buffer.append("FILTER (STR(?uid) = \"" + profiler.getUID() + "\") . \n\n");
		buffer.append("}");
		
		float minRated = (float) findMinimumValue(buffer.toString(), "scoreRated");
		
		if (minRated == Integer.MAX_VALUE) {
			return DEFAULT_MINIMUM_SCORE_RATED;
		}
		
		return minRated;
	}

	/**
	 * Gets minimum number of times visited for friends.
	 *
	 * @param profiler
	 * @return
	 */
	public static int getMinimumNumberOfTimesVisitedForFriends(IProfiler profiler) {
		if (profiler == null) return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
		buffer.append("SELECT  DISTINCT  ?numberOfTimes \n");
		buffer.append("WHERE {\n\n");
	    buffer.append("?meroot a owl:NamedIndividual .\n");
	    buffer.append("?meroot profile:hasUID ?uid .\n");
	    buffer.append("?meroot foaf:knows ?root .\n");
		buffer.append("?root profile:hasPreference ?p1 . \n");
		buffer.append("?p1 profile:hasUserEnteredRatings ?u1 . \n");
		buffer.append("?u1 ?predicate ?s1 . \n");
		buffer.append("?s1 profile:hasNumberofTimesVisited ?numberOfTimes  . \n");
		buffer.append("FILTER (STR(?uid) = \"" + profiler.getUID() + "\") . \n\n");
		buffer.append("}");
		
		int minNumberOfTimes = (int) findMinimumValue(buffer.toString(), "numberOfTimes");
		
		if (minNumberOfTimes == Integer.MAX_VALUE) {
			return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
		}
		
		return minNumberOfTimes;
	}

	/**
	 * Gets minimum score rated for friends.
	 *
	 * @param profiler
	 * @return
	 */
	public static float getMinimumScoreRatedForFriends(IProfiler profiler) {
		if (profiler == null) return DEFAULT_MINIMUM_SCORE_RATED;
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
		buffer.append("SELECT  DISTINCT  ?scoreRated \n");
		buffer.append("WHERE {\n\n");
	    buffer.append("?meroot a owl:NamedIndividual .\n");
	    buffer.append("?meroot profile:hasUID ?uid .\n");
	    buffer.append("?meroot foaf:knows ?root .\n");
		buffer.append("?root profile:hasPreference ?p1 . \n");
		buffer.append("?p1 profile:hasUserEnteredRatings ?u1 . \n");
		buffer.append("?u1 ?predicate ?s1 . \n");
		buffer.append("?s1 profile:hasRating ?r1 .\n");
		buffer.append("?r1 profile:hasUserDefinedRating ?scoreRated .\n");
		buffer.append("FILTER (STR(?uid) = \"" + profiler.getUID() + "\") . \n\n");
		buffer.append("}");
		
		float minRated = (float) findMinimumValue(buffer.toString(), "scoreRated");
		
		if (minRated == Integer.MAX_VALUE) {
			return DEFAULT_MINIMUM_SCORE_RATED;
		}
		
		return minRated;
	}

	/**
	 * Finds minimum value of a given variable name from UserProfile.
	 * @param queryStr
	 * @param varName
	 * @return
	 */
	private static double findMinimumValue(String queryStr, String varName) {
		double ret = Integer.MAX_VALUE;
		Model model = RdfFileManager.getInstance().getRdfModel();
		if (model == null) return ret;
		
	    Query query = QueryFactory.create(queryStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			try {
			    double d = Double.parseDouble(qs.getLiteral(varName).toString());
			    if (d < ret) ret = d;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		qe.close();
		return ret;
	}
	
	/**
	 * Prohibits instantiations.
	 */
	private PreferencesUtils() {
	}
}

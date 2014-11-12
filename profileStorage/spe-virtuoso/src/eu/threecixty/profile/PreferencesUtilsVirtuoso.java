package eu.threecixty.profile;



/**
 * Utility class to find minimum values in UserProfile.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class PreferencesUtilsVirtuoso {

	private static final int DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED = 1;
	private static final float DEFAULT_MINIMUM_SCORE_RATED = 3;

	/**
	 * Gets minimum number of times visited.
	 *
	 * @param uid
	 * @return
	 */
	public static int getMinimumNumberOfTimesVisited(String uid) {
		return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
	}

	/**
	 * Gets minimum score rated.
	 *
	 * @param uid
	 * @return
	 */
	public static float getMinimumScoreRated(String uid) {
		if (uid == null || uid.equals("")) return DEFAULT_MINIMUM_SCORE_RATED;
/*		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");

		buffer.append("select  ?ratingValue\n");
		buffer.append("where {\n");
		buffer.append("?x a <http://ontologydesignpatterns.org/ont/dul/DUL.owl#Place> .\n");
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?x schema:name ?name .\n");
		buffer.append("?review schema:creator ?creator .\n");
		buffer.append("?creator schema:name ?creatorName .\n");
		buffer.append("?creator schema:url ?creatorURI.\n");
		buffer.append("?review schema:reviewRating ?rating .\n");
		buffer.append("?rating schema:ratingValue ?ratingValue.\n");
		buffer.append("?x dcterms:source <http://www.google.com> .\n");
		buffer.append("FILTER (fn:ends-with(STR(?creatorURI),\"" + uid + "\")) .\n");
		buffer.append("} \n ORDER BY ?ratingValue \n LIMIT 1");
		
		float ret = Float.MAX_VALUE;
		
		// TODO: create a function for get value
		// XXX: The following commented lines work well, but maybe there is a problem
		// when we deploy two servers: one for QueryManager and another for Virtuoso.
		
//		Query query = QueryFactory.create(buffer.toString());
//		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,
//				VirtuosoManager.getInstance().getVirtGraph());
//		ResultSet results = vqe.execSelect();
//		while (results.hasNext()) {
//			QuerySolution rs = results.nextSolution();
//			try {
//			    double d = rs.getLiteral("ratingValue").getDouble();
//			    if (d < ret) ret = (float) d;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		    
//		}
//		vqe.close();
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(buffer.toString());
		if (jsonObj == null) return DEFAULT_MINIMUM_SCORE_RATED;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return DEFAULT_MINIMUM_SCORE_RATED;
			ret = Float.parseFloat((jsonArr.getJSONObject(0)).getJSONObject("ratingValue").getString("value"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (ret == Float.MAX_VALUE) {
			return DEFAULT_MINIMUM_SCORE_RATED;
		}
		
		return ret;*/
		return DEFAULT_MINIMUM_SCORE_RATED;
	}

	/**
	 * Gets minimum number of times visited for friends.
	 *
	 * @param uid
	 * @return
	 */
	public static int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
	}

	/**
	 * Gets minimum score rated for friends.
	 *
	 * @param uid
	 * @return
	 */
	public static float getMinimumScoreRatedForFriends(String uid) {
		
/*		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("select  ?ratingValue\n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		buffer.append("?meroot schema:knows	?friends .\n");
		buffer.append("?friends profile:review	?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		
		
		//buffer.append("?meroot profile:review	?review .\n");
		buffer.append("?x a <http://ontologydesignpatterns.org/ont/dul/DUL.owl#Place> .\n");
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?x schema:name ?name .\n");
		buffer.append("?review schema:creator ?creator .\n");
		//buffer.append("?creator schema:name ?creatorName .\n");
		buffer.append("?creator schema:url ?creatorURI.\n");
		buffer.append("?review schema:reviewRating ?rating .\n");
		buffer.append("?rating schema:ratingValue ?ratingValue.\n");
		buffer.append("?x dcterms:source <http://www.google.com> .\n");
		buffer.append("FILTER (fn:ends-with(STR(?creatorURI),\"" + uid + "\")) .\n");
		buffer.append("} \n ORDER BY ?ratingValue \n LIMIT 1");
		
		float ret = Float.MAX_VALUE;
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(buffer.toString());
		if (jsonObj == null) return DEFAULT_MINIMUM_SCORE_RATED;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return DEFAULT_MINIMUM_SCORE_RATED;
			ret = Float.parseFloat((jsonArr.getJSONObject(0)).getJSONObject("ratingValue").getString("value"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (ret == Float.MAX_VALUE) {
			return DEFAULT_MINIMUM_SCORE_RATED;
		}
		return ret;*/
		return DEFAULT_MINIMUM_SCORE_RATED;
	}
	
	/**
	 * Prohibits instantiations.
	 */
	private PreferencesUtilsVirtuoso() {
	}
}

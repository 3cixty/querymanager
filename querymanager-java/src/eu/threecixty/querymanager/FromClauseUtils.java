package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.Configuration;

public class FromClauseUtils {
	
	private static final String WHERE = "WHERE";
	private static final String NOT_FROM_PROFILE = "NOT FROM <" + Configuration.PROFILE_GRAPH + ">\n";
	
//	private static final String SCHEMA_PREFIX = "<http://schema.org/>";
//	private static final String PROFILE_PREFIX = "<http://3cixty.com/ontology/profile/>";
//	private static final String PROFILE_GRAPH = Configuration.PROFILE_GRAPH;
//	
//	private static final String [] FROM_GRAPHS = {"http://www.w3.org/2002/07/owl#", "http://3cixty.com/yelp",
//		"http://3cixty.com/foursquare", "http://3cixty.com/googleplaces", "http://3cixty.com/events",
//		"http://data.linkedevents.org/kos/yelp/", "http://3cixty.com/hotelMetro", "http://3cixty.com/eventMetro",
//		"http://3cixty.com/topCategories", "http://data.linkedevents.org/kos/foursquare/", "http://3cixty.com/metro"};
	
//	private static final Node GIVEN_NAME_PREDICATE = NodeFactory.createURI(SCHEMA_PREFIX + ":givenName");
//	private static final Node FAMILY_NAME_PREDICATE = NodeFactory.createURI(SCHEMA_PREFIX + ":familyName");
//	private static final Node KNOWS_PREDICATE = NodeFactory.createURI(SCHEMA_PREFIX + ":knows");
//	private static final Node TRAY_PREDICATE = NodeFactory.createURI(PROFILE_PREFIX + ":trayElement");;
	
	/**
	 * Checks whether or not a given query contains "FROM" clause from UserProfile.
	 *
	 * @param query
	 * @return
	 */
	public static boolean containFromProfile(Query query) {
		if (query == null) return false;
		// TODO: remove comment lines
//		if (query.hasDatasetDescription()) { // contain FROM, FROM NAMED
//			List <String> graphUris = query.getGraphURIs();
//			if (graphUris != null && graphUris.contains(PROFILE_GRAPH)) return true;
//			List <String> namedGraphUris = query.getNamedGraphURIs();
//			if (namedGraphUris != null && namedGraphUris.contains(PROFILE_GRAPH)) return true;
//		}
		return false;
	}
	
	/**
	 * Adds <code>NOT FROM</code> to a given query.
	 * <br>
	 * Note that <code>NOT FROM</code> is not a standard query and is not supported by Jena.
	 * @param query
	 * @return a query in string format.
	 */
	public static String addNotFromTo(Query query) {
		if (query == null) return null;
		// query's keywords are always in upper-case after using query.toString
		String originalQuery = query.toString();
		int whereIndex = originalQuery.indexOf(WHERE);
		if (whereIndex < 0) return originalQuery; // it seems not to be existed
		else {
			return originalQuery.replaceFirst(WHERE, NOT_FROM_PROFILE + WHERE);
		}
	}
	
	/**
	 * Explicitly adds FROM GRAPHS to a given query to restrict information that can be got from sparql queries.
	 *
	 * XXX: Note that this strategy will affect that a sparql query cannot be able to get private information even though
	 * the system should be able to do so after a user grants permission to access to his data.
	 * @param query
	 */
	public static void addFromGraphs(Query query) {
		//TODO: remove comment lines
//		if (query == null) return;
//		for (String graph: FROM_GRAPHS) {
//			if (!query.usesGraphURI(graph)) query.addGraphURI(graph);
//		}
	}
	
//	private static String genSubjectVar(Query query) {
//		ElementGroup body = (ElementGroup) query.getQueryPattern();
//		int index = 0;
//		while (true) {
//			boolean found = false;
//			String tmpName = "x_" + index;
//			for (Element element: body.getElements()) {
//				if (element instanceof ElementTriplesBlock) {
//					ElementTriplesBlock etb = (ElementTriplesBlock) element;
//					Iterator <Triple> triples = etb.patternElts();
//					for ( ; triples.hasNext(); ) {
//						Triple triple = triples.next();
//						Node subjectNode = triple.getSubject();
//						if (!subjectNode.isVariable()) continue;
//						
//						if (subjectNode.getName().equals(tmpName)) {
//							found = true;
//							break;
//						}
//					}
//				}
//				if (found) break;
//			}
//			if (found) index++;
//			else return tmpName;
//		}
//	}
//
//	private static String genObjectVar(Query query) {
//		ElementGroup body = (ElementGroup) query.getQueryPattern();
//		int index = 0;
//		while (true) {
//			boolean found = false;
//			String tmpName = "y_" + index;
//			for (Element element: body.getElements()) {
//				if (element instanceof ElementTriplesBlock) {
//					ElementTriplesBlock etb = (ElementTriplesBlock) element;
//					Iterator <Triple> triples = etb.patternElts();
//					for ( ; triples.hasNext(); ) {
//						Triple triple = triples.next();
//						Node objectNode = triple.getObject();
//						if (!objectNode.isVariable()) continue;
//						
//						if (objectNode.getName().equals(tmpName)) {
//							found = true;
//							break;
//						}
//					}
//				}
//				if (found) break;
//			}
//			if (found) index++;
//			else return tmpName;
//		}
//	}
	
	private FromClauseUtils() {
	}
}

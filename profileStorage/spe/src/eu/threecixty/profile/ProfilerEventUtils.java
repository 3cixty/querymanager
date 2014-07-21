package eu.threecixty.profile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.ProfileManager.StartAndEndDate;

/**
 * Utility class for populating event information.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class ProfilerEventUtils {

	/**
	 * Adds event name into a given preference.
	 *
	 * @param model
	 * @param uID
	 */
	public static List <String> getEventNamesFromEventPreference(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
		StringBuffer buffer = new StringBuffer("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
	    buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
	    buffer.append("SELECT  DISTINCT  ?eventname\n");
	    buffer.append("WHERE {\n\n");
	    buffer.append("?root a owl:NamedIndividual .\n");
	    buffer.append("?root profile:hasUID ?uid .\n");
	    buffer.append("?root profile:hasPreference ?p1 . \n");
	    buffer.append("?p1 profile:hasEventPreference ?e1 . \n");
	    buffer.append("?e1 profile:hasEventDetailPreference ?ed1 . \n");
	    buffer.append("?ed1 profile:hasEventName ?eventname .\n");
	    buffer.append("?p1 profile:hasLike ?like .\n");
	    buffer.append("?like profile:hasLikeType ?liketype .\n");
	    buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
	    buffer.append("FILTER (STR(?liketype) = \"Event\") . \n\n");
	    buffer.append("}");

	    Query query = QueryFactory.create(buffer.toString());
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
		List <String> eventNames = new ArrayList <String>();
		
		String eventname = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	/**
	 * Adds preferred event StartDate and EndDate found in UserProfile to user preferences.
	 * @param model
	 * @param uID
	 */
	public static List <StartAndEndDate> getPreferredStartAndEndDates(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
		StringBuffer buffer = new StringBuffer("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
	    buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
	    buffer.append("SELECT  DISTINCT  ?startDate ?endDate \n");
	    buffer.append("WHERE {\n\n");
	    buffer.append("?root a owl:NamedIndividual .\n");
	    buffer.append("?root profile:hasUID ?uid .\n");
	    buffer.append("?root profile:hasPreference ?p1 . \n");
	    buffer.append("?p1 profile:hasEventPreference ?e1 . \n");
	    buffer.append("?e1 profile:hasEventDetailPreference ?ed1 . \n");
	    buffer.append("?ed1 profile:hasPreferredStartDate ?startDate .\n");
	    buffer.append("?ed1 profile:hasPreferredEndDate ?endDate .\n");
	    buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
	    buffer.append("}");

	    Query query = QueryFactory.create(buffer.toString());
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
		List <StartAndEndDate> startAndEndDates = new ArrayList<StartAndEndDate>();
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
	    
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			try {
			Date startDate = sdf.parse(qs.getLiteral("startDate").toString());
			Date endDate = sdf.parse(qs.getLiteral("endDate").toString());

			StartAndEndDate startAndEndDate = new StartAndEndDate(startDate, endDate);
			startAndEndDates.add(startAndEndDate);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		qe.close();

		return startAndEndDates;
	}


	/**
	 * Adds event names which were rated with a score more than a given <b>rating</b> into user preferences.
	 * @param model
	 * @param uID
	 * @param rating
	 */
	public static List<String> getEventNamesFromRating(Model model, String uID, float rating) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?eventname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    qStr += "?u1 profile:hasUserEventRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?r1 profile:hasUserInteractionMode ?mode .\n";
	    qStr += "?s1 profile:hasEventDetail ?ed1 .\n";
	    qStr += "?ed1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "FILTER (str(?mode) = \"Visited\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> eventNames = new ArrayList<String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	/**
	 * Adds event names which were visited with number of times more than a given <b>numberOfTimesVisited</b> into user preferences.
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 */
	public static List <String> getEventNamesFromNumberOfTimesVisited(Model model, String uID, int numberOfTimesVisited) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?eventname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    qStr += "?u1 profile:hasUserEventRating ?s1 .\n";
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    qStr += "?s1 profile:hasEventDetail ?h1 .\n";
	    qStr += "?h1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> eventNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static List <String> getEventNamesFromRatingOfFriends(Model model, String uID, float rating) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	    qStr += "SELECT  DISTINCT  ?eventname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?meroot a owl:NamedIndividual .\n";
	    qStr += "?meroot profile:hasUID ?uid .\n";
	    qStr += "?meroot foaf:knows ?root .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    qStr += "?u1 profile:hasUserEventRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?r1 profile:hasUserInteractionMode ?mode .\n";
	    qStr += "?s1 profile:hasEventDetail ?h1 .\n";
	    qStr += "?h1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "FILTER (str(?mode) = \"Visited\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> eventNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static List <String> getEventNamesFromNumberOfTimesVisitedOfFriends(Model model, String uID, int numberOfTimesVisited) {
		if (model == null || uID == null || uID.equals("")) return null;
		StringBuffer buffer = new StringBuffer("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
	    buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
	    buffer.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n");
	    buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
	    buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
	    buffer.append("SELECT  DISTINCT  ?eventname\n");
	    buffer.append("WHERE {\n\n");
	    buffer.append("?meroot a owl:NamedIndividual .\n");
	    buffer.append("?meroot profile:hasUID ?uid .\n");
	    buffer.append("?meroot foaf:knows ?root .\n");
	    buffer.append("?root profile:hasPreference ?p1 .\n");
	    buffer.append("?p1 profile:hasUserEnteredRatings ?u1 .\n");
	    buffer.append("?u1 profile:hasUserEventRating ?s1 .\n");
	    buffer.append("?s1 profile:hasNumberofTimesVisited ?n1  .\n");
	    buffer.append("?s1 profile:hasEventDetail ?h1 .\n");
	    buffer.append("?h1 profile:hasEventName ?eventname .\n");
	    buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
	    buffer.append("FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n");
	    buffer.append("}");

	    Query query = QueryFactory.create(buffer.toString());
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> eventNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	/**
	 * Adds event names which friends like to visit
	 * @param model
	 * @param uID
	 */
	public static List <String> getEventNamesWhichFriendsLikeToVisit(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
		StringBuffer buffer = new StringBuffer("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
	    buffer.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n");
	    buffer.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n");
	    buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");
	    buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
	    buffer.append("SELECT  DISTINCT  ?eventname\n");
	    buffer.append("WHERE {\n\n");
	    buffer.append("?meroot a owl:NamedIndividual .\n");
	    buffer.append("?meroot profile:hasUID ?uid .\n");
	    buffer.append("?meroot foaf:knows ?root .\n");
	    buffer.append("?root profile:hasPreference ?p1 .\n");
	    buffer.append("?p1 profile:hasUserEnteredRatings ?u1 .\n");
	    buffer.append("?u1 profile:hasUserEventRating ?s1 .\n");
	    buffer.append("?s1 profile:hasRating ?r1 .\n");
	    buffer.append("?r1 profile:hasUserInteractionMode ?mode .\n");
	    buffer.append("?s1 profile:hasEventDetail ?h1 .\n");
	    buffer.append("?h1 profile:hasEventName ?eventname .\n");
	    buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
	    buffer.append("FILTER (STR(?mode) = \"LikeTo\") . \n\n");
	    buffer.append("}");


	    Query query = QueryFactory.create(buffer.toString());
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> eventNames = new ArrayList<String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				eventNames.add(eventname);
			}
		}
		
		qe.close();

		return eventNames;
	}

	
	
	/**
	 * Prohibits instantiations.
	 */
	private ProfilerEventUtils() {
	}
}

package eu.threecixty.profile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.TemporalDetails;

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
	 * @param pref
	 * @param model
	 * @param uID
	 */
	public static void addEventName(Preference pref, Model model, String uID) {
	    String qStr = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?eventname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 . \n";
	    qStr += "?p1 profile:hasEventPreference ?e1 . \n";
	    qStr += "?e1 profile:hasEventDetailPreference ?ed1 . \n";
	    qStr += "?ed1 profile:hasEventName ?eventname .\n";
	    qStr += "?p1 profile:hasLike ?like .\n";
	    qStr += "?like profile:hasLikeType ?liketype .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (STR(?liketype) = \"Event\") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
		String eventname = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				Event event = new Event();
				EventDetail ed = new EventDetail();
				ed.setHasEventName(eventname);
				
				event.setHasEventDetail(ed);

			    events.add(event);
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}

	/**
	 * Adds preferred event StartDate and EndDate found in UserProfile to user preferences.
	 * @param pref
	 * @param model
	 * @param uID
	 */
	public static void addPreferredStartAndEndDate(Preference pref, Model model, String uID) {
	    String qStr = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?startDate ?endDate\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 . \n";
	    qStr += "?p1 profile:hasEventPreference ?e1 . \n";
	    qStr += "?e1 profile:hasEventDetailPreference ?ed1 . \n";
	    qStr += "?ed1 profile:hasPreferredStartDate ?startDate .\n";
	    qStr += "?ed1 profile:hasPreferredEndDate ?endDate .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
	    
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			try {
			Date startDate = sdf.parse(qs.getLiteral("startDate").toString());
			Date endDate = sdf.parse(qs.getLiteral("endDate").toString());

			Event event = new Event();
			EventDetail ed = new EventDetail();

			TemporalDetails td = new TemporalDetails();
			td.setHasDateFrom(startDate);
			td.setHasDateUntil(endDate);

			ed.setHasTemporalDetails(td);

			event.setHasEventDetail(ed);

			events.add(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}


	/**
	 * Adds event names which were rated with a score more than a given <b>rating</b> into user preferences.
	 * @param pref
	 * @param model
	 * @param uID
	 * @param rating
	 */
	public static void addEventNameFromRating(Preference pref, Model model, String uID, float rating) {
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
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?s1 profile:hasEventDetail ?ed1 .\n";
	    qStr += "?ed1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				Event event = new Event();
				EventDetail ed = new EventDetail();
				ed.setHasEventName(eventname);
				
				event.setHasEventDetail(ed);

			    events.add(event);
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}

	/**
	 * Adds event names which were visited with number of times more than a given <b>numberOfTimesVisited</b> into user preferences.
	 * @param pref
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 */
	public static void addEventNameFromNumberOfTimesVisited(Preference pref, Model model, String uID, int numberOfTimesVisited) {
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
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				Event event = new Event();
				EventDetail ed = new EventDetail();
				ed.setHasEventName(eventname);
				
				event.setHasEventDetail(ed);

			    events.add(event);
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addEventNameFromRatingOfFriends(Preference pref, Model model, String uID, float rating) {
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
	    qStr += "?s1 profile:hasEventDetail ?h1 .\n";
	    qStr += "?h1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				Event event = new Event();
				EventDetail ed = new EventDetail();
				ed.setHasEventName(eventname);
				
				event.setHasEventDetail(ed);

			    events.add(event);
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addEventNameFromNumberOfTimesVisitedOfFriends(Preference pref, Model model, String uID, int numberOfTimesVisited) {
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
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    qStr += "?s1 profile:hasEventDetail ?h1 .\n";
	    qStr += "?h1 profile:hasEventName ?eventname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Event> events = pref.getHasEvents();
	    if (events == null) events = new HashSet <Event>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String eventname = qs.getLiteral("eventname").getString();
			if (eventname != null && !eventname.equals("")) {
				Event event = new Event();
				EventDetail ed = new EventDetail();
				ed.setHasEventName(eventname);
				
				event.setHasEventDetail(ed);

			    events.add(event);
			}
		}
		
		qe.close();

		pref.setHasEvents(events);
	}
	
	/**
	 * Prohibits instantiations.
	 */
	private ProfilerEventUtils() {
	}
}

package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Preference;

/**
 * Utility class for populating information place.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class ProfilerPlaceUtils {
	
	/**
	 * Adds country name into preference.
	 *
	 * @param model
	 * 				RDF model.
	 * @param uID
	 * 				User identity.
	 */
	public static String getCountryName(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?countryname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root vcard:hasAddress ?address . \n";
	    qStr += "?address vcard:country-name ?countryname .";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
		String countryName = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			countryName = qs.getLiteral("countryname").getString();
			if (countryName != null) {
			    break;
			}
		}
		
		qe.close();
		return countryName;
	}

	/**
	 * Adds town name into preference.
	 *
	 * @param model
	 * 				The RDF model.
	 * @param uID
	 * 				User identity.
	 */
	public static String getTownName(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?locality\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root vcard:hasAddress ?address . \n";
	    qStr += "?address profile:townName ?locality .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
	    System.out.println(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
		String townName = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			townName = qs.getLiteral("locality").getString();
			if (townName != null) {
				System.out.println(townName);
			    break;
			}
		}
		
		qe.close();

		return townName;
	}

	/**
	 * Adds GPS coordinates into preference.
	 * @param model
	 * 				The RDF model.
	 * @param uID
	 * 				User identity.
	 */
	public static GpsCoordinate getCoordinates(Model model, String uID) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "PREFIX my: <java:eu.threecixty.functions.>\n\n";
	    qStr += "SELECT  DISTINCT  ?lon ?lat \n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root vcard:hasAddress ?address . \n";
	    qStr += "?address vcard:longitude ?lon . \n";
	    qStr += "?address vcard:latitude ?lat . \n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
		GpsCoordinate coordinate = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			double lon = qs.getLiteral("lon").getDouble();
			double lat = qs.getLiteral("lat").getDouble();
			
			coordinate = GpsCoordinateUtils.convert(lat, lon);
			if (coordinate != null) break;

		}
		
		qe.close();

		return coordinate;
	}


	/**
	 * Gets both Hotel and Place Names.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceNamesFromRating(Model model, String uID, float rating) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?placename\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    qStr += "?u1 ?userRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?r1 profile:hasUserInteractionMode ?mode .\n";
	    qStr += "?s1 ?ratingDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	   // qStr += "FILTER (str(?mode) = \"Visited\") . \n\n";
	    qStr += "}";
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> placeNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
				placeNames.add(placename);
			}
		}
		
		qe.close();

		return placeNames;
	}


	/**
	 * Gets both hotel and place names
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 * @return
	 */
	public static List <String> getPlaceNamesFromNumberOfTimesVisited(Model model, String uID, int numberOfTimesVisited) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?placename\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    //qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?u1 ?userRating ?s1 .\n";
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    //qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?s1 ?ratingDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> placeNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
				placeNames.add(placename);
			}
		}
		
		qe.close();

		return placeNames;
	}

	/**
	 * Gets both hotel and place names.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceNamesFromRatingOfFriends(Model model, String uID, float rating) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	    qStr += "SELECT  DISTINCT  ?placename\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?meroot a owl:NamedIndividual .\n";
	    qStr += "?meroot profile:hasUID ?uid .\n";
	    qStr += "?meroot foaf:knows ?root .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    qStr += "?u1 ?userRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?r1 profile:hasUserInteractionMode ?mode .\n";
	    qStr += "?s1 ?ratingDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    //qStr += "FILTER (str(?mode) = \"Visited\") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> placeNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
				placeNames.add(placename);
			}
		}
		
		qe.close();

		return placeNames;
	}

	/**
	 * Gets both hotel and place names.
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 * @return
	 */
	public static List <String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(Model model, String uID, int numberOfTimesVisited) {
		if (model == null || uID == null || uID.equals("")) return null;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	    qStr += "SELECT  DISTINCT  ?placename\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?meroot a owl:NamedIndividual .\n";
	    qStr += "?meroot profile:hasUID ?uid .\n";
	    qStr += "?meroot foaf:knows ?root .\n";
	    qStr += "?root profile:hasPreference ?p1 .\n";
	    qStr +=	"?p1 profile:hasUserEnteredRatings ?u1 .\n";
	    //qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?u1 ?userRating ?s1 .\n";
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    //qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?s1 ?ratingDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		List <String> placeNames = new ArrayList <String>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
				placeNames.add(placename);
			}
		}
		
		qe.close();

		return placeNames;
	}

	public static void addDays(Preference pref, Period period) {
		try {
			//
//			java.text.DateFormat format = new java.text.SimpleDateFormat("d/M/yyyy");
//			Date startDate = format.parse("1/1/2014");
//			Date startDate = new Date();
//
//			Date endDate = new Date(startDate.getTime() + days * 24 * 60 * 60 * 1000L);
//			Period period = new Period(startDate, endDate);
			Set <Period> periods = pref.getHasPeriods();
			if (periods == null) periods = new HashSet <Period>();
			periods.add(period);
			pref.setHasPeriods(periods);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ProfilerPlaceUtils() {
	}
}

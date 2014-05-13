package eu.threecixty.profile;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.models.NatureOfPlace;
import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Preference;

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
	 * @param pref
	 * 				The preference
	 * @param model
	 * 				RDF model.
	 * @param uID
	 * 				User identity.
	 */
	public static void addCountryName(Preference pref, Model model, String uID) {
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n\n";
	    qStr += "SELECT  DISTINCT  ?countryname\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root vcard:hasName ?name .\n";
	    qStr += "?name vcard:given-name ?gn .\n";
	    qStr += "?name vcard:family-name ?fn .\n";
	    qStr += "?root ?p1 ?uid .\n";
	    qStr += "?root ?p2 ?oPrefs .\n";
	    qStr += " ?oPrefs ?p3 ?oOPrefs .\n\n";
	    qStr += "?oOPrefs ?p4 ?oOOPrefs .    ?oOOPrefs ?p5 ?oOOOPrefs .  ?oOOPrefs ?p6 ?oOOOOPrefs .  ?oOOOPrefs vcard:country-name ?countryname .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (STR(?oOOOOPrefs) = \"Country\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		String countryName = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			countryName = qs.getLiteral("countryname").getString();
			if (countryName != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(countryName);
			    pd.setHasNatureOfPlace(NatureOfPlace.Country);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}

	/**
	 * Adds town name into preference.
	 *
	 * @param pref
	 * 				The preference.
	 * @param model
	 * 				The RDF model.
	 * @param uID
	 * 				User identity.
	 */
	public static void addTownName(Preference pref, Model model, String uID) {
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	    qStr += "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?locality\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root ?p1 ?uid .\n";
	    qStr += "?root ?p2 ?oPrefs .\n";
	    qStr += " ?oPrefs ?p3 ?oOPrefs .\n\n";
	    qStr += "?oOPrefs ?p4 ?oOOPrefs .    ?oOOPrefs ?p5 ?oOOOPrefs .  ?oOOOPrefs  profile:townName ?locality .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		ResultSet rs = qe.execSelect();
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		String townName = null;
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			townName = qs.getLiteral("locality").getString();
			if (townName != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(townName);
			    pd.setHasNatureOfPlace(NatureOfPlace.City);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addPlaceNameFromRating(Preference pref, Model model, String uID, float rating) {
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
	    qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(placename);
			    pd.setHasNatureOfPlace(NatureOfPlace.Others);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addPlaceNameFromNumberOfTimesVisited(Preference pref, Model model, String uID, int numberOfTimesVisited) {
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
	    qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(placename);
			    pd.setHasNatureOfPlace(NatureOfPlace.Others);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}


	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addPlaceNameFromRatingOfFriends(Preference pref, Model model, String uID, float rating) {
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
	    qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?s1 profile:hasRating ?r1 .\n";
	    qStr += "?r1 profile:hasUserDefinedRating ?r2 .\n";
	    qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?r2 >= " + rating + ") . \n\n";
	    qStr += "}";
	    
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(placename);
			    pd.setHasNatureOfPlace(NatureOfPlace.Others);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}

	// TODO: this query only works with HotelPlace. Need to update RDF UserProfile Model so that
	// we only use something generic, for example instead of using hasHotelDetail, we use hasPlaceDetail, ...
	public static void addPlaceNameFromNumberOfTimesVisitedOfFriends(Preference pref, Model model, String uID, int numberOfTimesVisited) {
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
	    qStr += "?u1 profile:hasUserHotelRating ?s1 .\n";
	    qStr += "?s1 profile:hasNumberofTimesVisited ?n1  .\n";
	    qStr += "?s1 profile:hasHotelDetail ?h1 .\n";
	    qStr += "?h1 profile:hasPlaceName ?placename .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uID + "\") . \n\n";
	    qStr += "FILTER (?n1 >= " + numberOfTimesVisited + ") . \n\n";
	    qStr += "}";

	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
	    Set <Place> places = pref.getHasPlaces();
	    if (places == null) places = new HashSet <Place>();
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String placename = qs.getLiteral("placename").getString();
			if (placename != null) {
			    Place place = new Place();
			    PlaceDetail pd = new PlaceDetail();
			    pd.setHasPlaceName(placename);
			    pd.setHasNatureOfPlace(NatureOfPlace.Others);
			    place.setHasPlaceDetail(pd);
			    places.add(place);
			}
		}
		
		qe.close();

		pref.setHasPlaces(places);
	}

	private ProfilerPlaceUtils() {
	}
}

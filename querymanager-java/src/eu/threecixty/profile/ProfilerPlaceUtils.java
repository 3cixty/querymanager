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
import eu.threecixty.profile.models.UserPlaceRating;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Preference;

/**
 * Utility class for populating informaton place.
 *
 * @author Cong-Kinh NGUYEn
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
		
		String countryName = null;
		if (rs.hasNext()) {
			QuerySolution qs = rs.next();
			countryName = qs.getLiteral("countryname").getString();
			
		}
		
		qe.close();

		if (countryName != null) {
		    Set <UserPlaceRating> places = pref.getHasPlaces();
		    if (places == null) places = new HashSet <UserPlaceRating>();
		    UserPlaceRating place = new UserPlaceRating();
		    PlaceDetail pd = new PlaceDetail();
		    pd.setHasName(countryName);
		    pd.setIsTheNatureOfPlace(NatureOfPlace.Country);
		    place.setHasPlaceDetail(pd);
		    places.add(place);
		    pref.setHasPlaces(places);
		}
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
		
		String countryName = null;
		if (rs.hasNext()) {
			QuerySolution qs = rs.next();
			countryName = qs.getLiteral("locality").getString();
			
		}
		
		qe.close();

		if (countryName != null) {
		    Set <UserPlaceRating> places = pref.getHasPlaces();
		    if (places == null) places = new HashSet <UserPlaceRating>();
		    UserPlaceRating place = new UserPlaceRating();
		    PlaceDetail pd = new PlaceDetail();
		    pd.setHasName(countryName);
		    pd.setIsTheNatureOfPlace(NatureOfPlace.City);
		    place.setHasPlaceDetail(pd);
		    places.add(place);
		    pref.setHasPlaces(places);
		}
	}

	private ProfilerPlaceUtils() {
	}
}

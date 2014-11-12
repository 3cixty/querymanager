package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Preference;

/**
 * Utility class for populating information place.
 *
 * @author Cong-Kinh NGUYEN, Rachit Agarwal
 *
 */
public class ProfilerPlaceUtilsVirtuoso {
	
	/**
	 * Gets country name.
	 *
	 * @param uID
	 * 				User identity.
	 */
	public static String getCountryName(String uID) {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("select  ?countryName\n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		buffer.append("?meroot schema:address	?address .\n");
		buffer.append("?address schema:addressCountry	?countryName .\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
		buffer.append("}");
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(buffer.toString());
		if (jsonObj == null) return null;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return null;
			return jsonArr.getJSONObject(0).getJSONObject("countryName").getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Gets town name.
	 *
	 * @param uID
	 * 				User identity.
	 */
	public static String getTownName(String uID) {
		if (uID == null || uID.equals("")) return null;

		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("select  ?townName\n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		buffer.append("?meroot schema:address	?address .\n");
		buffer.append("?address schema:addressLocality	?townName .\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
		buffer.append("}");
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(buffer.toString());
		if (jsonObj == null) return null;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return null;
			return jsonArr.getJSONObject(0).getJSONObject("townName").getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Gets GPS coordinates.
	 * @param uID
	 * 				User identity.
	 */
	public static GpsCoordinate getCoordinates(String uID) {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("select  ?lon ?lat \n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		buffer.append("?meroot schema:homeLocation	?homeLocation .\n");
		buffer.append("?homeLocation schema:geo	?geo .\n");
		buffer.append("?geo schema:latitude	?lat .\n");
		buffer.append("?geo schema:longitude ?lon .\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
		buffer.append("}");
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(buffer.toString());
		if (jsonObj == null) return null;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return null;
			double lat = Double.parseDouble(jsonArr.getJSONObject(0).getJSONObject("lat").getString("value"));
			double lon = Double.parseDouble(jsonArr.getJSONObject(0).getJSONObject("lon").getString("value"));
			GpsCoordinate coordinate = new GpsCoordinate(lat, lon);
			return coordinate;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Gets Place Names.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceNamesFromRating(String uID, float rating) {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("SELECT  ?name \n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		buffer.append("?meroot profile:review	?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?review schema:itemReviewed	?itemReviewed .\n");
		buffer.append("?itemReviewed schema:name	?name .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		buffer.append("}");
		
		return getPlaceNamesFromQuery(buffer.toString());
	}


	/**
	 * Gets both hotel and place names
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 * @return
	 */
	public static List <String> getPlaceNamesFromNumberOfTimesVisited(String uID, int numberOfTimesVisited) {
		return null;
	}

	/**
	 * Gets both hotel and place names.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceNamesFromRatingOfFriends(String uID, float rating) {
		if (uID == null || uID.equals("")) return null;

		StringBuffer buffer = new StringBuffer();
		buffer.append("PREFIX schema: <http://schema.org/>\n");
		buffer.append("PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n");
		buffer.append("PREFIX dcterms: <http://purl.org/dc/terms/>\n");
		buffer.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n");
		buffer.append("PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n");

		buffer.append("SELECT  ?name \n");
		buffer.append("where {\n");
		buffer.append("?meroot rdf:type	foaf:Person .\n");
		buffer.append("?meroot profile:userID	?uid .\n");
		
		buffer.append("?meroot schema:knows	?knows .\n");
		
		buffer.append("?knows profile:review	?review .\n"); // friends' review
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?review schema:itemReviewed	?itemReviewed .\n");
		buffer.append("?itemReviewed schema:name	?name .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		buffer.append("}");

	    return getPlaceNamesFromQuery(buffer.toString());
	}

	/**
	 * Gets both hotel and place names.
	 * @param model
	 * @param uID
	 * @param numberOfTimesVisited
	 * @return
	 */
	public static List <String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(String uID, int numberOfTimesVisited) {
		if (uID == null || uID.equals("")) return null;
		return null;
	}

	private static List<String> getPlaceNamesFromQuery(String qStr) {
	    
		List <String> placeNames = new ArrayList <String>();
		
		JSONObject jsonObj = VirtuosoManager.getInstance().executeQuery(qStr);
		if (jsonObj == null) return placeNames;
		
		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			for (int index = 0; index < jsonArr.length(); index++) {
				placeNames.add(jsonArr.getJSONObject(index).getJSONObject("name").getString("value"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
	
	private ProfilerPlaceUtilsVirtuoso() {
	}
}

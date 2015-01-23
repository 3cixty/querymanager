package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.Configuration;
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
	
	private static final String PREFIXES = Configuration.PREFIXES;
	private static final String FROM_GOOGLE_PLACE_GRAPH = "FROM <http://3cixty.com/googleplaces>\n";
	private static final String FROM_PLACE_RATINGS_GRAPH = "FROM <http://3cixty.com/placesRating>\n";
	
	/**
	 * Gets country name.
	 *
	 * @param uID
	 * 				User identity.
	 */
	public static String getCountryName(String uID) throws TooManyConnections {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("select  ?countryName\n");
		buffer.append(" from <" + VirtuosoManager.getInstance().getGraph(uID) + "> ");
		buffer.append("where {\n");
		buffer.append(getPersonURI(uID) + " schema:address	?address .\n");
		buffer.append("?address schema:addressCountry	?countryName .\n");
		
		buffer.append("}");
		
		JSONObject jsonObj;
		try {
			jsonObj = VirtuosoManager.getInstance().executeQueryWithDBA(buffer.toString());
			if (jsonObj == null) return null;

			try {
				JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
				if (jsonArr.length() == 0) return null;
				return jsonArr.getJSONObject(0).getJSONObject("countryName").getString("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		
		return null;
	}

	/**
	 * Gets town name.
	 *
	 * @param uID
	 * 				User identity.
	 */
	public static String getTownName(String uID) throws TooManyConnections {
		if (uID == null || uID.equals("")) return null;

		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("select  ?townName\n");
		buffer.append(" from <" + VirtuosoManager.getInstance().getGraph(uID) + "> ");
		buffer.append("where {\n");
		buffer.append( getPersonURI(uID) + " schema:address	?address .\n");
		buffer.append("?address schema:addressLocality	?townName .\n");
		
		buffer.append("FILTER (STR(?uid) = \"" + uID + "\") . \n\n");
		buffer.append("}");
		
		JSONObject jsonObj;
		try {
			jsonObj = VirtuosoManager.getInstance().executeQueryWithDBA(buffer.toString());
			if (jsonObj == null) return null;
			
			try {
				JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
				if (jsonArr.length() == 0) return null;
				return jsonArr.getJSONObject(0).getJSONObject("townName").getString("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		
		return null;
	}

	/**
	 * Gets GPS coordinates.
	 * @param uID
	 * 				User identity.
	 */
	public static GpsCoordinate getCoordinates(String uID) throws TooManyConnections {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("select  ?lon ?lat \n");
		buffer.append(" from <" + VirtuosoManager.getInstance().getGraph(uID) + "> ");
		buffer.append("where {\n");
		buffer.append(getPersonURI(uID) + " schema:homeLocation	?homeLocation .\n");
		buffer.append("?homeLocation schema:geo	?geo .\n");
		buffer.append("?geo schema:latitude	?lat .\n");
		buffer.append("?geo schema:longitude ?lon .\n");
		buffer.append("}");
		
		JSONObject jsonObj;
		try {
			jsonObj = VirtuosoManager.getInstance().executeQueryWithDBA(buffer.toString());
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
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		
		return null;
	}


	/**
	 * Actually, this method is to get a list of place Ids.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceIdsFromRating(String uID, float rating) throws TooManyConnections {
		if (uID == null || uID.equals("")) return null;
		
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("SELECT  ?x \n");
		buffer.append(FROM_GOOGLE_PLACE_GRAPH);
		buffer.append(FROM_PLACE_RATINGS_GRAPH);
		buffer.append("where {\n");
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		buffer.append("?review schema:creator ?creator . \n");
		buffer.append("?creator schema:url " + getGoogleReviewCreator(uID) + ".\n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		buffer.append("}");
		
		return getPlaceIdsFromQuery(buffer.toString());
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
	 * Gets place IDs.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceIdsFromRatingOfFriends(String uID, float rating) throws TooManyConnections {
		if (uID == null || uID.equals("")) return null;

		List <String> friendUids = getFriendUIDs(uID);
		if (friendUids == null || friendUids.size() == 0) return null;
		
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("SELECT  ?x \n");
		buffer.append(FROM_GOOGLE_PLACE_GRAPH);
		buffer.append(FROM_PLACE_RATINGS_GRAPH);
		buffer.append("where {\n");
		
		//buffer.append(getPersonURI(uID) +  " schema:knows	?knows .\n");
		
		//buffer.append("?knows profile:userID	?friendsUID .\n"); // friends' UID
		
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		buffer.append("?review schema:creator ?creator . \n");
		buffer.append("?creator schema:url ?creatorURI .\n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
//		buffer.append("FILTER(fn:ends-with(STR(?creatorURI), fn:substring(STR(?knows), "
//		        + GetSetQueryStrings.PROFILE_URI.length() + "))) \n");
		buffer.append("FILTER(");
		boolean first = true;
		for (String friendUid: friendUids) {
			if (first) {
				buffer.append("(?creatorURI = <https://plus.google.com/" + friendUid + ">)");
				first = false;
			} else buffer.append("|| (?creatorURI = <https://plus.google.com/" + friendUid + ">)");
		}
		buffer.append(")");
		buffer.append("}");

	    return getPlaceIdsFromQuery(buffer.toString());
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

	private static List<String> getPlaceIdsFromQuery(String qStr) throws TooManyConnections {
	    
		List <String> placeNames = new ArrayList <String>();
		
		JSONObject jsonObj;
		try {
			jsonObj = VirtuosoManager.getInstance().executeQueryWithDBA(qStr);
			if (jsonObj == null) return placeNames;
			
			try {
				JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
				for (int index = 0; index < jsonArr.length(); index++) {
					placeNames.add(jsonArr.getJSONObject(index).getJSONObject("x").getString("value"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return placeNames;
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}
	
	public static List <String> getFriendUIDs(String uid) throws TooManyConnections {
		List <String> friendUids = new LinkedList <String>();
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("SELECT  ?friendUID \n");
		buffer.append("FROM <" + VirtuosoManager.getInstance().getGraph(uid) + ">\n");
		buffer.append("where {\n");
		
		buffer.append(getPersonURI(uid) +  " schema:knows	?knows .\n");
		
		buffer.append("?knows profile:userID	?friendUID .\n"); // friend's UID
		
		buffer.append("}");
		
		JSONObject jsonObj;
		try {
			jsonObj = VirtuosoManager.getInstance().executeQueryWithDBA(buffer.toString());
			if (jsonObj == null) return friendUids;
			
			try {
				JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
				for (int index = 0; index < jsonArr.length(); index++) {
					friendUids.add(jsonArr.getJSONObject(index).getJSONObject("friendUID").getString("value"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return friendUids;
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}
	
	private static String getGoogleReviewCreator(String uid) {
		return "<https://plus.google.com/" + uid + ">";
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
	
	private static String getPersonURI(String uid) {
		return "<" + GetSetQueryStrings.PROFILE_URI + uid + ">";
	}
	
	private ProfilerPlaceUtilsVirtuoso() {
	}
}

package eu.threecixty.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.Configuration;
import eu.threecixty.cache.ProfileCacheManager;
import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * Utility class for populating information place.
 *
 * @author Cong-Kinh NGUYEN, Rachit Agarwal
 *
 */
public class MySQLProfilerPlaceUtils {
	
	private static final String PREFIXES = Configuration.PREFIXES;
	private static final String FROM_GOOGLE_PLACE_GRAPH = "FROM <http://3cixty.com/googleplaces>\n";
	private static final String FROM_PLACE_RATINGS_GRAPH = "FROM <http://3cixty.com/placesRating>\n";
	private static final String FROM_USERPROFILE_MANUAL_GRAPH = "FROM <http://3cixty.com/userprofile>\n";
	
	private static final String JSON_APP_FORMAT = "application/sparql-results+json";
	
	private static final Logger LOGGER = Logger.getLogger(
			 UserUtils.class.getName());


	/**
	 * Actually, this method is to get a list of place Ids.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceIdsFromRating(UserProfile userProfile,
			float rating) throws IOException, UnknownException {
		String googleUID = getGoogleUID(userProfile);
		if (googleUID == null) return new ArrayList <String>();
		
		StringBuffer buffer = new StringBuffer(PREFIXES);
		
		buffer.append("SELECT  ?x \n");
		buffer.append(FROM_GOOGLE_PLACE_GRAPH);
		buffer.append(FROM_PLACE_RATINGS_GRAPH);
		buffer.append(FROM_USERPROFILE_MANUAL_GRAPH);
		buffer.append("where {\n");
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		buffer.append("?review schema:creator ?creator . \n");
		buffer.append("?creator schema:url " + getGoogleReviewCreator(googleUID) + ".\n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		buffer.append("}");
		
		return getPlaceIdsFromQuery(buffer.toString());
	}

	/**
	 * Gets place IDs.
	 * @param model
	 * @param uID
	 * @param rating
	 * @return
	 */
	public static List <String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float rating) throws IOException, UnknownException {

		List <String> googleUIDsFromFriends = getGoogleUIDsFromFriends(userProfile);
		if (googleUIDsFromFriends == null || googleUIDsFromFriends.size() == 0) return null;
		
		StringBuffer buffer = new StringBuffer(PREFIXES);

		buffer.append("SELECT  ?x \n");
		buffer.append(FROM_GOOGLE_PLACE_GRAPH);
		buffer.append(FROM_PLACE_RATINGS_GRAPH);
		buffer.append(FROM_USERPROFILE_MANUAL_GRAPH);
		buffer.append("where {\n");
		
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		buffer.append("?review schema:creator ?creator . \n");
		buffer.append("?creator schema:url ?creatorURI .\n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		
		StringBuilder tmpBuilder = new StringBuilder();
		boolean first = true;
		for (String googleUidFromFriend: googleUIDsFromFriends) {
			if (first) {
				tmpBuilder.append("(?creatorURI = <https://plus.google.com/" + googleUidFromFriend + ">)");
				first = false;
			} else tmpBuilder.append("|| (?creatorURI = <https://plus.google.com/" + googleUidFromFriend + ">)");
		}
		if (!first) {
		    buffer.append("FILTER(").append(tmpBuilder.toString());
		    buffer.append(")");
		} else { // all friends are facebook UIDs
			return null;
		}
		buffer.append("}");

	    return getPlaceIdsFromQuery(buffer.toString());
	}

	private static List<String> getPlaceIdsFromQuery(
			String qStr) throws IOException, UnknownException {
	    
		List <String> placeNames = new ArrayList <String>();
		StringBuilder result = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(qStr, JSON_APP_FORMAT,
				SparqlEndPointUtils.HTTP_POST, result);
		JSONObject jsonObj;

		jsonObj = new JSONObject(result.toString());

		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			for (int index = 0; index < jsonArr.length(); index++) {
				placeNames.add(jsonArr.getJSONObject(index).getJSONObject("x").getString("value"));
			}

		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
			throw new UnknownException(e);
		}
		return placeNames;
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
	
	private static List <String> getGoogleUIDsFromFriends(UserProfile userProfile) {
		// firstly, get 3cixty UIDs from user's friends list
		Set <String> _3cixtyUIDs = userProfile.getKnows();
		if (_3cixtyUIDs == null) {
			_3cixtyUIDs = new HashSet<String>();
		}
		
		// secondly, get 3cixty UIDs from accompanying list
		Set <Accompanying> accompanyings = userProfile.getAccompanyings();
		if (accompanyings != null) {
			for (Accompanying accompanying: accompanyings) {
				String tmp3cixtyUID = accompanying.getHasAccompanyUserid2ST();
				if (!_3cixtyUIDs.contains(tmp3cixtyUID)) _3cixtyUIDs.add(tmp3cixtyUID);
			}
		}
		List <String> results = ProfileCacheManager.getInstance().getGoogleUIDsOfFriends(userProfile.getHasUID());
		if (results != null) return results;
		results = UserUtils.getGoogleUidsFrom3cixtyUIDs(_3cixtyUIDs);
		if (results != null) {
			ProfileCacheManager.getInstance().putGoogleUIDsOfFriens(userProfile.getHasUID(), results);
		}
		return results;
	}
	
	private static String getGoogleUID(UserProfile userProfile) {
		for (ProfileIdentities pi: userProfile.getHasProfileIdenties()) {
			if (pi.getHasSourceCarrier().equals(GoogleAccountUtils.GOOGLE_SOURCE))
				return pi.getHasUserAccountID();
		}
		return null;
	}
	
	private MySQLProfilerPlaceUtils() {
	}
}

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
	
	private static final String JSON_APP_FORMAT = "application/sparql-results+json";
	
	private static final Logger LOGGER = Logger.getLogger(
			 UserUtils.class.getName());

	/**
	 * Find placeIDs and the corresponding social scores.
	 * @param profile
	 * @param rating
	 * @param placeIds
	 * @param socialScores
	 * @throws IOException
	 * @throws UnknownException
	 */
	public static void findPlaceIdsAndSocialScore(UserProfile profile, float rating,
			List<String> placeIds, List<Double> socialScores) throws IOException, UnknownException {
		String googleUID = getGoogleUID(profile);
		if (googleUID == null) return;
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("SELECT  ?x \n");
		buffer.append("where {\n");
		buffer.append("?x schema:review ?review .\n");
		buffer.append("?review schema:reviewRating	?reviewRating .\n");
		buffer.append("?reviewRating schema:ratingValue ?ratingValue.\n");
		buffer.append("?review schema:creator ?creator . \n");
		buffer.append("?creator schema:url " + getGoogleReviewCreator(googleUID) + ".\n");
		buffer.append("FILTER (xsd:decimal(?ratingValue) >= " + rating + ") . \n\n");
		buffer.append("}");
		List <String> rets = getPlaceIdsFromQuery(buffer.toString());
		if (rets == null) return;
		placeIds.addAll(rets);
		for (int i = 0; i < placeIds.size(); i++) {
			socialScores.add(1.0);
		}
	}

	public static void findPlaceIdsAndSocialScoreForFriends(UserProfile profile,
			float rating, List<String> placeIds, List<Double> socialScores) throws IOException, UnknownException {
		// TODO Auto-generated method stub
		List <String> googleUIDsFromFriends = getGoogleUIDsFromFriends(profile);
		if (googleUIDsFromFriends == null || googleUIDsFromFriends.size() == 0) return;
		
		StringBuffer buffer = new StringBuffer();

		buffer.append("select ?x (SUM(?result)/(2*?count2) as ?socialScore) \n");
		buffer.append("{ select ?creator ?x (count(?x) as ?count2) (?sum/(?maxRating*?count) as ?result) \n");
		buffer.append("{ select ?creator ?x (MAX(?ratingValue) as ?maxRating) (count(?creator)as ?count) (SUM(?ratingValue) as ?sum) \n");
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
			return;
		}
		buffer.append("} \n");
		buffer.append("Group by ?creator ?x \n");
		buffer.append("} } Group by ?x ?count2");
		
		StringBuilder result = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(buffer.toString(), JSON_APP_FORMAT,
				SparqlEndPointUtils.HTTP_POST, result);
		JSONObject jsonObj;

		jsonObj = new JSONObject(result.toString());

		try {
			JSONArray jsonArr = jsonObj.getJSONObject("results").getJSONArray("bindings");
			for (int index = 0; index < jsonArr.length(); index++) {
				placeIds.add(jsonArr.getJSONObject(index).getJSONObject("x").getString("value"));
				String score = jsonArr.getJSONObject(index).getJSONObject("socialScore").getString("value");
				socialScores.add(Double.parseDouble(score));
			}

		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
			throw new UnknownException(e);
		}
	}
	

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
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("SELECT  ?x \n");
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
		
		StringBuffer buffer = new StringBuffer();

		buffer.append("SELECT  ?x \n");
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
			if (pi.getHasSourceCarrier().equals(SPEConstants.GOOGLE_SOURCE))
				return pi.getHasUserAccountID();
		}
		return null;
	}
	
	private MySQLProfilerPlaceUtils() {
	}
}

package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.theresis.humanization.datastorage.ProfileException;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.datastorage.ProfileManager.ProfileStatus;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

public class ThalesProfileManagerImpl implements ProfileManager {

	public UserProfile getProfile(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveProfile(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean existUID(String uid) {
		ThalesProfileManagerAndSession profileMngAndSession = new ThalesProfileManagerAndSession(uid);
		try {
			ProfileStatus status =  profileMngAndSession.profileMgr.getProfileStatus(profileMngAndSession.session, uid);
			if (status == ProfileStatus.ACTIVE) return true;
		} catch (ProfileException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getMinimumNumberOfTimesVisited(String uid) {
		List <Integer> numbers = new ArrayList <Integer>();
		findNumberOfTimes(uid, "hasUserHotelRating", numbers);
		findNumberOfTimes(uid, "hasUserPlaceRating", numbers);
		findNumberOfTimes(uid, "hasUserEventRating", numbers);
		int min = Integer.MAX_VALUE;
		for (Integer number: numbers) {
			if (min > number) min = number;
		}
		return min;
	}

	public float getMinimumScoreRated(String uid) {
		List <Double> scores = new ArrayList <Double>();
		findScoresRated(uid, "hasUserHotelRating", scores);
		findScoresRated(uid, "hasUserPlaceRating", scores);
		findScoresRated(uid, "hasUserEventRating", scores);
		double min = Double.MAX_VALUE;
		for (Double score: scores) {
			if (min > score) min = score;
		}
		return (float) min;
	}

	public int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		List <Integer> numbers = new ArrayList <Integer>();
		findNumberOfTimesVisitedByFriends(uid, "hasUserHotelRating", numbers);
		findNumberOfTimesVisitedByFriends(uid, "hasUserPlaceRating", numbers);
		findNumberOfTimesVisitedByFriends(uid, "hasUserEventRating", numbers);
		int min = Integer.MAX_VALUE;
		for (Integer number: numbers) {
			if (min > number) min = number;
		}
		return min;
	}

	public float getMinimumScoreRatedForFriends(String uid) {
		List <Double> scores = new ArrayList <Double>();
		findScoresRatedByFriends(uid, "hasUserHotelRating", scores);
		findScoresRatedByFriends(uid, "hasUserPlaceRating", scores);
		findScoresRatedByFriends(uid, "hasUserEventRating", scores);
		double min = Double.MAX_VALUE;
		for (Double score: scores) {
			if (min > score) min = score;
		}
		return (float) min;
	}

	public String getCountryName(String uid) {
		return getInformationFromProfileAddress(uid, "vcard:country-name");
	}

	public String getTownName(String uid) {
		return getInformationFromProfileAddress(uid, ":townName");
	}

	public GpsCoordinate getCoordinate(String uid) {
		ThalesProfileManagerAndSession profileMngAndSession = new ThalesProfileManagerAndSession(uid);
		String vcardLat = "vcard:hasAddress/vcard:latitude";
		String vcardLon = "vcard:hasAddress/vcard:longitude";

		List<String> propertyPaths = new ArrayList<String>();
		propertyPaths.add(vcardLat);
		propertyPaths.add(vcardLon);
		try {
			Collection<ValuedProperty> propertyValues =  profileMngAndSession.profileMgr.getProfileProperties(
					profileMngAndSession.session, uid, propertyPaths);
			if (propertyValues == null || propertyValues.size() <= 1) return null;
			String latStr = propertyValues.iterator().next().getValue(0);
			String lonStr = propertyValues.iterator().next().getValue(0);
			return new GpsCoordinate(getDoubleFromXSDValue(latStr), getDoubleFromXSDValue(lonStr));
		} catch (ProfileException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getPlaceNamesFromRating(String uid, float rating) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceNamesFromRatingOfFriends(String uid,
			float rating) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromEventPreferences(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StartAndEndDate> getPreferredStartAndEndDates(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromRating(String uid, float rating) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisited(String uid,
			int number) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromRatingOfFriends(String uid,
			float rating) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesWhichFriendsLikeToVisit(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IDMapping> getIDMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Find scores rated by user and store in a given list.
	 * @param uid
	 * 				Google UID
	 * @param predicate
	 * 				Predicate, for example <code>hasUserHotelRating, hasUserPlaceRating, hasUserEventRating</code>
	 * @param scores
	 * 				List of scores
	 */
	private void findScoresRated(String uid, String predicate, List <Double> scores) {
		//String ratingPath = ":hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasRating/:hasUserDefinedRating";
		String ratingPath = ":hasPreference/:hasUserEnteredRatings/" + predicate + "/:hasRating/:hasUserDefinedRating";
		findScoresRatedFromPath(uid, ratingPath, scores);
	}

	/**
	 * Find scores rated by friends and store in a given list.
	 * @param uid
	 * 				Google UID
	 * @param predicate
	 * 				Predicate, for example <code>hasUserHotelRating, hasUserPlaceRating, hasUserEventRating</code>
	 * @param scores
	 * 				List of scores
	 */
	private void findScoresRatedByFriends(String uid, String predicate, List <Double> scores) {
		//String ratingPath = "foaf:knows/:hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasRating/:hasUserDefinedRating";
		String ratingPath = "foaf:knows/:hasPreference/:hasUserEnteredRatings/" + predicate + "/:hasRating/:hasUserDefinedRating";
		findScoresRatedFromPath(uid, ratingPath, scores);
	}
	
	/**
	 * Find number of times visited by friends and store in a given list.
	 * @param uid
	 * 				Google UID
	 * @param predicate
	 * 				Predicate, for example <code>hasUserHotelRating, hasUserPlaceRating, hasUserEventRating</code>
	 * @param scores
	 * 				List of scores
	 */
	private void findNumberOfTimesVisitedByFriends(String uid, String predicate, List <Integer> numbers) {
		String numberOfTimesVisitedPath = "foaf:knows/:hasPreference/:hasUserEnteredRatings/" + predicate + "/:hasNumberofTimesVisited";
		findNumberOfTimesFromPath(uid, numberOfTimesVisitedPath, numbers);
	}
	
	/**
	 * Find number of times visited by user and store in a given list.
	 * @param uid
	 * 				Google UID
	 * @param predicate
	 * 				Predicate, for example <code>hasUserHotelRating, hasUserPlaceRating, hasUserEventRating</code>
	 * @param numbers
	 */
	private void findNumberOfTimes(String uid, String predicate, List <Integer> numbers) {
		//String ratingPath = ":hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasNumberofTimesVisited";
		String numberOfTimesVisitedPath = ":hasPreference/:hasUserEnteredRatings/" + predicate + "/:hasNumberofTimesVisited";
		findNumberOfTimesFromPath(uid, numberOfTimesVisitedPath, numbers);
	}

	/**
	 * Finds scores rated from a given path and stores in a list.
	 * @param uid
	 * @param path
	 * @param scores
	 */
	private void findScoresRatedFromPath(String uid, String path, List <Double> scores) {
		ThalesProfileManagerAndSession profileMngAndSession = new ThalesProfileManagerAndSession(uid);
		List<String> propertyPaths = new ArrayList<String>();
		propertyPaths.add(path);
		try {
			Collection<ValuedProperty> propertyValues =  profileMngAndSession.profileMgr.getProfileProperties(
					profileMngAndSession.session, uid, propertyPaths);
			if (propertyValues == null || propertyValues.size() == 0) return;
			for (ValuedProperty vp: propertyValues) {
				double score = getDoubleFromXSDValue(vp.getValue(0));
				scores.add(score);
			}
		} catch (ProfileException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds scores rated from a given path and stores in a list.
	 * @param uid
	 * @param path
	 * @param numbers
	 */
	private void findNumberOfTimesFromPath(String uid, String path, List <Integer> numbers) {
		ThalesProfileManagerAndSession profileMngAndSession = new ThalesProfileManagerAndSession(uid);
		List<String> propertyPaths = new ArrayList<String>();
		propertyPaths.add(path);
		try {
			Collection<ValuedProperty> propertyValues =  profileMngAndSession.profileMgr.getProfileProperties(
					profileMngAndSession.session, uid, propertyPaths);
			if (propertyValues == null || propertyValues.size() == 0) return;
			for (ValuedProperty vp: propertyValues) {
				int number = getIntegerFromXSDValue(vp.getValue(0));
				numbers.add(number);
			}
		} catch (ProfileException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets address information.
	 * @param uid
	 * 				Google UID
	 * @param concept
	 * 				The concept, for example <code>vcard:country-name</code>, <code>:townName</code>, etc.
	 * @return Corresponding information about address if there exists, <code>null</code> otherwise.
	 */
	private String getInformationFromProfileAddress(String uid, String concept) {
		ThalesProfileManagerAndSession profileMngAndSession = new ThalesProfileManagerAndSession(uid);
		String vcardAddr = "vcard:hasAddress/" + concept;
		List<String> propertyPaths = new ArrayList<String>();
		propertyPaths.add(vcardAddr);
		try {
			Collection<ValuedProperty> propertyValues =  profileMngAndSession.profileMgr.getProfileProperties(
					profileMngAndSession.session, uid, propertyPaths);
			if (propertyValues == null || propertyValues.size() == 0) return null;
			return propertyValues.iterator().next().getValue(0);
		} catch (ProfileException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Extract double value from the following format <code>4.0^^http://www.w3.org/2001/XMLSchema#double</code>
	 * @param doubleInXSDValue
	 * @return
	 */
	private double getDoubleFromXSDValue(String doubleInXSDValue) {
		int index = doubleInXSDValue.indexOf("^^");
		if (index < 0) throw new RuntimeException("Invalid value");
		String str = doubleInXSDValue.substring(0, index);
		return Double.parseDouble(str);
	}

	/**
	 * Extract double value from the following format <code>4^^http://www.w3.org/2001/XMLSchema#integer</code>
	 * @param doubleInXSDValue
	 * @return
	 */
	private int getIntegerFromXSDValue(String integerInXSDValue) {
		int index = integerInXSDValue.indexOf("^^");
		if (index < 0) throw new RuntimeException("Invalid value");
		String str = integerInXSDValue.substring(0, index);
		return Integer.parseInt(str);
	}
}

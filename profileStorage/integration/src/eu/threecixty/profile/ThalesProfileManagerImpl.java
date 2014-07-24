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
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMinimumScoreRated(String uid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMinimumScoreRatedForFriends(String uid) {
		// TODO Auto-generated method stub
		return 0;
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

	private double getDoubleFromXSDValue(String doubleInXSDValue) {
		int index = doubleInXSDValue.indexOf("^^");
		if (index < 0) throw new RuntimeException("Invalid value");
		String str = doubleInXSDValue.substring(0, index);
		return Double.parseDouble(str);
	}
}

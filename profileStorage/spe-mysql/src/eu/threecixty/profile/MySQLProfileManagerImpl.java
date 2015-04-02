package eu.threecixty.profile;

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.threecixty.partners.Partner;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.oldmodels.Address;

class MySQLProfileManagerImpl implements ProfileManager {

	public boolean checkAttributeToStore(Map<String, Boolean> arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean existUID(String _3cixtyUid) throws TooManyConnections {
		return UserUtils.exists(_3cixtyUid);
	}

	public String find3cixtyUID(String uid, String profileImage) {
		// TODO: need to refactor code with source
		return UserUtils.find3cixtyUID(uid, null, profileImage);
	}

	public List<UserProfile> getAllUserProfiles() {
		// TODO Auto-generated method stub
		return null;
	}

	public GpsCoordinate getCoordinate(UserProfile userProfile)
			throws TooManyConnections {
		// TODO Auto-generated method stub
		
		return null;
	}

	public String getCountryName(UserProfile userProfile) throws TooManyConnections {
		Address address = userProfile == null ? null : userProfile.getHasAddress();
		return address == null ? null : address.getCountryName();
	}

	public List<String> getEventNamesFromEventPreferences(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromRating(UserProfile userProfile, float arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesFromRatingOfFriends(UserProfile userProfile,
			float arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IDCrawlTimeMapping> getIDCrawlTimeMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IDMapping> getIDMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinimumNumberOfTimesVisited(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMinimumScoreRated(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMinimumScoreRatedForFriends(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Partner getPartner() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float arg1)
			throws TooManyConnections {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float arg1) throws TooManyConnections {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserProfile getProfile(String _3cixtyUID, Map<String, Boolean> arg1)
			throws TooManyConnections {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTownName(UserProfile userProfile) throws TooManyConnections {
		Address address = userProfile == null ? null : userProfile.getHasAddress();
		return address == null ? null : address.getTownName();
	}

	public TrayManager getTrayManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveProfile(UserProfile userProfile, Map<String, Boolean> arg1)
			throws TooManyConnections {
		// TODO Auto-generated method stub
		return false;
	}

}

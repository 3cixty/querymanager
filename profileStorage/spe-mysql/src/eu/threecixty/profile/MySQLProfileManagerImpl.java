package eu.threecixty.profile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerImpl;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.oldmodels.Address;

class MySQLProfileManagerImpl implements ProfileManager {
	
	private static final int DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED = 1;
	private static final float DEFAULT_MINIMUM_SCORE_RATED = 3;

	public boolean checkAttributeToStore(Map<String, Boolean> arg0, String arg1) {
		return true;
	}

	public boolean existUID(String _3cixtyUid) throws TooManyConnections {
		return UserUtils.exists(_3cixtyUid);
	}

	public String find3cixtyUID(String uid, String source, String profileImage) {
		return UserUtils.find3cixtyUID(uid, source, profileImage);
	}

	public List<UserProfile> getAllUserProfiles() {
		return null;
	}

	public GpsCoordinate getCoordinate(UserProfile userProfile)
			throws TooManyConnections {
		if (userProfile == null) return null;
		Address addr = userProfile.getHasAddress();
		if (addr == null) return null;
		return new GpsCoordinate(addr.getLatitude(), addr.getLongitute());
	}

	public String getCountryName(UserProfile userProfile) throws TooManyConnections {
		Address address = userProfile == null ? null : userProfile.getHasAddress();
		return address == null ? null : address.getCountryName();
	}

	public List<String> getEventNamesFromEventPreferences(UserProfile userProfile) {
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int arg1) {
		return null;
	}

	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int arg1) {
		return null;
	}

	public List<String> getEventNamesFromRating(UserProfile userProfile, float arg1) {
		return null;
	}

	public List<String> getEventNamesFromRatingOfFriends(UserProfile userProfile,
			float arg1) {
		return null;
	}

	public List<String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile) {
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
		return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
	}

	public int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile) {
		return DEFAULT_MINIMUM_NUMBER_OF_TIMES_VISITED;
	}

	public float getMinimumScoreRated(UserProfile userProfile) {
		return DEFAULT_MINIMUM_SCORE_RATED;
	}

	public float getMinimumScoreRatedForFriends(UserProfile userProfile) {
		return DEFAULT_MINIMUM_SCORE_RATED;
	}

	public Partner getPartner() {
		return PartnerImpl.getInstance();
	}

	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float rating)
			throws TooManyConnections {
		try {
			return MySQLProfilerPlaceUtils.getPlaceIdsFromRating(userProfile, rating);
		} catch (IOException e) {
			throw new TooManyConnections(e.getMessage());
		} catch (UnknownException e) {
			throw new TooManyConnections(e.getMessage());
		}
	}

	public List<String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float rating) throws TooManyConnections {
		try {
			return MySQLProfilerPlaceUtils.getPlaceIdsFromRatingOfFriends(userProfile, rating);
		} catch (IOException e) {
			throw new TooManyConnections(e.getMessage());
		} catch (UnknownException e) {
			throw new TooManyConnections(e.getMessage());
		}
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		return null;
	}

	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int arg1) {
		return null;
	}

	public List<StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile) {
		return null;
	}

	public UserProfile getProfile(String _3cixtyUID, Map<String, Boolean> arg1)
			throws TooManyConnections {
		return UserUtils.getUserProfile(_3cixtyUID);
	}

	public String getTownName(UserProfile userProfile) throws TooManyConnections {
		Address address = userProfile == null ? null : userProfile.getHasAddress();
		return address == null ? null : address.getTownName();
	}

	public TrayManager getTrayManager() {
		return MySQLTrayManager.getInstance();
	}

	public boolean saveProfile(UserProfile userProfile, Map<String, Boolean> arg1)
			throws TooManyConnections {
		return UserUtils.saveUserProfile(userProfile);
	}

	public Set<String> find3cixtyUIDs(List<String> accountIds, String source,
			List <String> unfoundAccountIds) {
		return UserUtils.find3cixtyUIDs(accountIds, source, unfoundAccountIds);
	}

	public boolean createProfiles(List<UserProfile> profiles) throws IOException,
			UnknownException {
		return UserUtils.createProfiles(profiles);
	}

	public UserProfile findUserProfile(String uid, String source, String profileImage) {
		return UserUtils.findUserProfile(uid, source, profileImage);
	}

}

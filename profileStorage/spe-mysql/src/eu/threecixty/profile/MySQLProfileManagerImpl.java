package eu.threecixty.profile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.threecixty.cache.ProfileCacheManager;
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
		UserProfile profile = ProfileCacheManager.getInstance().getProfile(_3cixtyUid);
		if (profile != null) return true;
		return UserUtils.exists(_3cixtyUid);
	}

	public String find3cixtyUID(String uid, String source) {
		UserProfile userProfile = ProfileCacheManager.getInstance().findProfile(uid, source);
		if (userProfile != null) return userProfile.getHasUID();
		return UserUtils.find3cixtyUID(uid, source);
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
		return UserUtils.getIDCrawlTimeMappings();
	}

	public Set<IDMapping> getIDMappings() {
		return UserUtils.getIDMappings();
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
		UserProfile profile = ProfileCacheManager.getInstance().getProfile(_3cixtyUID);
		if (profile != null) return profile;
		profile = UserUtils.getUserProfile(_3cixtyUID);
		if (profile != null) {
			ProfileCacheManager.getInstance().put(profile);
		}
		return profile;
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
		boolean successful = UserUtils.saveUserProfile(userProfile);
		if (successful) ProfileCacheManager.getInstance().put(userProfile);
		return successful;
	}

	public Set<String> find3cixtyUIDs(List<String> accountIds, String source,
			List <String> unfoundAccountIds) {
		if (accountIds == null || accountIds.size() == 0) return Collections.emptySet();
		Set <String> _3cixtyUidsInCache = new HashSet <String>();
		for (String accountId: accountIds) {
			UserProfile tmpProfile = ProfileCacheManager.getInstance().findProfile(accountId, source);
			if (tmpProfile != null) _3cixtyUidsInCache.add(tmpProfile.getHasUID());
		}
		if (_3cixtyUidsInCache.size() == accountIds.size()) return _3cixtyUidsInCache;
		return UserUtils.find3cixtyUIDs(accountIds, source, unfoundAccountIds);
	}

	public boolean createProfiles(List<UserProfile> profiles) throws IOException,
			UnknownException {
		boolean successful = UserUtils.createProfiles(profiles);
		if (successful) {
			for (UserProfile profile: profiles) {
				ProfileCacheManager.getInstance().put(profile);
			}
		}
		return successful;
	}

	public UserProfile findUserProfile(String uid, String source) {
		UserProfile userProfile = ProfileCacheManager.getInstance().findProfile(uid, source);
		if (userProfile != null) return userProfile;
		userProfile = UserUtils.findUserProfile(uid, source);
		if (userProfile != null) {
			ProfileCacheManager.getInstance().put(userProfile);
		}
		return userProfile;
	}

	public boolean updateKnows(UserProfile profile, Set<String> knows) {
		boolean successful = UserUtils.updateKnows(profile, knows);
		if (successful) {
			ProfileCacheManager.getInstance().put(profile);
		}
		return successful;
	}

	@Override
	public void findPlaceIdsAndSocialScore(UserProfile profile, float rating,
			List<String> placeIds, List<Double> socialScores) {
		try {
			MySQLProfilerPlaceUtils.findPlaceIdsAndSocialScore(profile,
					rating, placeIds, socialScores);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void findPlaceIdsAndSocialScoreForFriends(UserProfile profile,
			float rating, List<String> placeIds, List<Double> socialScores) {
		try {
			MySQLProfilerPlaceUtils.findPlaceIdsAndSocialScoreForFriends(
					profile, rating, placeIds, socialScores);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownException e) {
			e.printStackTrace();
		}
	}

	public List<Friend> findAll3cixtyFriendsHavingMyUIDInKnows(String my3cixtyUID) {
		return UserUtils.findAll3cixtyFriendsHavingMyUIDInKnows(my3cixtyUID);
	}

	/**
	 * Find all my friends in my list of knows.
	 */
	@Override
	public List<Friend> findAllFriends(String my3cixtyUID) {
		return UserUtils.findAllFriendsInMyListOfKnows(my3cixtyUID);
	}
}

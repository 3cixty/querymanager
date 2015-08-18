package eu.threecixty.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;
import eu.threecixty.partners.Partner;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This class is to switch ProfileManager among implementation versions of a RDF model file, Virtuoso code,
 * and Thales code. This will be done by using a corresponding dependency. By default, a RDF model file will
 * be used if there isn't any corresponding dependency included.
 * @author Cong Kinh Nguyen
 *
 */
public class ProfileManagerImpl implements ProfileManager {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 ProfileManagerImpl.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	public static final String SPARQL_ENDPOINT_URL = Configuration.getVirtuosoServer() + "/sparql?default-graph-uri=&query=";
	
	private static final String MYSQL_PM_IMPL = "eu.threecixty.profile.MySQLProfileManagerImpl";
	
	// TODO: make sure this full name is correct with the corresponding implementation with VIRTUOSO
	private static final String VIRTUOSO_PM_IMPL = "eu.threecixty.profile.VirtuosoProfileManagerImpl";
	
	// TODO: make sure this full name is correct with the corresponding implementation with Thales
	private static final String THALES_PM_IMPL = "eu.threecixty.profile.ThalesProfileManagerImpl";

	// TODO: make sure this full name is correct with the simple implementation of profile manager
	private static final String SIMPLE_PM_IMPL = "eu.threecixty.profile.SimpleProfileManagerImpl";
	
	private ProfileManager profileManager;

	public static ProfileManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public UserProfile getProfile(String uid, Map <String, Boolean> attributes) throws TooManyConnections {
		if (profileManager != null) return profileManager.getProfile(uid, attributes);
		return null;
	}

	@Override
	public boolean saveProfile(UserProfile userProfile, Map <String, Boolean> attributes) throws TooManyConnections {
		if (profileManager != null) return profileManager.saveProfile(userProfile, attributes);
		return false;
	}

	@Override
	public boolean existUID(String uid) throws TooManyConnections {
		if (profileManager != null) return profileManager.existUID(uid);
		return false;
	}

	@Override
	public int getMinimumNumberOfTimesVisited(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getMinimumNumberOfTimesVisited(userProfile);
		return 0;
	}

	@Override
	public float getMinimumScoreRated(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getMinimumScoreRated(userProfile);
		return 0;
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile) {
		if (profileManager != null)
			return profileManager.getMinimumNumberOfTimesVisitedForFriends(userProfile);
		return 0;
	}

	@Override
	public float getMinimumScoreRatedForFriends(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getMinimumScoreRatedForFriends(userProfile);
		return 0;
	}

	@Override
	public String getCountryName(UserProfile userProfile) throws TooManyConnections {
		if (profileManager != null)
			return profileManager.getCountryName(userProfile);
		return null;
	}

	@Override
	public String getTownName(UserProfile userProfile) throws TooManyConnections {
		if (profileManager != null) return profileManager.getTownName(userProfile);
		return null;
	}
	
	public GpsCoordinate getCoordinate(UserProfile userProfile) throws TooManyConnections {
		if (profileManager != null) return profileManager.getCoordinate(userProfile);
		return null;
	}

	@Override
	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float rating) throws TooManyConnections {
		if (profileManager != null) return profileManager.getPlaceIdsFromRating(userProfile, rating);
		return new ArrayList<String>();
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		if (profileManager != null)
			return profileManager.getPlaceNamesFromNumberOfTimesVisited(userProfile, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float rating) throws TooManyConnections {
		if (profileManager != null) return profileManager.getPlaceIdsFromRatingOfFriends(userProfile, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		if (profileManager != null)
			return profileManager.getPlaceNamesFromNumberOfTimesVisitedOfFriends(userProfile, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getEventNamesFromEventPreferences(userProfile);
		return new ArrayList <String>();
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getPreferredStartAndEndDates(userProfile);
		return new ArrayList <StartAndEndDate>();
	}

	@Override
	public List<String> getEventNamesFromRating(UserProfile userProfile, float rating) {
		if (profileManager != null) return profileManager.getEventNamesFromRating(userProfile, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		if (profileManager != null) return profileManager.getEventNamesFromNumberOfTimesVisited(userProfile, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(UserProfile userProfile,
			float rating) {
		if (profileManager != null) return profileManager.getEventNamesFromRatingOfFriends(userProfile, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		if (profileManager != null) return profileManager.getEventNamesFromNumberOfTimesVisitedOfFriends(userProfile, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile) {
		if (profileManager != null) return profileManager.getEventNamesWhichFriendsLikeToVisit(userProfile);
		return new ArrayList <String>();
	}

	@Override
	public Set<IDMapping> getIDMappings() {
		if (profileManager != null) return profileManager.getIDMappings();
		return new HashSet <IDMapping>();
	}

	@Override
	public Set<IDCrawlTimeMapping> getIDCrawlTimeMappings() {
		if (profileManager != null) return profileManager.getIDCrawlTimeMappings();
		return new HashSet <IDCrawlTimeMapping>();
	}
	
	@Override
	public Partner getPartner() {
		if (profileManager != null) return profileManager.getPartner();
		return null;
	}

	@Override
	public TrayManager getTrayManager() {
		if (profileManager != null) return profileManager.getTrayManager();
		return null;
	}

	@Override
	public List<UserProfile> getAllUserProfiles() {
		if (profileManager != null) return profileManager.getAllUserProfiles();
		return null;
	}
	
	public boolean checkAttributeToStore(Map <String, Boolean> attributes, String attrChecked) {
		if (attributes == null) return true;
		return Boolean.TRUE.equals(attributes.get(attrChecked));
	}
	
	@Override
	public String find3cixtyUID(String uid, String source) {
		if (profileManager != null) return profileManager.find3cixtyUID(uid, source);
		return null;
	}

	@Override
	public Set<String> find3cixtyUIDs(List<String> accountIds, String source,
			List <String> unfoundAccountIds) {
		if (profileManager != null) return profileManager.find3cixtyUIDs(
				accountIds, source, unfoundAccountIds);
		return null;
	}

	@Override
	public boolean createProfiles(List<UserProfile> userProfiles)
			throws IOException, UnknownException {
		if (profileManager != null) return profileManager.createProfiles(userProfiles);
		return false;
	}
	
	@Override
	public UserProfile findUserProfile(String uid, String source) {
		if (profileManager != null) return profileManager.findUserProfile(uid, source);
		return null;
	}
	
	private ProfileManagerImpl() {
		boolean found = false;
		try {
			profileManager = (ProfileManager) Class.forName(MYSQL_PM_IMPL).newInstance();
			found = true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		if (!found) {
			try {
				profileManager = (ProfileManager) Class.forName(VIRTUOSO_PM_IMPL).newInstance();
				found = true;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		if (!found) {
			try {
				profileManager = (ProfileManager) Class.forName(THALES_PM_IMPL).newInstance();
				found = true;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		if (!found) {
			try {
				profileManager = (ProfileManager) Class.forName(SIMPLE_PM_IMPL).newInstance();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		logInfo("profileManager = " + profileManager.getClass().getName());
	}
	
	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}
	
	/**Singleton holder*/
	private static class SingletonHolder {
		private static final ProfileManager INSTANCE = new ProfileManagerImpl();
	}

	@Override
	public boolean updateKnows(UserProfile profile, Set<String> knows) {
		if (profileManager != null) return profileManager.updateKnows(profile, knows);
		return false;
	}

	@Override
	public List<Friend> findAll3cixtyFriendsHavingMyUIDInKnows(String my3cixtyUID) {
		if (profileManager != null) return profileManager.findAll3cixtyFriendsHavingMyUIDInKnows(my3cixtyUID);
		return null;
	}

	@Override
	public List<Friend> findAllFriends(String my3cixtyUID) {
		if (profileManager != null) return profileManager.findAllFriends(my3cixtyUID);
		return null;
	}

	@Override
	public ForgottenUserManager getForgottenUserManager() {
		if (profileManager != null) return profileManager.getForgottenUserManager();
		return null;
	}
}

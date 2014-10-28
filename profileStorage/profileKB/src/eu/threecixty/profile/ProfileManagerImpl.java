package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This class is to switch ProfileManager among implementation versions of a RDF model file, Virtuoso code,
 * and Thales code. This will be done by using a corresponding dependency. By default, a RDF model file will
 * be used if there isn't any corresponding dependency included.
 * @author Cong Kinh Nguyen
 *
 */
public class ProfileManagerImpl implements ProfileManager {

	public static final String SPARQL_ENDPOINT_URL = "http://dev.3cixty.com/sparql?default-graph-uri=&query=";
	
	private static final Object _sync = new Object();
	
	// TODO: make sure this full name is correct with the corresponding implementation with VIRTUOSO
	private static final String VIRTUOSO_PM_IMPL = "eu.threecixty.profile.VirtuosoProfileManagerImpl";
	
	// TODO: make sure this full name is correct with the corresponding implementation with Thales
	private static final String THALES_PM_IMPL = "eu.threecixty.profile.ThalesProfileManagerImpl";

	// TODO: make sure this full name is correct with the simple implementation of profile manager
	private static final String SIMPLE_PM_IMPL = "eu.threecixty.profile.SimpleProfileManagerImpl";

	private static ProfileManagerImpl singleton;
	
	private ProfileManager profileManager;

	public static ProfileManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				singleton = new ProfileManagerImpl();
			}
		}
		return singleton;
	}

	@Override
	public UserProfile getProfile(String uid) {
		if (profileManager != null) return profileManager.getProfile(uid);
		return null;
	}

	@Override
	public boolean saveProfile(UserProfile userProfile, String type) {
		if (profileManager != null) return profileManager.saveProfile(userProfile, type);
		return false;
	}

	@Override
	public boolean existUID(String uid) {
		if (profileManager != null) return profileManager.existUID(uid);
		return false;
	}

	@Override
	public int getMinimumNumberOfTimesVisited(String uid) {
		if (profileManager != null) return profileManager.getMinimumNumberOfTimesVisited(uid);
		return 0;
	}

	@Override
	public float getMinimumScoreRated(String uid) {
		if (profileManager != null) return profileManager.getMinimumScoreRated(uid);
		return 0;
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		if (profileManager != null) return profileManager.getMinimumNumberOfTimesVisitedForFriends(uid);
		return 0;
	}

	@Override
	public float getMinimumScoreRatedForFriends(String uid) {
		if (profileManager != null) return profileManager.getMinimumScoreRatedForFriends(uid);
		return 0;
	}

	@Override
	public String getCountryName(String uid) {
		if (profileManager != null) return profileManager.getCountryName(uid);
		return null;
	}

	@Override
	public String getTownName(String uid) {
		if (profileManager != null) return profileManager.getTownName(uid);
		return null;
	}
	
	public GpsCoordinate getCoordinate(String uid) {
		if (profileManager != null) return profileManager.getCoordinate(uid);
		return null;
	}

	@Override
	public List<String> getPlaceNamesFromRating(String uid, float rating) {
		if (profileManager != null) return profileManager.getPlaceNamesFromRating(uid, rating);
		return new ArrayList<String>();
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		if (profileManager != null) return profileManager.getPlaceNamesFromNumberOfTimesVisited(uid, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getPlaceNamesFromRatingOfFriends(String uid,
			float rating) {
		if (profileManager != null) return profileManager.getPlaceNamesFromRatingOfFriends(uid, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		if (profileManager != null) return profileManager.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(String uid) {
		if (profileManager != null) return profileManager.getEventNamesFromEventPreferences(uid);
		return new ArrayList <String>();
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(String uid) {
		if (profileManager != null) return profileManager.getPreferredStartAndEndDates(uid);
		return new ArrayList <StartAndEndDate>();
	}

	@Override
	public List<String> getEventNamesFromRating(String uid, float rating) {
		if (profileManager != null) return profileManager.getEventNamesFromRating(uid, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(String uid,
			int number) {
		if (profileManager != null) return profileManager.getEventNamesFromNumberOfTimesVisited(uid, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(String uid,
			float rating) {
		if (profileManager != null) return profileManager.getEventNamesFromRatingOfFriends(uid, rating);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		if (profileManager != null) return profileManager.getEventNamesFromNumberOfTimesVisitedOfFriends(uid, number);
		return new ArrayList <String>();
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(String uid) {
		if (profileManager != null) return profileManager.getEventNamesWhichFriendsLikeToVisit(uid);
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
	
	private ProfileManagerImpl() {
		boolean found = false;
		try {
			profileManager = (ProfileManager) Class.forName(VIRTUOSO_PM_IMPL).newInstance();
			found = true;
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		} catch (InstantiationException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		}
		if (!found) {
			try {
				profileManager = (ProfileManager) Class.forName(THALES_PM_IMPL).newInstance();
				found = true;
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			} catch (InstantiationException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
		}
		if (!found) {
			try {
				profileManager = (ProfileManager) Class.forName(SIMPLE_PM_IMPL).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

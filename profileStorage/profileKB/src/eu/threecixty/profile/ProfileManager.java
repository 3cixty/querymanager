package eu.threecixty.profile;

import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This is an interface which deals with getting information from KB.
 * @author Cong-Kinh Nguyen
 *
 */
public interface ProfileManager {

	/**
	 * Gets user profile from KB.
	 * @param uid
	 * 				The Goolge UID.
	 * @return <code>UserProfile</code> if the corresponding user exists in the KB, <code>false</code> otherwise.
	 */
	UserProfile getProfile(String uid);

	/**
	 * Saves a given user profile into the KB.
	 * @param userProfile
	 * 				The user profile.
	 * @return <code>true</code> if the method is successful to save data, <code>false</code> otherwise. 
	 */
	boolean saveProfile(UserProfile userProfile);

	/**
	 * Checks whether or not a given Google UID exists in the KB.
	 * @param uid
	 * 				The Google UID.
	 * @return <code>true</code> if the given Google UID exists in the KB, <code>false</code> otherwise.
	 */
	boolean existUID(String uid);

	/**
	 * Gets minimum number of times visited.
	 * @param uid
	 * 				Google UID.
	 * @return Minimum number of times visited. This value should be updated by the miner component.
	 */
	int getMinimumNumberOfTimesVisited(String uid);

	/**
	 * Gets minimum score rated by a given Google UID.
	 * @param uid
	 * 				Google UID.
	 * @return Minimum score rated by a given Google UID.
	 */
	float getMinimumScoreRated(String uid);

	/**
	 * 
	 * @param uid
	 * @return
	 */
	int getMinimumNumberOfTimesVisitedForFriends(String  uid);
	float getMinimumScoreRatedForFriends(String uid);

	/**
	 * Gets country name.
	 * @param uid
	 * 				Google UID.
	 * @return Country name if there exists in the KB, <code>null</code> otherwise. 
	 */
	String getCountryName(String uid) throws TooManyConnections;

	/**
	 * Gets town name.
	 * @param uid
	 * 				Google UID.
	 * @return Town name if there exists in the KB, <code>null</code> otherwise.
	 */
	String getTownName(String uid) throws TooManyConnections;

	/**
	 * Gets GPS coordinates.
	 * @param uid
	 * 				Google UID.
	 * @return GPS coordinates information.
	 */
	GpsCoordinate getCoordinate(String uid) throws TooManyConnections;

	/**
	 * Gets a list of place IDs which were rated with a score more than a given <code>rating</code> value. 
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list of place IDs if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceIdsFromRating(String uid, float rating) throws TooManyConnections;

	/**
	 * Gets a list of place names which were visited more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of place names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceNamesFromNumberOfTimesVisited(String uid, int number);

	/**
	 * Gets a list of place IDs which were rated by friends with a score more than a given <code>rating</code>.
	 * @param uid
	 * 				Googel UID.
	 * @param rating
	 * 				Rating score
	 * @return A list of place IDs if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceIdsFromRatingOfFriends(String uid, float rating) throws TooManyConnections;
	
	/**
	 * Gets a list of place names which were visited by friends more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of place names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(String uid, int number);

	/**
	 * Gets a list of event names which are preferred by user.
	 * @param uid
	 * 				Google UID.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromEventPreferences(String uid);

	/**
	 * Gets a list of date ranges which are preferred by user.
	 * @param uid
	 * 				Google UID.
	 * @return A list of date ranges if Google UID is valid, <code>null</code> otherwise.
	 */
	List <StartAndEndDate> getPreferredStartAndEndDates(String uid);
	
	/**
	 * Gets a list of event names which were rated with a score more than <code>rating</code>.
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list if event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromRating(String uid, float rating);
	
	/**
	 * Gets a list of event names which were visited by user more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromNumberOfTimesVisited(String uid, int number);
	
	/**
	 * Gets a list of event names which were rated by friends with a score more than <code>rating</code>.
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromRatingOfFriends(String uid, float rating);

	/**
	 * Gets a list of event names which were visited by friends with number of times more than <code>number</code>.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromNumberOfTimesVisitedOfFriends(String uid, int number);

	/**
	 * Gets a list of event names that friends like to visit. 
	 * @param uid
	 * 				Google UID.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesWhichFriendsLikeToVisit(String uid);
	
	/**
	 * Gets all the mapping between a Google UID and Mobidot information.
	 * @return
	 */
	Set<IDMapping> getIDMappings();
	
	/**
	 * Map crawl times to User profile
	 * @return
	 */
	Set<IDCrawlTimeMapping> getIDCrawlTimeMappings();
	
	/**
	 * Gets Mobidot interface.
	 * @return
	 */
	Partner getMobidot();
	
	/**
	 * Gets GoFlow interface.
	 * @return
	 */
	Partner getGoFlow();
	
	/**
	 * Gets interface to deal with Tray element.
	 * @return
	 */
	TrayManager getTrayManager();

	/**
	 * Lists all user profiles in the KB.
	 * @return
	 */
	List <UserProfile> getAllUserProfiles();
	
	public class StartAndEndDate {

		private Date startDate;
		private Date endDate;
		
		public StartAndEndDate(Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
	}
}

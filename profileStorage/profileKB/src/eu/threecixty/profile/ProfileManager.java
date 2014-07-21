package eu.threecixty.profile;

import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

public interface ProfileManager {

	UserProfile getProfile(String uid);
	boolean saveProfile(UserProfile userProfile);

	boolean existUID(String uid);

	int getMinimumNumberOfTimesVisited(String uid);

	float getMinimumScoreRated(String uid);

	int getMinimumNumberOfTimesVisitedForFriends(String  uid);
	float getMinimumScoreRatedForFriends(String uid);

	String getCountryName(String uid);

	String getTownName(String uid);

	GpsCoordinate getCoordinate(String uid);

	List <String> getPlaceNamesFromRating(String uid, float rating);

	List <String> getPlaceNamesFromNumberOfTimesVisited(String uid, int number);

	List <String> getPlaceNamesFromRatingOfFriends(String uid, float rating);
	
	List <String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(String uid, int number);

	List <String> getEventNamesFromEventPreferences(String uid);

	List <StartAndEndDate> getPreferredStartAndEndDates(String uid);
	
	List <String> getEventNamesFromRating(String uid, float rating);
	
	List <String> getEventNamesFromNumberOfTimesVisited(String uid, int number);
	
	List <String> getEventNamesFromRatingOfFriends(String uid, float rating);

	List <String> getEventNamesFromNumberOfTimesVisitedOfFriends(String uid, int number);

	List <String> getEventNamesWhichFriendsLikeToVisit(String uid);
	
	Set<IDMapping> getIDMappings();
	
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

/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.threecixty.partners.Partner;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This is an interface which deals with getting information from KB.
 *
 */
public interface ProfileManager {

	/**
	 * Gets user profile from KB.
	 * @param uid
	 * 				The 3cixty UID.
	 * 
	 * @return <code>UserProfile</code> if the corresponding user exists in the KB, <code>false</code> otherwise.
	 */
	UserProfile getProfile(String uid) throws TooManyConnections;

	/**
	 * Persist user profile.
	 * @param userProfile
	 * 				The user profile.
	 * @return <code>true</code> if the method is successful to save data, <code>false</code> otherwise. 
	 */
	boolean saveProfile(UserProfile userProfile) throws TooManyConnections;

	/**
	 * Checks whether or not a given Google UID exists in the KB.
	 * @param uid
	 * 				The Google UID.
	 * @return <code>true</code> if the given Google UID exists in the KB, <code>false</code> otherwise.
	 */
	boolean existUID(String uid) throws TooManyConnections;

	/**
	 * Gets minimum number of times visited.
	 * @param uid
	 * 				Google UID.
	 * @return Minimum number of times visited. This value should be updated by the miner component.
	 */
	int getMinimumNumberOfTimesVisited(UserProfile userProfile);

	/**
	 * Gets minimum score rated by a given Google UID.
	 * @param uid
	 * 				Google UID.
	 * @return Minimum score rated by a given Google UID.
	 */
	float getMinimumScoreRated(UserProfile userProfile);

	/**
	 * 
	 * @param uid
	 * @return
	 */
	int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile);
	float getMinimumScoreRatedForFriends(UserProfile userProfile);

	/**
	 * Gets country name.
	 * @param uid
	 * 				Google UID.
	 * @return Country name if there exists in the KB, <code>null</code> otherwise. 
	 */
	String getCountryName(UserProfile userProfile) throws TooManyConnections;

	/**
	 * Gets town name.
	 * @param uid
	 * 				Google UID.
	 * @return Town name if there exists in the KB, <code>null</code> otherwise.
	 */
	String getTownName(UserProfile userProfile) throws TooManyConnections;

	/**
	 * Gets GPS coordinates.
	 * @param uid
	 * 				Google UID.
	 * @return GPS coordinates information.
	 */
	GpsCoordinate getCoordinate(UserProfile userProfile) throws TooManyConnections;

	/**
	 * Gets a list of place IDs which were rated with a score more than a given <code>rating</code> value. 
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list of place IDs if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceIdsFromRating(UserProfile userProfile, float rating, String endPointUrl) throws TooManyConnections;

	/**
	 * Gets a list of place names which were visited more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of place names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile, int number);

	/**
	 * Gets a list of place IDs which were rated by friends with a score more than a given <code>rating</code>.
	 * @param uid
	 * 				Googel UID.
	 * @param rating
	 * 				Rating score
	 * @return A list of place IDs if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile, float rating, String endPointUrl) throws TooManyConnections;
	
	/**
	 * Find place IDs and corresponding social scores for places rated by me.
	 * @param profile
	 * @param rating
	 * @param toPlaceIds
	 * @param toSocialScores
	 */
	void findPlaceIdsAndSocialScore(UserProfile profile, float rating,
			List <String> toPlaceIds, List <Double> toSocialScores, String endPointUrl);
	
	/**
	 * Find place IDs and corresponding social scores for places rated by my friends.
	 * @param profile
	 * @param rating
	 * @param toPlaceIds
	 * @param toSocialScores
	 */
	void findPlaceIdsAndSocialScoreForFriends(UserProfile profile, float rating,
			List <String> toPlaceIds, List <Double> toSocialScores, String endPointUrl);
	
	/**
	 * Gets a list of place names which were visited by friends more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of place names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(UserProfile userProfile, int number);

	/**
	 * Gets a list of event names which are preferred by user.
	 * @param uid
	 * 				Google UID.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromEventPreferences(UserProfile userProfile);

	/**
	 * Gets a list of date ranges which are preferred by user.
	 * @param uid
	 * 				Google UID.
	 * @return A list of date ranges if Google UID is valid, <code>null</code> otherwise.
	 */
	List <StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile);
	
	/**
	 * Gets a list of event names which were rated with a score more than <code>rating</code>.
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list if event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromRating(UserProfile userProfile, float rating);
	
	/**
	 * Gets a list of event names which were visited by user more than <code>number</code> times.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile, int number);
	
	/**
	 * Gets a list of event names which were rated by friends with a score more than <code>rating</code>.
	 * @param uid
	 * 				Google UID.
	 * @param rating
	 * 				Rating score.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromRatingOfFriends(UserProfile userProfile, float rating);

	/**
	 * Gets a list of event names which were visited by friends with number of times more than <code>number</code>.
	 * @param uid
	 * 				Google UID.
	 * @param number
	 * 				Number of times visited.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesFromNumberOfTimesVisitedOfFriends(UserProfile userProfile, int number);

	/**
	 * Gets a list of event names that friends like to visit. 
	 * @param uid
	 * 				Google UID.
	 * @return A list of event names if Google UID is valid, <code>null</code> otherwise.
	 */
	List <String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile);
	
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
	 * Gets partner interface.
	 * @return
	 */
	Partner getPartner();
	
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
	
	/**
	 * Finds 3cixty UID from a given UDI (Google or Facebook), and a given profile image.
	 *
	 * @param uid
	 * @param source
	 * @param profileImage
	 * @return
	 */
	String find3cixtyUID(String uid, String source);
	
	/**
	 * Finds the corresponding 3cixty UIDs from a given list of account IDs and source.
	 * @param accountIds
	 * @param source
	 * @param unfoundAccountIds
	 * @return
	 */
	Set <String> find3cixtyUIDs(List <String> accountIds, String source,
			List <String> unfoundAccountIds);
	
	/**
	 * Creates user profiles.
	 * @param userProfiles
	 * @return
	 * @throws IOException
	 * @throws UnknownException
	 */
	boolean createProfiles(List <UserProfile> userProfiles) throws IOException, UnknownException;
	
	/**
	 * Finds the corresponding user profile with a given external
	 * (Facebook, Google) uid, source, and profile image.
	 * @param uid
	 * @param source
	 * @param profileImage
	 * @return
	 */
	UserProfile findUserProfile(String uid, String source);
	
	boolean updateKnows(UserProfile profile, Set <String> knows);
	
	/**
	 * It's possible that I don't have any information at all about them within 3cixty context.
	 * @param my3cixtyUID
	 * @return
	 */
	List <Friend> findAll3cixtyFriendsHavingMyUIDInKnows(String my3cixtyUID);
	
	/**
	 * Find all my friends in my knows.
	 * @param my3cixtyUID
	 * @return
	 */
	List <Friend> findAllFriends(String my3cixtyUID);
	
	/**
	 * Gets interface to manage forgotten users.
	 * @return
	 */
	ForgottenUserManager getForgottenUserManager();
	
	/**
	 * Finds account Id from a given user profile and a given source.
	 * @param profile
	 * @param source
	 * @return
	 */
	String findAccountId(UserProfile profile, String source);
	
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

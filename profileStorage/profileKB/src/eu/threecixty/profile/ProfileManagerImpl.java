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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.threecixty.partners.Partner;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This class is to switch ProfileManager among implementation versions of a RDF model file, Virtuoso code,
 * and mysql code. This will be done by using a corresponding dependency. By default, mySQL is used to
 * persist user profile.
 *
 */
public class ProfileManagerImpl implements ProfileManager {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 ProfileManagerImpl.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
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
	public UserProfile getProfile(String uid) throws TooManyConnections {
		if (profileManager != null) return profileManager.getProfile(uid);
		return null;
	}

	@Override
	public boolean saveProfile(UserProfile userProfile) throws TooManyConnections {
		if (profileManager != null) return profileManager.saveProfile(userProfile);
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
	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float rating, String endPointUrl) throws TooManyConnections {
		if (profileManager != null) return profileManager.getPlaceIdsFromRating(userProfile, rating, endPointUrl);
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
			float rating, String endPointUrl) throws TooManyConnections {
		if (profileManager != null) return profileManager.getPlaceIdsFromRatingOfFriends(userProfile, rating, endPointUrl);
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

	public void findPlaceIdsAndSocialScore(UserProfile profile, float rating,
			List<String> toPlaceIds, List<Double> toSocialScores, String endPointUrl) {
		if (profileManager != null) profileManager.findPlaceIdsAndSocialScore(
				profile, rating, toPlaceIds, toSocialScores, endPointUrl);
	}

	@Override
	public void findPlaceIdsAndSocialScoreForFriends(UserProfile profile,
			float rating, List<String> toPlaceIds, List<Double> toSocialScores, String endPointUrl) {
		if (profileManager != null) profileManager.findPlaceIdsAndSocialScoreForFriends(
				profile, rating, toPlaceIds, toSocialScores, endPointUrl);
	}

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

	@Override
	public String findAccountId(UserProfile profile, String source) {
		if (profileManager != null) return profileManager.findAccountId(profile, source);
		return null;
	}
}

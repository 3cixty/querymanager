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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.threecixty.db.PersistentObjectForWorker;

/**
 * 
 * This class is to save or update knows.
 *
 */
public class KnowsPersistence implements PersistentObjectForWorker {
	
	private static final Logger LOGGER = Logger.getLogger(
			 KnowsPersistence.class.getName());
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	
	
	private String source;
	private String accessToken;
	private UserProfile profile;
	private String user_id;

	public KnowsPersistence(String accessToken, String source, String user_id, UserProfile profile) {
		this.accessToken = accessToken;
		this.source = source;
		this.user_id = user_id;
		this.profile = profile;
	}
	
	@Override
	public boolean saveOrUpdate() {
		if (SPEConstants.GOOGLE_SOURCE.equals(source)) {
			return saveOrUpdateForGoogle();
		} else if (SPEConstants.FACEBOOK_SOURCE.equals(source)) {
			return saveOrUpdateForFacebook();
		}
		return false;
	}

	private boolean saveOrUpdateForFacebook() {
		long time1 = System.currentTimeMillis();
		List <String> fUIDsFromFriends = new LinkedList<String>();
		FaceBookAccountUtils.findFacebookUidsFromFriends(FaceBookAccountUtils.FACEBOOK_FRIENDS_PREFIX, accessToken, fUIDsFromFriends);
		long time2 = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Time to get info + friends list from Facebook server: " + (time2 - time1) + " ms");
		
		try {
			Set<String> knows = Utils.getOrCreate3cixtyUIDsForKnows(fUIDsFromFriends, SPEConstants.FACEBOOK_SOURCE);
			
			String [] arrs = knows.toArray(new String[knows.size()]);
			knows.clear();
			ForgottenUserManager fum = ProfileManagerImpl.getInstance().getForgottenUserManager();
			String _3cixtyUID = profile.getHasUID();
			for (int i = 0; i < arrs.length; i++) {
				String know = arrs[i];
				if ((!fum.isCrawlable(_3cixtyUID, know)) || fum.isBlockedByUidOwner(know)) {
					continue;
				}
				knows.add(know);
			}
			
			boolean knowsModified = Utils.checkKnowsModified(profile, knows);
			if (knowsModified) profile.setKnows(knows);
			ProfileManagerImpl.getInstance().updateKnows(profile, knows);
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return false;
	}

	private boolean saveOrUpdateForGoogle() {
		boolean knowsModified = false;
		long time2 = 0;
		try {
			long time1 = System.currentTimeMillis();
			List <String> googleUidsOfFriends = GoogleAccountUtils.getGoogleUidsOfFriends(accessToken);
			time2 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to get info + friends list from Google server: " + (time2 - time1) + " ms");

			Set<String> knows = Utils.getOrCreate3cixtyUIDsForKnows(googleUidsOfFriends, SPEConstants.GOOGLE_SOURCE);
			String [] arrs = knows.toArray(new String[knows.size()]);
			knows.clear();
			ForgottenUserManager fum = ProfileManagerImpl.getInstance().getForgottenUserManager();
			String _3cixtyUID = profile.getHasUID();
			for (int i = 0; i < arrs.length; i++) {
				String know = arrs[i];
				if ((!fum.isCrawlable(_3cixtyUID, know)) || fum.isBlockedByUidOwner(know)) {
					continue;
				}
				knows.add(know);
			}
			
			// hack for Tony
			if (user_id.contains("117895882057702509461")) {
				String animesh3cixtyUID = ProfileManagerImpl.getInstance().find3cixtyUID(
						"103411760688868522737", SPEConstants.GOOGLE_SOURCE);
				if (!knows.contains(animesh3cixtyUID)) { // does not know Animesh
					knows.add(animesh3cixtyUID);
				}
			}
			knowsModified = Utils.checkKnowsModified(profile, knows);
			if (knowsModified) profile.setKnows(knows);
			
			ProfileManagerImpl.getInstance().updateKnows(profile, knows);

			return true;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			//return null; // TI's code isn't able to get friends list
		}
		return false;
	}

}

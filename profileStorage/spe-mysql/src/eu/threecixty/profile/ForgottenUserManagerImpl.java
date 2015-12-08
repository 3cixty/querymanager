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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.threecixty.userprofile.ForgottenUser;

public class ForgottenUserManagerImpl implements ForgottenUserManager {
	
	private Map <String, ForgottenUser> forgottenUsers;
	
	/**
	 * Checks whether or not a given UID is forbidden to crawl.
	 * @param uid
	 * @return
	 */
	public boolean isBlockedByUidOwner(String uid) {
		if (uid == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser != null) return forgottenUser.isNeedToAvoidBeingCrawled();
		return false;
	}
	
	/**
	 * Checks whether or not the user corresponding with a given UID lets 3cixty crawl
	 * another user which is associated with a given know.
	 * @param uid
	 * @param know
	 * @return
	 */
	public boolean isCrawlable(String uid, String know) {
		if (uid == null || know == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser == null) return true;
		Set <String> knows = forgottenUser.getKnowsNotToCrawl();
		if (knows == null || knows.size() == 0) return true;
		return !knows.contains(know);
	}

	
	/**
	 * Gets the forgotten user of a given UID.
	 * @param uid
	 * @return
	 */
	public ForgottenUser getForgottenUser(String uid) {
		ForgottenUser forgottenUser = forgottenUsers.get(uid);
		if (forgottenUser == null) {
			forgottenUser = ForgottenUserUtils.get(uid);
			if (forgottenUser != null) forgottenUsers.put(uid, forgottenUser);
		}
		return forgottenUser;
	}
	
	public ForgottenUserManagerImpl() {
		forgottenUsers = new HashMap <String, ForgottenUser>();
	}

	@Override
	public boolean remove(String uid) {
		if (uid == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser  != null) {
			boolean ok = false;
			boolean before = forgottenUser.isNeedToAvoidBeingCrawled();
			if (forgottenUser.getKnowsNotToCrawl() == null
					|| forgottenUser.getKnowsNotToCrawl().size() == 0) {
				ok = ForgottenUserUtils.detele(forgottenUser);
				if (ok) forgottenUsers.remove(uid);
			} else {
				forgottenUser.setNeedToAvoidBeingCrawled(false);
				ok = ForgottenUserUtils.update(forgottenUser);
				if (!ok) {
					forgottenUser.setNeedToAvoidBeingCrawled(before);
				}
			}
			return ok;
		}
		return false;
	}

	@Override
	public boolean add(String uid) {
		if (uid == null) return false;
		ForgottenUser forgottenUser = new ForgottenUser();
		forgottenUser.setUid(uid);
		forgottenUser.setNeedToAvoidBeingCrawled(true);
		boolean ok = ForgottenUserUtils.add(forgottenUser);
		if (ok) forgottenUsers.put(uid, forgottenUser);
		return ok;
	}

	@Override
	public boolean add(String uid, String know) {
		if (uid == null || know == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser != null) {
			Set <String> knows = forgottenUser.getKnowsNotToCrawl();
			if (knows == null) {
				knows = new HashSet <String>();
				forgottenUser.setKnowsNotToCrawl(knows);
			}
			if (knows.contains(know)) return false; // already contain the given know
			knows.add(know);
			forgottenUsers.put(uid, forgottenUser);
			boolean ok = ForgottenUserUtils.update(forgottenUser);
			if (!ok) knows.remove(know);
			return ok;
		}
		forgottenUser = new ForgottenUser();
		forgottenUser.setUid(uid);
		forgottenUser.setNeedToAvoidBeingCrawled(false);
		Set <String> knows = new HashSet <String>();
		knows.add(know);
		forgottenUser.setKnowsNotToCrawl(knows);
		boolean ok = ForgottenUserUtils.add(forgottenUser);
		if (ok) forgottenUsers.put(uid, forgottenUser);
		return ok;
	}

	@Override
	public boolean add(String uid, Set<String> knows) {
		if (uid == null || knows == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser != null) {
			Set <String> cloneOfKnows = new HashSet <String>();
			Set <String> originalKnows = forgottenUser.getKnowsNotToCrawl();
			if (originalKnows == null) {
				originalKnows = new HashSet <String>();
				forgottenUser.setKnowsNotToCrawl(originalKnows);
			}
			cloneOfKnows.addAll(originalKnows);
			boolean allexisted = true;
			for (String know: knows) {
				if (!originalKnows.contains(know)) {
					allexisted = false;
					originalKnows.add(know);
				}
			}
			if (allexisted) return false; // all existed
			
			boolean ok = ForgottenUserUtils.update(forgottenUser);
			if (!ok) {
				originalKnows.clear();
				originalKnows.addAll(cloneOfKnows);
			}
			return ok;
		}
		forgottenUser = new ForgottenUser();
		forgottenUser.setUid(uid);
		forgottenUser.setNeedToAvoidBeingCrawled(false);
		forgottenUser.setKnowsNotToCrawl(knows);
		boolean ok = ForgottenUserUtils.add(forgottenUser);
		if (ok) forgottenUsers.put(uid, forgottenUser);
		return ok;
	}

	@Override
	public boolean setPreventUserFromCrawling(String uid) {
		if (uid == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser != null) {
			boolean before = forgottenUser.isNeedToAvoidBeingCrawled();
			forgottenUser.setNeedToAvoidBeingCrawled(true);
			boolean ok = ForgottenUserUtils.update(forgottenUser);
			if (!ok) forgottenUser.setNeedToAvoidBeingCrawled(before);
			return ok;
		} else {
			return add(uid);
		}
	}

	@Override
	public boolean remove(String uid, String know) {
		if (uid == null || know == null) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser == null) return false;
		if (forgottenUser.getKnowsNotToCrawl() == null
				|| forgottenUser.getKnowsNotToCrawl().size() == 0) return false;
		if (!forgottenUser.getKnowsNotToCrawl().contains(know)) return false; // does not exist
		forgottenUser.getKnowsNotToCrawl().remove(know);
		boolean ok = ForgottenUserUtils.update(forgottenUser);
		if (!ok) forgottenUser.getKnowsNotToCrawl().add(know);
		return ok;
	}

	@Override
	public boolean remove(String uid, Set<String> knows) {
		if (uid == null || (knows == null || knows.size() == 0)) return false;
		ForgottenUser forgottenUser = getForgottenUser(uid);
		if (forgottenUser == null) return false;
		Set <String> originalKnows = forgottenUser.getKnowsNotToCrawl();
		if (originalKnows == null) return false;
		originalKnows.removeAll(knows);
		boolean ok = ForgottenUserUtils.update(forgottenUser);
		return ok;
	}

	@Override
	public boolean deleteUserProfile(String uid) {
		return ForgottenUserUtils.deleteProfile(uid);
	}
	
	
}

package eu.threecixty.profile;

import java.util.HashMap;
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
		ForgottenUser forgottenUser = forgottenUsers.get(uid);
		if (forgottenUser != null) return forgottenUser.isNeedToAvoidBeingCrawled();
		forgottenUser = ForgottenUserUtils.get(uid);
		if (forgottenUser != null) {
			forgottenUsers.put(uid, forgottenUser);
			return forgottenUser.isNeedToAvoidBeingCrawled();
		}
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
		ForgottenUser forgottenUser = forgottenUsers.get(uid);
		if (forgottenUser == null) {
			forgottenUser = ForgottenUserUtils.get(uid);
			if (forgottenUser != null) forgottenUsers.put(uid, forgottenUser);
		}
		if (forgottenUser == null) return true;
		Set <String> knows = forgottenUser.getKnowsNotToCrawl();
		if (knows == null || knows.size() == 0) return true;
		return !knows.contains(know);
	}
	
	/**
	 * Update a given forgotten user.
	 * @param forgottenUser
	 * @return
	 */
	public boolean update(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		boolean ok = false;
		if (forgottenUser.getId() != null) {
			ok = ForgottenUserUtils.update(forgottenUser);
		} else {
			ok = ForgottenUserUtils.add(forgottenUser);
		}
		if (ok) {
			forgottenUsers.put(forgottenUser.getUid(), forgottenUser);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the forgotten user of a given UID.
	 * @param uid
	 * @return
	 */
	public ForgottenUser getForgottenUser(String uid) {
		ForgottenUser forgottenUser = ForgottenUserUtils.get(uid);
		return forgottenUser;
	}
	
	public ForgottenUserManagerImpl() {
		forgottenUsers = new HashMap <String, ForgottenUser>();
	}
}

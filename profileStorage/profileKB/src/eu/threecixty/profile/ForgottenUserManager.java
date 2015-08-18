package eu.threecixty.profile;

import eu.threecixty.userprofile.ForgottenUser;

public interface ForgottenUserManager {

	/**
	 * Checks whether or not a given UID is forbidden to crawl.
	 *
	 * @param uid
	 * @return
	 */
	boolean isBlockedByUidOwner(String uid);
	
	/**
	 * Checks whether or not the user corresponding with a given UID lets 3cixty crawl
	 * another user which is associated with a given know.
	 *
	 * @param uid
	 * @param know
	 * @return
	 */
	boolean isCrawlable(String uid, String know);
	
	/**
	 * Update a given forgotten user.
	 * @param forgottenUser
	 * @return
	 */
	boolean update(ForgottenUser forgottenUser);
	
	/**
	 * Gets the forgotten user of a given UID.
	 * @param uid
	 * @return
	 */
	ForgottenUser getForgottenUser(String uid);
}

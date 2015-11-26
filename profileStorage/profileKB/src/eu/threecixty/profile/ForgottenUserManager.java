package eu.threecixty.profile;

import java.util.Set;

import eu.threecixty.userprofile.ForgottenUser;

/**
 * 
 * This interface is to mark a friend being avoided crawling from a user and to
 * mark that a user who wants to be ignored from all crawling jobs.
 *
 */
public interface ForgottenUserManager {

	/**
	 * Checks whether or not a given UID is blocked to crawl.
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
	 * Removes the corresponding forgotten user of a given UID.
	 * @param uid
	 * @return
	 */
	boolean remove(String uid);
	
	/**
	 * Removes a given know being blocked from crawler. That means after this
	 * method is invoked, the crawler should be able to crawl the user associated
	 * with the given know.
	 * @param uid
	 * @param know
	 * @return
	 */
	boolean remove(String uid, String know);
	
	/**
	 * Remove a given set of knows being blocked from crawler. After this method is invoked,
	 * the crawler should be able to crawl the knows when the corresponding user associated
	 * with the given UID logs in.
	 * @param uid
	 * @param knows
	 * @return
	 */
	boolean remove(String uid, Set <String> knows);
	
	/**
	 * Adds the corresponding forgotten user of a given UID to database.
	 * @param uid
	 * @return
	 */
	boolean add(String uid);
	
	/**
	 * Adds a given <code>know</code> into the corresponding ForgottenUser of a given UID to avoid crawling the know.
	 * If the corresponding ForgottenUser doesn't exist, it will be created.
	 * @param uid
	 * @param know
	 * @return
	 */
	boolean add(String uid, String know);
	
	/**
	 * Adds a given set of <code>knows</code> into the corresponding ForgottenUser of a given UID to avoid crawling the knows.
	 * If the corresponding ForgottenUser doesn't exist, it will be created.
	 * @param udi
	 * @param knows
	 * @return
	 */
	boolean add(String udi, Set <String> knows);
	
	/**
	 * Set a flag to prevent a given UID from crawling.
	 * @param uid
	 * @return
	 */
	boolean setPreventUserFromCrawling(String uid);
	
	/**
	 * Gets the forgotten user of a given UID.
	 * @param uid
	 * @return
	 */
	ForgottenUser getForgottenUser(String uid);
	
	/**
	 * Deletes user profile.
	 * @param uid
	 * @return
	 */
	boolean deleteUserProfile(String uid);
}

package eu.threecixty.cache;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;

import org.apache.log4j.Logger;

import eu.threecixty.profile.SPEConstants;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * 
 * This class is used to cache the user profile with Memcached server.
 *
 */
public class ProfileCacheManager {

	private static final String GOOGLE_UID_FRIENDS_KEY = "googleUIDFriends";
	private static final String USER_PROFILE_KEY = "userProfile";
	private static final String GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID = "generated3cixtyUIDWithActualUID";
	private static final int TIME_OUT_TO_GET_CACHE = 200; // in millisecond
	
	/**The attribute which is the list of clients where each client connects to different memcached server*/
	private List<MemcachedClient> memcachedClients;
	
	private static final ProfileCacheManager INSTANCE = new ProfileCacheManager();
	 private static final Logger LOGGER = Logger.getLogger(
			 ProfileCacheManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	public static ProfileCacheManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * This method puts the given user profile to memcached server.
	 * @param userProfile
	 */
	public void put(UserProfile userProfile) {
		if (DEBUG_MOD) LOGGER.info("Start putting profile in memory");
		if (userProfile == null) return;
		String _3cixtyUid = userProfile.getHasUID();
		if (_3cixtyUid == null || _3cixtyUid.equals("")) {
			if (DEBUG_MOD) LOGGER.info("3cixty UID is null or empty");
			return;
		}
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, USER_PROFILE_KEY + _3cixtyUid);
		if (memcachedClient != null) memcachedClient.set(USER_PROFILE_KEY + _3cixtyUid, 0, userProfile);

		if (DEBUG_MOD) LOGGER.info("Profile stored in memory with 3cixty UID = " + _3cixtyUid);
		
		// the following code is to also cache user profile with fake generated 3cixty UID so that
		// it's easier to check existing user profile later.
		Set <ProfileIdentities> pis = userProfile.getHasProfileIdenties();
		if (pis != null) {
			for (ProfileIdentities pi: pis) {
				String source = pi.getHasSourceCarrier();
				String uid = pi.getHasUserAccountID();
				UidSource uidSource = null;
				if (SPEConstants.GOOGLE_SOURCE.equals(source)) uidSource = UidSource.GOOGLE;
				else if (SPEConstants.FACEBOOK_SOURCE.equals(source)) uidSource = UidSource.FACEBOOK;
				if (uidSource != null) {
					String generatedID = Utils.gen3cixtyUID(uid, uidSource);
					MemcachedClient memcachedClient2 = MemcachedUtils.getMemcachedClient(memcachedClients,
							GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID);
					if (memcachedClient2 != null) memcachedClient2.set(
							GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID, 0, _3cixtyUid);
				}
			}
		}
		memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, GOOGLE_UID_FRIENDS_KEY + _3cixtyUid);
		if (memcachedClient != null) memcachedClient.delete(GOOGLE_UID_FRIENDS_KEY + _3cixtyUid);
	}
	
	/**
	 * This method removes the given user profile from memcached server.
	 * @param userProfile
	 */
	public void remove(UserProfile userProfile) {
		if (DEBUG_MOD) LOGGER.info("Start removing profile in memory");
		if (userProfile == null) return;
		String _3cixtyUid = userProfile.getHasUID();
		if (_3cixtyUid == null || _3cixtyUid.equals("")) {
			if (DEBUG_MOD) LOGGER.info("3cixty UID is null or empty");
			return;
		}
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, USER_PROFILE_KEY + _3cixtyUid);
		if (memcachedClient == null) return;
		memcachedClient.delete(USER_PROFILE_KEY + _3cixtyUid);
		if (DEBUG_MOD) LOGGER.info("Profile removed from memory with 3cixty UID = " + _3cixtyUid);
		memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, GOOGLE_UID_FRIENDS_KEY + _3cixtyUid);
		if (memcachedClient != null) memcachedClient.delete(GOOGLE_UID_FRIENDS_KEY + _3cixtyUid);
		
		// remove map between generated 'blink' 3cixty and actual 3cixty
		Set <ProfileIdentities> pis = userProfile.getHasProfileIdenties();
		if (pis != null) {
			for (ProfileIdentities pi: pis) {
				String source = pi.getHasSourceCarrier();
				String uid = pi.getHasUserAccountID();
				UidSource uidSource = null;
				if (SPEConstants.GOOGLE_SOURCE.equals(source)) uidSource = UidSource.GOOGLE;
				else if (SPEConstants.FACEBOOK_SOURCE.equals(source)) uidSource = UidSource.FACEBOOK;
				if (uidSource != null) {
					String generatedID = Utils.gen3cixtyUID(uid, uidSource);
					MemcachedClient memcachedClient2 = MemcachedUtils.getMemcachedClient(memcachedClients,
							GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID);
					if (memcachedClient2 != null) memcachedClient2.delete(GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID);
				}
			}
		}
	}
	
	/**
	 * This method is to find from memcached server the corresponding the user profile
	 * with a given Google UID or Facebook UID.
	 * @param uid
	 * @param source
	 * @return
	 */
	public UserProfile findProfile(String uid, String source) {
		if (DEBUG_MOD) LOGGER.info("Start finding profile in memory");
		String generatedID = Utils.gen3cixtyUID(uid,
				SPEConstants.GOOGLE_SOURCE.equals(source) ? UidSource.GOOGLE : UidSource.FACEBOOK);
		MemcachedClient memcachedClient2 = MemcachedUtils.getMemcachedClient(memcachedClients,
				GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID);
		if (memcachedClient2 == null) return null;
		String _3cixtyUID = null;
		Future<Object> f = memcachedClient2.asyncGet(GENERATED_3CIXTYUID_WITH_ACTUAL_3CIXTY_UID + generatedID);
		try {
			Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
			if (myObj != null) {
				_3cixtyUID = (String) myObj;
			}
		} catch(TimeoutException e) {
		    // Since we don't need this, go ahead and cancel the operation.  This
		    // is not strictly necessary, but it'll save some work on the server.
		    f.cancel(false);
		    // Do other timeout related stuff
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		UserProfile profile = null;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, USER_PROFILE_KEY + _3cixtyUID);
		if (memcachedClient == null) return null;
		if (_3cixtyUID != null) {
			f = memcachedClient.asyncGet(USER_PROFILE_KEY + _3cixtyUID);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) {
					profile = (UserProfile) myObj;
				}
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			if (profile != null) {
				if (DEBUG_MOD) LOGGER.info("Found profile in memory via UID and source");
				return profile;
			}
		}
		if (DEBUG_MOD) {
			if (profile == null) LOGGER.info("Not found profile in memory");
		}
		return null;
	}
	
	/**
	 * This method is to find the corresponding user profile of a given 3cixty UID.
	 * @param _3cixtyUID
	 * @return
	 */
	public UserProfile getProfile(String _3cixtyUID) {
		if (DEBUG_MOD) LOGGER.info("Checking in the memory for 3cixtyUID = " + _3cixtyUID);
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, USER_PROFILE_KEY + _3cixtyUID);
		if (memcachedClient == null) return null;
		UserProfile profile = null;
		if (_3cixtyUID != null) {
			Future<Object> f = memcachedClient.asyncGet(USER_PROFILE_KEY + _3cixtyUID);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) {
					profile = (UserProfile) myObj;
				}
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		if (DEBUG_MOD) {
			if (profile == null) LOGGER.info("Not found the corresponding profile with " + _3cixtyUID + " in memory");
			else LOGGER.info("Found the corresponding profile with " + _3cixtyUID + " in memory");
		}
		return profile;
	}
	
	/**
	 * Stores the list of Google Friends UID in the memcached server.
	 * @param _3cixtyUID
	 * @param googleUIDs
	 */
	public void putGoogleUIDsOfFriens(String _3cixtyUID, List <String> googleUIDs) {
		if (_3cixtyUID == null || googleUIDs == null) return;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, GOOGLE_UID_FRIENDS_KEY + _3cixtyUID);
		if (memcachedClient == null) return;
		memcachedClient.set(GOOGLE_UID_FRIENDS_KEY + _3cixtyUID, 0, googleUIDs);
	}
	
	/**
	 * Gets the list of Google Friends UID from memcached server.
	 * @param _3cixtyUID
	 * @return
	 */
	public List <String> getGoogleUIDsOfFriends(String _3cixtyUID) {
		if (_3cixtyUID == null) return null;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, GOOGLE_UID_FRIENDS_KEY + _3cixtyUID);
		if (memcachedClient == null) return null;
		Future<Object> f = memcachedClient.asyncGet(GOOGLE_UID_FRIENDS_KEY + _3cixtyUID);
		try {
			Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
			if (myObj != null) {
				
				@SuppressWarnings("unchecked")
				List <String> list = (List <String>) myObj;
				return list;
			}
		} catch(TimeoutException e) {
		    // Since we don't need this, go ahead and cancel the operation.  This
		    // is not strictly necessary, but it'll save some work on the server.
		    f.cancel(false);
		    // Do other timeout related stuff
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void stop() {
		if (memcachedClients != null) {
			for (MemcachedClient client: memcachedClients) {
				client.shutdown();
			}
		}
	}
	
	private ProfileCacheManager() {
		memcachedClients = MemcachedUtils.createClients();
	}
}

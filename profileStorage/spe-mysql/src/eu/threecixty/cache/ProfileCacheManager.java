package eu.threecixty.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.threecixty.profile.SPEConstants;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

public class ProfileCacheManager {

	private static final ProfileCacheManager INSTANCE = new ProfileCacheManager();
	 private static final Logger LOGGER = Logger.getLogger(
			 ProfileCacheManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private Map <String, UserProfile> profileCaches; // key is 3cixty UID
	private Map <String, String> uidSourceCaches; // key is generated ID by using Utils.generate3cixtyUID, values is a  3cixty UID
	private Map <String, List <String>> googleUIDsOfFriends;
	
	public static ProfileCacheManager getInstance() {
		return INSTANCE;
	}
	
	public void put(UserProfile userProfile) {
		if (DEBUG_MOD) LOGGER.info("Start putting profile in memory");
		if (userProfile == null) return;
		String _3cixtyUid = userProfile.getHasUID();
		if (_3cixtyUid == null || _3cixtyUid.equals("")) {
			if (DEBUG_MOD) LOGGER.info("3cixty UID is null or empty");
			return;
		}
		profileCaches.put(_3cixtyUid, userProfile);
		if (DEBUG_MOD) LOGGER.info("Profile stored in memory with 3cixty UID = " + _3cixtyUid);
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
					uidSourceCaches.put(generatedID, _3cixtyUid);
				}
			}
		}
		googleUIDsOfFriends.remove(_3cixtyUid);
	}
	
	public void remove(UserProfile userProfile) {
		if (DEBUG_MOD) LOGGER.info("Start removing profile in memory");
		if (userProfile == null) return;
		String _3cixtyUid = userProfile.getHasUID();
		if (_3cixtyUid == null || _3cixtyUid.equals("")) {
			if (DEBUG_MOD) LOGGER.info("3cixty UID is null or empty");
			return;
		}
		profileCaches.remove(_3cixtyUid);
		if (DEBUG_MOD) LOGGER.info("Profile removed from memory with 3cixty UID = " + _3cixtyUid);
		googleUIDsOfFriends.remove(_3cixtyUid);
	}
	
	public UserProfile findProfile(String uid, String source) {
		if (DEBUG_MOD) LOGGER.info("Start finding profile in memory");
		String generatedID = Utils.gen3cixtyUID(uid,
				SPEConstants.GOOGLE_SOURCE.equals(source) ? UidSource.GOOGLE : UidSource.FACEBOOK);
		String _3cixtyUID = uidSourceCaches.get(generatedID);
		UserProfile profile = null;
		if (_3cixtyUID != null) {
			profile = profileCaches.get(_3cixtyUID);
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
	
	public UserProfile getProfile(String _3cixtyUID) {
		if (DEBUG_MOD) LOGGER.info("Checking in the memory for 3cixtyUID = " + _3cixtyUID);
		UserProfile profile = profileCaches.get(_3cixtyUID);
		if (DEBUG_MOD) {
			if (profile == null) LOGGER.info("Not found the corresponding profile with " + _3cixtyUID + " in memory");
			else LOGGER.info("Found the corresponding profile with " + _3cixtyUID + " in memory");
		}
		return profile;
	}
	
	public void putGoogleUIDsOfFriens(String _3cixtyUID, List <String> googleUIDs) {
		if (_3cixtyUID == null || googleUIDs == null) return;
		googleUIDsOfFriends.put(_3cixtyUID, googleUIDs);
	}
	
	public List <String> getGoogleUIDsOfFriends(String _3cixtyUID) {
		return googleUIDsOfFriends.get(_3cixtyUID);
	}
	
	public void printProfilesInCache() {
		StringBuilder sb = new StringBuilder();
		for (String key: profileCaches.keySet()) {
			sb.append("key = " + key).append("\n");
			UserProfile profile = profileCaches.get(key);
			Name name = profile.getHasName();
			sb.append("profile: " + " uid = " + profile.getHasUID()
					+ ", given name = " + name == null ? "" : name.getGivenName()
					+ ", family name = " + name == null ? "": name.getFamilyName()
					+ "\n");
		}
		if (DEBUG_MOD) LOGGER.info(sb.toString());
	}
	
	private ProfileCacheManager() {
		profileCaches = new ConcurrentHashMap<String, UserProfile>();
		uidSourceCaches = new ConcurrentHashMap<String, String>();
		googleUIDsOfFriends = new ConcurrentHashMap<String, List<String>>();
	}
}

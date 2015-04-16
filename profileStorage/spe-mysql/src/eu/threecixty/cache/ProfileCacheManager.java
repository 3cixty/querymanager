package eu.threecixty.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.threecixty.profile.FaceBookAccountUtils;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

public class ProfileCacheManager {

	private static final ProfileCacheManager INSTANCE = new ProfileCacheManager();
	
	private Map <String, UserProfile> profileCaches; // key is 3cixty UID
	private Map <String, String> uidSourceCaches; // key is generated ID by using Utils.generate3cixtyUID, values is a  3cixty UID
	private Map <String, String> profileImageCaches; // key is profile image, values is a  3cixty UID
	private Map <String, List <String>> googleUIDsOfFriends;
	
	public static ProfileCacheManager getInstance() {
		return INSTANCE;
	}
	
	public void put(UserProfile userProfile) {
		if (userProfile == null) return;
		String _3cixtyUid = userProfile.getHasUID();
		if (_3cixtyUid == null || !_3cixtyUid.equals("")) return;
		profileCaches.put(_3cixtyUid, userProfile);
		Set <ProfileIdentities> pis = userProfile.getHasProfileIdenties();
		if (pis != null) {
			for (ProfileIdentities pi: pis) {
				String source = pi.getHasSourceCarrier();
				String uid = pi.getHasUserAccountID();
				UidSource uidSource = null;
				if (GoogleAccountUtils.GOOGLE_SOURCE.equals(source)) uidSource = UidSource.GOOGLE;
				else if (FaceBookAccountUtils.FACEBOOK_SOURCE.equals(source)) uidSource = UidSource.FACEBOOK;
				if (uidSource != null) {
					String generatedID = Utils.gen3cixtyUID(uid, uidSource);
					uidSourceCaches.put(generatedID, _3cixtyUid);
				}
			}
		}
		String profileImage = userProfile.getProfileImage();
		if (profileImage != null && !profileImage.equals("")) profileImageCaches.put(profileImage, _3cixtyUid);
		googleUIDsOfFriends.remove(_3cixtyUid);
	}
	
	public UserProfile findProfile(String uid, String source, String profileImage) {
		String generatedID = Utils.gen3cixtyUID(uid,
				GoogleAccountUtils.GOOGLE_SOURCE.equals(source) ? UidSource.GOOGLE : UidSource.FACEBOOK);
		String _3cixtyUID = uidSourceCaches.get(generatedID);
		if (_3cixtyUID == null) return null;
		UserProfile profile = profileCaches.get(_3cixtyUID);
		if (profile != null) return profile;
		_3cixtyUID = profileImageCaches.get(profileImage);
		if (_3cixtyUID == null) return null;
		profile = profileCaches.get(_3cixtyUID);
		return profile;
	}
	
	public UserProfile getProfile(String _3cixtyUID) {
		return profileCaches.get(_3cixtyUID);
	}
	
	public void putGoogleUIDsOfFriens(String _3cixtyUID, List <String> googleUIDs) {
		if (_3cixtyUID == null || googleUIDs == null) return;
		googleUIDsOfFriends.put(_3cixtyUID, googleUIDs);
	}
	
	public List <String> getGoogleUIDsOfFriends(String _3cixtyUID) {
		return googleUIDsOfFriends.get(_3cixtyUID);
	}
	
	private ProfileCacheManager() {
		profileCaches = new ConcurrentHashMap<String, UserProfile>();
		uidSourceCaches = new ConcurrentHashMap<String, String>();
		profileImageCaches = new ConcurrentHashMap<String, String>();
		googleUIDsOfFriends = new ConcurrentHashMap<String, List<String>>();
	}
}

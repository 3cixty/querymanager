package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.threecixty.Configuration;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.oldmodels.UserInteractionMode;

/**
 * This is a utility class.
 * @author Cong-Kinh Nguyen
 *
 */
public class Utils {
	
	/**A prefix of two characters is really enough for future: 99 social networks*/
	private static final String GOOGLE_PREFIX = "10";
	
	private static final String FACEBOOK_PREFIX = "11";
	private static final String NO_SOCIAL_NETWORK_PREFIX = "99";
	
	private static final String PROFILE_URI = Configuration.PROFILE_URI;
	
	/**
	 * Generates 3cixty UID from a given UID and source.
	 * <br>
	 * Note that there is possibility that Google & Facebook UID are the same,
	 * this method adds a prefix before hashed UID so that 3cixty UID is always
	 * distinguished even original UIDs from Google & Facebook are the same.
	 * @param originalUID
	 * @param source
	 * @return
	 */
	public static String gen3cixtyUID(String originalUID, UidSource source) {
		if (source == null || originalUID == null) return null;
		if (source == UidSource.GOOGLE) {
			return GOOGLE_PREFIX + Encoder.getInstance().encode(originalUID);
		} else if (source == UidSource.FACEBOOK) {
			return FACEBOOK_PREFIX + Encoder.getInstance().encode(originalUID);
		}
		return NO_SOCIAL_NETWORK_PREFIX + Encoder.getInstance().encode(originalUID);
	}

	public static void setProfileIdentities(String _3cixtyUID, String uid, String source,
			Set<ProfileIdentities> profileIdentities) {
		boolean found = false;
		for (ProfileIdentities pi: profileIdentities) {
			if (uid.equals(pi.getHasUserAccountID())) {
				found = true;
				break;
			}
		}
		if (found) return; // already existed
		addProfileIdentities(_3cixtyUID, uid, source, profileIdentities);
	}
	
	protected static Set <String> getOrCreate3cixtyUIDsForKnows(List <String> uids,
			String source) throws IOException, UnknownException {
		List <String> unfoundAccountIds = new LinkedList<String>();
		Set<String> knows = ProfileManagerImpl.getInstance().find3cixtyUIDs(
				uids, source, unfoundAccountIds);
		
		if (unfoundAccountIds.size() > 0) {
			List <UserProfile> userProfilesToBeCreated = new LinkedList<UserProfile>();
			for (String unfoundAccountId: unfoundAccountIds) {
				String tmp3cixtyUid = Utils.gen3cixtyUID(unfoundAccountId,
						SPEConstants.GOOGLE_SOURCE.equalsIgnoreCase(source) ? UidSource.GOOGLE : UidSource.FACEBOOK);
				UserProfile tmpUserProfile = new UserProfile();
				tmpUserProfile.setHasUID(tmp3cixtyUid);
				userProfilesToBeCreated.add(tmpUserProfile);
				Set <ProfileIdentities> tmpPis = new HashSet <ProfileIdentities>();
				Utils.setProfileIdentities(tmp3cixtyUid, unfoundAccountId, source, tmpPis);

				knows.add(tmp3cixtyUid);
			}
			ProfileManagerImpl.getInstance().createProfiles(userProfilesToBeCreated);
		}
		return knows;
	}
	
	protected static void addProfileIdentities(String _3cixtyUID, String partnerAccountId, String source,
			Set<ProfileIdentities> profileIdentities) {
		
		ProfileIdentities pi = new ProfileIdentities();
		pi.setHasSourceCarrier(source);
		pi.setHasUserAccountID(partnerAccountId);
		pi.setHasUserInteractionMode(UserInteractionMode.Active);
		pi.setHasProfileIdentitiesURI(PROFILE_URI+ _3cixtyUID + "/Account/" + pi.getHasSourceCarrier());
		
		profileIdentities.add(pi);
	}
	
	/**
	 * Gets content from a given URL string.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	protected static String readUrl(String urlString) throws Exception {
		StringBuffer buffer = new StringBuffer();
		InputStream reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = url.openStream();
	        
	        int read;
	        byte[] chars = new byte[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(new String(chars, 0, read, "UTF-8")); 

	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	    return buffer.toString();
	}
	
	protected static Map <String, Boolean> getAttributesToStoreForCrawlingSocialProfile() {
		Map <String, Boolean> attrs = new HashMap <String, Boolean>();
		attrs.put(ProfileManager.ATTRIBUTE_NAME, true);
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IMAGE, true);
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IDENTITIES, true);
		attrs.put(ProfileManager.ATTRIBUTE_KNOWS, true);
		attrs.put(ProfileManager.ATTRIBUTE_GENDER, true);
		return attrs;
	}
	
	protected static Map <String, Boolean> getAttributesToLoadProfileFromGoogleFB() {
		Map <String, Boolean> attrs = new HashMap <String, Boolean>();
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IMAGE, true);
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IDENTITIES, true);
		attrs.put(ProfileManager.ATTRIBUTE_KNOWS, true);
		attrs.put(ProfileManager.ATTRIBUTE_GENDER, true);
		attrs.put(ProfileManager.ATTRIBUTE_NAME, true);
		return attrs;
	}
	
	protected static boolean checkProfileIdentitiesModified(
			Set<ProfileIdentities> profileIdentities, String user_id,
			String source) {
		boolean found = false;
		for (ProfileIdentities pi: profileIdentities) {
			if (user_id.equals(pi.getHasUserAccountID())) {
				found = true;
				break;
			}
		}
		return !found;
	}

	protected static boolean checkKnowsModified(UserProfile profile,
			Set<String> knows) {
		Set <String> originalKnows = profile.getKnows();
		if (originalKnows == null || originalKnows.size() == 0) {
			if (knows != null && knows.size() > 0) return true;
			else return false;
		}
		if (knows == null || knows.size() == 0) return true;
		if (knows.size() != originalKnows.size()) return true;
		if (knows.containsAll(originalKnows)) return false;
		return true;
	}

	protected static boolean checkNameAndProfileImageModified(UserProfile profile,
			String firstName, String lastName, String profileImage) {
		Name name = profile.getHasName();
		if (name == null) return true;
		boolean unmodified = isSameString(firstName, name.getGivenName()) && isSameString(lastName, name.getFamilyName())
				&& isSameString(profileImage, profile.getProfileImage());
		return !unmodified;
	}
	
	private static boolean isSameString(String str1, String str2) {
		if (str1 == null) {
			if (str2 != null) return false;
		} else if (str2 == null) {
			if (str1 != null) return false;
		} else {
			return str1.equals(str2);
		}
		return true;
	}
	
	private Utils() {
	}
	
	public enum UidSource {
		GOOGLE, FACEBOOK
	}
}

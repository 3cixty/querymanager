package eu.threecixty.profile;

import java.util.HashMap;
import java.util.Map;

import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;


/**
 * This class is used to read and write profile information into the KB.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ProfileInformationStorage {

	/**
	 * Loads profile information from the KB.
	 * @param uid
	 * @return
	 * @throws TooManyConnections 
	 */
	public static ProfileInformation loadProfile(String uid) throws TooManyConnections {
		if (uid == null || uid.equals("")) return null;

		Map <String, Boolean> attrs = getAttributesForProfileInfo();
		
		UserProfile userProfile = ProfileManagerImpl.getInstance().getProfile(uid, attrs);

		if (userProfile == null) return null;

		ProfileInformation profileInfo = new ProfileInformation();
		profileInfo.setUid(uid);
		loadNameFromKBToPI(uid, userProfile, profileInfo);
		loadAddressInfoFromKBToPI(uid, userProfile, profileInfo);
		profileInfo.setProfileImage(userProfile.getProfileImage());

		if (userProfile.getPreferences() == null) {
			return profileInfo;
		}

		profileInfo.setPreference(userProfile.getPreferences());

		return profileInfo;
	}

	/**
	 * Saves profile information to the KB.
	 * @param profile
	 * @return
	 * @throws TooManyConnections 
	 */
	public static boolean saveProfile(ProfileInformation profile) throws TooManyConnections {
		if (profile == null) return false;

		UserProfile kbUserProfile = new UserProfile();
		kbUserProfile.setHasUID(profile.getUid());

		saveNameInfoToKB(profile, kbUserProfile);

		saveAddressInfoToKB(profile, kbUserProfile);
		if (profile.getPreference() != null) {
			kbUserProfile.setPreferences(profile.getPreference());
		}
		if (!isNullOrEmpty(profile.getProfileImage())) {
			kbUserProfile.setProfileImage(profile.getProfileImage());
		}

		Map <String, Boolean> attrs = getAttributesForProfileInfo();
		
		ProfileManagerImpl.getInstance().saveProfile(kbUserProfile, attrs);
		return true;
	}

	/**
	 * Saves name information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveNameInfoToKB(ProfileInformation profile,
			UserProfile kbUserProfile) {
		Name name = kbUserProfile.getHasName();
		if (name == null) {
			name = new Name();
			kbUserProfile.setHasName(name);
		}
		if (!isNullOrEmpty(profile.getLastName())) {
			name.setFamilyName(profile.getLastName());
			
		}
		if (!isNullOrEmpty(profile.getFirstName())) {
			name.setGivenName(profile.getFirstName());
		}
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(ProfileInformation profile,
			UserProfile kbUserProfile) {
		Address addr = kbUserProfile.getHasAddress();
		if (addr == null) {
			addr = new Address();
			kbUserProfile.setHasAddress(addr);
		}

		if (!isNullOrEmpty(profile.getCountryName())) {
			addr.setCountryName(profile.getCountryName());
		}
		if (!isNullOrEmpty(profile.getTownName())) {
			addr.setTownName(profile.getTownName());
		}

		if (profile.getLatitude() != 0) {
			addr.setLatitude(profile.getLatitude());
		}

		if (profile.getLongitude() != 0) {
			addr.setLongitute(profile.getLongitude());
		}
	}

//	/**
//	 * Loads likes from the KB to a preference instance.
//	 * @param from
//	 * @param to
//	 */
//	private static void loadLikesFromKBToPI(Preference from,
//			SpePreference to) {
//		if (!from.hasHasLike()) return;
//		Set <eu.threecixty.profile.oldmodels.Likes> toLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();
//		to.setHasLikes(toLikes);
//		Iterator <? extends Like> fromLikes = from.getHasLike().iterator();
//		for ( ; fromLikes.hasNext(); ) {
//			Like newLike = fromLikes.next();
//			eu.threecixty.profile.oldmodels.Likes oldLikes = new eu.threecixty.profile.oldmodels.Likes();
//			if (newLike.hasHasLikeName()) {
//				String likeName = newLike.getHasLikeName().iterator().next();
//				oldLikes.setHasLikeName(likeName);
//			}
//			if (newLike.hasHasLikeType()) {
//				Object objLikeType = newLike.getHasLikeType().iterator().next();
//				oldLikes.setHasLikeType(LikeType.valueOf(objLikeType.toString()));
//			}
//			toLikes.add(oldLikes);
//		}
//	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToPI(String uid, UserProfile from,
			ProfileInformation to) {
		if (from.getHasName() != null) {
			Name name = from.getHasName();
			if (name.getFamilyName() != null) {
				to.setLastName(name.getFamilyName());
			}
			if (name.getGivenName() != null) {
				to.setFirstName(name.getGivenName());
			}
		}
	}

	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToPI(String uid, UserProfile from,
			ProfileInformation to) {
		if (from.getHasAddress() == null) return;
		Address addr = from.getHasAddress();
		if (!isNullOrEmpty(addr.getCountryName())) {
			to.setCountryName(addr.getCountryName());
		}
		if (!isNullOrEmpty(addr.getTownName())) {
			to.setTownName(addr.getTownName());
		}
		if (addr.getLatitude() > 0) {
			to.setLatitude(addr.getLatitude());
		}
		if (addr.getLongitute() > 0) {
			to.setLongitude(addr.getLongitute());
		}
	}
	
	private static Map <String, Boolean> getAttributesForProfileInfo() {
		Map <String, Boolean> attrs = new HashMap <String, Boolean>();
		attrs.put(ProfileManager.ATTRIBUTE_NAME, true);
		attrs.put(ProfileManager.ATTRIBUTE_ADDRESS, true);
		attrs.put(ProfileManager.ATTRIBUTE_PROFILE_IMAGE, true);
		return attrs;
	}
	
	/**
	 * Checks whether or not a given input string contains something.
	 * @param input
	 * @return
	 */
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}
	
	
	/**
	 * Prohibits instantiations.
	 */
	private ProfileInformationStorage() {
	}
}

package eu.threecixty.profile;

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
	 */
	public static ProfileInformation loadProfile(String uid) {
		if (uid == null || uid.equals("")) return null;
		try {
			UserProfile userProfile = UserProfileStorage.loadProfile(uid);

			if (userProfile == null) return null;
			
			ProfileInformation profileInfo = new ProfileInformation();
			profileInfo.setUid(uid);
			loadNameFromKBToPI(uid, userProfile, profileInfo);
			loadAddressInfoFromKBToPI(uid, userProfile, profileInfo);

			if (userProfile.getPreferences() == null) {
				return profileInfo;
			}
			
			profileInfo.setPreference(userProfile.getPreferences());
			
			return profileInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Saves profile information to the KB.
	 * @param profile
	 * @return
	 */
	public synchronized static boolean saveProfile(ProfileInformation profile) {
		if (profile == null) return false;
		try {
			UserProfile kbUserProfile = UserProfileStorage.loadProfile(profile.getUid());

			if (kbUserProfile == null) {
				kbUserProfile = new UserProfile();
				kbUserProfile.setHasUID(profile.getUid());
			}
			
			saveNameInfoToKB(profile, kbUserProfile);
			
			saveAddressInfoToKB(profile, kbUserProfile);
			if (profile.getPreference() != null) {
				kbUserProfile.setPreferences(profile.getPreference());
			}
			
			
			UserProfileStorage.saveProfile(kbUserProfile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		if (addr.getCountryName() != null) {
			to.setCountryName(addr.getCountryName());
		}
		if (addr.getTownName() != null) {
			to.setTownName(addr.getTownName());
		}
		if (addr.getLatitude() > 0) {
			to.setLatitude(addr.getLatitude());
		}
		if (addr.getLongitute() > 0) {
			to.setLongitude(addr.getLongitute());
		}
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

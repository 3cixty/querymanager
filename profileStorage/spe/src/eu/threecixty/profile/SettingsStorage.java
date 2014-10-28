package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;


/**
 * This class is to deal with storing settings information into KB and loading the settings.
 * @author Cong-Kinh NGUYEN
 *
 */
public class SettingsStorage {

	/**
	 * Saves given settings information into KB.
	 * @param settings
	 */
	public synchronized static void save(ThreeCixtySettings settings) {
		if (settings == null) return;
		try {
			UserProfile userProfile = new UserProfile();
			userProfile.setHasUID(settings.getUid());
			
			saveNameInfoToKB(settings, userProfile);
			saveAddressInfoToKB(settings, userProfile);
			addProfileIdentitiesIntoUserProfile(settings, userProfile);

			ProfileManagerImpl.getInstance().saveProfile(userProfile,"Insert");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads settings information persisted in the KB.
	 * @param uid
	 * @return
	 */
	public static ThreeCixtySettings load(String uid) {
		if (!isNotNullOrEmpty(uid)) return null;
		try {

			UserProfile userProfile = ProfileManagerImpl.getInstance().getProfile(uid);
			if (userProfile == null) return null;

			ThreeCixtySettings settings = new ThreeCixtySettings();
			settings.setUid(uid);
			
			loadProfileIdentitiesFromUserProfile(userProfile, settings);
			
			loadNameFromKBToPI(uid, userProfile, settings);
			loadAddressInfoFromKBToPI(uid, userProfile, settings);
			//loadAddressInfoAndNameFromUserProfile(uid, settings);

//			loadEventPreferenceFromUserProfile(userProfile, settings);
			return settings;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Loads profile identities from a given user profile to a given settings instance.
	 * @param userProfile
	 * @param settings
	 */
	private static void loadProfileIdentitiesFromUserProfile(
			UserProfile userProfile, ThreeCixtySettings settings) {
		if (userProfile.getHasProfileIdenties() == null) return;
		List <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = settings.getIdentities();
		if (oldProfiles == null) oldProfiles = new ArrayList <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		settings.setIdentities(oldProfiles);
		oldProfiles.addAll(userProfile.getHasProfileIdenties());
	}

	/**
	 * Adds profile identities found in a given settings instance into the KB.
	 * @param settings
	 * @param userProfile
	 */
	private static void addProfileIdentitiesIntoUserProfile(
			ThreeCixtySettings settings, UserProfile userProfile) {
		if (settings.getIdentities() != null) {
			Set <ProfileIdentities> setOfProfileIdentities = userProfile.getHasProfileIdenties();
			if (setOfProfileIdentities == null) {
				setOfProfileIdentities = new HashSet <ProfileIdentities>();
				userProfile.setHasProfileIdenties(setOfProfileIdentities);
			}
			setOfProfileIdentities.addAll(settings.getIdentities());
		}
	}

//	/**
//	 * Loads event preference information from KB (user profile) to a given settings instance.
//	 * @param userProfile
//	 * @param settings
//	 */
//	private static void loadEventPreferenceFromUserProfile(
//			UserProfile userProfile, ThreeCixtySettings settings) {
//		if (!userProfile.hasHasPreference()) return;
//		Preference pref = userProfile.getHasPreference().iterator().next();
//		if (!pref.hasHasEventPreference()) return;
//		EventPreference ep = pref.getHasEventPreference().iterator().next();
//		if (!ep.hasHasEventDetailPreference()) return;
//		EventDetailPreference edp = ep.getHasEventDetailPreference().iterator().next();
//		
//		eu.threecixty.profile.oldmodels.EventDetailPreference oldEdp = null;
//		if (settings.getEventDetailPreference() != null) oldEdp = settings.getEventDetailPreference();
//		else oldEdp = new eu.threecixty.profile.oldmodels.EventDetailPreference();
//		
//		if (edp.hasHasPreferredStartDate()) {
//			Date startDate = (Date) edp.getHasPreferredStartDate().iterator().next();
//			if (startDate != null) oldEdp.setHasPreferredStartDate(startDate);
//		}
//		if (edp.hasHasPreferredEndDate()) {
//			Date endDate = (Date) edp.getHasPreferredEndDate().iterator().next();
//			if (endDate != null) oldEdp.setHasPreferredEndDate(endDate);
//		}
//		if (edp.hasHasNatureOfEvent()) {
//			String noeStr = edp.getHasNatureOfEvent().iterator().next().toString();
//			try {
//			    eu.threecixty.profile.oldmodels.NatureOfEvent oldNoe = eu.threecixty.profile.oldmodels.NatureOfEvent.valueOf(noeStr);
//			    oldEdp.setHasNatureOfEvent(oldNoe);
//			} catch (Exception e) {
//			}
//		}
//		settings.setEventDetailPreference(oldEdp);
//	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToPI(String uid, UserProfile from,
			ThreeCixtySettings to) {
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
			ThreeCixtySettings to) {
		if (from.getHasAddress() == null) return;
		Address addr = from.getHasAddress();
		if (addr.getCountryName() != null) {
			to.setCountryName(addr.getCountryName());
		}
		if (addr.getTownName() != null) {
			to.setTownName(addr.getTownName());
		}
		if (addr.getLatitude() > 0) {
			to.setCurrentLatitude(addr.getLatitude());
		}
		if (addr.getLongitute() > 0) {
			to.setCurrentLongitude(addr.getLongitute());
		}
	}
	
	/**
	 * Saves name information into the KB.
	 * @param settings
	 * @param kbUserProfile
	 */
	private static void saveNameInfoToKB(ThreeCixtySettings settings,
			UserProfile kbUserProfile) {
		Name name = kbUserProfile.getHasName();
		if (name == null) {
			name = new Name();
			kbUserProfile.setHasName(name);
		}
		if (isNotNullOrEmpty(settings.getLastName())) {
			name.setFamilyName(settings.getLastName());
			
		}
		if (isNotNullOrEmpty(settings.getFirstName())) {
			name.setGivenName(settings.getFirstName());
		}
	}
	
	/**
	 * Saves address information into the KB.
	 * @param settings
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(ThreeCixtySettings settings,
			UserProfile kbUserProfile) {
		Address addr = kbUserProfile.getHasAddress();
		if (addr == null) {
			addr = new Address();
			kbUserProfile.setHasAddress(addr);
		}

		if (isNotNullOrEmpty(settings.getCountryName())) {
			addr.setCountryName(settings.getCountryName());
		}
		if (isNotNullOrEmpty(settings.getTownName())) {
			addr.setTownName(settings.getTownName());
		}

		if (settings.getCurrentLatitude() != 0) {
			addr.setLatitude(settings.getCurrentLatitude());
		}

		if (settings.getCurrentLongitude() != 0) {
			addr.setLongitute(settings.getCurrentLongitude());
		}
	}

	/**
	 * Checks whether or not a given string is not null or empty.
	 * @param str
	 * @return
	 */
	private static boolean isNotNullOrEmpty(String str) {
		if (str == null || str.equals("")) return false;
		return true;
	}

	/**Prohibits instantiations*/
	private SettingsStorage() {
	}
}

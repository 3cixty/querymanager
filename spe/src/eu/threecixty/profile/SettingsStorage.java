package eu.threecixty.profile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;


import eu.threecixty.models.Address;
import eu.threecixty.models.EventDetailPreference;
import eu.threecixty.models.EventPreference;
import eu.threecixty.models.MyFactory;
import eu.threecixty.models.Name;
import eu.threecixty.models.Preference;
import eu.threecixty.models.ProfileIdentities;
import eu.threecixty.models.UserProfile;

/**
 * This class is to deal with storing settings information into KB and loading the settings.
 * @author Cong-Kinh NGUYEN
 *
 */
public class SettingsStorage {
	private static final String PROFILE_URI = "http://www.eu.3cixty.org/profile#";

	/**
	 * Saves given settings information into KB.
	 * @param settings
	 */
	public synchronized static void save(ThreeCixtySettings settings) {
		if (settings == null) return;
		try {

//			addAddressInfoAndNameIntoUserProfile(settings);
			
			MyFactory mf = getMyFactory();
			String uid = settings.getUid();
			
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);

			if (userProfile == null) return;
			
			saveNameInfoToKB(settings, userProfile, mf);
			saveAddressInfoToKB(settings, userProfile, mf);
			addProfileIdentitiesIntoUserProfile(settings, mf, userProfile);

			addEventPreferenceInfoIntoUserProfile(settings, mf, userProfile);

			mf.saveOwlOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads settings information persisted in the KB.
	 * @param uid
	 * @return
	 */
	public synchronized static ThreeCixtySettings load(String uid) {
		if (!isNotNullOrEmpty(uid)) return null;
		try {
			MyFactory mf = getMyFactory();

			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);
			if (userProfile == null) return null;

			ThreeCixtySettings settings = new ThreeCixtySettings();
			settings.setUid(uid);
			
			loadProfileIdentitiesFromUserProfile(userProfile, settings);
			
			loadNameFromKBToPI(mf, uid, userProfile, settings);
			loadAddressInfoFromKBToPI(mf, uid, userProfile, settings);
			//loadAddressInfoAndNameFromUserProfile(uid, settings);

			loadEventPreferenceFromUserProfile(userProfile, settings);
			return settings;
		} catch (OWLOntologyCreationException e) {
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
		if (!userProfile.hasHasProfileIdentities()) return;
		List <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = settings.getIdentities();
		if (oldProfiles == null) oldProfiles = new ArrayList <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		settings.setIdentities(oldProfiles);
		for (ProfileIdentities pi: userProfile.getHasProfileIdentities()) {
			eu.threecixty.profile.oldmodels.ProfileIdentities tmpProfile = new eu.threecixty.profile.oldmodels.ProfileIdentities();
			if (pi.hasHasSource()) {
				tmpProfile.setHasSource(pi.getHasSource().iterator().next());
			}
			if (pi.hasHasUserAccountID()) {
				tmpProfile.setHasUserAccountID(pi.getHasUserAccountID().iterator().next());
			}
			if (pi.hasHasUserInteractionMode()) {
				tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(
						pi.getHasUserInteractionMode().iterator().next().toString()));
			}
			oldProfiles.add(tmpProfile);
		}
	}

	/**
	 * Adds profile identities found in a given settings instance into the KB.
	 * @param settings
	 * @param mf
	 * @param userProfile
	 */
	private static void addProfileIdentitiesIntoUserProfile(
			ThreeCixtySettings settings, MyFactory mf, UserProfile userProfile) {
		if (settings.getIdentities() != null) {
			String uid = settings.getUid();
			for (eu.threecixty.profile.oldmodels.ProfileIdentities oldProfile: settings.getIdentities()) {
				ProfileIdentities pi = convertProfileIdentities(oldProfile, mf, uid);
				if (pi != null && !userProfile.getHasProfileIdentities().contains(pi))
					userProfile.addHasProfileIdentities(pi);
			}
		}
	}

	/**
	 * Adds event preference information into a given settings instance.
	 * @param settings
	 * @param mf
	 * @param userProfile
	 */
	private static void addEventPreferenceInfoIntoUserProfile(ThreeCixtySettings settings,
			MyFactory mf, UserProfile userProfile) {
		eu.threecixty.profile.oldmodels.EventDetailPreference edp = settings.getEventDetailPreference();
		if (edp == null) return;
		Date startDate = edp.getHasPreferredStartDate();		
		Date endDate = edp.getHasPreferredEndDate();
		eu.threecixty.profile.oldmodels.NatureOfEvent noe = edp.getHasNatureOfEvent();
		
		String uid = settings.getUid();
		
		Preference pref = null;
		if (!userProfile.hasHasPreference()) pref = mf.createPreference(PROFILE_URI + uid + "Preference");
		else pref = userProfile.getHasPreference().iterator().next();
		
		EventPreference ep = null;
		if (pref.hasHasEventPreference()) ep = pref.getHasEventPreference().iterator().next();
		else ep = mf.createEventPreference(PROFILE_URI + uid + "EventPreference");

		EventDetailPreference newEdp = null;
		if (ep.hasHasEventDetailPreference()) newEdp = ep.getHasEventDetailPreference().iterator().next();
		else newEdp = mf.createEventDetailPreference(PROFILE_URI + uid + "EventDetailPreference");
		
		OWLDataFactory factory = OWLManager.getOWLDataFactory();
		
		if (startDate != null) {
			if (newEdp.hasHasPreferredStartDate()) {
				Object startObj = newEdp.getHasPreferredStartDate().iterator().next();
				newEdp.removeHasPreferredStartDate(startObj);
			}

			OWLLiteral startDateLiteral = factory.getOWLLiteral(convert(startDate),
					new OWLDatatypeImpl(XSDVocabulary.DATE_TIME.getIRI()));
			
			newEdp.addHasPreferredStartDate(startDateLiteral);
		}
		if (endDate != null) {
			if (newEdp.hasHasPreferredEndDate()) {
				Object endObj = newEdp.getHasPreferredEndDate().iterator().next();
				newEdp.removeHasPreferredEndDate(endObj);
			}
			OWLLiteral endDateLiteral = factory.getOWLLiteral(convert(endDate),
					new OWLDatatypeImpl(XSDVocabulary.DATE_TIME.getIRI()));
			newEdp.addHasPreferredEndDate(endDateLiteral);
		}
		if (noe != null) {
			if (newEdp.hasHasNatureOfEvent()) {
				Object noeObj = newEdp.getHasNatureOfEvent().iterator().next();
				newEdp.removeHasNatureOfEvent(noeObj);
			}
			newEdp.addHasNatureOfEvent(noe.toString());
		}
	}

	/**
	 * Loads event preference information from KB (user profile) to a given settings instance.
	 * @param userProfile
	 * @param settings
	 */
	private static void loadEventPreferenceFromUserProfile(
			UserProfile userProfile, ThreeCixtySettings settings) {
		if (!userProfile.hasHasPreference()) return;
		Preference pref = userProfile.getHasPreference().iterator().next();
		if (!pref.hasHasEventPreference()) return;
		EventPreference ep = pref.getHasEventPreference().iterator().next();
		if (!ep.hasHasEventDetailPreference()) return;
		EventDetailPreference edp = ep.getHasEventDetailPreference().iterator().next();
		
		eu.threecixty.profile.oldmodels.EventDetailPreference oldEdp = null;
		if (settings.getEventDetailPreference() != null) oldEdp = settings.getEventDetailPreference();
		else oldEdp = new eu.threecixty.profile.oldmodels.EventDetailPreference();
		
		if (edp.hasHasPreferredStartDate()) {
			Date startDate = (Date) edp.getHasPreferredStartDate().iterator().next();
			if (startDate != null) oldEdp.setHasPreferredStartDate(startDate);
		}
		if (edp.hasHasPreferredEndDate()) {
			Date endDate = (Date) edp.getHasPreferredEndDate().iterator().next();
			if (endDate != null) oldEdp.setHasPreferredEndDate(endDate);
		}
		if (edp.hasHasNatureOfEvent()) {
			String noeStr = edp.getHasNatureOfEvent().iterator().next().toString();
			try {
			    eu.threecixty.profile.oldmodels.NatureOfEvent oldNoe = eu.threecixty.profile.oldmodels.NatureOfEvent.valueOf(noeStr);
			    oldEdp.setHasNatureOfEvent(oldNoe);
			} catch (Exception e) {
			}
		}
		settings.setEventDetailPreference(oldEdp);
	}
	
//	/**
//	 * Loads address information from KB (user profile).
//	 * @param settings
//	 * @param userProfile
//	 */
//	private static void loadAddressInfoAndNameFromUserProfile(String uid, ThreeCixtySettings settings) {
//		ProfileInformation profileInformation = ProfileInformationStorage.loadProfile(uid);
//		if (profileInformation == null) return;
//		
//		if (isNotNullOrEmpty(profileInformation.getFirstName())) settings.setFirstName(profileInformation.getFirstName());
//		if (isNotNullOrEmpty(profileInformation.getLastName())) settings.setLastName(profileInformation.getLastName());
//		
//		if (isNotNullOrEmpty(profileInformation.getCountryName())) settings.setCountryName(profileInformation.getCountryName());
//		if (isNotNullOrEmpty(profileInformation.getTownName())) settings.setTownName(profileInformation.getTownName());
//		if (profileInformation.getLatitude() > 0) settings.setCurrentLatitude(profileInformation.getLatitude());
//		if (profileInformation.getLongitude() > 0) settings.setCurrentLongitude(profileInformation.getLongitude());
//	}
//
//	/**
//	 * Adds address information into KB.
//	 * @param settings
//	 */
//	private synchronized static void addAddressInfoAndNameIntoUserProfile(ThreeCixtySettings settings) {
//		ProfileInformation profileInformation = new ProfileInformation();
//		profileInformation.setUid(settings.getUid());
//		
//		if (isNotNullOrEmpty(settings.getFirstName())) profileInformation.setFirstName(settings.getFirstName());
//		if (isNotNullOrEmpty(settings.getLastName())) profileInformation.setLastName(settings.getLastName());
//		
//		if (isNotNullOrEmpty(settings.getCountryName())) profileInformation.setCountryName(settings.getCountryName());
//		if (isNotNullOrEmpty(settings.getTownName())) profileInformation.setTownName(settings.getTownName());
//		if (settings.getCurrentLatitude() > 0) profileInformation.setLatitude(settings.getCurrentLatitude());
//		if (settings.getCurrentLongitude() > 0) profileInformation.setLongitude(settings.getCurrentLongitude());
//		ProfileInformationStorage.saveProfile(profileInformation);
//	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToPI(MyFactory mf, String uid, UserProfile from,
			ThreeCixtySettings to) {
		if (from.hasHas_name()) {
			Name name = mf.getName(PROFILE_URI + uid + "Name");
			if (name.hasFamily_name()) {
				to.setLastName(name.getFamily_name().iterator().next().toString());
			}
			if (name.hasGiven_name()) {
				to.setFirstName(name.getGiven_name().iterator().next().toString());
			}
		}
	}
	
	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToPI(MyFactory mf, String uid, UserProfile from,
			ThreeCixtySettings to) {
		if (!from.hasHas_address()) return;
		Address addr = mf.getAddress(PROFILE_URI + uid + "Address");
		if (addr.hasCountry_name()) {
			Object objCountryName = addr.getCountry_name().iterator().next();
			to.setCountryName(objCountryName.toString());
		}
		if (addr.hasTownName()) {
			String objTownName = addr.getTownName().iterator().next();
			to.setTownName(objTownName.toString());
		}
		if (addr.hasLatitude()) {
			Object objLatitude = addr.getLatitude().iterator().next();
			to.setCurrentLatitude(Double.parseDouble(objLatitude.toString()));
		}
		if (addr.hasLongitude()) {
			Object objLongitude = addr.getLongitude().iterator().next();
			to.setCurrentLongitude(Double.parseDouble(objLongitude.toString()));
		}
	}
	
	/**
	 * Saves name information into the KB.
	 * @param settings
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveNameInfoToKB(ThreeCixtySettings settings,
			UserProfile kbUserProfile, MyFactory mf) {
		if (kbUserProfile.hasHas_name()) {
			WrappedIndividual obj = kbUserProfile.getHas_name().iterator().next();
			kbUserProfile.removeHas_name(obj);
		}
		Name name = mf.createName(PROFILE_URI + settings.getUid() + "Name");
		if (isNotNullOrEmpty(settings.getLastName())) {
			Iterator <? extends Object> iterators = name.getFamily_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeFamily_name(obj);
			}
			name.addFamily_name(settings.getLastName());
			
		}
		if (isNotNullOrEmpty(settings.getFirstName())) {
			
			Iterator <? extends Object> iterators = name.getGiven_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeGiven_name(obj);
			}
			
			name.addGiven_name(settings.getFirstName());
		}
		kbUserProfile.addHas_name(name);
	}
	
	/**
	 * Saves address information into the KB.
	 * @param settings
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(ThreeCixtySettings settings,
			UserProfile kbUserProfile, MyFactory mf) {
		if (kbUserProfile.hasHas_address()) {
			WrappedIndividual addrObj = kbUserProfile.getHas_address().iterator().next();
			kbUserProfile.removeHas_address(addrObj);
		}

		Address addr = mf.createAddress(PROFILE_URI + settings.getUid() + "Address");
		kbUserProfile.addHas_address(addr);

		if (isNotNullOrEmpty(settings.getCountryName())) {
			if (addr.hasCountry_name()) {
				Object objCountryName = addr.getCountry_name().iterator().next();
				addr.removeCountry_name(objCountryName);
			}
			addr.addCountry_name(settings.getCountryName());
		}
		if (isNotNullOrEmpty(settings.getTownName())) {
			if (addr.hasTownName()) {
				String objTownName = addr.getTownName().iterator().next();
				addr.removeTownName(objTownName);
			}
			addr.addTownName(settings.getTownName());
		}

		if (settings.getCurrentLatitude() != 0) {
			if (addr.hasLatitude()) {
				Object objLatitude = addr.getLatitude().iterator().next();
				addr.removeLatitude(objLatitude);
			}
			addr.addLatitude((float) settings.getCurrentLatitude());
		}

		if (settings.getCurrentLongitude() != 0) {
			if (addr.hasLongitude()) {
				Object objLongitude = addr.getLongitude().iterator().next();
				addr.removeLongitude(objLongitude);
			}
			addr.addLongitude((float) settings.getCurrentLongitude());
		}
	}

	/**
	 * Converts profile identities from old model to a new one.
	 * @param oldProfile
	 * @param mf
	 * @param uid
	 * @return
	 */
	private static ProfileIdentities convertProfileIdentities(
			eu.threecixty.profile.oldmodels.ProfileIdentities oldProfile, MyFactory mf, String uid) {
		ProfileIdentities pi = mf.createProfileIdentities(PROFILE_URI
				+ uid + oldProfile.getHasSource() + oldProfile.getHasUserAccountID());
		if (oldProfile.getHasSource() != null) pi.addHasSource(oldProfile.getHasSource());
		if (oldProfile.getHasUserAccountID() != null) pi.addHasUserAccountID(oldProfile.getHasUserAccountID());
		return pi;
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

	/**
	 * Gets my factory.
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	private static MyFactory getMyFactory() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		File file = new File(RdfFileManager.getInstance().getPathToRdfFile());
		IRI iri= IRI.create(file);

		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);

		MyFactory mf = new MyFactory(ontology);
		return mf;
	}

	/**
	 * Converts a given date to string.
	 * @param date
	 * @return
	 */
	private static String convert(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(date);
	}
	
	/**Prohibits instantiations*/
	private SettingsStorage() {
	}
}

package eu.threecixty.profile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public static void save(ThreeCixtySettings settings) {
		if (settings == null) return;
		try {

			MyFactory mf = getMyFactory();
			String uid = settings.getUid();
			
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);
			System.out.println(uid);
			System.out.println(userProfile);
			if (userProfile == null) return;
			
			addProfileIdentitiesIntoUserProfile(settings, mf, userProfile);
			
			addAddressInfoIntoUserProfile(settings, mf, userProfile);

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
	public static ThreeCixtySettings load(String uid) {
		if (!isNotNullOrEmpty(uid)) return null;
		try {
			MyFactory mf = getMyFactory();

			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);
			if (userProfile == null) return null;

			ThreeCixtySettings settings = new ThreeCixtySettings();
			settings.setUid(uid);
			
			loadProfileIdentitiesFromUserProfile(userProfile, settings);
			
			loadAddressInfoFromUserProfile(userProfile, settings);

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
	
	/**
	 * Loads address information from KB (user profile).
	 * @param settings
	 * @param userProfile
	 */
	private static void loadAddressInfoFromUserProfile(UserProfile userProfile,
			ThreeCixtySettings settings) {
		if (!userProfile.hasHas_address()) return;
		Address addr = (Address) userProfile.getHas_address().iterator().next();
		if (addr.hasCountry_name()) {
			Object objCountryName = addr.getCountry_name().iterator().next();
			settings.setCountryName(objCountryName.toString());
		}
		if (addr.hasTownName()) {
			String objTownName = addr.getTownName().iterator().next();
			settings.setTownName(objTownName.toString());
		}
		if (addr.hasLatitude()) {
			Object objLatitude = addr.getLatitude().iterator().next();
			settings.setCurrentLatitude((Double) objLatitude);
		}
		if (addr.hasLongitude()) {
			Object objLongitude = addr.getLongitude().iterator().next();
			settings.setCurrentLongitude((Double) objLongitude);
		}
	}

	/**
	 * Adds address information into KB.
	 * @param settings
	 * @param mf
	 * @param userProfile
	 */
	private static void addAddressInfoIntoUserProfile(ThreeCixtySettings settings,
			MyFactory mf, UserProfile userProfile) {
		Address addr = null;
		if (userProfile.hasHas_address()) addr = (Address) userProfile.getHas_address().iterator().next();
		else addr = mf.createAddress(PROFILE_URI + settings.getUid() + "Address");
		System.out.println(addr);
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

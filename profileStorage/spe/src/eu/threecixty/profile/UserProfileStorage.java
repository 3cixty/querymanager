package eu.threecixty.profile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.models.Accompanying;
import eu.threecixty.models.Address;
import eu.threecixty.models.EventDetails;
import eu.threecixty.models.HotelDetail;
import eu.threecixty.models.Like;
import eu.threecixty.models.MyFactory;
import eu.threecixty.models.Name;
import eu.threecixty.models.PersonalPlace;
import eu.threecixty.models.PlaceDetail;
import eu.threecixty.models.Preference;
import eu.threecixty.models.ProfileIdentities;
import eu.threecixty.models.Rating;
import eu.threecixty.models.RegularTrip;
import eu.threecixty.models.TemporalDetails;
import eu.threecixty.models.Transport;
import eu.threecixty.models.UserEnteredRatings;
import eu.threecixty.models.UserEventRating;
import eu.threecixty.models.UserHotelRating;
import eu.threecixty.models.UserPlaceRating;
import eu.threecixty.models.UserProfile;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.LikeType;
import eu.threecixty.profile.oldmodels.UserEnteredRating;

public class UserProfileStorage {
	
	private static final Object _sync = new Object();
	private static MyFactory myFactory;

	private static final String PROFILE_URI = "http://www.eu.3cixty.org/profile#";
	
	public static List <eu.threecixty.profile.UserProfile> getAllProfiles() {
		List <eu.threecixty.profile.UserProfile> allProfiles = new LinkedList <eu.threecixty.profile.UserProfile>();
		try {
			MyFactory mf = getMyFactory();
			for (UserProfile userProfile: mf.getAllUserProfileInstances()) {
				String uid = null;
				if (userProfile.hasHasUID()) uid = userProfile.getHasUID().iterator().next().toString();
				if (uid == null) continue;
				eu.threecixty.profile.UserProfile tmp = createUserProfileFromKB(userProfile, uid, mf);
				allProfiles.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allProfiles;
	}
	
	/**
	 * Loads profile information from the KB.
	 * @param uid
	 * @return
	 */
	public static eu.threecixty.profile.UserProfile loadProfile(String uid) {
		if (uid == null || uid.equals("")) return null;
		try {
			MyFactory mf = getMyFactory();
			
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);

			if (userProfile == null) return null;

			if (!userProfile.hasHasUID()) {
				synchronized (_sync) {
					userProfile.addHasUID(uid);
					mf.saveOwlOntology();
				}
			}
			
			eu.threecixty.profile.UserProfile toUserProfile = createUserProfileFromKB(userProfile, uid, mf);
			
			return toUserProfile;
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
	public static boolean saveProfile(eu.threecixty.profile.UserProfile profile) {
		if (profile == null) return false;
		synchronized (_sync) {


			try {
				MyFactory mf = getMyFactory();
				UserProfile kbUserProfile = mf.getUserProfile(PROFILE_URI + profile.getHasUID());

				if (kbUserProfile == null) {
					kbUserProfile = mf.createUserProfile(PROFILE_URI + profile.getHasUID());
				}

				if (!kbUserProfile.hasHasUID()) {
					kbUserProfile.addHasUID(profile.getHasUID());
				}

				saveNameInfoToKB(profile, kbUserProfile, mf);

				saveAddressInfoToKB(profile, kbUserProfile, mf);

				saveProfileIdentitiesToKB(profile, mf, kbUserProfile);
				saveKnowsToKB(profile, kbUserProfile, mf);

				if (profile.getPreferences() != null) {
					savePreferenceToKB(profile.getHasUID(), profile.getPreferences(), kbUserProfile, mf);
				}

				mf.saveOwlOntology();

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private static eu.threecixty.profile.UserProfile createUserProfileFromKB(UserProfile userProfile, String uid, MyFactory mf) {
		eu.threecixty.profile.UserProfile toUserProfile = new eu.threecixty.profile.UserProfile();
		toUserProfile.setHasUID(uid);
		loadNameFromKBToUserProfile(mf, uid, userProfile, toUserProfile);
		loadAddressInfoFromKBToUserProfile(mf, uid, userProfile, toUserProfile);
		
		loadProfileIdentitiesFromUserProfile(userProfile, toUserProfile);
		loadKnowsFromUserProfile(userProfile, toUserProfile);

		if (!userProfile.hasHasPreference()) {
			return toUserProfile;
		}
		
		eu.threecixty.profile.oldmodels.Preference toPrefs = new eu.threecixty.profile.oldmodels.Preference();
		toUserProfile.setPreferences(toPrefs);

		Preference kbPrefs = userProfile.getHasPreference().iterator().next();
		
		loadLikesFromKBToPI(kbPrefs, toPrefs);
		
		if (kbPrefs.hasHasUserEnteredRatings()) {
			Set <eu.threecixty.profile.oldmodels.UserEnteredRating> toUserEnteredRatings = new HashSet <eu.threecixty.profile.oldmodels.UserEnteredRating>();
		    UserEnteredRatings fromUserEnteredRating = kbPrefs.getHasUserEnteredRatings().iterator().next();
		    UserEnteredRating toUserEnteredRating = new UserEnteredRating();
		    loadUserEnteredRatingFromKBToPI(mf, fromUserEnteredRating, toUserEnteredRating);
		    toUserEnteredRatings.add(toUserEnteredRating);
		    toPrefs.setHasUserEnteredRating(toUserEnteredRatings);
		}
		
		loadTransportFromKB(userProfile, toPrefs);
		
		return toUserProfile;
	}

	/**
	 * Adds knows information to KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveKnowsToKB(
			eu.threecixty.profile.UserProfile profile,
			UserProfile kbUserProfile, MyFactory mf) {
		Set <String> knowsSets = profile.getKnows();
		if (knowsSets == null) return;
		for (String knows: knowsSets) {
			UserProfile tmpUserProfile = mf.getUserProfile(PROFILE_URI + knows);
			if (tmpUserProfile == null) {
				tmpUserProfile = mf.createUserProfile(PROFILE_URI + knows);
			} else {
			    if (kbUserProfile.getKnows().contains(tmpUserProfile)) continue;
			}
			kbUserProfile.addKnows(tmpUserProfile);
		}
	}

	/**
	 * Adds profile identities found in a given instance of userprofile into the KB.
	 * @param fromUserProfile
	 * @param mf
	 * @param kbUserProfile
	 */
	private static void saveProfileIdentitiesToKB(
			eu.threecixty.profile.UserProfile fromUserProfile, MyFactory mf, UserProfile kbUserProfile) {
		if (fromUserProfile.getHasProfileIdenties() != null) {
			String uid = fromUserProfile.getHasUID();
			for (eu.threecixty.profile.oldmodels.ProfileIdentities oldProfile: fromUserProfile.getHasProfileIdenties()) {
				ProfileIdentities pi = convertProfileIdentities(oldProfile, mf, uid);
				if (pi != null && !kbUserProfile.getHasProfileIdentities().contains(pi))
					kbUserProfile.addHasProfileIdentities(pi);
			}
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
	 * TODO: Note that this method checks the newForKB attribute for each corresponding class to
	 * decide whether or not an instance is new to store.
	 *
	 * Saves preferences into the KB.
	 * @param preference
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void savePreferenceToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference,
			UserProfile kbUserProfile, MyFactory mf) {
		Preference kbPreference = null;
		if (kbUserProfile.hasHasPreference()) {
			kbPreference = kbUserProfile.getHasPreference().iterator().next();
		} else {
			kbPreference = mf.createPreference(PROFILE_URI + uid + "Preference");
			kbUserProfile.addHasPreference(kbPreference);
		}
		if (preference.getHasUserEnteredRating() != null
				&& preference.getHasUserEnteredRating().size() > 0) {
			UserEnteredRatings kbUserEnteredRatings = null;
			if (kbPreference.hasHasUserEnteredRatings()) {
				kbUserEnteredRatings = kbPreference.getHasUserEnteredRatings().iterator().next();
			} else {
				kbUserEnteredRatings = mf.createUserEnteredRatings(PROFILE_URI + uid + "UserEnteredRatings");
				kbPreference.addHasUserEnteredRatings(kbUserEnteredRatings);
			}
			Iterator <UserEnteredRating> iterators = preference.getHasUserEnteredRating().iterator();
			for (; iterators.hasNext(); ) {
				UserEnteredRating userEnteredRating = iterators.next();
			    saveUserEnteredRatingToKB(uid, userEnteredRating, kbUserEnteredRatings, mf);
			}
		}
		if (preference.getHasTransport() != null) {
			for (eu.threecixty.profile.oldmodels.Transport transport: preference.getHasTransport()) {
				if (transport.getNewForKB() == null || Boolean.TRUE.equals(transport.getNewForKB())) {
					saveTransportToKB(uid, transport, kbUserProfile, mf);
				}
			}
		}
	}

	/**
	 * Creates transport object store in the KB.
	 * @param uid
	 * @param transport
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveTransportToKB(String uid,
			eu.threecixty.profile.oldmodels.Transport transport,
			UserProfile kbUserProfile, MyFactory mf) {
		long timeCreated = System.currentTimeMillis();
		Transport toTransport = mf.createTransport(PROFILE_URI + uid + "Transport" + timeCreated);
		if (transport.getHasRegularTrip() != null) {
			int index = 0;
			for (eu.threecixty.profile.oldmodels.RegularTrip fromRegularTrip: transport.getHasRegularTrip()) {
				RegularTrip toRegularTrip = createRegularTripsInKB(uid, fromRegularTrip, mf, kbUserProfile, timeCreated, index++);
				toTransport.addHasRegularTrip(toRegularTrip);
			}
		}
		if (transport.getHasAccompanyings() != null) {
			int index = 0;
			for (eu.threecixty.profile.oldmodels.Accompanying fromAccompanying: transport.getHasAccompanyings()) {
				Accompanying toAccompanying = createAccompanyingInKB(uid, fromAccompanying, mf, kbUserProfile, timeCreated, index++);
				if (toAccompanying != null) toTransport.addHasAccompany(toAccompanying);
			}
		}
		kbUserProfile.addHasTransport(toTransport);
	}

	/**
	 * Creates accompanying object to store in the KB.
	 * @param uid
	 * @param fromAccompanying
	 * @param mf
	 * @param kbUserProfile
	 * @param timeCreated
	 * @param index
	 * @return
	 */
	private static Accompanying createAccompanyingInKB(String uid,
			eu.threecixty.profile.oldmodels.Accompanying fromAccompanying,
			MyFactory mf, UserProfile kbUserProfile, long timeCreated, int index) {
		Accompanying accompany = mf.createAccompanying(PROFILE_URI + uid + "AccompanyingDetails" + Long.toString(timeCreated) + "_" + Integer.toString(index));
		if (fromAccompanying.getHasAccompanyId() > 0) accompany.addHasAccompanyID(fromAccompanying.getHasAccompanyId());
		if (fromAccompanying.getHasAccompanyUserid1() > 0) accompany.addHasAccompanyUserID1(fromAccompanying.getHasAccompanyUserid1().toString());
		if (fromAccompanying.getHasAccompanyUserid2() > 0) accompany.addHasAccompanyUserID2(fromAccompanying.getHasAccompanyUserid2().toString());
		if (fromAccompanying.getHasAccompanyScore() > 0) accompany.addHasAccompanyScore(fromAccompanying.getHasAccompanyScore().floatValue());
		if (fromAccompanying.getHasAccompanyTime() > 0) accompany.addHasAccompanyTime(fromAccompanying.getHasAccompanyTime());
		if (fromAccompanying.getHasAccompanyValidity() > 0) accompany.addHasAccompanyValidity(fromAccompanying.getHasAccompanyValidity());
		return accompany;
	}

	/**
	 * create the regular trip object to store in KB
	 * @param uID
	 * @param jsonobj
	 * @param mf
	 * @param user
	 * @param currentTime
	 * @param index
	 * @return regularTrip object
	 */
	private static RegularTrip createRegularTripsInKB(String uID, eu.threecixty.profile.oldmodels.RegularTrip fromRegularTrip,
			MyFactory mf, UserProfile kbUserProfile,Long currentTime, int index) {
		RegularTrip regularTrip = mf.createRegularTrip(PROFILE_URI + uID + "RegularTrip" + Long.toString(currentTime)+"_"+Integer.toString(index));
		regularTrip.addHasUID(fromRegularTrip.getHasUID().toString());
		
		if (fromRegularTrip.getHasRegularTripName() != null) regularTrip.addHasRegularTripName(fromRegularTrip.getHasRegularTripName());
		if (fromRegularTrip.getHasRegularTripDepartureTime() > 0) regularTrip.addHasRegularTripDepartureTime(fromRegularTrip.getHasRegularTripDepartureTime());
		if (fromRegularTrip.getHasRegularTripDepartureTimeSD() > 0) regularTrip.addHasRegularTripDepartureTimeSD(fromRegularTrip.getHasRegularTripDepartureTimeSD());
		if (fromRegularTrip.getHasRegularTripTravelTime() > 0) regularTrip.addHasRegularTripTravelTime(fromRegularTrip.getHasRegularTripTravelTime());
		if (fromRegularTrip.getHasRegularTripTravelTimeSD() > 0) regularTrip.addHasRegularTripTravelTimeSD(fromRegularTrip.getHasRegularTripTravelTimeSD());
		if (fromRegularTrip.getHasRegularTripFastestTravelTime() > 0) regularTrip.addHasRegularTripFastestTravelTime(fromRegularTrip.getHasRegularTripFastestTravelTime());
		if (fromRegularTrip.getHasRegularTripTotalDistance() > 0) regularTrip.addHasRegularTripTotalDistance(Float.parseFloat(fromRegularTrip.getHasRegularTripTotalDistance().toString()));
		if (fromRegularTrip.getHasRegularTripTotalCount() > 0) regularTrip.addHasRegularTripTotalCount(fromRegularTrip.getHasRegularTripTotalCount());
		if (fromRegularTrip.getHasModalityType() != null) regularTrip.addHasModalityType(fromRegularTrip.getHasModalityType().toString());
		if (fromRegularTrip.getHasRegularTripWeekdayPattern() != null) regularTrip.addHasRegularTripWeekdayPattern(fromRegularTrip.getHasRegularTripWeekdayPattern());
		if (fromRegularTrip.getHasRegularTripDayhourPattern() != null) regularTrip.addHasRegularTripDayHourPattern(fromRegularTrip.getHasRegularTripDayhourPattern());
		if (fromRegularTrip.getHasRegularTripLastChanged() > 0) regularTrip.addHasRegularTripLastChanged(fromRegularTrip.getHasRegularTripLastChanged());
		if (fromRegularTrip.getHasRegularTripTravelTimePattern() != null) regularTrip.addHasRegularTripTravelTimePattern(fromRegularTrip.getHasRegularTripTravelTimePattern());
		
		eu.threecixty.profile.oldmodels.PersonalPlace [] fromPersonalPlaces = fromRegularTrip.getHasPersonalPlaces();
		if (fromPersonalPlaces == null || fromPersonalPlaces.length == 0) return regularTrip;
		
		for (int i = 0; i < fromPersonalPlaces.length; i++)
		{
			eu.threecixty.profile.oldmodels.PersonalPlace fromPersonalPlace = fromPersonalPlaces[i];
			PersonalPlace personalPlace=mf.createPersonalPlace(PROFILE_URI + uID + "RegularTrip" + Long.toString(currentTime) + "_" + Integer.toString(index) + "PersonalPlace"+Integer.toString(i));
			if (fromPersonalPlace.getHasPersonalPlaceexternalIds() != null) personalPlace.addHasPersonalPlaceExternalIds(fromPersonalPlace.getHasPersonalPlaceexternalIds());
			if (fromPersonalPlace.getPostalcode() != null) personalPlace.addPostal_code(fromPersonalPlace.getPostalcode());
			if (fromPersonalPlace.getHasPersonalPlaceWeekdayPattern() != null) personalPlace.addHasPersonalPlaceWeekDayPattern(fromPersonalPlace.getHasPersonalPlaceDayhourPattern());
			if (fromPersonalPlace.getHasPersonalPlaceStayPercentage() > 0) personalPlace.addHasPersonalPlaceStayPercentage(fromPersonalPlace.getHasPersonalPlaceStayPercentage().floatValue());
			if (fromPersonalPlace.getHasPersonalPlaceType() != null) personalPlace.addHasPersonalPlaceType(fromPersonalPlace.getHasPersonalPlaceType());
			personalPlace.addHasUID(uID);
			if (fromPersonalPlace.getHasPersonalPlaceDayhourPattern() != null) personalPlace.addHasPersonalPlaceDayHourPattern(fromPersonalPlace.getHasPersonalPlaceDayhourPattern());
			if (fromPersonalPlace.getHasPersonalPlaceName() != null) personalPlace.addHasPersonalPlaceName(fromPersonalPlace.getHasPersonalPlaceName());
			if (fromPersonalPlace.getLatitude() > 0) personalPlace.addLatitude(fromPersonalPlace.getLatitude().floatValue());
			if (fromPersonalPlace.getLongitude() > 0) personalPlace.addLongitude(fromPersonalPlace.getLongitude().floatValue());
			if (fromPersonalPlace.getHasPersonalPlaceStayDuration() > 0) personalPlace.addHasPersonalPlaceStayDuration(fromPersonalPlace.getHasPersonalPlaceStayDuration());
			if (fromPersonalPlace.getHasPersonalPlaceAccuracy() > 0) personalPlace.addHasPersonalPlaceAccuracy(fromPersonalPlace.getHasPersonalPlaceAccuracy().floatValue());
			
			regularTrip.addHasPersonalPlace(personalPlace);
		}
		
		return regularTrip;
	}

	/**
	 * Save user entered rating to the KB.
	 * @param uid
	 * @param userEnteredRating
	 * @param kbUserEnteredRatings
	 * @param mf
	 */
	private static void saveUserEnteredRatingToKB(String uid,
			UserEnteredRating userEnteredRating,
			UserEnteredRatings kbUserEnteredRatings, MyFactory mf) {
		if (userEnteredRating.getHasUserEventRating() != null) {
			for (eu.threecixty.profile.oldmodels.UserEventRating userEventRating:
				userEnteredRating.getHasUserEventRating()) {
				if (userEventRating.getNewForKB() == null || Boolean.TRUE.equals(userEventRating.getNewForKB())) {
				    saveUserEventRatingToKB(uid, userEventRating, kbUserEnteredRatings, mf);
				}
			}
		}
		if (userEnteredRating.getHasUserPlaceRating() != null) {
			for (eu.threecixty.profile.oldmodels.UserPlaceRating userPlaceRating:
				userEnteredRating.getHasUserPlaceRating()) {
				if (userPlaceRating.getNewForKB() == null || Boolean.TRUE.equals(userPlaceRating.getNewForKB())) {
				    saveUserPlaceRatingToKB(uid, userPlaceRating, kbUserEnteredRatings, mf);
				}
			}
		}
		if (userEnteredRating.getHasUserHotelRating() != null) {
			for (eu.threecixty.profile.oldmodels.UserHotelRating userHotelRating:
				userEnteredRating.getHasUserHotelRating()) {
				if (userHotelRating.getNewForKB() == null || Boolean.TRUE.equals(userHotelRating.getNewForKB())) {
				    saveUserHotelRatingToKB(uid, userHotelRating, kbUserEnteredRatings, mf);
				}
			}
		}
	}

	/**
	 * Saves user hotel rating to the KB.
	 * @param uid
	 * @param userHotelRating
	 * @param kbUserEnteredRatings
	 * @param mf
	 */
	private static void saveUserHotelRatingToKB(String uid,
			eu.threecixty.profile.oldmodels.UserHotelRating userHotelRating,
			UserEnteredRatings kbUserEnteredRatings, MyFactory mf) {
		UserHotelRating kbUserHotelRating = mf.createUserHotelRating(PROFILE_URI + uid + "UserHotelRating" + System.currentTimeMillis());
		if (userHotelRating.getHasNumberOfTImesVisited() > 0) {
			kbUserHotelRating.addHasNumberofTimesVisited(userHotelRating.getHasNumberOfTImesVisited());
		}
		if (userHotelRating.getHasRating() != null) {
			Rating kbRating = mf.createRating(PROFILE_URI + uid + "Rating" + System.currentTimeMillis());
			saveRatingToKB(userHotelRating.getHasRating(), kbRating, mf);
			kbUserHotelRating.addHasRating(kbRating);
		}
		if (userHotelRating.getHasHotelDetail() != null) {
			HotelDetail kbHotelDetail = mf.createHotelDetail(PROFILE_URI + uid + "HotelDetail" + System.currentTimeMillis());
			saveHotelDetailToKB(uid, userHotelRating.getHasHotelDetail(), kbHotelDetail, mf);
			kbUserHotelRating.addHasHotelDetail(kbHotelDetail);
		}
		kbUserEnteredRatings.addHasUserHotelRating(kbUserHotelRating);
	}

	/**
	 * Saves hotel detail to the KB.
	 * @param uid
	 * @param hotelDetail
	 * @param kbHotelDetail
	 * @param mf
	 */
	private static void saveHotelDetailToKB(String uid,
			eu.threecixty.profile.oldmodels.HotelDetail hotelDetail,
			HotelDetail kbHotelDetail, MyFactory mf) {
		if (hotelDetail.getHasAddress() != null) {
			Address kbAddress = mf.createAddress(PROFILE_URI + uid + "Address" + System.currentTimeMillis());
			saveAddressToKB(uid, hotelDetail.getHasAddress(), kbAddress, mf);
			kbHotelDetail.addHas_address(kbAddress);
		}
		if (hotelDetail.getHasHotelChains() != null) {
			Iterator <eu.threecixty.profile.oldmodels.Address> hotelChainAddresses = hotelDetail.getHasHotelChains().iterator();
			for ( ; hotelChainAddresses.hasNext(); ) {
				eu.threecixty.profile.oldmodels.Address hotelChainAddress = hotelChainAddresses.next();
				Address kbHotelChainAddress = mf.createAddress(PROFILE_URI + uid + "HotelChainAddress" + System.currentTimeMillis());
				saveAddressToKB(uid, hotelChainAddress, kbHotelChainAddress, mf);
				kbHotelDetail.addHasHotelChains(kbHotelChainAddress);
			}
		}
		if (hotelDetail.getHasHotelPriceHigh() > 0) {
			kbHotelDetail.addHasHotelPriceHigh(hotelDetail.getHasHotelPriceHigh().floatValue());
		}
		if (hotelDetail.getHasHotelPriceLow() > 0) {
			kbHotelDetail.addHasHotelPriceLow(hotelDetail.getHasHotelPriceLow().floatValue());
		}
		if (!isNullOrEmpty(hotelDetail.getHasHotelRoomTypes())) {
			kbHotelDetail.addHasHotelRoomType(hotelDetail.getHasHotelRoomTypes());
		}
		if (hotelDetail.getHasHotelStarCategory() > 0) {
			kbHotelDetail.addHasHotelStarCategory(hotelDetail.getHasHotelStarCategory());
		}
		if (!isNullOrEmpty(hotelDetail.getHasNearbyTransportMode())) {
			kbHotelDetail.addHasNearByTransportMode(hotelDetail.getHasNearbyTransportMode());
		}
		if (hotelDetail.getHasNatureOfPlace() != null) {
			kbHotelDetail.addHasNatureOfPlace(hotelDetail.getHasNatureOfPlace().toString());
		}
		if (!isNullOrEmpty(hotelDetail.getHasPlaceName())) {
			kbHotelDetail.addHasPlaceName(hotelDetail.getHasPlaceName());
		}
		if (hotelDetail.getHasTypeOfFood() != null) {
			kbHotelDetail.addHasTypeOfFood(hotelDetail.getHasTypeOfFood().toString());
		}
	}

	/**
	 * Saves user place rating to the KB.
	 * @param uid
	 * @param userPlaceRating
	 * @param kbUserEnteredRatings
	 * @param mf
	 */
	private static void saveUserPlaceRatingToKB(String uid,
			eu.threecixty.profile.oldmodels.UserPlaceRating userPlaceRating,
			UserEnteredRatings kbUserEnteredRatings, MyFactory mf) {
		long timeCreated = System.currentTimeMillis();
		UserPlaceRating kbUserPlaceRating = mf.createUserPlaceRating(PROFILE_URI + uid + "UserPlaceRating" + timeCreated);
		if (userPlaceRating.getHasNumberOfTimesVisited() > 0) {
			kbUserPlaceRating.addHasNumberofTimesVisited(userPlaceRating.getHasNumberOfTimesVisited());
		}
		if (userPlaceRating.getHasRating() != null) {
			Rating kbRating = mf.createRating(PROFILE_URI + uid + "Rating" + timeCreated);
			saveRatingToKB(userPlaceRating.getHasRating(), kbRating, mf);
			kbUserPlaceRating.addHasRating(kbRating);
		}
		if (userPlaceRating.getHasPlaceDetail() != null) {
			PlaceDetail kbPlaceDetail = mf.createPlaceDetail(PROFILE_URI + uid + "PlaceDetail" + timeCreated);
			savePlaceDetailToKB(uid, userPlaceRating.getHasPlaceDetail(), kbPlaceDetail, mf);
			kbUserPlaceRating.addHasPlaceDetail(kbPlaceDetail);
		}
		
		kbUserEnteredRatings.addHasUserPlaceRating(kbUserPlaceRating);
	}

	/**
	 * Saves place detail to the KB.
	 * @param placeDetail
	 * @param kbPlaceDetail
	 * @param mf
	 */
	private static void savePlaceDetailToKB(String uid,
			eu.threecixty.profile.oldmodels.PlaceDetail placeDetail,
			PlaceDetail kbPlaceDetail, MyFactory mf) {
		if (placeDetail.getHasAddress() != null) {
			Address kbAddress = mf.createAddress(PROFILE_URI + uid + "Address" + System.currentTimeMillis());
			saveAddressToKB(uid, placeDetail.getHasAddress(), kbAddress, mf);
			kbPlaceDetail.addHas_address(kbAddress);
		}
		if (!isNullOrEmpty(placeDetail.getHasPlaceName())) {
			kbPlaceDetail.addHasPlaceName(placeDetail.getHasPlaceName());
		}
		if (placeDetail.getHasNatureOfPlace() != null) {
			kbPlaceDetail.addHasNatureOfPlace(placeDetail.getHasNatureOfPlace().toString());
		}
	}

	/**
	 * Saves user event rating to the KB.
	 * @param uid
	 * @param userEventRating
	 * @param kbUserEnteredRatings
	 * @param mf
	 */
	private static void saveUserEventRatingToKB(String uid,
			eu.threecixty.profile.oldmodels.UserEventRating userEventRating,
			UserEnteredRatings kbUserEnteredRatings, MyFactory mf) {
		long time = System.currentTimeMillis();
		UserEventRating kbUserEventRating = mf.createUserEventRating(PROFILE_URI + uid + "UserEventRating" + time);
		
		kbUserEventRating.addHasNumberofTimesVisited(userEventRating.getHasNumberOfTimesVisited());
		
		if (userEventRating.getHasEventDetail() != null) {
			EventDetails kbEventDetail = mf.createEventDetails(PROFILE_URI + uid + "UserEventRating" +  "EventDetails" + time);
			saveEventDetailToKB(uid, userEventRating.getHasEventDetail(), kbEventDetail, mf);
			kbUserEventRating.addHasEventDetail(kbEventDetail);
		}
		
		if (userEventRating.getHasRating() != null) {
			Rating kbRating = mf.createRating(PROFILE_URI + uid + "UserEventRating" + "Rating" + time);
			saveRatingToKB(userEventRating.getHasRating(), kbRating, mf);
			kbUserEventRating.addHasRating(kbRating);
		}
		
		kbUserEnteredRatings.addHasUserEventRating(kbUserEventRating);
	}

	/**
	 * Saves rating information to the KB.
	 * @param rating
	 * @param kbRating
	 * @param mf
	 */
	private static void saveRatingToKB(
			eu.threecixty.profile.oldmodels.Rating rating, Rating kbRating,
			MyFactory mf) {
		if (rating.getHasUseDefinedRating() > 0) {
			kbRating.addHasUserDefinedRating((float) rating.getHasUseDefinedRating());
		}
		if (rating.getHasUserInteractionMode() != null) {
			kbRating.addHasUserInteractionMode(rating.getHasUserInteractionMode().toString());
		}
	}

	/**
	 * Saves event detail to the KB.
	 * @param eventDetail
	 * @param kbEventDetail
	 * @param mf
	 */
	private static void saveEventDetailToKB(String uid, EventDetail eventDetail,
			EventDetails kbEventDetail, MyFactory mf) {
		if (eventDetail.getHasAddress() != null) {
			Address kbAddress = mf.createAddress(PROFILE_URI + uid + "Address" + System.currentTimeMillis());
			saveAddressToKB(uid, eventDetail.getHasAddress(), kbAddress, mf);
			kbEventDetail.addAt_place(kbAddress);
		}
		if (!isNullOrEmpty(eventDetail.getHasEventName())) {
			kbEventDetail.addHasEventName(eventDetail.getHasEventName());
		}
		if (eventDetail.getHasNatureOfEvent() != null) {
			kbEventDetail.addHasNatureOfEvent(eventDetail.getHasNatureOfEvent().toString());
		}
		if (eventDetail.getHasTemporalDetails() != null) {
			TemporalDetails kbTemporalDetails = mf.createTemporalDetails(PROFILE_URI + uid + "TemporalDetails" + System.currentTimeMillis());
			saveTemporalDetailsToKB(uid, eventDetail.getHasTemporalDetails(), kbTemporalDetails, mf);
			kbEventDetail.addHasTemporalDetails(kbTemporalDetails);
		}
	}

	/**
	 * Saves temporal details to the KB.
	 * @param uid
	 * @param temporalDetails
	 * @param kbTemporalDetails
	 * @param mf
	 */
	private static void saveTemporalDetailsToKB(String uid,
			eu.threecixty.profile.oldmodels.TemporalDetails temporalDetails,
			TemporalDetails kbTemporalDetails, MyFactory mf) {
		if (temporalDetails.getHasDateFrom() != null) {
			OWLLiteral startDateLiteral = OWLManager.getOWLDataFactory().getOWLLiteral(
					convert(temporalDetails.getHasDateFrom()),
					new OWLDatatypeImpl(XSDVocabulary.DATE_TIME.getIRI()));
			kbTemporalDetails.addHasDateFrom(startDateLiteral);
		}
		if (temporalDetails.getHasDateUntil() != null) {
			OWLLiteral endDateLiteral = OWLManager.getOWLDataFactory().getOWLLiteral(
					convert(temporalDetails.getHasDateUntil()),
					new OWLDatatypeImpl(XSDVocabulary.DATE_TIME.getIRI()));
			kbTemporalDetails.addHasDateUntil(endDateLiteral);
		}
	}

	/**
	 * Saves address to the KB.
	 * @param uid
	 * @param address
	 * @param kbAddress
	 * @param mf
	 */
	private static void saveAddressToKB(String uid,
			eu.threecixty.profile.oldmodels.Address address,
			Address kbAddress, MyFactory mf) {
		if (!isNullOrEmpty(address.getCountryName())) {
			kbAddress.addCountry_name(address.getCountryName());
		}
		if (!isNullOrEmpty(address.getPostalCode())) {
			kbAddress.addPostal_code(address.getPostalCode());
		}
		if (address.getPostOfficeBox() > 0) {
			kbAddress.addPost_office_box(address.getPostOfficeBox() + "");
		}
		if (!isNullOrEmpty(address.getStreetAddress())) {
			kbAddress.addStreet_address(address.getStreetAddress());
		}
		if (!isNullOrEmpty(address.getTownName())) {
			kbAddress.addTownName(address.getTownName());
		}
		if (address.getLatitude() > 0) {
			kbAddress.addLatitude((float) address.getLatitude());
		}
		if (address.getLongitute() > 0) {
			kbAddress.addLongitude((float) address.getLongitute());
		}
	}

	/**
	 * Saves name information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveNameInfoToKB(eu.threecixty.profile.UserProfile profile,
			UserProfile kbUserProfile, MyFactory mf) {
		eu.threecixty.profile.oldmodels.Name fromName = profile.getHasName();
		if (fromName == null) return;
		if (kbUserProfile.hasHas_name()) {
			WrappedIndividual obj = kbUserProfile.getHas_name().iterator().next();
			kbUserProfile.removeHas_name(obj);
		}
		Name name = mf.createName(PROFILE_URI + profile.getHasUID() + "Name");
		if (!isNullOrEmpty(fromName.getFamilyName())) {
			Iterator <? extends Object> iterators = name.getFamily_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeFamily_name(obj);
			}
			name.addFamily_name(fromName.getFamilyName());
			
		}
		if (!isNullOrEmpty(fromName.getGivenName())) {
			
			Iterator <? extends Object> iterators = name.getGiven_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeGiven_name(obj);
			}
			
			name.addGiven_name(fromName.getGivenName());
		}
		kbUserProfile.addHas_name(name);
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(eu.threecixty.profile.UserProfile profile,
			UserProfile kbUserProfile, MyFactory mf) {
		eu.threecixty.profile.oldmodels.Address fromAddress = profile.getHasAddress();
		if (fromAddress == null) return;
		if (kbUserProfile.hasHas_address()) {
			WrappedIndividual addrObj = kbUserProfile.getHas_address().iterator().next();
			kbUserProfile.removeHas_address(addrObj);
		}

		Address addr = mf.createAddress(PROFILE_URI + profile.getHasUID() + "Address");
		kbUserProfile.addHas_address(addr);

		if (!isNullOrEmpty(fromAddress.getCountryName())) {
			if (addr.hasCountry_name()) {
				Object objCountryName = addr.getCountry_name().iterator().next();
				addr.removeCountry_name(objCountryName);
			}
			addr.addCountry_name(fromAddress.getCountryName());
		}
		if (!isNullOrEmpty(fromAddress.getTownName())) {
			if (addr.hasTownName()) {
				String objTownName = addr.getTownName().iterator().next();
				addr.removeTownName(objTownName);
			}
			addr.addTownName(fromAddress.getTownName());
		}

		if (fromAddress.getLatitude() != 0) {
			if (addr.hasLatitude()) {
				Object objLatitude = addr.getLatitude().iterator().next();
				addr.removeLatitude(objLatitude);
			}
			addr.addLatitude((float) fromAddress.getLatitude());
		}

		if (fromAddress.getLongitute() != 0) {
			if (addr.hasLongitude()) {
				Object objLongitude = addr.getLongitude().iterator().next();
				addr.removeLongitude(objLongitude);
			}
			addr.addLongitude((float) fromAddress.getLongitute());
		}
	}

	/**
	 * Loads user entered ratings from the KB to a UserEnteredRating instance.
	 * @param from
	 * @param to
	 */
	private static void loadUserEnteredRatingFromKBToPI(MyFactory mf,
			UserEnteredRatings from, UserEnteredRating to) {
		Set <eu.threecixty.profile.oldmodels.UserEventRating> toUserEventRatings = new HashSet <eu.threecixty.profile.oldmodels.UserEventRating>();
		to.setHasUserEventRating(toUserEventRatings);
		if (from.hasHasUserEventRating()) {
			Iterator <? extends UserEventRating> fromUserEventRatings = from.getHasUserEventRating().iterator();
			for (; fromUserEventRatings.hasNext(); ) {
				UserEventRating fromUserEventRating = fromUserEventRatings.next();
				eu.threecixty.profile.oldmodels.UserEventRating toUserEventRating = new eu.threecixty.profile.oldmodels.UserEventRating();
				toUserEventRatings.add(toUserEventRating);
				loadUserEventRatingFromKBToPI(fromUserEventRating, toUserEventRating);
				toUserEventRating.setNewForKB(false);
			}
		}

		Set <eu.threecixty.profile.oldmodels.UserPlaceRating> toUserPlaceRatings = new HashSet <eu.threecixty.profile.oldmodels.UserPlaceRating>();
		to.setHasUserPlaceRating(toUserPlaceRatings);
		if (from.hasHasUserPlaceRating()) {
			Iterator <? extends UserPlaceRating> fromUserPlaceRatings = from.getHasUserPlaceRating().iterator();
			for (; fromUserPlaceRatings.hasNext(); ) {
				UserPlaceRating fromUserPlaceRating = fromUserPlaceRatings.next();
				eu.threecixty.profile.oldmodels.UserPlaceRating toUserPlaceRating = new eu.threecixty.profile.oldmodels.UserPlaceRating();
				toUserPlaceRatings.add(toUserPlaceRating);
				loadUserPlaceRatingFromKBToPI(mf, fromUserPlaceRating, toUserPlaceRating);
				toUserPlaceRating.setNewForKB(false);
			}
		}
		
		Set <eu.threecixty.profile.oldmodels.UserHotelRating> toUserHotelRatings = new HashSet <eu.threecixty.profile.oldmodels.UserHotelRating>();
		to.setHasUserHotelRating(toUserHotelRatings);
		if (from.hasHasUserHotelRating()) {
			Iterator <? extends UserHotelRating> fromUserHotelRatings = from.getHasUserHotelRating().iterator();
			for (; fromUserHotelRatings.hasNext(); ) {
				UserHotelRating fromUserHotelRating = fromUserHotelRatings.next();
				eu.threecixty.profile.oldmodels.UserHotelRating toUserHotelRating = new eu.threecixty.profile.oldmodels.UserHotelRating();
				toUserHotelRatings.add(toUserHotelRating);
				loadUserHotelRatingFromKBToPI(mf, fromUserHotelRating, toUserHotelRating);
				toUserHotelRating.setNewForKB(false);
			}
		}
	}

	/**
	 * Loads user hotel rating from the KB to a UserHotelRating instance.
	 * @param fromUserHotelRating
	 * @param toUserHotelRating
	 */
	private static void loadUserHotelRatingFromKBToPI(MyFactory mf,
			UserHotelRating fromUserHotelRating,
			eu.threecixty.profile.oldmodels.UserHotelRating toUserHotelRating) {
		if (fromUserHotelRating.hasHasNumberofTimesVisited()) {
			toUserHotelRating.setHasNumberOfTImesVisited(fromUserHotelRating.getHasNumberofTimesVisited().iterator().next());
		}
		if (fromUserHotelRating.hasHasNumberofTimesVisited()) {
			toUserHotelRating.setHasNumberOfTImesVisited(fromUserHotelRating.getHasNumberOfTimesVisited().iterator().next());
		}
		if (fromUserHotelRating.hasHasRating()) {
			Rating fromRating = fromUserHotelRating.getHasRating().iterator().next();
			eu.threecixty.profile.oldmodels.Rating toRating = new eu.threecixty.profile.oldmodels.Rating();
			toUserHotelRating.setHasRating(toRating);
			loadRatingFromKBToPI(fromRating, toRating);
		}
		if (fromUserHotelRating.hasHasHotelDetail()) {
			HotelDetail fromHotelDetail = fromUserHotelRating.getHasHotelDetail().iterator().next();
			eu.threecixty.profile.oldmodels.HotelDetail toHotelDetail = new eu.threecixty.profile.oldmodels.HotelDetail();
			toUserHotelRating.setHasHotelDetail(toHotelDetail);
			loadHotelDetailFromKBToPI(mf, fromHotelDetail, toHotelDetail);
		}
	}

	/**
	 * Loads hotel detail from the KB to a HotelDetail instance.
	 * @param fromHotelDetail
	 * @param toHotelDetail
	 */
	private static void loadHotelDetailFromKBToPI(MyFactory mf, HotelDetail fromHotelDetail,
			eu.threecixty.profile.oldmodels.HotelDetail toHotelDetail) {
		if (fromHotelDetail.hasHas_address()) {
			WrappedIndividual wi = fromHotelDetail.getHas_address().iterator().next();
			Address fromAddress = mf.getAddress(wi.getOwlIndividual().getIRI().toString());
			eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
			toHotelDetail.setHasAddress(toAddress);
			loadAddressFromKBToPI(fromAddress, toAddress);
		}
		if (fromHotelDetail.hasHasHotelChains()) {
			Set <eu.threecixty.profile.oldmodels.Address> toHotelChainAddresses = new HashSet <eu.threecixty.profile.oldmodels.Address>();
			toHotelDetail.setHasHotelChains(toHotelChainAddresses);
			Iterator <? extends Address> fromHotelChainAddresses = fromHotelDetail.getHasHotelChains().iterator();
			for (; fromHotelChainAddresses.hasNext(); ) {
				Address fromHotelChainAddress = fromHotelChainAddresses.next();
				eu.threecixty.profile.oldmodels.Address toHotelChainAddress = new eu.threecixty.profile.oldmodels.Address();
				toHotelChainAddresses.add(toHotelChainAddress);
				loadAddressFromKBToPI(fromHotelChainAddress, toHotelChainAddress);
			}
		}
		if (fromHotelDetail.hasHasHotelPriceHigh()) {
			toHotelDetail.setHasHotelPriceHigh(Double.parseDouble(
					fromHotelDetail.getHasHotelPriceHigh().iterator().next().toString()));
		}
		if (fromHotelDetail.hasHasHotelPriceLow()) {
			toHotelDetail.setHasHotelPriceLow(Double.parseDouble(
					fromHotelDetail.getHasHotelPriceLow().iterator().next().toString()));
		}
		if (fromHotelDetail.hasHasHotelRoomType()) {
			toHotelDetail.setHasHotelRoomTypes(fromHotelDetail.getHasHotelRoomType().iterator().next().toString());
		}
		if (fromHotelDetail.hasHasHotelStarCategory()) {
			toHotelDetail.setHasHotelStarCategory((Integer) fromHotelDetail.getHasHotelStarCategory().iterator().next());
		}
		if (fromHotelDetail.hasHasNatureOfPlace()) {
			String nopStr = fromHotelDetail.getHasNatureOfPlace().iterator().next().toString();
			toHotelDetail.setHasNatureOfPlace(eu.threecixty.profile.oldmodels.NatureOfPlace.valueOf(nopStr));
		}
		if (fromHotelDetail.hasHasNearByTransportMode()) {
			toHotelDetail.setHasNearbyTransportMode(fromHotelDetail.getHasNearByTransportMode().iterator().next());
		}
		if (fromHotelDetail.hasHasPlaceName()) {
			toHotelDetail.setHasPlaceName(fromHotelDetail.getHasPlaceName().iterator().next());
		}
		if (fromHotelDetail.hasHasTypeOfFood()) {
			String tofStr = fromHotelDetail.getHasTypeOfFood().iterator().next().toString();
			toHotelDetail.setHasTypeOfFood(eu.threecixty.profile.oldmodels.TypeOfFood.valueOf(tofStr));
		}
	}

	/**
	 * Loads user place rating from the KB to a UserPlaceRating instance.
	 * @param fromUserPlaceRating
	 * @param toUserPlaceRating
	 */
	private static void loadUserPlaceRatingFromKBToPI(MyFactory mf,
			UserPlaceRating fromUserPlaceRating,
			eu.threecixty.profile.oldmodels.UserPlaceRating toUserPlaceRating) {
		if (fromUserPlaceRating.hasHasNumberofTimesVisited()) {
			toUserPlaceRating.setHasNumberOfTimesVisited(fromUserPlaceRating.getHasNumberofTimesVisited().iterator().next());
		}
		if (fromUserPlaceRating.hasHasRating()) {
			Rating fromRating = fromUserPlaceRating.getHasRating().iterator().next();
			eu.threecixty.profile.oldmodels.Rating toRating = new eu.threecixty.profile.oldmodels.Rating();
			toUserPlaceRating.setHasRating(toRating);
			loadRatingFromKBToPI(fromRating, toRating);
		}
		if (fromUserPlaceRating.hasHasPlaceDetail()) {
			PlaceDetail fromPlaceDetail = fromUserPlaceRating.getHasPlaceDetail().iterator().next();
			eu.threecixty.profile.oldmodels.PlaceDetail toPlaceDetail = new eu.threecixty.profile.oldmodels.PlaceDetail();
			toUserPlaceRating.setHasPlaceDetail(toPlaceDetail);
			loadPlaceDetailFromKBToPI(mf, fromPlaceDetail, toPlaceDetail);
		}
	}

	/**
	 * Loads place detail from the KB to a PlaceDetail instance.
	 * @param fromPlaceDetail
	 * @param toPlaceDetail
	 */
	private static void loadPlaceDetailFromKBToPI(MyFactory mf, PlaceDetail fromPlaceDetail,
			eu.threecixty.profile.oldmodels.PlaceDetail toPlaceDetail) {
		if (fromPlaceDetail.hasHas_address()) {
			WrappedIndividual wi = fromPlaceDetail.getHas_address().iterator().next();
			Address fromAddress = mf.getAddress(wi.getOwlIndividual().getIRI().toString());
			eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
			toPlaceDetail.setHasAddress(toAddress);
			loadAddressFromKBToPI(fromAddress, toAddress);
		}
		if (fromPlaceDetail.hasHasPlaceName()) {
			toPlaceDetail.setHasPlaceName(fromPlaceDetail.getHasPlaceName().iterator().next());
		}
		if (fromPlaceDetail.hasHasNatureOfPlace()) {
			String nopStr = fromPlaceDetail.getHasNatureOfPlace().iterator().next().toString();
			toPlaceDetail.setHasNatureOfPlace(eu.threecixty.profile.oldmodels.NatureOfPlace.valueOf(nopStr));
		}
	}

	/**
	 * Loads user event rating from the KB to a UserEventRating instance.
	 * @param from
	 * @param to
	 */
	private static void loadUserEventRatingFromKBToPI(
			UserEventRating from, eu.threecixty.profile.oldmodels.UserEventRating to) {
		if (from.hasHasNumberofTimesVisited()) {
			int numberOfTimes = from.getHasNumberofTimesVisited().iterator().next();
			to.setHasNumberOfTimesVisited(numberOfTimes);
		}
		if (from.hasHasEventDetail()) {
			EventDetails fromEventDetail = from.getHasEventDetail().iterator().next();
			eu.threecixty.profile.oldmodels.EventDetail toEventDetail = new eu.threecixty.profile.oldmodels.EventDetail();
			to.setHasEventDetail(toEventDetail);
			loadEventDetailFromKBToPI(fromEventDetail, toEventDetail);
		}
		if (from.hasHasRating()) {
			Rating fromRating = from.getHasRating().iterator().next();
			eu.threecixty.profile.oldmodels.Rating toRating = new eu.threecixty.profile.oldmodels.Rating();
			to.setHasRating(toRating);
			loadRatingFromKBToPI(fromRating, toRating);
		}
	}

	/**
	 * Loads rating from KB to a rating instance.
	 * @param fromRating
	 * @param toRating
	 */
	private static void loadRatingFromKBToPI(Rating fromRating,
			eu.threecixty.profile.oldmodels.Rating toRating) {
		if (fromRating.hasHasUserDefinedRating()) {
			Object objRating = fromRating.getHasUserDefinedRating().iterator().next();
			toRating.setHasUseDefinedRating(Double.parseDouble(objRating.toString()));
		}
		if (fromRating.hasHasUserInteractionMode()) {
			String uimStr = fromRating.getHasUserInteractionMode().iterator().next().toString();
			toRating.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uimStr));
		}
	}

	/**
	 * Loads event detail from the KB to an EventDetail instance.
	 * @param fromEventDetail
	 * @param toEventDetail
	 */
	private static void loadEventDetailFromKBToPI(EventDetails fromEventDetail,
			EventDetail toEventDetail) {
		if (fromEventDetail.hasHasEventName()) {
			toEventDetail.setHasEventName(fromEventDetail.getHasEventName().iterator().next());
		}
		if (fromEventDetail.hasAt_place()) {
			Address fromAddress = fromEventDetail.getAt_place().iterator().next();
			eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
			toEventDetail.setHasAddress(toAddress);
			loadAddressFromKBToPI(fromAddress, toAddress);
		}
		if (fromEventDetail.hasHasNatureOfEvent()) {
			String noeStr = fromEventDetail.getHasNatureOfEvent().iterator().next().toString();
			toEventDetail.setHasNatureOfEvent(eu.threecixty.profile.oldmodels.NatureOfEvent.valueOf(noeStr));
		}
		if (fromEventDetail.hasHasTemporalDetails()) {
			TemporalDetails fromTemporalDetails = fromEventDetail.getHasTemporalDetails().iterator().next();
			eu.threecixty.profile.oldmodels.TemporalDetails toTemporalDetails = new eu.threecixty.profile.oldmodels.TemporalDetails();
			toEventDetail.setHasTemporalDetails(toTemporalDetails);
			loadTemporalDetailsFromKBToPI(fromTemporalDetails, toTemporalDetails);
		}
	}

	/**
	 * Loads temporal details from the KB to a TemporalDetails instance.
	 * @param fromTemporalDetails
	 * @param toTemporalDetails
	 */
	private static void loadTemporalDetailsFromKBToPI(
			TemporalDetails fromTemporalDetails,
			eu.threecixty.profile.oldmodels.TemporalDetails toTemporalDetails) {
		if (fromTemporalDetails.hasHasDateFrom()) {
			Object dateObj = fromTemporalDetails.getHasDateFrom().iterator().next();
			toTemporalDetails.setHasDateFrom(convert(dateObj.toString()));
		}
		if (fromTemporalDetails.hasHasDateUntil()) {
			Object dateObj = fromTemporalDetails.getHasDateUntil().iterator().next();
			toTemporalDetails.setHasDateUntil(convert(dateObj.toString()));
		}
	}

	/**
	 * Loads address from the KB to an Address instance.
	 * @param fromAddress
	 * @param toAddress
	 */
	private static void loadAddressFromKBToPI(Address fromAddress,
			eu.threecixty.profile.oldmodels.Address toAddress) {
		if (fromAddress.hasCountry_name()) {
			toAddress.setCountryName(fromAddress.getCountry_name().iterator().next().toString());
		}
		if (fromAddress.hasTownName()) {
			toAddress.setTownName(fromAddress.getTownName().iterator().next());
		}
		if (fromAddress.hasStreet_address()) {
			toAddress.setStreetAddress(fromAddress.getStreet_address().iterator().next().toString());
		}
		if (fromAddress.hasPostal_code()) {
			toAddress.setPostalCode(fromAddress.getPostal_code().iterator().next().toString());
		}
		if (fromAddress.hasPost_office_box()) {
			toAddress.setPostOfficeBox(Long.parseLong(fromAddress.getPost_office_box().iterator().next()));
		}
		if (fromAddress.hasLatitude()) {
			toAddress.setLatitude(Double.parseDouble(fromAddress.getLatitude().iterator().next().toString()));
		}
		if (fromAddress.hasLongitude()) {
			toAddress.setLongitute(Double.parseDouble(fromAddress.getLongitude().iterator().next().toString()));
		}
	}

	/**
	 * Loads likes from the KB to a preference instance.
	 * @param from
	 * @param to
	 */
	private static void loadLikesFromKBToPI(Preference from,
			eu.threecixty.profile.oldmodels.Preference to) {
		if (!from.hasHasLike()) return;
		Set <eu.threecixty.profile.oldmodels.Likes> toLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();
		to.setHasLikes(toLikes);
		Iterator <? extends Like> fromLikes = from.getHasLike().iterator();
		for ( ; fromLikes.hasNext(); ) {
			Like newLike = fromLikes.next();
			eu.threecixty.profile.oldmodels.Likes oldLikes = new eu.threecixty.profile.oldmodels.Likes();
			if (newLike.hasHasLikeName()) {
				String likeName = newLike.getHasLikeName().iterator().next();
				oldLikes.setHasLikeName(likeName);
			}
			if (newLike.hasHasLikeType()) {
				Object objLikeType = newLike.getHasLikeType().iterator().next();
				oldLikes.setHasLikeType(LikeType.valueOf(objLikeType.toString()));
			}
			toLikes.add(oldLikes);
		}
	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToUserProfile(MyFactory mf, String uid, UserProfile from,
			eu.threecixty.profile.UserProfile to) {
		eu.threecixty.profile.oldmodels.Name toName = new eu.threecixty.profile.oldmodels.Name();
		to.setHasName(toName);
		if (from.hasHas_name()) {
			Name name = mf.getName(PROFILE_URI + uid + "Name");
			if (name == null) return;
			if (name.hasFamily_name()) {
				toName.setFamilyName(name.getFamily_name().iterator().next().toString());
			}
			if (name.hasGiven_name()) {
				toName.setGivenName(name.getGiven_name().iterator().next().toString());
			}
		}
	}

	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToUserProfile(MyFactory mf, String uid, UserProfile from,
			eu.threecixty.profile.UserProfile to) {
		if (!from.hasHas_address()) return;
		Address addr = mf.getAddress(PROFILE_URI + uid + "Address");
		eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
		if (addr.hasCountry_name()) {
			Object objCountryName = addr.getCountry_name().iterator().next();
			toAddress.setCountryName(objCountryName.toString());
		}
		if (addr.hasTownName()) {
			String objTownName = addr.getTownName().iterator().next();
			toAddress.setTownName(objTownName.toString());
		}
		if (addr.hasLatitude()) {
			Object objLatitude = addr.getLatitude().iterator().next();
			toAddress.setLatitude(Double.parseDouble(objLatitude.toString()));
		}
		if (addr.hasLongitude()) {
			Object objLongitude = addr.getLongitude().iterator().next();
			toAddress.setLongitute(Double.parseDouble(objLongitude.toString()));
		}
		to.setHasAddress(toAddress);
	}
	
	/**
	 * Loads profile identities from a given user profile to a given settings instance.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadProfileIdentitiesFromUserProfile(
			UserProfile fromUserProfile, eu.threecixty.profile.UserProfile toUserProfile) {
		if (!fromUserProfile.hasHasProfileIdentities()) return;
		Set <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = toUserProfile.getHasProfileIdenties();
		if (oldProfiles == null) oldProfiles = new HashSet <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		toUserProfile.setHasProfileIdenties(oldProfiles);
		for (ProfileIdentities pi: fromUserProfile.getHasProfileIdentities()) {
			eu.threecixty.profile.oldmodels.ProfileIdentities tmpProfile = new eu.threecixty.profile.oldmodels.ProfileIdentities();
			if (pi.hasHasSource()) {
				tmpProfile.setHasSource(pi.getHasSource().iterator().next().toString());
			}
			if (pi.hasHasUserAccountID()) {
				tmpProfile.setHasUserAccountID(pi.getHasUserAccountID().iterator().next().toString());
			}
			if (pi.hasHasUserInteractionMode()) {
				tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(
						pi.getHasUserInteractionMode().iterator().next().toString()));
			}
			oldProfiles.add(tmpProfile);
		}
	}

	/**
	 * Loads knows information in the KB to a given instance of user profile.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadKnowsFromUserProfile(UserProfile fromUserProfile,
			eu.threecixty.profile.UserProfile toUserProfile) {
		if (!fromUserProfile.hasKnows()) return;
		Set <String> uids = new HashSet <String>();
		toUserProfile.setKnows(uids);
		for (UserProfile tmpUP: fromUserProfile.getKnows()) {
			if (!tmpUP.hasHasUID()) continue;
			uids.add(tmpUP.getHasUID().iterator().next().toString());
		}
	}

	/**
	 * Loads transport from the KB.
	 * @param userProfile
	 * @param toPrefs
	 */
	private static void loadTransportFromKB(UserProfile userProfile,
			eu.threecixty.profile.oldmodels.Preference toPrefs) {
		if (!userProfile.hasHasTransport()) return;
		Set <eu.threecixty.profile.oldmodels.Transport> toTransports = new HashSet <eu.threecixty.profile.oldmodels.Transport>();
		toPrefs.setHasTransport(toTransports);
		for (Transport transport: userProfile.getHasTransport()) {
			eu.threecixty.profile.oldmodels.Transport toTransport = new eu.threecixty.profile.oldmodels.Transport();
			toTransports.add(toTransport);
			if (transport.hasHasAccompany()) {
				Set <eu.threecixty.profile.oldmodels.Accompanying> toAccompanyings = new HashSet <eu.threecixty.profile.oldmodels.Accompanying>();
				toTransport.setHasAccompanyings(toAccompanyings);
				for (Accompanying accompanying: transport.getHasAccompany()) {
					eu.threecixty.profile.oldmodels.Accompanying toAccompanying = new eu.threecixty.profile.oldmodels.Accompanying();
					toAccompanyings.add(toAccompanying);
					loadAccompanyingFromKB(accompanying, toAccompanying);
				}
			}
			if (transport.hasHasRegularTrip()) {
				Set <eu.threecixty.profile.oldmodels.RegularTrip> toRegularTrips = new HashSet <eu.threecixty.profile.oldmodels.RegularTrip>();
				toTransport.setHasRegularTrip(toRegularTrips);
				for (RegularTrip regularTrip: transport.getHasRegularTrip()) {
					eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip = new eu.threecixty.profile.oldmodels.RegularTrip();
					toRegularTrips.add(toRegularTrip);
					loadRegularTripFromKB(regularTrip, toRegularTrip);
				}
			}
		}
	}

	/**
	 * Loads regular trip from the KB.
	 * @param regularTrip
	 * @param toRegularTrip
	 */
	private static void loadRegularTripFromKB(RegularTrip regularTrip,
			eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip) {
		if (regularTrip.hasHasUID()) toRegularTrip.setHasUID(Long.parseLong(regularTrip.getHasUID().iterator().next()));
		if (regularTrip.hasHasRegularTripName()) toRegularTrip.setHasRegularTripName(regularTrip.getHasRegularTripName().iterator().next());
		if (regularTrip.hasHasRegularTripDepartureTime()) toRegularTrip.setHasRegularTripDepartureTime(regularTrip.getHasRegularTripDepartureTime().iterator().next());
		if (regularTrip.hasHasRegularTripDepartureTimeSD()) toRegularTrip.setHasRegularTripDepartureTimeSD(regularTrip.getHasRegularTripDepartureTimeSD().iterator().next());
		if (regularTrip.hasHasRegularTripTravelTime()) toRegularTrip.setHasRegularTripTravelTime(regularTrip.getHasRegularTripTravelTime().iterator().next());
		if (regularTrip.hasHasRegularTripTravelTimeSD()) toRegularTrip.setHasRegularTripTravelTimeSD(regularTrip.getHasRegularTripTravelTimeSD().iterator().next());
		if (regularTrip.hasHasRegularTripFastestTravelTime()) toRegularTrip.setHasRegularTripFastestTravelTime(regularTrip.getHasRegularTripFastestTravelTime().iterator().next());
		if (regularTrip.hasHasRegularTripTotalDistance()) toRegularTrip.setHasRegularTripTotalDistance(Double.parseDouble(regularTrip.getHasRegularTripTotalDistance().iterator().toString()));
		if (regularTrip.hasHasRegularTripTotalCount()) toRegularTrip.setHasRegularTripTotalCount(Long.parseLong(regularTrip.getHasRegularTripTotalCount().iterator().next().toString()));
		if (regularTrip.hasHasModalityType()) toRegularTrip.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(regularTrip.getHasModalityType().iterator().next().toString()));
		if (regularTrip.hasHasRegularTripWeekdayPattern()) toRegularTrip.setHasRegularTripWeekdayPattern(regularTrip.getHasRegularTripWeekdayPattern().iterator().next());
		if (regularTrip.hasHasRegularTripDayHourPattern()) toRegularTrip.setHasRegularTripDayhourPattern(regularTrip.getHasRegularTripDayHourPattern().iterator().next());
		if (regularTrip.hasHasRegularTripLastChanged()) toRegularTrip.setHasRegularTripLastChanged(regularTrip.getHasRegularTripLastChanged().iterator().next());
		if (regularTrip.hasHasRegularTripTravelTimePattern()) toRegularTrip.setHasRegularTripTravelTimePattern(regularTrip.getHasRegularTripTravelTimePattern().iterator().next());
		
		if (regularTrip.hasHasPersonalPlace()) {
			List <eu.threecixty.profile.oldmodels.PersonalPlace> toPersonalPlaces = new ArrayList <eu.threecixty.profile.oldmodels.PersonalPlace>();
			for (PersonalPlace personalPlace: regularTrip.getHasPersonalPlace()) {
				eu.threecixty.profile.oldmodels.PersonalPlace toPersonalPlace = new eu.threecixty.profile.oldmodels.PersonalPlace();
				toPersonalPlaces.add(toPersonalPlace);
				loadPersonalPlaceFromKB(personalPlace, toPersonalPlace);
			}
			toRegularTrip.setHasPersonalPlaces(toPersonalPlaces.toArray(new eu.threecixty.profile.oldmodels.PersonalPlace[toPersonalPlaces.size()]));
		}
	}

	/**
	 * Loads personal place from the KB.
	 * @param personalPlace
	 * @param toPersonalPlace
	 */
	private static void loadPersonalPlaceFromKB(PersonalPlace personalPlace,
			eu.threecixty.profile.oldmodels.PersonalPlace toPersonalPlace) {
		if (personalPlace.hasHasPersonalPlaceExternalIds()) toPersonalPlace.setHasPersonalPlaceexternalIds(personalPlace.getHasPersonalPlaceExternalIds().iterator().next());
		if (personalPlace.hasPostal_code()) toPersonalPlace.setPostalcode(personalPlace.getPostal_code().iterator().next().toString());
		if (personalPlace.hasHasPersonalPlaceWeekDayPattern()) toPersonalPlace.setHasPersonalPlaceWeekdayPattern(personalPlace.getHasPersonalPlaceWeekDayPattern().iterator().next());
		if (personalPlace.hasHasPersonalPlaceStayPercentage()) toPersonalPlace.setHasPersonalPlaceStayPercentage(Double.parseDouble(personalPlace.getHasPersonalPlaceStayPercentage().iterator().next().toString()));
		if (personalPlace.hasHasPersonalPlaceType()) toPersonalPlace.setHasPersonalPlaceType(personalPlace.getHasPersonalPlaceType().iterator().next());
		if (personalPlace.hasHasUID()) toPersonalPlace.setHasUID(Long.parseLong(personalPlace.getHasUID().iterator().next()));
		if (personalPlace.hasHasPersonalPlaceDayHourPattern()) toPersonalPlace.setHasPersonalPlaceDayhourPattern(personalPlace.getHasPersonalPlaceDayHourPattern().iterator().next());
		if (personalPlace.hasHasPersonalPlaceName()) toPersonalPlace.setHasPersonalPlaceName(personalPlace.getHasPersonalPlaceName().iterator().next());
		if (personalPlace.hasLatitude()) toPersonalPlace.setLatitude(Double.parseDouble(personalPlace.getLatitude().iterator().next().toString()));
		if (personalPlace.hasLongitude()) toPersonalPlace.setLongitude(Double.valueOf(personalPlace.getLongitude().iterator().next().toString()));
		if (personalPlace.hasHasPersonalPlaceStayDuration()) toPersonalPlace.setHasPersonalPlaceStayDuration(personalPlace.getHasPersonalPlaceStayDuration().iterator().next());
		if (personalPlace.hasHasPersonalPlaceAccuracy()) toPersonalPlace.setHasPersonalPlaceAccuracy(Double.valueOf(personalPlace.getHasPersonalPlaceAccuracy().iterator().next().toString()));
	}

	/**
	 * Loads Accompanying from the KB.
	 * @param accompanying
	 * @param toAccompanying
	 */
	private static void loadAccompanyingFromKB(Accompanying accompanying,
			eu.threecixty.profile.oldmodels.Accompanying toAccompanying) {
		if (accompanying.hasHasAccompanyID()) toAccompanying.setHasAccompanyId(accompanying.getHasAccompanyID().iterator().next());
		if (accompanying.hasHasAccompanyUserID1()) toAccompanying.setHasAccompanyUserid1(Long.parseLong(accompanying.getHasAccompanyUserID1().iterator().next()));
		if (accompanying.hasHasAccompanyUserID2()) toAccompanying.setHasAccompanyUserid2(Long.parseLong(accompanying.getHasAccompanyUserID2().iterator().next()));
		if (accompanying.hasHasAccompanyScore()) toAccompanying.setHasAccompanyScore(Double.parseDouble(accompanying.getHasAccompanyScore().iterator().next().toString()));
		if (accompanying.hasHasAccompanyTime()) toAccompanying.setHasAccompanyTime(accompanying.getHasAccompanyTime().iterator().next());
		if (accompanying.hasHasAccompanyValidity()) toAccompanying.setHasAccompanyValidity(accompanying.getHasAccompanyValidity().iterator().next());
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
	 * Gets my factory.
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	private static MyFactory getMyFactory() throws OWLOntologyCreationException {
		if (myFactory == null) {
			synchronized (_sync) {
				if (myFactory == null) {
					OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
					File file = new File(RdfFileManager.getInstance().getPathToRdfFile());
					IRI iri= IRI.create(file);

					OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);

					myFactory = new MyFactory(ontology);
				}
			}
		}
		return myFactory;
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
	
	private static Date convert(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		int index = dateStr.indexOf("\"", 5);
		if (index < 0) return null;
		try {
			return sdf.parse(dateStr.substring(1, index));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Checks whether or not a given UID exists in the UserProfile.
	 * @param uid
	 * @return
	 */
	public static boolean existUID(String uid) {
		if (uid == null) return false;
		Model model = RdfFileManager.getInstance().getRdfModel();
		if (model == null) return false;
	    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?uid\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uid + "\") . \n\n";
	    qStr += "}";
	    Query query = QueryFactory.create(qStr);
	    
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		
		ResultSet rs = qe.execSelect();
		
		for ( ; rs.hasNext(); ) {
			QuerySolution qs = rs.next();
			String tmpuid = qs.getLiteral("uid").getString();
			if (tmpuid != null && !tmpuid.equals("")) {
				qe.close();
				return true;
			}
		}
		qe.close();
		return false;
	}
	
	private UserProfileStorage() {
	}
}

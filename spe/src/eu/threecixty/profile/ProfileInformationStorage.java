package eu.threecixty.profile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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

import eu.threecixty.models.Address;
import eu.threecixty.models.EventDetails;
import eu.threecixty.models.HotelDetail;
import eu.threecixty.models.Like;
import eu.threecixty.models.MyFactory;
import eu.threecixty.models.Name;
import eu.threecixty.models.PlaceDetail;
import eu.threecixty.models.Preference;
import eu.threecixty.models.Rating;
import eu.threecixty.models.TemporalDetails;
import eu.threecixty.models.UserEnteredRatings;
import eu.threecixty.models.UserEventRating;
import eu.threecixty.models.UserHotelRating;
import eu.threecixty.models.UserPlaceRating;
import eu.threecixty.models.UserProfile;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.LikeType;
import eu.threecixty.profile.oldmodels.UserEnteredRating;


/**
 * This class is used to read and write profile information into the KB.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ProfileInformationStorage {
	
	private static final String PROFILE_URI = "http://www.eu.3cixty.org/profile#";

	/**
	 * Loads profile information from the KB.
	 * @param uid
	 * @return
	 */
	public synchronized static ProfileInformation loadProfile(String uid) {
		if (uid == null || uid.equals("")) return null;
		try {
			MyFactory mf = getMyFactory();
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);

			if (userProfile == null) return null;
			
			ProfileInformation profileInfo = new ProfileInformation();
			profileInfo.setUid(uid);
			loadNameFromKBToPI(mf, uid, userProfile, profileInfo);
			loadAddressInfoFromKBToPI(mf, uid, userProfile, profileInfo);

			if (!userProfile.hasHasPreference()) {
				return profileInfo;
			}
			
			SpePreference spePrefs = new SpePreference();
			profileInfo.setPreference(spePrefs);

			Preference kbPrefs = userProfile.getHasPreference().iterator().next();
			
			loadLikesFromKBToPI(kbPrefs, spePrefs);
			
			if (kbPrefs.hasHasUserEnteredRatings()) {
				Set <eu.threecixty.profile.oldmodels.UserEnteredRating> toUserEnteredRatings = new HashSet <eu.threecixty.profile.oldmodels.UserEnteredRating>();
			    UserEnteredRatings fromUserEnteredRating = kbPrefs.getHasUserEnteredRatings().iterator().next();
			    UserEnteredRating toUserEnteredRating = new UserEnteredRating();
			    loadUserEnteredRatingFromKBToPI(mf, fromUserEnteredRating, toUserEnteredRating);
			    toUserEnteredRatings.add(toUserEnteredRating);
			    spePrefs.setHasUserEnteredRating(toUserEnteredRatings);
			}
			
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
			MyFactory mf = getMyFactory();
			UserProfile kbUserProfile = mf.getUserProfile(PROFILE_URI + profile.getUid());

			if (kbUserProfile == null) {
				kbUserProfile = mf.createUserProfile(PROFILE_URI + profile.getUid());
			}
			
			saveNameInfoToKB(profile, kbUserProfile, mf);
			
			saveAddressInfoToKB(profile, kbUserProfile, mf);
			if (profile.getPreference() != null) {
				savePreferenceToKB(profile.getUid(), profile.getPreference(), kbUserProfile, mf);
			}
			
			mf.saveOwlOntology();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * TODO: Note that this method does not remove old information about preferences.
	 *
	 * Saves preferences into the KB.
	 * @param preference
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void savePreferenceToKB(String uid, SpePreference preference,
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
				saveUserEventRatingToKB(uid, userEventRating, kbUserEnteredRatings, mf);
			}
		}
		if (userEnteredRating.getHasUserPlaceRating() != null) {
			for (eu.threecixty.profile.oldmodels.UserPlaceRating userPlaceRating:
				userEnteredRating.getHasUserPlaceRating()) {
				saveUserPlaceRatingToKB(uid, userPlaceRating, kbUserEnteredRatings, mf);
			}
		}
		if (userEnteredRating.getHasUserHotelRating() != null) {
			for (eu.threecixty.profile.oldmodels.UserHotelRating userHotelRating:
				userEnteredRating.getHasUserHotelRating()) {
				saveUserHotelRatingToKB(uid, userHotelRating, kbUserEnteredRatings, mf);
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
		UserPlaceRating kbUserPlaceRating = mf.createUserPlaceRating(PROFILE_URI + uid + "UserPlaceRating" + System.currentTimeMillis());
		if (userPlaceRating.getHasNumberOfTimesVisited() > 0) {
			kbUserPlaceRating.addHasNumberofTimesVisited(userPlaceRating.getHasNumberOfTimesVisited());
		}
		if (userPlaceRating.getHasRating() != null) {
			Rating kbRating = mf.createRating(PROFILE_URI + uid + "Rating" + System.currentTimeMillis());
			saveRatingToKB(userPlaceRating.getHasRating(), kbRating, mf);
			kbUserPlaceRating.addHasRating(kbRating);
		}
		if (userPlaceRating.getHasPlaceDetail() != null) {
			PlaceDetail kbPlaceDetail = mf.createPlaceDetail(PROFILE_URI + uid + "PlaceDetail" + System.currentTimeMillis());
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
			kbRating.addHasUserDefinedRating(rating.getHasUseDefinedRating().floatValue());
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
	private static void saveNameInfoToKB(ProfileInformation profile,
			UserProfile kbUserProfile, MyFactory mf) {
		if (kbUserProfile.hasHas_name()) {
			WrappedIndividual obj = kbUserProfile.getHas_name().iterator().next();
			kbUserProfile.removeHas_name(obj);
		}
		Name name = mf.createName(PROFILE_URI + profile.getUid() + "Name");
		if (!isNullOrEmpty(profile.getLastName())) {
			Iterator <? extends Object> iterators = name.getFamily_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeFamily_name(obj);
			}
			name.addFamily_name(profile.getLastName());
			
		}
		if (!isNullOrEmpty(profile.getFirstName())) {
			
			Iterator <? extends Object> iterators = name.getGiven_name().iterator();
			List <Object> list = new ArrayList <Object>();
			for ( ; iterators.hasNext(); ) {
				list.add(iterators.next());
			}
			for (Object obj: list) {
				name.removeGiven_name(obj);
			}
			
			name.addGiven_name(profile.getFirstName());
		}
		kbUserProfile.addHas_name(name);
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(ProfileInformation profile,
			UserProfile kbUserProfile, MyFactory mf) {
		if (kbUserProfile.hasHas_address()) {
			WrappedIndividual addrObj = kbUserProfile.getHas_address().iterator().next();
			kbUserProfile.removeHas_address(addrObj);
		}

		Address addr = mf.createAddress(PROFILE_URI + profile.getUid() + "Address");
		kbUserProfile.addHas_address(addr);

		if (!isNullOrEmpty(profile.getCountryName())) {
			if (addr.hasCountry_name()) {
				Object objCountryName = addr.getCountry_name().iterator().next();
				addr.removeCountry_name(objCountryName);
			}
			addr.addCountry_name(profile.getCountryName());
		}
		if (!isNullOrEmpty(profile.getTownName())) {
			if (addr.hasTownName()) {
				String objTownName = addr.getTownName().iterator().next();
				addr.removeTownName(objTownName);
			}
			addr.addTownName(profile.getTownName());
		}

		if (profile.getLatitude() != 0) {
			if (addr.hasLatitude()) {
				Object objLatitude = addr.getLatitude().iterator().next();
				addr.removeLatitude(objLatitude);
			}
			addr.addLatitude((float) profile.getLatitude());
		}

		if (profile.getLongitude() != 0) {
			if (addr.hasLongitude()) {
				Object objLongitude = addr.getLongitude().iterator().next();
				addr.removeLongitude(objLongitude);
			}
			addr.addLongitude((float) profile.getLongitude());
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
		to.setHasUserEventRatin(toUserEventRatings);
		if (from.hasHasUserEventRating()) {
			Iterator <? extends UserEventRating> fromUserEventRatings = from.getHasUserEventRating().iterator();
			for (; fromUserEventRatings.hasNext(); ) {
				UserEventRating fromUserEventRating = fromUserEventRatings.next();
				eu.threecixty.profile.oldmodels.UserEventRating toUserEventRating = new eu.threecixty.profile.oldmodels.UserEventRating();
				toUserEventRatings.add(toUserEventRating);
				loadUserEventRatingFromKBToPI(fromUserEventRating, toUserEventRating);
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
			SpePreference to) {
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
	private static void loadNameFromKBToPI(MyFactory mf, String uid, UserProfile from,
			ProfileInformation to) {
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
			ProfileInformation to) {
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
			to.setLatitude(Double.parseDouble(objLatitude.toString()));
		}
		if (addr.hasLongitude()) {
			Object objLongitude = addr.getLongitude().iterator().next();
			to.setLongitude(Double.parseDouble(objLongitude.toString()));
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
	 * Gets my factory.
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public static MyFactory getMyFactory() throws OWLOntologyCreationException {
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

	public static boolean existUID(String uid) {
		if (uid == null) return false;
		try {
			MyFactory mf = getMyFactory();
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);

			if (userProfile == null) return false;
			return true;
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Prohibits instantiations.
	 */
	private ProfileInformationStorage() {
	}
}

package eu.threecixty.profile;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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

	// TODO
	public static ProfileInformation loadProfile(String uid) {
		if (uid == null || uid.equals("")) return null;
		try {
			MyFactory mf = getMyFactory();
			UserProfile userProfile = mf.getUserProfile(PROFILE_URI + uid);

			if (userProfile == null) return null;
			
			ProfileInformation profileInfo = new ProfileInformation();
			
			profileInfo.setUid(uid);
			loadNameFromKBToPI(userProfile, profileInfo);
			loadAddressInfoFromKBToPI(userProfile, profileInfo);

			if (!userProfile.hasHasPreference()) return profileInfo;
			
			SpePreference spePrefs = new SpePreference();
			profileInfo.setPreference(spePrefs);

			Preference kbPrefs = userProfile.getHasPreference().iterator().next();
			
			loadLikesFromKBToPI(kbPrefs, spePrefs);
			
			if (kbPrefs.hasHasUserEnteredRatings()) {
				Set <eu.threecixty.profile.oldmodels.UserEnteredRating> toUserEnteredRatings = new HashSet <eu.threecixty.profile.oldmodels.UserEnteredRating>();
			    UserEnteredRatings fromUserEnteredRating = kbPrefs.getHasUserEnteredRatings().iterator().next();
			    UserEnteredRating toUserEnteredRating = new UserEnteredRating();
			    loadUserEnteredRatingFromKBToPI(fromUserEnteredRating, toUserEnteredRating);
			    toUserEnteredRatings.add(toUserEnteredRating);
			    spePrefs.setHasUserEnteredRating(toUserEnteredRatings);
			}
			
			
			return profileInfo;
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;
	}

	// TODO
	public static boolean saveProfile(ProfileInformation profile) {
		return false;
	}

	/**
	 * Loads user entered ratings from the KB to a UserEnteredRating instance.
	 * @param from
	 * @param to
	 */
	private static void loadUserEnteredRatingFromKBToPI(
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
				loadUserPlaceRatingFromKBToPI(fromUserPlaceRating, toUserPlaceRating);
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
				loadUserHotelRatingFromKBToPI(fromUserHotelRating, toUserHotelRating);
			}
		}
	}

	/**
	 * Loads user hotel rating from the KB to a UserHotelRating instance.
	 * @param fromUserHotelRating
	 * @param toUserHotelRating
	 */
	private static void loadUserHotelRatingFromKBToPI(
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
			loadHotelDetailFromKBToPI(fromHotelDetail, toHotelDetail);
		}
	}

	/**
	 * Loads hotel detail from the KB to a HotelDetail instance.
	 * @param fromHotelDetail
	 * @param toHotelDetail
	 */
	private static void loadHotelDetailFromKBToPI(HotelDetail fromHotelDetail,
			eu.threecixty.profile.oldmodels.HotelDetail toHotelDetail) {
		// TODO Auto-generated method stub
		//if (fromHotelDetail.h)
	}

	/**
	 * Loads user place rating from the KB to a UserPlaceRating instance.
	 * @param fromUserPlaceRating
	 * @param toUserPlaceRating
	 */
	private static void loadUserPlaceRatingFromKBToPI(
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
			loadPlaceDetailFromKBToPI(fromPlaceDetail, toPlaceDetail);
		}
	}

	/**
	 * Loads place detail from the KB to a PlaceDetail instance.
	 * @param fromPlaceDetail
	 * @param toPlaceDetail
	 */
	private static void loadPlaceDetailFromKBToPI(PlaceDetail fromPlaceDetail,
			eu.threecixty.profile.oldmodels.PlaceDetail toPlaceDetail) {
		if (fromPlaceDetail.hasHas_address()) {
			Address fromAddress = (Address) fromPlaceDetail.getHas_address().iterator().next();
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
			toRating.setHasUseDefinedRating((Double) objRating);
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
			Date fromDate = (Date) fromTemporalDetails.getHasDateFrom().iterator().next();
			toTemporalDetails.setHasDateFrom(fromDate);
		}
		if (fromTemporalDetails.hasHasDateUntil()) {
			Date toDate = (Date) fromTemporalDetails.getHasDateUntil().iterator().next();
			toTemporalDetails.setHasDateUntil(toDate);
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
			toAddress.setLatitude((Double) fromAddress.getLatitude().iterator().next());
		}
		if (fromAddress.hasLongitude()) {
			toAddress.setLongitute((Double) fromAddress.getLongitude().iterator().next());
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
	private static void loadNameFromKBToPI(UserProfile from,
			ProfileInformation to) {
		if (from.hasHas_name()) {
			Name name = (Name) from.getHas_name().iterator().next();
			if (name.hasGiven_name()) {
			    to.setFirstName(name.getGiven_name().iterator().next().toString());
			}
			if (name.hasFamily_name()) {
				to.setLastName(name.getFamily_name().iterator().next().toString());
			}
		}
	}

	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToPI(UserProfile from,
			ProfileInformation to) {
		if (!from.hasHas_address()) return;
		Address addr = (Address) from.getHas_address().iterator().next();
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
			to.setLatitude((Double) objLatitude);
		}
		if (addr.hasLongitude()) {
			Object objLongitude = addr.getLongitude().iterator().next();
			to.setLongitude((Double) objLongitude);
		}
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

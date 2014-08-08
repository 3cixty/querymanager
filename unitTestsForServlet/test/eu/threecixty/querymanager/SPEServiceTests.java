package eu.threecixty.querymanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import eu.threecixty.profile.ProfileInformation;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.HotelDetail;
import eu.threecixty.profile.oldmodels.NatureOfEvent;
import eu.threecixty.profile.oldmodels.NatureOfPlace;
import eu.threecixty.profile.oldmodels.PlaceDetail;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.Rating;
import eu.threecixty.profile.oldmodels.TemporalDetails;
import eu.threecixty.profile.oldmodels.TypeOfFood;
import eu.threecixty.profile.oldmodels.UserEnteredRating;
import eu.threecixty.profile.oldmodels.UserEventRating;
import eu.threecixty.profile.oldmodels.UserHotelRating;
import eu.threecixty.profile.oldmodels.UserInteractionMode;
import eu.threecixty.profile.oldmodels.UserPlaceRating;

public class SPEServiceTests extends HTTPCall {
	
	private static final String SPE_SERVICE = "http://localhost:8080/querymanagerServlet-1.0/services/spe/";
//	private static final String SPE_SERVICE = "http://localhost:8080/querymanagerServlet/services/spe/";
	
	// TODO: change whenever the access token is expired
	private String accessToken = "ya29.LgDtRCXgzvQqbCEAAAAcwkb-zwEke7eHzLOWHIYbJ_0I8z7am-lGPbnFcPAmPBohmoOUbI42b5ZSOa9-tIE";
	
	@Test
	public void testSaveAndLoadProfile() {
		ProfileInformation profileInformation = new ProfileInformation();
		String firstName = "First Name Test";
		String lastName = "Last Name Test";
		String countryName = "France";
		String townName = "Paris";
		double latitude = 13.983;
		double longitude = 0.765;
		
		profileInformation.setFirstName(firstName);
		profileInformation.setLastName(lastName);
		profileInformation.setCountryName(countryName);
		profileInformation.setTownName(townName);
		profileInformation.setLatitude(latitude);
		profileInformation.setLongitude(longitude);
		
		Preference pref = new Preference();
		profileInformation.setPreference(pref);
		
		Set <UserEnteredRating> userEnteredRatings = new HashSet <UserEnteredRating>();
		pref.setHasUserEnteredRating(userEnteredRatings);
		UserEnteredRating userEnteredRating = new UserEnteredRating();
		userEnteredRatings.add(userEnteredRating);
		
		Set <UserEventRating> userEventRatings = new HashSet<UserEventRating>();
		userEnteredRating.setHasUserEventRatin(userEventRatings);
		UserEventRating userEventRating = createUserEventRating();
		userEventRatings.add(userEventRating);
		

		Set <UserPlaceRating> userPlaceRatings = new HashSet<UserPlaceRating>();
		userEnteredRating.setHasUserPlaceRating(userPlaceRatings);
		UserPlaceRating userPlaceRating = createUserPlaceRating();
		userPlaceRatings.add(userPlaceRating);
		
		Set <UserHotelRating> userHotelRatings = new HashSet<UserHotelRating>();
		userEnteredRating.setHasUserHotelRating(userHotelRatings);
		UserHotelRating userHotelRating = createUserHotelRating();
		userHotelRatings.add(userHotelRating);
		
		Gson gson = new Gson();
		
		StringBuffer params = new StringBuffer("accessToken=");
		params.append(accessToken);
		params.append("&profile=");
		params.append(gson.toJson(profileInformation));
		
		System.out.println(gson.toJson(profileInformation));
		
		try {
			sendPost(SPE_SERVICE + "saveProfile", params.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		StringBuffer response = new StringBuffer();
		
		try {
			sendGET(SPE_SERVICE + "getProfile", accessToken, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(response.length() > 0);
		
		ProfileInformation loadedProfileInformation = gson.fromJson(response.toString(), ProfileInformation.class);
		Assert.assertNotNull(loadedProfileInformation);
		
		Assert.assertTrue(loadedProfileInformation.getUid().equals("103918130978226832690"));
		Assert.assertTrue(firstName.equals(loadedProfileInformation.getFirstName()));
		Assert.assertTrue(lastName.equals(loadedProfileInformation.getLastName()));
		Assert.assertTrue(countryName.equals(loadedProfileInformation.getCountryName()));
		Assert.assertTrue(townName.equals(loadedProfileInformation.getTownName()));
		Assert.assertTrue(latitude == profileInformation.getLatitude());
		Assert.assertTrue(longitude == loadedProfileInformation.getLongitude());
		
		Preference loadedPref = loadedProfileInformation.getPreference();
		
		Assert.assertTrue(loadedPref != null);
		Assert.assertTrue(loadedPref.getHasUserEnteredRating() != null);
		Assert.assertTrue(loadedPref.getHasUserEnteredRating().size() == 1);

		UserEnteredRating loadedUserEnteredRating = loadedPref.getHasUserEnteredRating().iterator().next();
		Assert.assertTrue(loadedUserEnteredRating.getHasUserEventRating() != null);
		Assert.assertTrue(loadedUserEnteredRating.getHasUserEventRating().size() == 1);
		
		UserEventRating loadedUserEventRating = loadedUserEnteredRating.getHasUserEventRating().iterator().next();
		
		checkUserEventRating(userEventRating, loadedUserEventRating);

		Assert.assertTrue(loadedUserEnteredRating.getHasUserPlaceRating() != null);
		Assert.assertTrue(loadedUserEnteredRating.getHasUserPlaceRating().size() == 1);
		UserPlaceRating loadedUserPlaceRating = loadedUserEnteredRating.getHasUserPlaceRating().iterator().next();
		
		checkUserPlaceRating(userPlaceRating, loadedUserPlaceRating);
		
		Assert.assertTrue(loadedUserEnteredRating.getHasUserHotelRating() != null);
		Assert.assertTrue(loadedUserEnteredRating.getHasUserHotelRating().size() == 1);
		UserHotelRating loadedUserHotelRating = loadedUserEnteredRating.getHasUserHotelRating().iterator().next();

		checkUserHotelRating(userHotelRating, loadedUserHotelRating);
	}


	@Test
	public void testGetUID() {
		try {
			StringBuffer response = new StringBuffer();
			sendPost(SPE_SERVICE + "getUID", "accessToken=" + accessToken, response);
			Assert.assertTrue(response.toString().equals("103918130978226832690")); // Kinh's UID
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testValidate() {
		try {
			StringBuffer response = new StringBuffer();
			sendPost(SPE_SERVICE + "validate", "accessToken=" + accessToken, response);
			Assert.assertTrue(response.toString().equalsIgnoreCase("true"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	private void checkUserHotelRating(UserHotelRating userHotelRating,
			UserHotelRating loadedUserHotelRating) {
		Assert.assertTrue(userHotelRating.getHasNumberOfTImesVisited() == loadedUserHotelRating.getHasNumberOfTImesVisited());
		
		checkRating(userHotelRating.getHasRating(), loadedUserHotelRating.getHasRating());
		
		HotelDetail hotelDetail = userHotelRating.getHasHotelDetail();
		HotelDetail loadedHotelDetail = loadedUserHotelRating.getHasHotelDetail();
		
		Assert.assertTrue(hotelDetail.getHasPlaceName().equals(loadedHotelDetail.getHasPlaceName()));
		Assert.assertTrue(hotelDetail.getHasHotelPriceHigh().equals(loadedHotelDetail.getHasHotelPriceHigh()));
		Assert.assertTrue(hotelDetail.getHasHotelPriceLow().equals(loadedHotelDetail.getHasHotelPriceLow()));
		Assert.assertTrue(hotelDetail.getHasHotelRoomTypes().equals(loadedHotelDetail.getHasHotelRoomTypes()));
		Assert.assertTrue(hotelDetail.getHasNearbyTransportMode().equals(loadedHotelDetail.getHasNearbyTransportMode()));
		Assert.assertTrue(hotelDetail.getHasHotelStarCategory() == loadedHotelDetail.getHasHotelStarCategory());
		Assert.assertTrue(hotelDetail.getHasNatureOfPlace() == loadedHotelDetail.getHasNatureOfPlace());
		Assert.assertTrue(hotelDetail.getHasTypeOfFood() == loadedHotelDetail.getHasTypeOfFood());
		
		Assert.assertTrue(hotelDetail.getHasHotelChains().size() == loadedHotelDetail.getHasHotelChains().size());
		
		checkAddress(hotelDetail.getHasAddress(), loadedHotelDetail.getHasAddress());
		
		checkAddress(hotelDetail.getHasHotelChains().iterator().next(), loadedHotelDetail.getHasHotelChains().iterator().next());
		
	}


	private UserHotelRating createUserHotelRating() {
		UserHotelRating userHotelRating = new UserHotelRating();
		userHotelRating.setHasNumberOfTImesVisited(8);

		Rating rating = new Rating();
		userHotelRating.setHasRating(rating);
		rating.setHasUseDefinedRating(4.0);
		rating.setHasUserInteractionMode(UserInteractionMode.Stayed);
		
		HotelDetail hotelDetail = new HotelDetail();
		userHotelRating.setHasHotelDetail(hotelDetail);
		
		Address hotelAddr = new Address();
		hotelAddr.setCountryName( "Italy");
		hotelAddr.setTownName("Milano");
		hotelAddr.setPostalCode("012387");
		hotelAddr.setStreetAddress("Via Trapattoni");
		hotelAddr.setPostOfficeBox(70L);
		hotelDetail.setHasAddress(hotelAddr);
		
		hotelDetail.setHasHotelPriceHigh(150.0);
		hotelDetail.setHasHotelPriceLow(45.0);
		hotelDetail.setHasHotelStarCategory(4);
		hotelDetail.setHasHotelRoomTypes("Luxury");
		hotelDetail.setHasNatureOfPlace(NatureOfPlace.Hotel);
		hotelDetail.setHasPlaceName("Hilton Hotel in Milano");
		hotelDetail.setHasNearbyTransportMode("To get to our hotel, please take the bus N5, N9");
		hotelDetail.setHasTypeOfFood(TypeOfFood.Continental);
		
		Set <Address> chainAddresses = new HashSet<Address>();
		hotelDetail.setHasHotelChains(chainAddresses);
		
		Address chainAddr = new Address();
		chainAddr.setCountryName( "Italy");
		chainAddr.setTownName("Milano");
		chainAddr.setPostalCode("9823");
		chainAddr.setStreetAddress("Via Ruste");
		chainAddr.setPostOfficeBox(13L);
		chainAddr.setLatitude(9.45);
		chainAddr.setLongitute(0.18675);
		
		chainAddresses.add(chainAddr);
		
		return userHotelRating;
	}
	
	private UserPlaceRating createUserPlaceRating() {
		UserPlaceRating userPlaceRating = new UserPlaceRating();
		userPlaceRating.setHasNumberOfTimesVisited(5);

		Rating rating = new Rating();
		userPlaceRating.setHasRating(rating);
		rating.setHasUseDefinedRating(5.0);
		rating.setHasUserInteractionMode(UserInteractionMode.Went);
		
		PlaceDetail placeDetail = new PlaceDetail();
		userPlaceRating.setHasPlaceDetail(placeDetail);
		
		Address eventAddr = new Address();
		eventAddr.setCountryName( "Italy");
		eventAddr.setTownName("Milano");
		eventAddr.setPostalCode("0123654");
		eventAddr.setStreetAddress("Via Maldini");
		eventAddr.setPostOfficeBox(25L);
		placeDetail.setHasAddress(eventAddr);

		placeDetail.setHasPlaceName("Cathedral of Milano");
		placeDetail.setHasNatureOfPlace(NatureOfPlace.Church);
		
		return userPlaceRating;
	}
	
	private UserEventRating createUserEventRating() {
		UserEventRating userEventRating = new UserEventRating();
		userEventRating.setHasNumberOfTimesVisited(3);
		
		Rating rating = new Rating();
		userEventRating.setHasRating(rating);
		rating.setHasUseDefinedRating(4.0);
		rating.setHasUserInteractionMode(UserInteractionMode.Visited);
		
		EventDetail eventDetail = new EventDetail();
		userEventRating.setHasEventDetail(eventDetail);
		
		String eventName = "Festival Music in Paris";
		eventDetail.setHasEventName(eventName);
		eventDetail.setHasNatureOfEvent(NatureOfEvent.Music);
		
		Address eventAddr = new Address();
		eventAddr.setCountryName( "Italy");
		eventAddr.setTownName("Milano");
		eventAddr.setPostalCode("123654");
		eventAddr.setStreetAddress("Via Rossi");
		eventAddr.setPostOfficeBox(14L);
		eventDetail.setHasAddress(eventAddr);
		
		TemporalDetails temporalDetails = new TemporalDetails();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String fromDate = "24/06/2014 10:30";
		String toDate = "24/06/2014 12:30";
		try {
			temporalDetails.setHasDateFrom(sdf.parse(fromDate));
			temporalDetails.setHasDateUntil(sdf.parse(toDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		eventDetail.setHasTemporalDetails(temporalDetails);
		return userEventRating;
	}
	
	private void checkUserPlaceRating(UserPlaceRating userPlaceRating,
			UserPlaceRating loadedUserPlaceRating) {
		Assert.assertTrue(userPlaceRating.getHasNumberOfTimesVisited() == loadedUserPlaceRating.getHasNumberOfTimesVisited());
		
		checkRating(userPlaceRating.getHasRating(), loadedUserPlaceRating.getHasRating());
		
		PlaceDetail placeDetail = userPlaceRating.getHasPlaceDetail();
		PlaceDetail loadedPlaceDetail = loadedUserPlaceRating.getHasPlaceDetail();
		
		Assert.assertTrue(placeDetail.getHasPlaceName().equals(loadedPlaceDetail.getHasPlaceName()));
		
		checkAddress(placeDetail.getHasAddress(), loadedPlaceDetail.getHasAddress());
		
		Assert.assertTrue(placeDetail.getHasNatureOfPlace() == loadedPlaceDetail.getHasNatureOfPlace());
	}
	
	private void checkUserEventRating(UserEventRating userEventRating,
			UserEventRating loadedUserEventRating) {
		Assert.assertTrue(loadedUserEventRating.getHasNumberOfTimesVisited() == userEventRating.getHasNumberOfTimesVisited());
		
		Rating loadedRating = loadedUserEventRating.getHasRating();
		Assert.assertTrue(loadedRating != null);
		
		checkRating(userEventRating.getHasRating(), loadedRating);
		
		Assert.assertTrue(loadedUserEventRating.getHasEventDetail() != null);
		
		EventDetail eventDetail = userEventRating.getHasEventDetail();
		EventDetail loadedEventDetail = loadedUserEventRating.getHasEventDetail();
		
		Assert.assertTrue(loadedEventDetail.getHasEventName().equals(eventDetail.getHasEventName()));
		Assert.assertTrue(loadedEventDetail.getHasNatureOfEvent() == eventDetail.getHasNatureOfEvent());
		
		checkAddress(eventDetail.getHasAddress(), loadedEventDetail.getHasAddress());
		checkTemporalDetails(eventDetail.getHasTemporalDetails(), loadedEventDetail.getHasTemporalDetails());
	}
	
	private void checkTemporalDetails(TemporalDetails temporalDetails,
			TemporalDetails loadedTemporalDetails) {
		if (temporalDetails == null && loadedTemporalDetails == null) return;
		else if (temporalDetails != null && loadedTemporalDetails != null) {
			Assert.assertTrue(temporalDetails.getHasDateFrom().equals(loadedTemporalDetails.getHasDateFrom()));
			Assert.assertTrue(temporalDetails.getHasDateUntil().equals(loadedTemporalDetails.getHasDateUntil()));
		} else {
			Assert.fail();
		}
	}

	private void checkAddress(Address address, Address loadedAddress) {
		if (address == null && loadedAddress == null) return;
		else if (address != null && loadedAddress != null) {
			Assert.assertTrue(address.getCountryName().equals(loadedAddress.getCountryName()));
			Assert.assertTrue(address.getTownName().equals(loadedAddress.getTownName()));
			Assert.assertTrue(address.getStreetAddress().equals(loadedAddress.getStreetAddress()));
			Assert.assertTrue(address.getPostalCode().equals(loadedAddress.getPostalCode()));
			Assert.assertTrue(address.getPostOfficeBox().equals(loadedAddress.getPostOfficeBox()));
			Assert.assertTrue(address.getLatitude() == loadedAddress.getLatitude());
			Assert.assertTrue(address.getLongitute() == loadedAddress.getLongitute());
		} else {
			Assert.fail();
		}
	}
	
	private void checkRating(Rating rating, Rating loadedRating) {
		Assert.assertTrue(rating.getHasUseDefinedRating().equals(loadedRating.getHasUseDefinedRating()));
		Assert.assertTrue(rating.getHasUserInteractionMode() == loadedRating.getHasUserInteractionMode());
		// current version of models no datetime voted
	}
}

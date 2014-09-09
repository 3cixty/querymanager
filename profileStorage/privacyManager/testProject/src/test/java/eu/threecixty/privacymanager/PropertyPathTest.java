package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.authen.simple.SimpleSessionManager;
import org.theresis.humanization.conf.ProfileStorageConf;
import org.theresis.humanization.datastorage.ProfileException;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;

@RunWith(JUnit4.class)
public class PropertyPathTest {

	private ProfileManager profileManager;
	//private ProfileStorageConf config;
	private Session session = null;
	static public String 	propertyFilePath = null;

	@Before
	public void setUp() throws Exception {
	
		SimpleProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();
		profileManager = profileFactory.getProfileManager( propertyFilePath );
		Service service = profileFactory.getService("test", "pwdTest");
		session = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
		//config = ProfileStorageConf.getInstance();
	}

	@Test
	public void getPropertyPath() {

		try {

			String userID = "110248277616794929135";
			
			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference/:hasPlaceDetailPreference/:hasNatureOfPlace";
			String knowsPath = "foaf:knows/:hasProfileIdentities*/:hasUserAccountID";
			String ratingPath = ":hasPreference/:hasUserPlaceRating*";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasGenderPath);
			propertyPaths.add(hasPlacePath);
			propertyPaths.add(knowsPath);
			propertyPaths.add(ratingPath);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID, propertyPaths);

			System.out.println(" results : ");

			for (ValuedProperty val : propertyValues) {

				System.out.println(" - " + val);
				if (val.getPropertyPath().compareTo(hasGenderPath) == 0) {
					assertTrue(val.getNbValues() == 1);
					assertEquals("Male", val.getValue(0));
				} else if (val.getPropertyPath().compareTo(hasPlacePath) == 0) {
					assertTrue(val.getNbValues() == 1);
					assertEquals("Country", val.getValue(0));
				} else if (val.getPropertyPath().compareTo(knowsPath) == 0) {
					assertTrue(val.getNbValues() == 2);
					assertEquals("junk2", val.getValue(0));
					assertEquals("100900047095598983805", val.getValue(1));
				} else if (val.getPropertyPath().compareTo(ratingPath) == 0) {
					assertTrue(val.getNbValues() == 1);
					assertEquals(
							"http://www.eu.3cixty.org/profile#110248277616794929135Preference",
							val.getValue(0));
				} else {
					fail("Not expected property " + val);
				}
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getCountryNamePropertyPath() {

		try {
			String userID = "100900047095598983805";
			
			String hasCountryName = "vcard:hasAddress/vcard:country-name";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasCountryName);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID, propertyPaths);

			System.out.println(" results : ");

			for (ValuedProperty val : propertyValues) {

				System.out.println(" - " + val);
				if (val.getPropertyPath().compareTo(hasCountryName) == 0) {
					assertTrue(val.getNbValues() == 1);
					assertEquals("Italy", val.getValue(0));
				} else {
					fail("Not expected property " + val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void deleteValuedPropertyPath() {

		try {
			String userID = "110248277616794929135";

			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference*";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Male");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasPlacePath);
			valuedPlacePref.addValue("http://www.eu.3cixty.org/profile#110248277616794929135ItalyPlacePreference");
			pairsPropertyPathValues.add(valuedPlacePref);

			profileManager.deleteProfileProperties( session, userID, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID);
			assertNotNull(jsonProfile);
			assertFalse(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertFalse(jsonProfile.contains("110248277616794929135ItalyPlacePreference"));

			System.out.println("Updated profile = " + jsonProfile);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void deleteNotSetPropertyPath() {

		try {
			String userID = "110248277616794929135";

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);
			profileManager.deleteProfileProperties( session, userID, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile( session, userID );
			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertTrue(jsonProfile.contains("Male"));

			System.out.println("Updated profile = " + jsonProfile);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void mergeValuedPropertyPath() {

		try {
			String userID = "110248277616794929135";

			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference/:hasPlaceDetailPreference/:hasNatureOfPlace";
			String hasProfileIdentities = ":hasProfileIdentities";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasPlacePath);
			valuedPlacePref.addValue("Restaurant");
			pairsPropertyPathValues.add(valuedPlacePref);

			ValuedProperty valuedProfilePref = new ValuedProperty(
					hasProfileIdentities);
			valuedProfilePref.addValue("http://www.eu.3cixty.org/profile#110248277616794929135facebookProf");
			pairsPropertyPathValues.add(valuedProfilePref);

			profileManager.mergeProfileProperties( session, userID, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID);
			System.out.println("Merged profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasPlacePath);
			Collection<ValuedProperty> placeRes = profileManager.getProfileProperties(session, userID, propertyPathValues);
			assertTrue(placeRes.size() == 1);
			assertTrue(placeRes.iterator().next().getValues().contains("Restaurant"));

			assertTrue(jsonProfile.contains("110248277616794929135facebookProf"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void mergeStringValuePropertyPath() {

		try {
			String updatedUser = "110248277616794929135";

			String hasGenderPath = ":hasEmail";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			String email = "110248277616794929135@free.fr";
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue(email);
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, updatedUser, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, updatedUser);
			System.out.println("Merged profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasEmail"));
			assertTrue(jsonProfile.contains(email));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}



	@Test
	public void mergeInvalidEnumValuePropertyPath() {

		try {

			String userID = "110248277616794929135";

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Toto");
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, userID, pairsPropertyPathValues);

		} catch (Exception e) {
			// Normal exception
			assertTrue(e instanceof ProfileException);
		}
	}

	@Test
	public void replaceValuedPropertyPath() {

		try {

			String updatedUser = "110248277616794929135";

			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference/:hasPlaceDetailPreference/:hasNatureOfPlace";
			String hasProfileIdentities = ":hasProfileIdentities";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasPlacePath);
			valuedPlacePref.addValue("Restaurant");
			pairsPropertyPathValues.add(valuedPlacePref);

			ValuedProperty valuedProfilePref = new ValuedProperty(
					hasProfileIdentities);
			valuedProfilePref
					.addValue("http://www.eu.3cixty.org/profile#110248277616794929135facebookProf");
			pairsPropertyPathValues.add(valuedProfilePref);

			profileManager.replaceProfileProperties(session, updatedUser,
					pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, updatedUser);
			System.out.println("Replace profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasPlacePath);
			Collection<ValuedProperty> placeRes = profileManager
					.getProfileProperties(session, updatedUser, propertyPathValues);
			assertTrue(placeRes.size() == 1);
			assertTrue(placeRes.iterator().next().getValues()
					.contains("Restaurant"));

			propertyPathValues.clear();
			propertyPathValues.add(hasProfileIdentities);
			Collection<ValuedProperty> profRes = profileManager
					.getProfileProperties(session, updatedUser, propertyPathValues);
			assertTrue(profRes.size() == 1);
			assertTrue(jsonProfile
					.contains("110248277616794929135facebookProf"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void getFriendsRatingPropertyPath() {

		try {

			String userID = "110248277616794929135";

			String hasFriendRating = "foaf:knows/:hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasRating/:hasUserDefinedRating";

			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasFriendRating);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID, propertyPaths);

			System.out.println(" results : ");

			for (ValuedProperty val : propertyValues) {

				System.out.println(" - " + val);
				if (val.getPropertyPath().compareTo(hasFriendRating) == 0) {
					assertTrue(val.getNbValues() == 1);
					System.out.println(val.getValue(0));
					assertTrue(val.getValue(0).startsWith("5.0"));
				} else {
					fail("Not expected property " + val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

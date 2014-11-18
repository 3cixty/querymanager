package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.conf.ProfileStorageConf;
import org.theresis.humanization.datastorage.ProfileException;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyContractStorageFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;
import org.theresis.humanization.privacy.generated.UserPrivacyContract;
import org.theresis.humanization.profilestore.ThreeCixtyFactory;

@RunWith(JUnit4.class)
public class PropertyPathTest {

	private ProfileManager profileManager;
	static public String 		propertyFilePath = "src/test/resources/TestProfileStorage.properties";
	static public String 		privacyPropertyFilePath = "src/test/resources/TestPrivacyAuthority.properties";
	static private String		appName = "Test";
	static private String		appversion = "1.0";
	static private String		userID1  = "110248277616794929135";
	static private String		userID2  = "100900047095598983805";

	@Before
	public void setUp() throws Exception {
	
		// the privacy DB
		ProfileStorageConf.setPropertyFile(propertyFilePath);
		PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
		PrivacyDBInitialize.resetAndInit("toto", "toto", "toto", "toto");
		FileInputStream is = new FileInputStream( "src/test/resources/UPC_TestApp.xml" );
		UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
		PrivacyContractStorageFactory.getInstance().store(	userID1, 
															appName, 
															upc);
		PrivacyContractStorageFactory.getInstance().store(	userID2, 
															appName, 
															upc);
		
		ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
		profileManager = profileFactory.getProfileManager( propertyFilePath );		
	}

	@Test
	public void getPropertyPath() {

		try {

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion);
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1,  null) );
			
			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference/:hasPlaceDetailPreference/:hasNatureOfPlace";
			String knowsPath = "foaf:knows/:hasProfileIdentities*/:hasUserAccountID";
			String ratingPath = ":hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasRating";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasGenderPath);
			propertyPaths.add(hasPlacePath);
			propertyPaths.add(knowsPath);
			propertyPaths.add(ratingPath);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID1, propertyPaths);

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
					assertTrue( val.getNbValues() == 1 );
					assertTrue( val.getValue(0).contains("hasUserInteractionMode") );									
					assertTrue( val.getValue(0).contains("Visited") );
					assertTrue( val.getValue(0).contains("hasUserDefinedRating") );
					assertTrue( val.getValue(0).contains("4.0") );
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

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID2,  null) );
			
			String hasCountryName = "vcard:hasAddress/vcard:country-name";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasCountryName);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID2, propertyPaths);

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
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference*";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Male");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasPlacePath);
			valuedPlacePref.addValue("http://www.eu.3cixty.org/profile#110248277616794929135ItalyPlacePreference");
			pairsPropertyPathValues.add(valuedPlacePref);

			profileManager.deleteProfileProperties( session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1);
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
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1,  null) );

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);
			profileManager.deleteProfileProperties( session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile( session, userID1 );
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
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );

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

			profileManager.mergeProfileProperties( session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1);
			System.out.println("Merged profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasPlacePath);
			Collection<ValuedProperty> placeRes = profileManager.getProfileProperties(session, userID1, propertyPathValues);
			assertTrue(placeRes.size() == 1);
			assertTrue(placeRes.iterator().next().getValues().contains("Restaurant"));

			assertTrue(jsonProfile.contains("profile2:facebookProf"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	@Test
	public void mergeStringValuePropertyPath() {

		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );

			String hasGenderPath = ":hasEmail";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			String email = "110248277616794929135@free.fr";
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue(email);
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1);
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

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Toto");
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);

		} catch (Exception e) {
			// Normal exception
			assertTrue(e instanceof ProfileException);
		}
	}

	
	@Test
	public void replaceValuedPropertyPath() {

		try {

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1,  null) );

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

			profileManager.replaceProfileProperties(session, userID1,
					pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1);
			System.out.println("Replace profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasPlacePath);
			Collection<ValuedProperty> placeRes = profileManager
					.getProfileProperties(session, userID1, propertyPathValues);
			assertTrue(placeRes.size() == 1);
			assertTrue(placeRes.iterator().next().getValues()
					.contains("Restaurant"));

			propertyPathValues.clear();
			propertyPathValues.add(hasProfileIdentities);
			Collection<ValuedProperty> profRes = profileManager
					.getProfileProperties(session, userID1, propertyPathValues);
			assertTrue(profRes.size() == 1);
			assertTrue(jsonProfile
					.contains("profile2:facebookProf"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.conf.ProfileStorageConf;
import org.theresis.humanization.datastorage.ProfileException;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.datastorage.ProfileManager.Scope;
import org.theresis.humanization.privacy.CertificationAndPrivacyRequest;
import org.theresis.humanization.privacy.PrivacyCertAuthorityFactory;
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.UserPrivacyContractStorageFactory;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;
import org.theresis.humanization.privacy.generated.PrivacyContract;
import org.theresis.humanization.privacy.generated.UserPrivacyContract;
import org.theresis.humanization.profilestore.ThreeCixtyFactory;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertyPathTest {

	private static final Logger 	logger = LoggerFactory.getLogger(DataStorageTest.class);
	static private ProfileManager 	profileManager;
	static public String 			propertyFilePath = "src/test/resources/TestProfileStorage.properties";
	static public String 			privacyPropertyFilePath = "src/test/resources/TestPrivacyAuthority.properties";
	private static final String 	serviceID1 = "Test";
	private static final String 	serviceID2 = "service2";
	private static Service 			service1 = null;
	private static Service 			service2 = null;
	private static final String 	userID1 = "KHS72H3SJ398";
	private static final String 	userID2 = "FL37ZB18JQB";

	@BeforeClass
	public static void setUp() {
	
		try {
			// the privacy DB
			ProfileStorageConf.setPropertyFile(propertyFilePath);
			PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
			PrivacyDBInitialize.resetAndInit("toto", "toto", "toto");
			//assert(false);
			
			// certify the applications
			CertificationToolBox.certifyApplicationContract(serviceID1, 
															"src/test/resources/exploreMi360.csr",
															"src/test/resources/PrivacyContract_PTestApp.xml",
															"toto");
	
			// certify the applications
			CertificationToolBox.certifyApplicationContract(serviceID2, 
															"src/test/resources/exploreMi360.csr",
															"src/test/resources/PrivacyContract_Pservice2App.xml",
															"toto");
				
			FileInputStream is = new FileInputStream( "src/test/resources/UPC_TestApp.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().store( 	userID1, 
																	serviceID1, 
																	upc);
			UserPrivacyContractStorageFactory.getInstance().store( 	userID2, 
																	serviceID1, 
																	upc);

			FileInputStream is2 = new FileInputStream( "src/test/resources/UPCuser1_service2.xml" );
			UserPrivacyContract upc2 = PrivacyContractFactory.buildUserPrivacyContract( is2 );

			UserPrivacyContractStorageFactory.getInstance().store( 	userID1, 
																	serviceID2, 
																	upc2);
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			profileManager = profileFactory.getProfileManager( propertyFilePath );		

			service1 = profileFactory.getService(serviceID1, "1.0");
			service2 = profileFactory.getService(serviceID2, "1.0");
			
			String profileUser1 = 
				"{"+
					  "\"@graph\" : [ {"+
					    "\"@id\" : \":KHS72H3SJ398\","+
					    "\"@type\" : \":UserProfile\","+
					    "\"hasGender\" : \"Male\","+
					    "\"hasHobbies\" : \"Basket\","+
					    "\"hasLanguage\" : \":langUS\","+
					    "\"hasPreference\" : \":Pref1\","+
					    "\"hasTransport\" : \":transp1\","+
					    "\"hasUID\" : \"KHS72H3SJ398\","+
					    "\"hasAddress\" : \"vcard:home\""+
					  "}, {"+
					    "\"@id\" : \":Pref1\","+
					    "\"@type\" : \":Preference\","+
					    "\"hasUserEnteredRatings\" : \":userEnterRat1\""+
					  "}, {"+
					    "\"@id\" : \":hotelRatingFormule1\","+
					    "\"@type\" : \":UserHotelRating\","+
					    "\":hasNumberOfTimesVisited\" : 10"+
					  "}, {"+
					    "\"@id\" : \":langUS\","+
					    "\"@type\" : \":Language\","+
					    "\"hasKeyTags\" : [ \"toto\", \"titi\" ],"+
					    "\"hasLanguageState\" : \"Speak\""+
					  "}, {"+
					    "\"@id\" : \":transp1\","+
					    "\"@type\" : \":Transport\""+
					  "}, {"+
					    "\"@id\" : \":userEnterRat1\","+
					    "\"@type\" : \":UserEnteredRatings\","+
					    "\"hasUserHotelRating\" : \":hotelRatingFormule1\""+
					  "}, {"+
					    "\"@id\" : \"vcard:home\","+
					    "\"@type\" : \"vcard:Address\","+
					    "\"country-name\" : \"France\""+
					  "} ],"+
					  "\"@context\" : {"+
					    "\"country-name\" : \"http://www.w3.org/2006/vcard/ns#country-name\","+
					    "\"hasNumberOfTimesVisited\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasNumberOfTimesVisited\","+
					      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#integer\""+
					    "},"+
					    "\"hasUserHotelRating\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasUserHotelRating\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
					    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasAddress\" : {"+
					      "\"@id\" : \"http://www.w3.org/2006/vcard/ns#hasAddress\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"hasPreference\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasPreference\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasLanguage\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasTransport\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasTransport\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasUserEnteredRatings\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasUserEnteredRatings\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"@base\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"relationship\" : \"http://purl.org/vocab/relationship/\","+
					    "\"lode\" : \"http://linkedevents.org/ontology/\","+
					    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
					    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
					    "\"dcmitype\" : \"http://purl.org/dc/dcmitype/\","+
					    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
					    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
					    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
					    "\"vcard\" : \"http://www.w3.org/2006/vcard/ns#\""+
					  "}"+
					"}";					

			Session session1 = profileFactory.getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			profileManager.mergeProfile(session1, userID1, profileUser1 );						
			session1 = profileFactory.getSession( profileFactory.getAuthenticator(service1, userID2, null) );
			
			String profileUser2 = 
				"{"+
					  "\"@id\" : \":FL37ZB18JQB\","+
					  "\"@type\" : \":UserProfile\","+
					  "\":hasEmail\" : \"test@gmail.com\","+
					  "\"hasEmail2\" : \"toto@gmail.com\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					  "\"hasUID\" : \"FL37ZB18JQB\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasEmail2\" : \"http://www.eu.3cixty.org/profile#hasEmail2\","+
					    "\"hasEmail\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasEmail\","+
					      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
					    "},"+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"@base\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"relationship\" : \"http://purl.org/vocab/relationship/\","+
					    "\"lode\" : \"http://linkedevents.org/ontology/\","+
					    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
					    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
					    "\"dcmitype\" : \"http://purl.org/dc/dcmitype/\","+
					    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
					    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
					    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
					    "\"vcard\" : \"http://www.w3.org/2006/vcard/ns#\""+
					  "}"+
					"}";
			profileManager.mergeProfile(session1, userID2, profileUser2 );
			
			Session session2 = profileFactory.getSession( profileFactory.getAuthenticator(service2,userID1, null) );
			String profileUser1Serv2 = 
				"{"+
					  "\"@id\" : \":KHS72H3SJ398\","+
					  "\"@type\" : \":UserProfile\","+
					  "\":hasEmail\" : \"test@gmail.com\","+
					  "\"hasEmail2\" : \"toto@gmail.com\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					  "\"hasUID\" : \"KHS72H3SJ398\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasEmail2\" : \"http://www.eu.3cixty.org/profile#hasEmail2\","+
					    "\"hasEmail\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasEmail\","+
					      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
					    "},"+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"@base\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"\" : \"http://www.eu.3cixty.org/profile#\","+
					    "\"relationship\" : \"http://purl.org/vocab/relationship/\","+
					    "\"lode\" : \"http://linkedevents.org/ontology/\","+
					    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
					    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
					    "\"dcmitype\" : \"http://purl.org/dc/dcmitype/\","+
					    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
					    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
					    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
					    "\"vcard\" : \"http://www.w3.org/2006/vcard/ns#\""+
					    "}"+
					"}";			
			profileManager.mergeProfile(session2, userID1, profileUser1Serv2 );			
			
			System.out.println( " -------- end setUp -------- ");
		}
		catch ( Exception e) {
			e.printStackTrace();
			fail( "Error during initialization " + e.getMessage() );
		}
	}

	@Test
	public void test_a_getAllUsersIDs() {

		logger.info(" * test_a" );		
		logger.info(" ---- getAllUsersIDs by Service1 ----" );
		
		try {
			
			Service service = ThreeCixtyFactory.getInstance().getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( ThreeCixtyFactory.getInstance().getAuthenticator(service, null) );
				
			Set<String> users = profileManager.getAllUsersIDs(session);
		
			// No privacy in this test so the 2 registered users are listed
			assertEquals(2, users.size());			
			assertTrue(users.contains( userID1));
			assertTrue(users.contains( userID2));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_b_mergeValuedPropertyPathOnUnknownProfile() {

		String unknownUserID = "toto";
		logger.info(" * test_b" );		
		logger.info(" ------> merge propertypath on user " +  unknownUserID + " by Service1 ----" );		
		
		try {
			
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, unknownUserID, null) );
			
			String hasGenderPath = ":hasGender";
			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, unknownUserID, pairsPropertyPathValues);

			// Now check that the profile has not been created
			// because the user is not in the database
			String jsonProfile = profileManager.getProfile(session, unknownUserID, Scope.PRIVATE);

			assertNull( jsonProfile );

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void test_c_mergeValuedPropertyPath() {

		logger.info(" * test_c" );		
		logger.info(" ------> merge propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {

			Session session = ThreeCixtyFactory.getInstance().getSession( ThreeCixtyFactory.getInstance().getAuthenticator(service1, userID1, null) );
					
			String hasGenderPath = ":hasGender";
			String hasLangPath = ":hasLanguage/:hasLanguageState";
			String hasProfileIdentities = ":hasProfileIdentities";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasLangPath);
			valuedPlacePref.addValue("Write");
			pairsPropertyPathValues.add(valuedPlacePref);

			ValuedProperty valuedProfilePref = new ValuedProperty( hasProfileIdentities );
			valuedProfilePref.addValue("http://www.eu.3cixty.org/profile#110248277616794929135facebookProf");
			pairsPropertyPathValues.add(valuedProfilePref);

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);
		
			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug(" - merged profile =\n" + jsonProfile);

			assertNotNull( jsonProfile );
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));
			assertTrue( jsonProfile.contains("Basket") );
			assertTrue( jsonProfile.contains("facebookProf") );
			assertFalse(jsonProfile.contains("Speak"));
			assertTrue(jsonProfile.contains("Write"));
						

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void test_d_mergeIntegerValuePropertyPath() {
		
		logger.info(" * test_d" );		
		logger.info(" ------> merge propertypath (val=int) on user " +  userID1 + " by Service1 ----" );		
		
		try {			
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
											
			String hasHotelVisitTimesPath = ":hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasNumberOfTimesVisited";
			
			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			
			String visitTimes = "8";
			ValuedProperty valuedHitelVisitTime = new ValuedProperty(hasHotelVisitTimesPath);
			valuedHitelVisitTime.addValue( visitTimes );
			pairsPropertyPathValues.add( valuedHitelVisitTime );

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);
						
			// now, check that the properties has changed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug(" - updated profile = " + jsonProfile);

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasHotelVisitTimesPath);
			Collection<ValuedProperty> 
				timesRes = profileManager.getProfileProperties(session, userID1, propertyPathValues,  Scope.PRIVATE);
			assertTrue(timesRes.size() == 1);
			assertTrue(timesRes.iterator().next().getValues().contains(visitTimes));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	@Test
	public void test_e_mergeStringValuePropertyPath() {
		
		logger.info(" * test_e" );		
		logger.info(" ------> merge propertypath (val=string) on user " +  userID1 + " by Service1 ----" );		
		
		try {
			
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1,userID1, null) );
											
			String hasGenderPath = ":hasEmail";
			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			String email = "KHS72H3SJ398@free.fr";
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue(email);
			pairsPropertyPathValues.add(valuedGender);

			String hasHobbiesPath = ":hasHobbies";
			String hobby = "Cooking";
			ValuedProperty valuedHobbies = new ValuedProperty(hasHobbiesPath);
			valuedHobbies.addValue(hobby);
			pairsPropertyPathValues.add(valuedHobbies);
			
			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties has changed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug(" - updated profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasEmail"));
			assertTrue(jsonProfile.contains(email));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void test_f_mergeMaxCardValuePropertyPath() {

		logger.info(" * test_f" );		
		logger.info(" ------> merge propertypath (maxCard) on user " +  userID1 + " by Service1 ----" );		
		
		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			
			String hasGenderPath = ":hasEmail2";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			String email = "KHS72H3SJ398@gmail.fr";
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue(email);
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug("Merged profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasEmail2"));
			assertTrue(jsonProfile.contains(email));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
		
	@Test
	public void test_g_mergeInvalidEnumValuePropertyPath() {

		logger.info(" * test_g" );		
		logger.info(" ------> merge propertypath (invalid enum) on user " +  userID1 + " by Service1 ----" );		
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Toto");
			pairsPropertyPathValues.add(valuedGender);

			profileManager.mergeProfileProperties(session, userID1, pairsPropertyPathValues);

			fail("This test must throw an exception because of invalid enum value");
			
		} catch (Exception e) {
			// Normal exception
			assertTrue(e instanceof ProfileException);
		}
	}
	
	// model OK apres test verifie
	@Test
	public void test_h_replaceValuedPropertyPath() {

		logger.info(" * test_h" );		
		logger.info(" ------> replace propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {
			
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			
			String hasGenderPath = ":hasGender";
			String hasLangPath = ":hasLanguage/:hasLanguageState";
			String hasProfileIdentities = ":hasProfileIdentities";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasLangPath);
			valuedPlacePref.addValue("ReadWriteSpeak");
			pairsPropertyPathValues.add(valuedPlacePref);

			ValuedProperty valuedProfilePref = new ValuedProperty( hasProfileIdentities );
			valuedProfilePref.addValue("http://www.eu.3cixty.org/profile#facebookProf_KH");
			pairsPropertyPathValues.add(valuedProfilePref);

			profileManager.replaceProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug("Replace profile = " + jsonProfile);

			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Male"));
			assertTrue(jsonProfile.contains("Female"));
			assertFalse(jsonProfile.contains("Country"));

			Collection<String> propertyPathValues = new ArrayList<String>();
			propertyPathValues.add(hasLangPath);
			Collection<ValuedProperty> langRes = profileManager.getProfileProperties(session, userID1, propertyPathValues, Scope.PRIVATE);
			assertTrue(langRes.size() == 1);
			assertTrue(langRes.iterator().next().getValues().contains( "ReadWriteSpeak" ));

			propertyPathValues.clear();
			propertyPathValues.add(hasProfileIdentities);
			Collection<ValuedProperty> profRes = profileManager.getProfileProperties(session, userID1, propertyPathValues, Scope.PRIVATE);
			assertTrue(profRes.size() == 1);
			assertTrue( jsonProfile.contains( "facebookProf_KH" ) );

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
		
	@Test
	public void test_i_deleteNotSetPropertyPath() {

		logger.info(" * test_i" );		
		logger.info(" ------> delete not set propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );			

			String hasGenderPath = ":hasGender";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();
			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Male");
			pairsPropertyPathValues.add(valuedGender);
			profileManager.deleteProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			assertNotNull(jsonProfile);
			assertTrue(jsonProfile.contains("hasGender"));
			assertTrue(jsonProfile.contains("Female"));

			logger.debug("Updated profile = " + jsonProfile);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_j_deleteValuedPropertyPath() {

		logger.info(" * test_j" );		
		logger.info(" ------> delete propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {
	
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			
			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasLanguage/:hasKeyTags*";

			Collection<ValuedProperty> pairsPropertyPathValues = new ArrayList<ValuedProperty>();

			ValuedProperty valuedGender = new ValuedProperty(hasGenderPath);
			valuedGender.addValue("Female");
			pairsPropertyPathValues.add(valuedGender);

			ValuedProperty valuedPlacePref = new ValuedProperty(hasPlacePath);
			valuedPlacePref.addValue("titi");
			pairsPropertyPathValues.add(valuedPlacePref);

			profileManager.deleteProfileProperties(session, userID1, pairsPropertyPathValues);

			// now, check that the properties have been removed
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			assertNotNull(jsonProfile);
			assertFalse(jsonProfile.contains("hasGender"));
			assertFalse(jsonProfile.contains("Female"));
			assertTrue( jsonProfile.contains("hasKeyTags") );
			assertTrue( jsonProfile.contains("toto") );
			assertFalse( jsonProfile.contains("\"hasKeyTags\" : [ \"titi\", \"toto\" ]") );
						
			//logger.debug("Updated profile = " + jsonProfile);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	@Test
	public void test_l_getPropertyPathUnknownService() {

		logger.info(" * test_l" );		
		logger.info(" ------> get propertypath on user " +  userID1 + " by ServiceX ----" );		
		
		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service serviceUnknown = profileFactory.getService("serviceX", "1.0" );
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(serviceUnknown, userID1, null) );
			
			String hasGenderPath = ":hasGender";

			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasGenderPath);			
			Collection<ValuedProperty> propertyValues =  profileManager.getProfileProperties(session, 
																							userID1, 
																							propertyPaths,							
																							Scope.PRIVATE );
			
			// the service has no data for this user, the result must be empty
			logger.debug(" results : ");
			for ( ValuedProperty val : propertyValues ) {
				
				logger.debug( " - " + val );
				assertTrue( val.getPropertyPath().compareTo(hasGenderPath) == 0 );
				assertTrue( val.getValues().size() == 1 );
				assertTrue( val.getValue(0).isEmpty() );
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	
	@Test
	public void test_m_getPropertyPath() {

		logger.info(" * test_m" );		
		logger.info(" ------> get propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			
			String hasGenderPath = ":hasGender";
			String hasHobbiesPath = ":hasHobbies";
			String langPath = ":hasLanguage/:hasKeyTags";
			String hasHotelVisitTimesPath = ":hasPreference/:hasUserEnteredRatings/:hasUserHotelRating/:hasNumberOfTimesVisited";

			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasGenderPath);
			propertyPaths.add(hasHobbiesPath);
			propertyPaths.add(langPath);
			propertyPaths.add( hasHotelVisitTimesPath );
			
			Collection<ValuedProperty> propertyValues =  profileManager.getProfileProperties(session, 
																							userID1, 
																							propertyPaths,
																							Scope.PRIVATE );		

			logger.debug(" results : ");
			for ( ValuedProperty val : propertyValues ) {
				
				logger.debug( " - " + val );
				if ( val.getPropertyPath().compareTo( hasGenderPath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "", val.getValue(0) );				
				}
				else if ( val.getPropertyPath().compareTo( hasHobbiesPath) == 0 ) {
					assertTrue( val.getNbValues() == 2 );
					assertTrue( val.getValues().contains( "Cooking") );									
					assertTrue(  val.getValues().contains( "Basket") );									
				}			
				else if ( val.getPropertyPath().compareTo( langPath ) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertTrue( val.getValue(0).contains("toto") );									
				}				
				else if ( val.getPropertyPath().compareTo( hasHotelVisitTimesPath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "8", val.getValue(0) );														
				}
				else {
					fail( "Not expected property "  + val);
				}
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void test_n_getCountryNamePropertyPath() {

		logger.info(" * test_n" );		
		logger.info(" ------> get propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );
			
			String hasCountryName = "vcard:hasAddress/vcard:country-name";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasCountryName);

			Collection<ValuedProperty> propertyValues = profileManager.getProfileProperties(session, userID1, propertyPaths, Scope.PRIVATE);
		
			logger.debug(" results : ");

			for (ValuedProperty val : propertyValues) {

				logger.debug(" - " + val);
				if (val.getPropertyPath().compareTo(hasCountryName) == 0) {
					assertTrue(val.getNbValues() == 1);
					assertEquals("France", val.getValue(0));
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
	public void test_o_getPropertyPath() {

		logger.info(" * test_o" );		
		logger.info(" ------> get public propertypath on user " +  userID1 + " by Service1 ----" );		
		
		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service1, userID1, null) );			
			
			String hasGenderPath = ":hasGender";
	
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add(hasGenderPath);
					
			Collection<ValuedProperty> propertyValues =  profileManager.getProfileProperties(session, 
																							userID1, 
																							propertyPaths,
																							Scope.GLOBAL );		
			logger.debug(" results : ");
			for ( ValuedProperty val : propertyValues ) {
				
				logger.debug( " - " + val );
				if ( val.getPropertyPath().compareTo( hasGenderPath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "Female", val.getValue(0) );				
				}
				else {
					fail( "Not expected property "  + val);
				}
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}		
}

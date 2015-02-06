package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;

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
import org.theresis.humanization.datastorage.ProfileManager.Scope;
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.UserPrivacyContractStorageFactory;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;
import org.theresis.humanization.privacy.generated.UserPrivacyContract;
import org.theresis.humanization.profilestore.ThreeCixtyFactory;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataStorageTest {

	private static final Logger 	logger = LoggerFactory.getLogger(DataStorageTest.class);
	static private ProfileManager 	profileManager;
	static public String 			propertyFilePath = "src/test/resources/TestProfileStorage.properties";
	static public String 			privacyPropertyFilePath = "src/test/resources/TestPrivacyAuthority.properties";
	private static final String 	serviceID1 = "Test";
	private static final String 	serviceID2 = "service2";
	private static final String 	userID1 = "KHS72H3SJ398";
	private static final String 	userID2 = "FL37ZB18JQB";

	@BeforeClass
	public static void setUp() {
	
		try {

			// the privacy DB
			ProfileStorageConf.setPropertyFile(propertyFilePath);
			PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
			PrivacyDBInitialize.resetAndInit("toto", "toto", "toto");
			
			// certify the applications
			CertificationToolBox.certifyApplicationContract(serviceID1, 
															"src/test/resources/exploreMi360.csr",
															"src/test/resources/PrivacyContract_TestApp.xml",
															"toto");
	
			// certify the applications
			CertificationToolBox.certifyApplicationContract(serviceID2, 
															"src/test/resources/exploreMi360.csr",
															"src/test/resources/PrivacyContract_service2App.xml",
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
			System.out.println( " -------- end setUp -------- ");
		}
		catch ( Exception e) {
			e.printStackTrace();
			logger.error("Error during initialization " + e.getMessage());
			fail( "Error during initialization " + e.getMessage() );
		}
	}

	@Test
	public void test_a_createUserProfileService1() {

		logger.info(" * test_a ");
		logger.info(" ---- createUserProfile " +  userID1 + " by Service1 ----" );

		try {

			Service service = ThreeCixtyFactory.getInstance().getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( ThreeCixtyFactory.getInstance().getAuthenticator(service, userID1, null) );
			
			// create a user profile in the model
			String newSerialProfile =
					"{"+
					  "\"@graph\" : [ {"+
					    "\"@id\" : \":KHS72H3SJ398\","+
					    "\"@type\" : \":UserProfile\","+
					    "\"hasGender\" : \"Male\","+
					    "\"hasHobbies\" : \"Basket\","+
					    "\"hasLanguage\" : \":langUS\","+
					    "\"hasTransport\" : \":transp1\","+
					    "\"hasUID\" : \"KHS72H3SJ398\""+
					  "}, {"+
					    "\"@id\" : \":langUS\","+
					    "\"@type\" : \":Language\","+
					    "\"hasKeyTags\" : [ \"toto\", \"titi\" ],"+
					    "\"hasLanguageState\" : \"Speak\""+
					  "}, {"+
					    "\"@id\" : \":transp1\","+
					    "\"@type\" : \":Transport\""+
					  "} ],"+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"hasLanguage\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasTransport\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasTransport\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
					    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
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
					
			logger.debug("new Serialized Profile = " + newSerialProfile);
			profileManager.mergeProfile(session, userID1, newSerialProfile);

			String createdProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE );
			
			assertTrue( createdProfile.contains( userID1) );
			assertTrue( createdProfile.contains( "toto") );
			assertTrue( createdProfile.contains( "titi") );
			assertTrue( createdProfile.contains( "langUS") );
						
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getMessage());
		}			
	}



	@Test
	public void test_b_createUserProfileService2() {

		logger.info(" ---- createUserProfile " +  userID1 + " by Service2 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID2, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			// create a user profile in the model
			String newSerialProfile =
				"{"+
					  "\"@graph\" : [ {"+
					    "\"@id\" : \":KHS72H3SJ398\","+
					    "\"@type\" : \":UserProfile\","+
					    "\":hasEmail\" : \"test@gmail.com\","+
					    "\"hasEmail2\" : \"toto@gmail.com\","+
					    "\"hasGender\" : \"Female\","+
					    "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					    "\"hasLanguage\" : \":langIT\","+
					    "\"hasUID\" : \"KHS72H3SJ398\""+
					  "}, {"+
					    "\"@id\" : \":langIT\","+
					    "\"@type\" : \":Language\","+
					    "\"hasKeyTags\" : [ \"Roma\", \"Firenze\" ],"+
					    "\"hasLanguageState\" : [ \"parlato\", \"scritto\" ]"+
					  "} ],"+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasEmail2\" : \"http://www.eu.3cixty.org/profile#hasEmail2\","+
					    "\"hasEmail\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasEmail\","+
					      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
					    "},"+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"hasLanguage\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
					    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
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
					
			logger.debug("new Serialized Profile = " + newSerialProfile);
			profileManager.mergeProfile(session, userID1, newSerialProfile);
			
			String newProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			assertTrue( newProfile.contains( userID1) );
			assertTrue( newProfile.contains( "test@gmail.com") );
			assertTrue( newProfile.contains( "toto@gmail.com") );
			assertTrue( newProfile.contains( "langIT") );
			
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
	
	@Test
	public void test_c_modifyUserProfileService1() {


		logger.info(" * test_c  ---");
		logger.info(" ---> modifyUserProfile " +  userID1 + " by Service1 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			// create a user profile in the model
			String newSerialProfile = 
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
			
			logger.debug("new Serialized Profile = " + newSerialProfile);
			profileManager.mergeProfile(session, userID1, newSerialProfile);

			// check the profile
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug(jsonProfile);
			assertNotNull(jsonProfile);
						
			assertTrue( jsonProfile.contains("\"hasGender\" : [ \"Female\" ]") );
			assertTrue( jsonProfile.contains("\"hasHobbies\" : [ \"Piano\", \"Gardening\", \"Basket\" ]") );
			assertTrue( jsonProfile.contains("\"hasEmail\" : [ \"test@gmail.com\" ]") );
			assertTrue( jsonProfile.contains("\"hasEmail2\" : [ \"toto@gmail.com\" ]") );
			
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
	
	@Test
	public void test_d_createUserProfileWithNoUserIDProperty() {
		
		logger.info(" * test_d  ---");
		logger.info(" ---> CreateUserProfile " + userID2 + " with no key by Service1 ----" );

		try {
	
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID2, null) );
				
			// create a user profile in the model
			String newSerialProfile = 
				"{"+
					  "\"@graph\" : [ {"+
					    "\"@id\" : \":FL37ZB18JQB\","+
					    "\"@type\" : \":UserProfile\","+
					    "\"hasGender\" : \"Male\","+
					    "\"hasHobbies\" : \"Basket\","+
					    "\"hasLanguage\" : \":langUS\","+
					    "\"hasTransport\" : \":transp1\""+
					  "}, {"+
					    "\"@id\" : \":langUS\","+
					    "\"@type\" : \":Language\","+
					    "\"hasKeyTags\" : [ \"toto\", \"titi\" ],"+
					    "\"hasLanguageState\" : \"Speak\""+
					  "}, {"+
					    "\"@id\" : \":transp1\","+
					    "\"@type\" : \":Transport\""+
					  "} ],"+
					  "\"@context\" : {"+
					    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
					    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"hasLanguage\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasTransport\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasTransport\","+
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
			
			logger.debug(" - new Profile = \n" + newSerialProfile);

			profileManager.mergeProfile(session, userID2, newSerialProfile);

			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, userID2, Scope.PRIVATE );
			logger.debug(jsonProfile);
			assertNotNull(jsonProfile);
						
			assertFalse( jsonProfile.contains("\"hasGender\" : [ \"Female\" ]") );
			assertFalse( jsonProfile.contains("\"hasHobbies\" : [ \"Piano\", \"Gardening\", \"Basket\" ]") );
			assertFalse( jsonProfile.contains("\"hasEmail\" : [ \"test@gmail.com\" ]") );
			assertFalse( jsonProfile.contains("\"hasEmail2\" : [ \"toto@gmail.com\" ]") );
			
			assertTrue( jsonProfile.contains("\"hasGender\" : [ \"Male\" ]") );
			assertTrue( jsonProfile.contains("\"hasHobbies\" : [ \"Basket\" ]") );
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}		

	@Test
	public void test_e_modifyUserProfileWithNoUserID() {

		logger.info(" * test_e  ---");
		logger.info(" ---> modifyUserProfile " +  userID2 + " by Service1 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID2, null) );
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile =
				"{"+
					  "\"@id\" : \":FL37ZB18JQB\","+
					  "\"@type\" : \":UserProfile\","+
					  "\":hasEmail\" : \"test@gmail.com\","+
					  "\"hasEmail2\" : \"toto@gmail.com\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					  "\"@context\" : {"+
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

			logger.debug(" - merged profile = " + newSerialProfile);

			profileManager.mergeProfile(session, userID2, newSerialProfile);
			
			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, userID2, Scope.PRIVATE );
			logger.debug(" new profile = \n" + jsonProfile);
			assertNotNull(jsonProfile);
						
			assertTrue( jsonProfile.contains("\"hasGender\" : [ \"Female\" ]") );
			assertTrue( jsonProfile.contains("\"hasHobbies\" : [ \"Basket\", \"Gardening\", \"Piano\" ]") );
			assertTrue( jsonProfile.contains("\"hasEmail\" : [ \"test@gmail.com\" ]") );
			assertTrue( jsonProfile.contains("\"hasEmail2\" : [ \"toto@gmail.com\" ]") );
			
			assertFalse( jsonProfile.contains("\"hasGender\" : [ \"Male\" ]") );
			assertFalse( jsonProfile.contains("\"hasHobbies\" : [ \"Basket\" ]") );
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_f_createNotConsistentUserProfile() {

		logger.info(" * test_f  ---");
		logger.info(" ---> createUserProfile " +  userID1 + " by Service2 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID2, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			// create a user profile in the model with the wrong user ID
			String newSerialProfile =
				"{"+
					  "\"@graph\" : [ {"+
					    "\"@id\" : \":KHS72H3SJ398\","+
					    "\"@type\" : \":UserProfile\","+
					    "\":hasEmail\" : \"test@gmail.com\","+
					    "\"hasEmail2\" : \"toto@gmail.com\","+
					    "\"hasGender\" : \"Female\","+
					    "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					    "\"hasLanguage\" : \":langIT\","+
					    "\"hasUID\" : \"WRONGID\""+
					  "}, {"+
					    "\"@id\" : \":langIT\","+
					    "\"@type\" : \":Language\","+
					    "\"hasKeyTags\" : [ \"Roma\", \"Firenze\" ],"+
					    "\"hasLanguageState\" : [ \"parlato\", \"scritto\" ]"+
					  "} ],"+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasEmail2\" : \"http://www.eu.3cixty.org/profile#hasEmail2\","+
					    "\"hasEmail\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasEmail\","+
					      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
					    "},"+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
					    "\"hasLanguage\" : {"+
					      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
					      "\"@type\" : \"@id\""+
					    "},"+
					    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
					    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
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
			logger.debug(" - new Profile = " + newSerialProfile);

			profileManager.mergeProfile(session, userID1, newSerialProfile);

			fail();

		} catch (Exception e) {
			
			// Normal exception
			assertTrue(e instanceof ProfileException);
		}
	}
	

	@Test
	public void test_g_replaceUserProfile() {

		logger.info(" * test_g  ---");
		logger.info(" ---> replaceUserProfile " +  userID1 + " by Service1 ----" );

		try {
							
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0" );
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1, null) );
				
			String newSerialProfile =
			"{"+
				  "\"@id\" : \":KHS72H3SJ398\","+
				  "\"@type\" : \":UserProfile\","+
				  "\":hasEmail\" : \"test3@gmail.com\","+
				  "\"hasGender\" : \"Male\","+
				  "\"hasHobbies\" : \"Cooking\","+
				  "\"hasUID\" : \"KHS72H3SJ398\","+
				  "\"@context\" : {"+
				    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
				    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
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
			
			logger.debug(" - replacement Profile = " + newSerialProfile);
		
			profileManager.replaceProfile(session, userID1, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE );
			logger.debug(" new profile = \n" + jsonProfile);
			assertNotNull(jsonProfile);
			
			assertTrue( jsonProfile.contains("\"hasGender\" : [ \"Male\" ]") );
			assertTrue( jsonProfile.contains("\"hasHobbies\" : [ \"Cooking\" ]") );
			assertTrue( jsonProfile.contains("\"hasEmail\" : [ \"test3@gmail.com\" ]") );
	
			assertFalse( jsonProfile.contains("\"hasEmail2\"") );			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void test_h_replaceUserProfileWithNoUserID() {

		logger.info(" * test_h  ---");
		logger.info(" ---> replaceUserProfile " +  userID1 + "with no key by Service2 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID2, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1,  null) );
			
			String newSerialProfile = 
				"{"+
					  "\"@id\" : \":KHS72H3SJ398\","+
					  "\"@type\" : \":UserProfile\","+
					  "\":hasEmail\" : \"test3@gmail.com\","+
					  "\"hasGender\" : \"Male\","+
					  "\"hasHobbies\" : \"Cooking\","+
					  "\"@context\" : {"+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
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
			logger.debug(" - replacement Profile = " + newSerialProfile);
		
			profileManager.replaceProfile(session, userID1, newSerialProfile);		

			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			assertNotNull(jsonProfile);
			logger.debug("Result of replace profile is : " + jsonProfile);
				
			assertTrue( jsonProfile.contains("\"hasEmail\" : [ \"test3@gmail.com\" ]") );
			assertTrue( jsonProfile.contains("\"hasGender\" : [ \"Male\" ]") );
			assertTrue( jsonProfile.contains("\"hasHobbies\" : [ \"Cooking\" ]") );
			
			assertFalse( jsonProfile.contains( "\"hasEmail\" : [ \"test@gmail.com\" ]") );
			assertFalse( jsonProfile.contains( "hasEmail2") );
			assertFalse( jsonProfile.contains( "\"hasLanguage\" : [ \":langIT\" ]") );

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_i_replaceNotConsistentUserProfile() {

		logger.info(" * test_i  ---");
		logger.info(" ---> replaceUserProfile " +  userID1 + "with wrong id by Service1 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile =
					"{"+
						  "\"@graph\" : [ {"+
						    "\"@id\" : \":KHS72H3SJ398\","+
						    "\"@type\" : \":UserProfile\","+
						    "\":hasEmail\" : \"test@gmail.com\","+
						    "\"hasEmail2\" : \"toto@gmail.com\","+
						    "\"hasGender\" : \"Female\","+
						    "\"hasHobbies\" : [ \"Piano\" ],"+
						    "\"hasLanguage\" : \":langIT\","+
						    "\"hasUID\" : \"WRONGID\""+
						  "}, {"+
						    "\"@id\" : \":langIT\","+
						    "\"@type\" : \":Language\","+
						    "\"hasKeyTags\" : [ \"Roma\", \"Firenze\" ],"+
						    "\"hasLanguageState\" : [ \"parlato\", \"scritto\" ]"+
						  "} ],"+
						  "\"@context\" : {"+
						    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
						    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
						    "\"hasEmail2\" : \"http://www.eu.3cixty.org/profile#hasEmail2\","+
						    "\"hasEmail\" : {"+
						      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasEmail\","+
						      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
						    "},"+
						    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\","+
						    "\"hasLanguage\" : {"+
						      "\"@id\" : \"http://www.eu.3cixty.org/profile#hasLanguage\","+
						      "\"@type\" : \"@id\""+
						    "},"+
						    "\"hasLanguageState\" : \"http://www.eu.3cixty.org/profile#hasLanguageState\","+
						    "\"hasKeyTags\" : \"http://www.eu.3cixty.org/profile#hasKeyTags\","+
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

			logger.debug(" - Replacement Profile = " + newSerialProfile);

			profileManager.replaceProfile(session, userID1, newSerialProfile);

			fail();

		} catch (Exception e) {

			// Normal exception
			assertTrue(e instanceof ProfileException);
			logger.error(e.getMessage());

		}
	}
	
	@Test
	public void test_j_deleteUserProfile() {

		logger.info(" * test_j  ---");
		logger.info(" ---> deleteUserProfile " +  userID2 + " by Service1 ----" );

		try {

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService(serviceID1, "1.0");
			Session session = ThreeCixtyFactory.getInstance().getSession( profileFactory.getAuthenticator(service, userID2, null) );
			
			String result = profileManager.getProfile(session, userID2, Scope.PRIVATE);
			assertNotNull(result);

			boolean res = profileManager.deleteProfile(session, userID2);
			assertTrue(res);

			result = profileManager.getProfile(session, userID2, Scope.PRIVATE);
			assertNull( result );
			
			assertFalse (profileManager.hasProfile(session, userID2) );
						
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}

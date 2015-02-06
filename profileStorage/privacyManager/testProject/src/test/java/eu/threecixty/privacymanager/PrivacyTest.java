/**
 * @file	PrivacyTest.java
 * @brief 
 * @date	Oct 27, 2014
 * @author	Flore Lantheaume
 * @copyright THALES 2014. All rights reserved.
 * THALES PROPRIETARY/CONFIDENTIAL.
*/

package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.conf.ProfileStorageConf;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.datastorage.ProfileManager.Scope;
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.UserPrivacyContractStorageFactory;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;
import org.theresis.humanization.privacy.generated.UserPrivacyContract;
import org.theresis.humanization.profilestore.ThreeCixtyFactory;

/**
 * Test on privacy filtering
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivacyTest {

	private ProfileManager 		profileManager;
	private static final Logger logger = LoggerFactory.getLogger(PrivacyTest.class);
	static public String 		propertyFilePath = "src/test/resources/3CixtyProfileStorage.properties";
	static public String 		privacyPropertyFilePath = "src/test/resources/3CixtyPrivacyAuthority.properties";
	private static String 		userID1 = "62fcec64-ebef-4faf-ba97-c58cd99ae107";
	private static String		userID2 = "33d38e2d-8b34-4962-bcfb-7cc14e54599a";
	private static String		userID3 = "529jd9js-29gd-hd7z-sv2h-2k948kjd3jd8";
	static private String		appName1 = "ExploreMi 360";
	static private String		appversion1 = "1.0";
	static private String		appName2 = "service2";
	static private String		appversion2 = "1.0";	
	
	@BeforeClass
	public static void configurePrivacyDB() {
		
		try {
			// reset the DB
			PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
			PrivacyDBInitialize.resetAndInit("toto", "toto", "toto");
			
			// certify the applications
			CertificationToolBox.certifyApplicationContract(appName1, 
															"src/test/resources/exploreMi360.csr",
															"src/test/resources/PrivacyContract_ExploreMi360_example.xml",
															"toto");
			
			CertificationToolBox.certifyApplicationContract(appName2, 
					"src/test/resources/exploreMi360.csr",
					"src/test/resources/PrivacyContract_service2.xml",
					"toto");
			
			// add UPC
			FileInputStream is = new FileInputStream( "src/test/resources/UPC_ExploreMi360.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().store(	userID1, 
																	appName1, 
																	upc);
			
			is = new FileInputStream( "src/test/resources/UPC_service2.xml" );
			upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().store( userID1, 
																   appName2, 
																   upc);		
			
			is = new FileInputStream( "src/test/resources/UPC_ExploreMi360.xml" );
			upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().store( 	userID3, 
																	appName1, 
																	upc);
		}
		catch ( Exception e) {
			e.printStackTrace();
			fail( "Error during initialization " + e.getMessage() );
		}		
	}
	
	@Before
	public  void setUp() {
	
		try {
			// the privacy DB
			ProfileStorageConf.setPropertyFile(propertyFilePath);
		
			// the profile store
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();		
			profileManager = profileFactory.getProfileManager( propertyFilePath );	
			
			// create a session for the application
			Service service = profileFactory.getService( appName1, appversion1);
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String profileUser1 = getUser008();
			profileManager.mergeProfile(session, userID1,  profileUser1);						
			System.out.println(" New profile flore :\n" + profileUser1 + "\n--- fin flore ---");

			Service service2 = profileFactory.getService( appName2, appversion2);
			Session session2 = profileFactory.getSession( profileFactory.getAuthenticator(service2, userID2, null) );
			
			String profileUser2 = getUser009(); //ProfileTTLModelToolBox.createUser1("OWLNamedIndividual_000009", userID2, true);
			profileManager.mergeProfile(session2, userID2, profileUser2 );
			
			Session session2_1 = profileFactory.getSession( profileFactory.getAuthenticator(service2, userID1, null) );			
			String profileUser2_1 = getUser008Service2(); //ProfileTTLModelToolBox.createUser2("OWLNamedIndividual_000008", userID1, true);
			profileManager.mergeProfile(session2_1, userID1, profileUser2_1 );
		}
		catch ( Exception e) {
			e.printStackTrace();
			fail( "Error during initialization " + e.getMessage() );
		}
	}

	@Test
	public void test_a_getAllUsersIDsPrivacy() {
		
		logger.info(" * test_a  ---");
		logger.info(" ---> getAllUsersIDsPrivacy by service  " + appName1 + "/" + appversion1 + "----" );
		
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1);
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, null) );
			
			Set<String> users = profileManager.getAllUsersIDs(session);

			// only user1 has a privacy contract with the service
			assertEquals(1, users.size());
			assertTrue(users.contains( userID1));

		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void test_b_getUserProfileNoContractPrivacy() {

		logger.info(" * test_b  ---");
		logger.info(" ---> getUserProfile " + userID1 + " with NoContractPrivacy with service test ----" );
		
		try {
			
			// the storage
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService("test", "4.0");
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			assertNull( jsonProfile );
	
			jsonProfile = profileManager.getProfile(session, userID1, Scope.GLOBAL);
			assertNull( jsonProfile );
			
			logger.info( " -> getUserProfileNoContractPrivacy OK");
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	
	@Test
	public void test_c_getUserProfilePrivacyByOtherUser() {

		logger.info(" * test_c  ---");
		logger.info(" ---> getUserProfilePrivacy of user " + userID1 + " by another user U2678 ----" );
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);			
			assertNull(jsonProfile);
		
			jsonProfile = profileManager.getProfile(session, userID1, Scope.GLOBAL);			
			assertNull(jsonProfile);
				
			logger.info( " -> getUserProfilePrivacyByOtherUser OK");			
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void test_d_getMyUserProfilePrivacy() {

		logger.info(" * test_d  ---");
		logger.info(" ---> get private userProfile of user " + userID1 + " by itself by service "+ appName1 + "/"+ appversion1 + "----" );
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			logger.debug(jsonProfile);
			System.out.println("jsonProfile = " + jsonProfile );
			assertNotNull(jsonProfile);
			
			assertTrue( jsonProfile.contains("\"gender\" : [ \"female\" ]") );
			assertTrue( jsonProfile.contains("\"givenName\" : [ \"La colombo\" ]") );
			assertTrue( jsonProfile.contains("attendedTrayItemDate") );
			assertTrue( jsonProfile.contains("attendedTrayItem") );
			assertTrue( jsonProfile.contains("\"familyName\" : [ \"Colombo\" ]") );
			assertTrue( ! jsonProfile.contains("\"familyName\" : [ \"Maurel\" ]") );
			assertTrue( jsonProfile.contains("Le resto a mr & Mme Colombo\" ]") );
			assertTrue( jsonProfile.contains("http://www.lerestodu15ieme.fr") );			

			logger.info( " -> getMyUserProfilePrivacy OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void test_e_getMyUserProfilePrivacy() {

		logger.info(" * test_e  ---");
		logger.info(" ---> getMyUserProfile privacy of user " + userID1 + " by itself by service "+ appName1 + "/"+ appversion1 + "----" );
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.GLOBAL);
			logger.debug(jsonProfile);
			System.out.println("jsonProfile test_e = " + jsonProfile );
			assertNotNull(jsonProfile);
			
			assertFalse( jsonProfile.contains("gender") );
			assertFalse( jsonProfile.contains("givenName") );
			assertFalse( jsonProfile.contains("surname") );
			assertTrue( jsonProfile.contains("\"attendedTrayItemDate\" : [ \"2014-09-24\" ]") );
			assertTrue( jsonProfile.contains("\"attendedTrayItem\" : [ \"true\" ]") );
			assertTrue( jsonProfile.contains("\"familyName\" : [ \"Colombo\" ]") );
			assertFalse( jsonProfile.contains("\"familyName\" : [ \"Maurel\" ]") );
			assertFalse( jsonProfile.contains("Le MacDo Beaugrenelle") );
			assertFalse( jsonProfile.contains("Le Cine Beaugrenelle") );
			assertTrue( jsonProfile.contains("\"accountName\" : [ \"Le resto a mr & Mme Colombo\" ]") );			

			logger.info( " -> getMyUserProfilePrivacy OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void test_f_writeAnotherUserProfilePrivacy() {

		logger.info(" * test_f  ---");
		logger.info(" ---> write profile privacy of user " + userID1 + " by another by service "+ appName1 + "/"+ appversion1 + "----" );
		
		// try to write a profile of a user by another user 
		// => it's not possible. There's no crash but the user profile of the service 
		// is not written
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String jsonProfile = "";
			profileManager.mergeProfile(session, userID3, jsonProfile);
						
			// check that nothing has been written
			jsonProfile = profileManager.getProfile(session, userID3, Scope.PRIVATE);
			assertNull(jsonProfile);
			
			jsonProfile = profileManager.getProfile(session, userID3, Scope.GLOBAL);
			assertNull(jsonProfile);
			
			logger.info( " -> writeAnotherUserProfilePrivacy OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}	
	
	@Test
	public void test_g_getProfilePropertiesPrivacy() {

		logger.info(" * test_g  ---");
		logger.info(" ---> get private userProfile of user " + userID1 + " by itself by service "+ appName1 + "/"+ appversion1 + "----" );
		
		try {
			FileInputStream is = new FileInputStream( "src/test/resources/UPC2_ExploreMi360.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().update( userID1, 
																	appName1, 
																	upc);

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
		
			Collection<String> propertyPaths = new ArrayList<String>();
			String propPath1 = "profile:trayElement[profile:hasRating / schema:ratingValue > \"2\"]/profile:hasAccount /foaf:accountName";
			propertyPaths.add( propPath1);
			String propPath2 = "foaf:surname";
			propertyPaths.add( propPath2);
						
			
			Collection<ValuedProperty> res = profileManager.getProfileProperties(session, 
																				userID1, 
																				propertyPaths,
																				Scope.PRIVATE);	

			System.out.println("res = " + res );
			
			for ( ValuedProperty val : res ) {
				
				String pp = val.getPropertyPath() ;
				assertTrue( val.getNbValues() == 1 );
				if ( pp.compareTo(propPath1) == 0 ) {
					assertTrue( val.getValue(0).contains( "Le resto a mr & Mme Colombo" ) );
				}
				else if ( pp.compareTo(propPath2) == 0 ) {
					assertTrue( val.getValue(0).compareTo("Mme Colombo") == 0 );
				}
				else 
					fail( " ??");
			}
			
			Collection<ValuedProperty> resG = profileManager.getProfileProperties(session, 
																					userID1, 
																					propertyPaths,
																					Scope.GLOBAL);	
			System.out.println("resG = " + resG );
			for ( ValuedProperty val : resG ) {
				
				String pp = val.getPropertyPath() ;
				assertTrue( val.getNbValues() == 1 );
				if ( pp.compareTo(propPath1) == 0 ) {
					assertTrue( val.getValue(0).isEmpty() );
				}
				else if ( pp.compareTo(propPath2) == 0 ) {
					assertTrue( val.getValue(0).isEmpty() );
				}
				else 
					fail( " ??");
			}
			logger.info( " -> getProfilePropertiesPrivacy OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void test_h_getFullAccessProfile() {

		logger.info(" * test_h  ---");
		logger.info(" ---> get full access userProfile of user " + userID1 + " by itself by service "+ appName1 + "/"+ appversion1 + "----" );
			
		try {
			FileInputStream is = new FileInputStream( "src/test/resources/UPC2_AppFull.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			UserPrivacyContractStorageFactory.getInstance().update( userID1, 
																	appName1, 
																	upc);

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName1, appversion1 );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1, Scope.PRIVATE);
			
			System.out.println("res = " + jsonProfile );
			
			assertTrue( jsonProfile.contains("\"gender\" : [ \"female\" ]") );
			assertTrue( jsonProfile.contains("\"givenName\" : [ \"La colombo\" ]") );
			assertTrue( jsonProfile.contains("\"surname\" : [ \"Mme Colombo\" ]") );
			assertTrue( jsonProfile.contains("\"familyName\" : [ \"Colombo\" ]"));
			assertTrue( jsonProfile.contains("\"accountName\" : [ \"Le Cine Beaugrenelle\" ]") );
			assertFalse( jsonProfile.contains("Le MacDo Beaugrenelle") );
			assertFalse( jsonProfile.contains("La folle") );
			assertTrue( jsonProfile.contains("\"accountName\" : [ \"Le resto a mr & Mme Colombo\" ]") );
			assertTrue( jsonProfile.contains("\"attendedTrayItemDate\" : [ \"2014-09-24\" ]") );			
			
			logger.info( " -> getFullAccessProfile OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	public static String getUser008() {
		
			String newProfile = "{"+
				"\"@graph\" : [ {"+						 
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005\","+
			    "\"@type\" : \"profile:Tray\","+
			    "\"profile:attendedTrayItem\" : \"true\","+
			    "\"attendedTrayItemDate\" : \"2014-09-24\","+
			    "\"hasAccount\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_account\","+
			    "\"hasRating\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_rating\","+
			    "\"trayItemID\" : \"feb1f528-ff85-485b-92c7-98c0fa8d4d49\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_account\","+
			    "\"@type\" : \"foaf:OnlineAccount\","+
			    "\"accountName\" : \"Le resto a mr & Mme Colombo\","+
			    "\"\" : \"http://www.lerestodu15ieme.fr\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_rating\","+
			    "\"@type\" : \"schema:Rating\","+
			    "\"schema:ratingValue\" : \"5\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000008\","+
			    "\"@type\" : \"schema:Person\","+
			    "\"trayElement\" : [ \"http://www.3cixty.com#OWLNamedIndividual_000021\", \"http://www.3cixty.com#OWLNamedIndividual_000005\" ],"+
			    "\"userID\" : \"62fcec64-ebef-4faf-ba97-c58cd99ae107\","+
			    "\"age\" : \"30\","+
			    "\"familyName\" : \"Colombo\","+
			    "\"gender\" : \"female\","+
			    "\"givenName\" : \"La colombo\","+
			    "\"knows\" : [ \"http://www.3cixty.com#OWLNamedIndividual_000012\", \"http://www.3cixty.com#OWLNamedIndividual_000001\", \"http://www.3cixty.com#OWLNamedIndividual_000009\", \"http://www.3cixty.com#OWLNamedIndividual_000010\", \"http://www.3cixty.com#OWLNamedIndividual_000011\" ],"+
			    "\"surname\" : \"Mme Colombo\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000021\","+
			    "\"@type\" : \"profile:Tray\","+
			    "\"profile:attendedTrayItem\" : \"false\","+
			    "\"hasAccount\" : \"http://www.3cixty.com#OWLNamedIndividual_000021_account\","+
			    "\"trayItemID\" : \"70cd29a4-fc30-4e29-8c04-9dfa2c7e4b6f\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000021_account\","+
			    "\"@type\" : \"foaf:OnlineAccount\","+
			    "\"accountName\" : \"Le Cine Beaugrenelle\","+
			    "\"\" : \"http://www.cine-beaugrenelle.paris\""+
			  "}, {"+
			    "\"@id\" : \"http://www.cine-beaugrenelle.paris\","+
			    "\"@type\" : \"foaf:Document\""+
			  "}, {"+
			    "\"@id\" : \"http://www.lerestodu15ieme.fr\","+
			    "\"@type\" : \"foaf:Document\""+
			  "} ],"+
			  "\"@context\" : {"+
			    "\"attendedTrayItemDate\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/attendedTrayItemDate\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#date\""+
			    "},"+
			    "\"attendedTrayItem\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/attendedTrayItem\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"trayItemID\" : \"http://3cixty.eurecom.fr/ontology/profile/trayItemID\","+
			    "\"hasRating\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasRating\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"hasAccount\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasAccount\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"accountName\" : \"http://xmlns.com/foaf/0.1/accountName\","+
			    "\"\" : {"+
			      "\"@id\" : \"http://xmlns.com/foaf/0.1/accountServiceHomepage \","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"ratingValue\" : {"+
			      "\"@id\" : \"http://schema.org/ratingValue\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"knows\" : {"+
			      "\"@id\" : \"http://xmlns.com/foaf/0.1/knows\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"userID\" : \"http://3cixty.eurecom.fr/ontology/profile/userID\","+
			    "\"gender\" : \"http://xmlns.com/foaf/0.1/gender\","+
			    "\"surname\" : \"http://xmlns.com/foaf/0.1/surname\","+
			    "\"age\" : \"http://xmlns.com/foaf/0.1/age\","+
			    "\"givenName\" : \"http://xmlns.com/foaf/0.1/givenName\","+
			    "\"trayElement\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/trayElement\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"familyName\" : \"http://xmlns.com/foaf/0.1/familyName\","+
			    "\"dc\" : \"http://purl.org/dc/elements/1.1/\","+
			    "\"schema\" : \"http://schema.org/\","+
			    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
			    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
			    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
			    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
			    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
			    "\"mbt\" : \"http://3cixty.eurecom.fr/ontology/mobidot/\","+
			    "\"vann\" : \"http://purl.org/vocab/vann/\","+
			    "\"vs\" : \"http://www.w3.org/2003/06/sw-vocab-status/ns#\","+
			    "\"profile\" : \"http://3cixty.eurecom.fr/ontology/profile/\""+
			  "}"+
			"}";
			
			return newProfile;
	}
	
	public static String getUser009() {
		
		String newProfile = 
			"{"+
			  "\"@graph\" : [ {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005\","+
			    "\"@type\" : \"profile:Tray\","+
			    "\"profile:attendedTrayItem\" : \"true\","+
			    "\"attendedTrayItemDate\" : \"2014-09-24\","+
			    "\"hasAccount\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_account\","+
			    "\"hasRating\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_rating\","+
			    "\"trayItemID\" : \"feb1f528-ff85-485b-92c7-98c0fa8d4d49\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_account\","+
			    "\"@type\" : \"foaf:OnlineAccount\","+
			    "\"accountName\" : \"Le resto a mr & Mme Colombo\","+
			    "\"\" : \"http://www.lerestodu15ieme.fr\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000005_rating\","+
			    "\"@type\" : \"schema:Rating\","+
			    "\"schema:ratingValue\" : \"5\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000009\","+
			    "\"@type\" : \"schema:Person\","+
			    "\"trayElement\" : [ \"http://www.3cixty.com#OWLNamedIndividual_000021\", \"http://www.3cixty.com#OWLNamedIndividual_000005\" ],"+
			    "\"userID\" : \"33d38e2d-8b34-4962-bcfb-7cc14e54599a\","+
			    "\"age\" : \"30\","+
			    "\"familyName\" : \"Colombo\","+
			    "\"gender\" : \"female\","+
			    "\"givenName\" : \"La colombo\","+
			    "\"knows\" : [ \"http://www.3cixty.com#OWLNamedIndividual_000011\", \"http://www.3cixty.com#OWLNamedIndividual_000010\", \"http://www.3cixty.com#OWLNamedIndividual_000001\", \"http://www.3cixty.com#OWLNamedIndividual_000012\", \"http://www.3cixty.com#OWLNamedIndividual_000009\" ],"+
			    "\"surname\" : \"Mme Colombo\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000021\","+
			    "\"@type\" : \"profile:Tray\","+
			    "\"profile:attendedTrayItem\" : \"false\","+
			    "\"hasAccount\" : \"http://www.3cixty.com#OWLNamedIndividual_000021_account\","+
			    "\"trayItemID\" : \"70cd29a4-fc30-4e29-8c04-9dfa2c7e4b6f\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000021_account\","+
			    "\"@type\" : \"foaf:OnlineAccount\","+
			    "\"accountName\" : \"Le Cine Beaugrenelle\","+
			    "\"\" : \"http://www.cine-beaugrenelle.paris\""+
			  "}, {"+
			    "\"@id\" : \"http://www.cine-beaugrenelle.paris\","+
			    "\"@type\" : \"foaf:Document\""+
			  "}, {"+
			    "\"@id\" : \"http://www.lerestodu15ieme.fr\","+
			    "\"@type\" : \"foaf:Document\""+
			  "} ],"+
			  "\"@context\" : {"+
			    "\"attendedTrayItemDate\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/attendedTrayItemDate\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#date\""+
			    "},"+
			    "\"attendedTrayItem\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/attendedTrayItem\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"trayItemID\" : \"http://3cixty.eurecom.fr/ontology/profile/trayItemID\","+
			    "\"hasRating\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasRating\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"hasAccount\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasAccount\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"accountName\" : \"http://xmlns.com/foaf/0.1/accountName\","+
			    "\"\" : {"+
			      "\"@id\" : \"http://xmlns.com/foaf/0.1/accountServiceHomepage \","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"ratingValue\" : {"+
			      "\"@id\" : \"http://schema.org/ratingValue\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"givenName\" : \"http://xmlns.com/foaf/0.1/givenName\","+
			    "\"familyName\" : \"http://xmlns.com/foaf/0.1/familyName\","+
			    "\"trayElement\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/trayElement\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"knows\" : {"+
			      "\"@id\" : \"http://xmlns.com/foaf/0.1/knows\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"surname\" : \"http://xmlns.com/foaf/0.1/surname\","+
			    "\"age\" : \"http://xmlns.com/foaf/0.1/age\","+
			    "\"gender\" : \"http://xmlns.com/foaf/0.1/gender\","+
			    "\"userID\" : \"http://3cixty.eurecom.fr/ontology/profile/userID\","+
			    "\"dc\" : \"http://purl.org/dc/elements/1.1/\","+
			    "\"schema\" : \"http://schema.org/\","+
			    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
			    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
			    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
			    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
			    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
			    "\"mbt\" : \"http://3cixty.eurecom.fr/ontology/mobidot/\","+
			    "\"vann\" : \"http://purl.org/vocab/vann/\","+
			    "\"vs\" : \"http://www.w3.org/2003/06/sw-vocab-status/ns#\","+
			    "\"profile\" : \"http://3cixty.eurecom.fr/ontology/profile/\""+
			  "}"+
			"}";
		return newProfile;
	}
	
	public static String getUser008Service2() {
		
		String newProfile =
			"{"+
			  "\"@graph\" : [ {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000008\","+
			    "\"@type\" : \"schema:Person\","+
			    "\"trayElement\" : \"http://www.3cixty.com#OWLNamedIndividual_000045\","+
			    "\"userID\" : \"62fcec64-ebef-4faf-ba97-c58cd99ae107\","+
			    "\"familyName\" : \"Colombo\","+
			    "\"givenName\" : \"La folle\","+
			    "\"surname\" : \"Mme Colombo\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000045\","+
			    "\"@type\" : \"profile:Tray\","+
			    "\"profile:attendedTrayItem\" : \"false\","+
			    "\"hasAccount\" : \"http://www.3cixty.com#OWLNamedIndividual_000045_account\","+
			    "\"hasRating\" : \"http://www.3cixty.com#OWLNamedIndividual_000045_rating\","+
			    "\"trayItemID\" : \"64b7da47-af16-4f30-bbee-4f5282aeb6f3\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000045_account\","+
			    "\"@type\" : \"foaf:OnlineAccount\","+
			    "\"accountName\" : \"Le MacDo Beaugrenelle\","+
			    "\"\" : \"http://www.restaurants.mcdonalds.fr/PARIS-BEAUGRENELLE\""+
			  "}, {"+
			    "\"@id\" : \"http://www.3cixty.com#OWLNamedIndividual_000045_rating\","+
			    "\"@type\" : \"schema:Rating\","+
			    "\"schema:ratingValue\" : \"1\""+
			  "}, {"+
			    "\"@id\" : \"http://www.restaurants.mcdonalds.fr/PARIS-BEAUGRENELLE\","+
			    "\"@type\" : \"foaf:Document\""+
			  "} ],"+
			  "\"@context\" : {"+
			    "\"ratingValue\" : {"+
			      "\"@id\" : \"http://schema.org/ratingValue\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"userID\" : \"http://3cixty.eurecom.fr/ontology/profile/userID\","+
			    "\"familyName\" : \"http://xmlns.com/foaf/0.1/familyName\","+
			    "\"surname\" : \"http://xmlns.com/foaf/0.1/surname\","+
			    "\"givenName\" : \"http://xmlns.com/foaf/0.1/givenName\","+
			    "\"trayElement\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/trayElement\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"accountName\" : \"http://xmlns.com/foaf/0.1/accountName\","+
			    "\"\" : {"+
			      "\"@id\" : \"http://xmlns.com/foaf/0.1/accountServiceHomepage \","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"attendedTrayItem\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/attendedTrayItem\","+
			      "\"@type\" : \"http://www.w3.org/2001/XMLSchema#string\""+
			    "},"+
			    "\"trayItemID\" : \"http://3cixty.eurecom.fr/ontology/profile/trayItemID\","+
			    "\"hasRating\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasRating\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"hasAccount\" : {"+
			      "\"@id\" : \"http://3cixty.eurecom.fr/ontology/profile/hasAccount\","+
			      "\"@type\" : \"@id\""+
			    "},"+
			    "\"dc\" : \"http://purl.org/dc/elements/1.1/\","+
			    "\"schema\" : \"http://schema.org/\","+
			    "\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\","+
			    "\"foaf\" : \"http://xmlns.com/foaf/0.1/\","+
			    "\"owl\" : \"http://www.w3.org/2002/07/owl#\","+
			    "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\","+
			    "\"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","+
			    "\"mbt\" : \"http://3cixty.eurecom.fr/ontology/mobidot/\","+
			    "\"vann\" : \"http://purl.org/vocab/vann/\","+
			    "\"vs\" : \"http://www.w3.org/2003/06/sw-vocab-status/ns#\","+
			    "\"profile\" : \"http://3cixty.eurecom.fr/ontology/profile/\""+
			  "}"+
			"}";		
	
		return newProfile;
	}
}

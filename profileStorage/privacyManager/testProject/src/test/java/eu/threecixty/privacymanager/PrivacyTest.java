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
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyContractStorageFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
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
	
	private String 				userID1 = "62fcec64-ebef-4faf-ba97-c58cd99ae107";
	private String				userID2 = "33d38e2d-8b34-4962-bcfb-7cc14e54599a";
	static private String		appName = "ExploreMi 360";
	static private String		appversion = "1.0";
	
	@Before
	public void setUp() throws Exception {
	
		// the privacy DB
		ProfileStorageConf.setPropertyFile(propertyFilePath);
		PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
		PrivacyDBInitialize.resetAndInit("toto", "toto", "toto", "toto");
		FileInputStream is = new FileInputStream( "src/test/resources/UPC_ExploreMi360.xml" );
		UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
		PrivacyContractStorageFactory.getInstance().store( 	userID1, 
															appName, 
															upc);
		
		ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
		profileManager = profileFactory.getProfileManager( propertyFilePath );				
	}

	@Test
	public void test1_getAllUsersIDsPrivacy() {
		
		logger.info(" ---- getAllUsersIDsPrivacy ----" );
		
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			Set<String> users = profileManager.getAllUsersIDs(session);

			// there's 4 profiles in the DB but only one is returned
			assertEquals(1, users.size());
			assertTrue(users.contains( userID1));

		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}	
	
	@Test
	public void test2_hasProfilePrivacy() {

		logger.info(" ---- hasProfilePrivacy ----" );
		
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678",  null) );
			
			assertTrue( profileManager.hasProfile(session, userID1) );

		} catch (Exception e) {
			fail(e.getMessage());
		}				
	}
	
	@Test
	public void test3_hasNoProfilePrivacy() {

		logger.info(" ---- test3_hasNoProfilePrivacy ----" );
		
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			assertTrue( ! profileManager.hasProfile(session, "toto") );

		} catch (Exception e) {
			fail(e.getMessage());
		}				
	}	
	
	@Test
	public void test4_getUserProfileNoContractPrivacy() {

		logger.info(" ---- getUserProfileNoContractPrivacy ----" );
		
		try {
			
			// the storage
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService("test", "4.0");
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678",  null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1);
			assertTrue( jsonProfile.isEmpty() );
			
			logger.info( " -> getUserProfileNoContractPrivacy OK");
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	
	@Test
	public void test5_getUserProfilePrivacy() {

		logger.info(" ---- getUserProfile privacy ----" );
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion);
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID2, null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1);
			logger.debug(" Found profile :\n" + jsonProfile);
			
			assertTrue( jsonProfile.isEmpty() );
			
			logger.info( " -> getUserProfilePrivacy OK");			
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void test6_getMyUserProfilePrivacy() {

		System.out.println(" ---- getMyUserProfile privacy---");
		
		try {
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			String jsonProfile = profileManager.getProfile(session, userID1);
			logger.debug(jsonProfile);
			System.out.println("jsonProfile = " + jsonProfile );
			assertNotNull(jsonProfile);
			
			assertTrue( ! jsonProfile.contains("gender") );
			assertTrue( ! jsonProfile.contains("givenName") );
			assertTrue( jsonProfile.contains("attendedTrayItemDate") );
			assertTrue( jsonProfile.contains("attendedTrayItem") );
			assertTrue( jsonProfile.contains("\"familyName\" : [ \"Colombo\" ]") );
			assertTrue( ! jsonProfile.contains("\"familyName\" : [ \"Maurel\" ]") );
			assertTrue( jsonProfile.contains("Flo & Vince\" ]") );
			assertTrue( jsonProfile.contains("http://www.lerestodu15ieme.fr") );						
			
			logger.info( " -> getMyUserProfilePrivacy OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void test7_getProfilePropertiesPrivacy() {

		System.out.println(" ---- getProfileProperties with privacy---");
			
		try {
			FileInputStream is = new FileInputStream( "src/test/resources/UPC2_ExploreMi360.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			PrivacyContractStorageFactory.getInstance().store( 	userID2, 
																appName, 
																upc);

			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, userID1, null) );
			
			Collection<String> propertyPaths = new ArrayList<String>();
			String propPath1 = "profile:trayElement[profile:hasRating / schema:ratingValue > \"2\"]/profile:hasAccount /foaf:accountName";
			propertyPaths.add( propPath1);
			String propPath2 = "foaf:surname";
			propertyPaths.add( propPath2);
						
			
			Collection<ValuedProperty> res = profileManager.getProfileProperties(session, 
																	userID2, 
																	propertyPaths);	

			System.out.println("res = " + res );
			
			/*for ( ValuedProperty val : res ) {
				
				String pp = val.getPropertyPath() ;
				assertTrue( val.getNbValues() == 1 );
				if ( pp.compareTo(propPath1) == 0 ) {
					//assertTrue( val.getValue(0).compareTo("http://www.3cixty.com#OWLNamedIndividual_000005") == 0 );
					assertTrue( val.getValue(0).contains( "http://www.3cixty.com#OWLNamedIndividual_000005") );
					assertTrue( val.getValue(0).contains( "http://www.3cixty.com#OWLNamedIndividual_000019") );
					assertTrue( val.getValue(0).contains( "http://www.3cixty.com#OWLNamedIndividual_000017") );
					assertTrue( val.getValue(0).contains( "feb1f528-ff85-485b-92c7-98c0fa8d4d49") );
					assertTrue( val.getValue(0).contains( "Le resto à Flo" ) );
					assertTrue( val.getValue(0).contains( "http://www.lerestodu15ieme.fr" ) );
				}
				else if ( pp.compareTo(propPath2) == 0 ) {
					assertTrue( val.getValue(0).compareTo("Mme Colombo") == 0 );
				}
				else 
					fail( " ??");
			}*/
			logger.info( " -> getProfileProperties OK");
		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
}

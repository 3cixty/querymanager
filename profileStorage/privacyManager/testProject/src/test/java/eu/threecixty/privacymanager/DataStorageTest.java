package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.conf.ProfileStorageConf;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.privacy.PrivacyContractFactory;
import org.theresis.humanization.privacy.PrivacyContractStorageFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;
import org.theresis.humanization.privacy.generated.UserPrivacyContract;
import org.theresis.humanization.profilestore.ThreeCixtyFactory;

@RunWith(JUnit4.class)
public class DataStorageTest {

	private ProfileManager profileManager;
	static public String 		propertyFilePath = "src/test/resources/TestProfileStorage.properties";
	static public String 		privacyPropertyFilePath = "src/test/resources/TestPrivacyAuthority.properties";
	static private String		appName = "Test";
	static private String		appversion = "1.0";
	static private String		userID1  = "110248277616794929135";
	static private String		userID2  = "100900047095598983805";

	@Before
	public void setUp() throws Exception {
	
		System.out.println( " -------- setUp -------- ");
		
		// the privacy DB
		ProfileStorageConf.setPropertyFile(propertyFilePath);
		PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
		PrivacyDBInitialize.resetAndInit("toto", "toto", "toto", "toto");
		FileInputStream is = new FileInputStream( "src/test/resources/UPC_TestApp.xml" );
		UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
		System.out.println("-- coucou1");
		PrivacyContractStorageFactory.getInstance().store( 	userID1, 
															appName, 
															upc);
		System.out.println("-- coucou2");
		PrivacyContractStorageFactory.getInstance().store( 	userID2, 
															appName, 
															upc);
		System.out.println("-- coucou3");
		ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
		System.out.println("-- coucou4");
		
		profileManager = profileFactory.getProfileManager( propertyFilePath );		
		System.out.println( " -------- end setUp -------- ");
	}

	@Test
	public void getAllUsersIDs() {

	
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion);
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, null) );

			Set<String> users = profileManager.getAllUsersIDs(session);

			assertEquals(2, users.size());
			assertTrue(users.contains( userID1 ));
			assertTrue(users.contains( userID2 ));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getUnexistingProfile() {

		try {

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			String rawProfile = profileManager.getProfile(session, "dummy" );
			assertTrue( rawProfile.isEmpty() );

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getExistingProfile() {

		try {

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			String profileResult = profileManager.getProfile(session, userID1 );
			assertNotNull(profileResult);

			System.out.println(profileResult); // only a visual confirmation
												// printed on output

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void getUserProfile() {
		
		System.out.println("--------- getUserProfile ---------");
		try {
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			String jsonProfile = profileManager.getProfile( session, userID1 );		
			System.out.println(jsonProfile);
			assertNotNull(jsonProfile);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
		System.out.println("--------- end  getUserProfile ---------");
			}
	
	@Test
	public void createUserProfile() {
		
		try {
			String newUserID = "2037381987";
			
			// create a user profile in the model
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#2037381987\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Male\","+
					  "\"hasHobbies\" : \"Basket\","+
					  "\"hasLanguage\" : \"http://www.eu.3cixty.org/profile#langUS\","+
					  "\"hasTransport\" : \"http://www.eu.3cixty.org/profile#transp1\","+
					  "\"hasUID\" : \"2037381987\","+
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
					    "}"+
					  "}"+
					"}";			

			// declare a contract
			FileInputStream is = new FileInputStream( "src/test/resources/UPC_TestApp.xml" );
			UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
			PrivacyContractStorageFactory.getInstance().store( 	newUserID, 
																appName, 
																upc);
			
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			profileManager.mergeProfile(session, newUserID, newSerialProfile);

			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, newUserID);
			assertNotNull(jsonProfile);
			System.out.println("Created Profile is : " + jsonProfile);

			// first check the users already registered
			Set<String> users = profileManager.getAllUsersIDs(session);
			assertEquals(3, users.size());
			assertTrue(users.contains(newUserID));
			System.out.println("Registred users = " + users);
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void mergeUserProfile() {
		
		try {
			// the modified user
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					  "\"hasUID\" : \"100900047095598983805\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\""+
					  "}"+
					"}";		
			
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );

			profileManager.mergeProfile(session, userID2, newSerialProfile);

			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, userID2);
			assertNotNull(jsonProfile);
			System.out.println("Merged Profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
	}
	
	@Test
	public void replaceUserProfile() {
		
		try {
			// the modified user
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Cooking\", \"Music\" ],"+
					  "\"hasUID\" : \"100900047095598983805\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\""+
					  "}"+
					"}";
		
			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678",  null) );

			profileManager.replaceProfile(session, userID2, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileManager.getProfile(session, userID2);		
			assertNotNull(jsonProfile);
			System.out.println("Result of replace profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
	}
	
	@Test
	public void deleteUserProfile() {
		
		try {

			// build the session
			ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
			Service service = profileFactory.getService( appName, appversion );
			Session session = profileFactory.getSession( profileFactory.getAuthenticator(service, "U2678", null) );
			
			String result = profileManager.getProfile(session, userID1);
			assertNotNull(result);

			boolean res = profileManager.deleteProfile(session, userID1);
			assertTrue(res);

			assertFalse(profileManager.hasProfile(session, userID1));
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
	}	
}

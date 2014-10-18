package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.authen.simple.SimpleSessionManager;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;

@RunWith(JUnit4.class)
public class DataStorageTest {

	private ProfileManager profileManager;
	private Session session = null;
	static public String 	propertyFilePath = null;

	@Before
	public void setUp() throws Exception {
	
		SimpleProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();
		profileManager = profileFactory.getProfileManager( propertyFilePath );
		Service service = profileFactory.getService("test", "pwdTest");
		session = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
	}

	@Test
	public void getAllUsersIDs() {

	
		try {
			Set<String> users = profileManager.getAllUsersIDs(session);

			assertEquals(2, users.size());
			assertTrue(users.contains("110248277616794929135"));
			assertTrue(users.contains("100900047095598983805"));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getUnexistingProfile() {

		try {

			String rawProfile = profileManager.getProfile(session, "dummy" );
			assertEquals(null, rawProfile);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getExistingProfile() {

		try {

			String profileResult = profileManager.getProfile(session, "110248277616794929135" );
			assertNotNull(profileResult);

			System.out.println(profileResult); // only a visual confirmation
												// printed on output

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void getUserProfile() {
		
		try {
			String readUserID = "110248277616794929135";
			
			String jsonProfile = profileManager.getProfile( session, readUserID );		
			System.out.println(jsonProfile);
			assertNotNull(jsonProfile);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
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
			String mergedUserID = "100900047095598983805";
			
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
			
			profileManager.mergeProfile(session, mergedUserID, newSerialProfile);

			// Now check the creation
			String jsonProfile = profileManager.getProfile(session, mergedUserID);
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
			String replUserId = "100900047095598983805";
			
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
		
			profileManager.replaceProfile(session, replUserId, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileManager.getProfile(session, replUserId);		
			assertNotNull(jsonProfile);
			System.out.println("Result of replace profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
	}
	
	@Test
	public void deleteUserProfile() {
		
		try {
			String userID = "110248277616794929135";

			String result = profileManager.getProfile(session, userID);
			assertNotNull(result);

			boolean res = profileManager.deleteProfile(session, userID);
			assertTrue(res);

			assertFalse(profileManager.hasProfile(session, userID));
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}
	}	
}

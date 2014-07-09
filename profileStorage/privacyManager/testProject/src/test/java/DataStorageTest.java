

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;

import com.thalesgroup.theresis.perso.authen.impl.simple.SimpleSessionManager;
import com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleProfileManagerFactory;

import eu.threecixty.privacy.authen.Service;
import eu.threecixty.privacy.authen.Session;
import eu.threecixty.privacy.datastorage.ProfileManager;
import eu.threecixty.privacy.datastorage.ProfileManager.ProfileStatus;
import eu.threecixty.privacy.datastorage.ProfileManagerFactory;

@RunWith(JUnit4.class)
public class DataStorageTest {

	static public String 	propertyFilePath = null;
	
	
	@Test
	public void getAllUsersIDs() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
			
			Set<String> userIDS =  profileMgr.getAllUsersIDs(currentSession);		
			
			assertEquals(2, userIDS.size() );
			
			assertTrue( userIDS.contains( "110248277616794929135" ) );
			assertTrue( userIDS.contains( "100900047095598983805" ) );
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void getUnknowProfileStatus() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
			
			ProfileManager.ProfileStatus status =  profileMgr.getProfileStatus(currentSession, "1");		
			
			assertEquals(ProfileManager.ProfileStatus.NONE, status);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void getActiveProfileStatus() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
			
			ProfileManager.ProfileStatus status =  profileMgr.getProfileStatus(currentSession, "110248277616794929135");		
			
			assertEquals(ProfileManager.ProfileStatus.ACTIVE, status);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void getUserProfile() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
				
			String jsonProfile =  profileMgr.getProfile(currentSession, "110248277616794929135");		
			System.out.println(jsonProfile);
			assertNotNull(jsonProfile);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void createUserProfile() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
			
			// the new user
			String userID = "2037381987";
			
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
					
			profileMgr.mergeProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Created Profile is : " + jsonProfile);
			
			// first check the users already registered
			Set<String> userIDS =  profileMgr.getAllUsersIDs(currentSession);					
			assertEquals(3, userIDS.size() );
			System.out.println("Registred users = " + userIDS );
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	@Test
	public void mergeUserProfile() {
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
				
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  //"\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
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
				
			profileMgr.mergeProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Created Profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void replaceUserProfile() {
		
		try {
		ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
				
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  //"\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
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
			
			System.out.println(" flore newSerialProfile = " + newSerialProfile);
		
			profileMgr.replaceProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Created Profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void deleteUserProfile() {
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "toto");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "service1", "toto", null) );
			
			String userID = "110248277616794929135";
			boolean res =  profileMgr.deleteProfile(currentSession, userID);		
			assertTrue(res);
			
			ProfileStatus status = profileMgr.getProfileStatus(currentSession, userID);
			assertEquals(ProfileManager.ProfileStatus.NONE, status);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
}

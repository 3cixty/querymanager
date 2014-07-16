package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.authen.simple.SimpleSessionManager;
import org.theresis.humanization.datastorage.ProfileException;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ProfileManagerFactory;
import org.theresis.humanization.datastorage.ProfileManager.ProfileStatus;
import org.theresis.humanization.profilestore.ProfileStorageConf;
import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

@RunWith(JUnit4.class)
public class DataStorageTest {

	static public String 	propertyFilePath = null;
	
	
	@Test
	public void getAllUsersIDs() {
		
		System.out.println(" ---- getAllUsersIDs ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
			
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
		
		System.out.println(" ---- getUnknowProfileStatus ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
			
			ProfileManager.ProfileStatus status =  profileMgr.getProfileStatus(currentSession, "1");		
			
			assertEquals(ProfileManager.ProfileStatus.NONE, status);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void getActiveProfileStatus() {
		
		System.out.println(" ---- getActiveProfileStatus ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
			
			ProfileManager.ProfileStatus status =  profileMgr.getProfileStatus(currentSession, "110248277616794929135");		
			
			assertEquals(ProfileManager.ProfileStatus.ACTIVE, status);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void getUserProfile() {
		
		System.out.println(" ---- getUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
				
			String jsonProfile =  profileMgr.getProfile(currentSession, "110248277616794929135");		
			System.out.println(jsonProfile);
			assertNotNull(jsonProfile);
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}		
	}
	
	@Test
	public void createUserProfile() {
		
		System.out.println(" ---- createUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
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
			
			// first check the users already registred
			Set<String> userIDS =  profileMgr.getAllUsersIDs(currentSession);					
			assertEquals(3, userIDS.size() );
			System.out.println("Registred users = " + userIDS );
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	@Test
	public void createUserProfileWithNoUserIDProperty() {
		
		System.out.println(" ---- createUserProfileWithNoUserIDProperty ----");

		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
				
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
			
			// first check the users already registred
			Set<String> userIDS =  profileMgr.getAllUsersIDs(currentSession);					
			assertEquals(3, userIDS.size() );
			System.out.println("Registred users = " + userIDS );
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}	
	
	@Test
	public void createNotConsistentUserProfile() {
		
		System.out.println(" ---- createNotConsistentUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
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
					  "\"hasUID\" : \"4645868\","+
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
			
		} catch (Exception e) {
			
			// Normal exception
			assertTrue( e instanceof ProfileException );
		}			
	}	
	@Test
	public void mergeUserProfile() {
		
		System.out.println(" ---- mergeUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
						
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
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
			
			profileMgr.mergeProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Merged Profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void mergeUserProfileWithNoUserID() {
		
		System.out.println(" ---- mergeUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
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
			System.out.println("Merged Profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void mergeNotConsistentUserProfile() {
		
		System.out.println(" ---- mergeUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
						
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Piano\", \"Gardening\" ],"+
					  "\"hasUID\" : \"97E873946\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\""+
					  "}"+
					"}";		
			
			System.out.println("merged newSerialProfile = " + newSerialProfile);
		
			profileMgr.mergeProfile(currentSession, userID, newSerialProfile);					
			
		} catch (Exception e) {
			// Normal exception
			assertTrue( e instanceof ProfileException );
		}			
	}	
	@Test
	public void replaceUserProfile() {
		
		System.out.println(" ---- replaceUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
			// the modified user
			String userID = "100900047095598983805";
			
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
		
			profileMgr.replaceProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Result of replace profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void replaceUserProfileWithNoUserID() {
		
		System.out.println(" ---- replaceUserProfileWithNoUserID ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Cooking\", \"Music\" ],"+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\""+
					  "}"+
					"}";		
			profileMgr.replaceProfile(currentSession, userID, newSerialProfile);		
			
			// Now check the creation
			String jsonProfile =  profileMgr.getProfile(currentSession, userID);		
			assertNotNull(jsonProfile);
			System.out.println("Result of replace profile is : " + jsonProfile);						
			
		} catch (Exception e) {
			fail( e.getMessage() );
		}			
	}
	
	@Test
	public void replaceNotConsistentUserProfile() {
		
		System.out.println(" ---- replaceNotConsistentUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
			// the modified user
			String userID = "100900047095598983805";
			
			// Modify the user profile of user ID to change some properties
			String newSerialProfile = "{"+
					  "\"@id\" : \"http://www.eu.3cixty.org/profile#100900047095598983805\","+
					  "\"@type\" : \"http://www.eu.3cixty.org/profile#UserProfile\","+
					  "\"hasGender\" : \"Female\","+
					  "\"hasHobbies\" : [ \"Cooking\", \"Music\" ],"+
					  "\"hasUID\" : \"875230\","+
					  "\"@context\" : {"+
					    "\"hasUID\" : \"http://www.eu.3cixty.org/profile#hasUID\","+
					    "\"hasGender\" : \"http://www.eu.3cixty.org/profile#hasGender\","+
					    "\"hasHobbies\" : \"http://www.eu.3cixty.org/profile#hasHobbies\""+
					  "}"+
					"}";			
			
			System.out.println(" Replacement Serialized Profile = " + newSerialProfile);
		
			profileMgr.replaceProfile(currentSession, userID, newSerialProfile);		
											
		} catch (Exception e) {
			
			// Normal exception
			assertTrue( e instanceof ProfileException );
			System.out.println( e.getMessage() );
		}			
	}	
	@Test
	public void deleteUserProfile() {
		
		System.out.println(" ---- deleteUserProfile ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
					
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

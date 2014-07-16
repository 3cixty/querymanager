package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.theresis.humanization.authen.Service;
import org.theresis.humanization.authen.Session;
import org.theresis.humanization.authen.simple.SimpleSessionManager;
import org.theresis.humanization.datastorage.ProfileManager;
import org.theresis.humanization.datastorage.ProfileManagerFactory;
import org.theresis.humanization.datastorage.ValuedProperty;
import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;

@RunWith(JUnit4.class)
public class PropertyPathTest {

	static public String 	propertyFilePath = null;
	
	
	@Test
	public void getPropertyPath() {
		
		System.out.println(" ---- getPropertyPath ----");
		
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance(); 
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyFilePath );
			Service service = profileFactory.getService("test", "pwdTest");
			Session currentSession = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator(service, "U2678", "pwd", null) );
				
			
			String userID = "110248277616794929135";
			System.out.println( profileMgr.getProfile(currentSession, userID) );
			
			String hasGenderPath = ":hasGender";
			String hasPlacePath = ":hasPreference/:hasPlacePreference/:hasPlaceDetailPreference/:hasNatureOfPlace";
			String knowsPath = "foaf:knows/:hasProfileIdentities*/:hasUserAccountID";
			String ratingPath = ":hasPreference/:hasUserPlaceRating*";
			List<String> propertyPaths = new ArrayList<String>();
			propertyPaths.add( hasGenderPath );
			propertyPaths.add( hasPlacePath );
			propertyPaths.add( knowsPath );
			propertyPaths.add( ratingPath );
			
			Collection<ValuedProperty> propertyValues =  profileMgr.getProfileProperties(	currentSession, 
																							userID, 
																							propertyPaths);		
						
			
			System.out.println(" results : ");
			
			for ( ValuedProperty val : propertyValues ) {
				
				System.out.println( " - " + val );
				if ( val.getPropertyPath().compareTo( hasGenderPath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "Male", val.getValue(0) );				
				}
				else if ( val.getPropertyPath().compareTo( hasPlacePath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "Country", val.getValue(0) );									
				}
				else if ( val.getPropertyPath().compareTo( knowsPath) == 0 ) {
					assertTrue( val.getNbValues() == 2 );
					assertEquals( "junk2", val.getValue(0) );									
					assertEquals( "100900047095598983805", val.getValue(1) );									
				}					
				else if ( val.getPropertyPath().compareTo( ratingPath) == 0 ) {
					assertTrue( val.getNbValues() == 1 );
					assertEquals( "http://www.eu.3cixty.org/profile#110248277616794929135Preference", val.getValue(0) );									
				}						
				else {
					fail( "Not expected property "  + val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail( e.getMessage() );
		}		
	}
}

import java.util.Set;

import com.thalesgroup.theresis.perso.authen.impl.simple.SimpleSessionManager;
import com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleProfileManagerFactory;

import eu.threecixty.privacy.authen.Authenticator;
import eu.threecixty.privacy.authen.Service;
import eu.threecixty.privacy.authen.Session;
import eu.threecixty.privacy.datastorage.ProfileManager;
import eu.threecixty.privacy.datastorage.ProfileManagerFactory;

/**
 * @file	TestAlain.java
 * @brief 
 * @date	Jul 3, 2014
 * @author	T0125851
 * @copyright THALES 2014. All rights reserved.
 * THALES PROPRIETARY/CONFIDENTIAL.
 */

/**
 * @author T0125851
 *
 */
public class ReadMeTest {

	public static void main(String[] args) {
	
		try {
			ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();

			String propertyPath = "C:/3cixty/config/3CixtyProfileStorage.properties";
			ProfileManager profileMgr = profileFactory.getProfileManager( propertyPath );
	
			// Get a reference on the dataminer service
			Service service = profileFactory.getService( "http://3cixty/dataminer",
			    "kACAH-1Ng1MImB85QDSJQSxhqbAA7acjdY9pTD9M" );
	
			// Get an authentication token
			Authenticator auth = profileFactory.getAuthenticator(service,
			    "root",
			    "admin",
			    null ); // no additional security/protocol option
	
			// Open a session for the dataminer.
			// In this particular case, the requesting user is the system and not an end-user thus the need for
			// the user 'root'.
			Session session = SimpleSessionManager.getInstance().getSession( auth );
	
			// Get the list of users
			Set<String> userIDS = profileMgr.getAllUsersIDs( session );
			System.out.println( "userIDs=" + userIDS );
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
}

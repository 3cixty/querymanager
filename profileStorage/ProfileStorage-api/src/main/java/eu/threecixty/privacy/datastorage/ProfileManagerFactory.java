/**
 * @file	ProfileManagerFactory.java
 * @brief 	API of the ProfileManager factory
 * @date	
 * @author	Flore Lantheaume
 * @author	Alain Crouzet
 * @copyright	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
 */
package eu.threecixty.privacy.datastorage;

import java.util.Properties;

import eu.threecixty.privacy.authen.Authenticator;
import eu.threecixty.privacy.authen.Service;

/**
 * <pre>
 * try {
 * // PlainCredentialProfileFactory is a dummy factory where authentication is based on plain text credentials.
 * ProfileManagerFactory factory = new PlainCredentialProfileFactory();
 * ProfileManager mgr = factory.getProfileManager("/etc/profilemanager.cfg");
 * 
 * // To get a session we need an authenticator
 * Service service = factory.getService(serviceID, serviceAuth);
 * 
 * // Get the authenticator token
 * Properties protocolExtra = new Properties();
 * // Add here required extras for authentication protocol if necessary
 * Authenticator auth = factory.getAuthenticator(service, "bob", "bob-is-marley", protocolExtra);
 * 
 * // Now open the session with the authenticator
 * Session session = mgr.getSession(auth);
 * if (session != null) {
 *   // The session is now opened.
 *   mgr.getProfileStatus(session, user)
 * } else {
 *   // Authentication failure.
 * }
 * } catch (SecurityException security) {
 * } catch (Exception e) {
 * }
 * </pre>
 */
public interface ProfileManagerFactory {

	/**
	 * Create a ProfileManager using a specified configuration or get an
	 * existing ProfileManager.
	 * 
	 * @param confFilePath
	 *            the path of the configuration file for the ProfileManager to
	 *            get
	 * @return a valid ProfileManager
	 */
	public abstract ProfileManager getProfileManager(String confFilePath)
			throws Exception;

	/**
	 * Identify and authenticate a service.
	 * 
	 * @param serviceID
	 *            the identifier of the service
	 * @param serviceAuth
	 *            the authentication information for the service
	 * @return if the service is authenticated, this method returns a Service
	 *         token to be passed to
	 *         {@link #getAuthenticator(Service, String, String, Properties)}
	 * @throws SecurityException
	 *             if the service cannot be identified or authenticated.
	 */
	public abstract Service getService(String serviceID, String serviceAuth)
			throws SecurityException;

	/**
	 * Obtain an {@link Authenticator} token in order to establish a Session for
	 * a user and requester service.
	 * 
	 * @param service
	 *            an authenticated service, which should have been obtained with
	 *            {@link #getService(String, String)} beforehand.
	 * @param user
	 *            the user identifier. Pass an empty string for anonymous user.
	 *            Null is not permitted.
	 * @param auth
	 *            the authentication information for the user. Pass an empty
	 *            string for an anonymous user. Null is not permitted.
	 * @param protocolOptions
	 *            optional information for management of the authentication.
	 *            This parameter can be null.
	 * @return if the user is authenticated or is anonymous, this method returns
	 *         an Authenticator holding the information about the authenticated
	 *         user and service that can be passed to
	 *         {@link #getAuthenticator(Service, String, String, Properties)} to
	 *         open a session for this user and service.
	 * @throws SecurityException
	 *             if the user cannot be identified or authenticated.
	 */
	public abstract Authenticator getAuthenticator(Service service,
			String user, String auth, Properties protocolOptions)
			throws SecurityException;
}

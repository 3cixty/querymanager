/**
 * @file	Session.java
 * @brief 	Interface of a session
 * @date	
 * @author	Flore Lantheaume
 * @author	Alain Crouzet
 * @copyright	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
 */
package eu.threecixty.privacy.authen;

import java.util.Properties;

/**
 * Session information that must be passed to methods of {@link ProfileManager}
 * Instance of this interfaces should be obtained with
 * {@link ProfileManager#getSession(Authenticator)}
 */
public interface Session {

	/**
	 * @return the identifier of the authenticated user. The value will be an
	 *         empty string if the user is anonymous. The returned value is
	 *         never null.
	 */
	String getUserID();

	/**
	 * @return the authenticated requester service. This is never null.
	 */
	Service getService();

	/**
	 * @return the protocol options that can be null.
	 */
	Properties getProtocolOptions();
}

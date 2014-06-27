/**
 * @file	Authenticator.java
 * @brief 	API of the ProfileManager
 * @date	
 * @author	Flore Lantheaume
 * @author	Alain Crouzet
 * @copyright	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
 */
package eu.threecixty.privacy.authen;

import java.util.Properties;

/**
 * Authenticator token used to get open a {@link Session}
 */
public interface Authenticator {

	/**
	 * @return the unique identifier of the user. This user identified may not
	 *         be authenticated.
	 */
	String getUserID();

	/**
	 * <b>This is function is not implemented yet and returns null.</b>
	 * 
	 * @return the specification of the requester service. This object should
	 *         enable the identification and authentication of the service.
	 */
	Service getRequesterService();

	/**
	 * <b>This is function is not implemented yet and returns null.</b>
	 * 
	 * @return the optional arguments for the authentication protocol and
	 *         session management.
	 */
	Properties getProtocolExtra();
}

/**
 * @file	Service.java
 * @brief 	Definition of an authenticated service
 * @date	
 * @author	Flore Lantheaume
 * @author	Alain Crouzet
 * @copyright	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
 */
package eu.threecixty.privacy.authen;

import java.util.Properties;

/**
 * Authenticated service information.
 */
public interface Service {

	/**
	 * @return the unique identifier of the service which has been
	 *         authenticated.
	 */
	String getServiceID();

	/**
	 * @return implementation specific additional information about the service.
	 */
	Properties getExtraDescription();
}

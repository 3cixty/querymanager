/**
 * @file	ProfileManager.java
 * @brief 	API of the ProfileManager
 * @date	
 * @author	Flore Lantheaume
 * @author	Alain Crouzet
 * @copyright	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
 */
package eu.threecixty.privacy.datastorage;

import java.util.Collection;
import java.util.Set;

import eu.threecixty.privacy.authen.Authenticator;
import eu.threecixty.privacy.authen.Session;


/**
 * Public interface of the system managing controlled access to users profiles.
 */
public interface ProfileManager {

	/** 
	 * Status of the profile
	 */
	public enum ProfileStatus {
		NONE,				// No profile for the requested user 
		ACTIVATION_PENDING,	// User has a profile, but not activated yet
		ACTIVE				// User has an active profile (default status)
	};
	
/* USERS IDs GETTERS */
	
	/**
	 * Get the IDs of all the users stored in the database if the requester has
	 * the access rights to those data.
	 * 
	 * @param session
	 *            The current session of the requester.
	 * @return A set of unique user ID. In case of denied access, the set will
	 *         be empty
	 */
	Set<String> getAllUsersIDs(Session session);
	

/* PROFILE GETTERS */
	
	/**
	 * Get a session that must be passed for each operation on a user profile
	 * This session usually authenticates the user and calling service.
	 * 
	 * @param auth
	 *            Authentication token
	 * @return A session to pass to other functions of the manager
	 * @throws ProfileException
	 *             In case of authentication failure
	 */
	Session getSession(Authenticator auth) throws ProfileException;

	/**
	 * Get the current status of the profile of a user
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @return The current status
	 * @throws ProfileException
	 *             In case of session issue
	 */
	ProfileStatus getProfileStatus(Session session, String userID)
			throws ProfileException;

	/**
	 * Get the whole profile of a user, filtered according to the rights and
	 * domain of the requester ( this profile could be incomplete because some
	 * data are not accessible)
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @return A string that contains the serialization of the profile. The
	 *         serialization format is JSON-LD.
	 * @throws ProfileException
	 *             In case of session issue
	 */
	String getProfile(Session session, String userID) throws ProfileException;

	/**
	 * Get a user profile with only some preferences, filtered according to the
	 * rights and domain of the requester
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @param propertyPaths
	 *            A list of SPARQL1.1 Property Path (path to a element of the
	 *            ontology profile)
	 * @return A string that contains the serialization of the profile. The
	 *         serialization format is JSON-LD.
	 * @throws ProfileException
	 *             if a property path has not the appropriate format
	 * @throws ProfileException
	 *             In case of session issue
	 */
	String getProfileProperties(Session session, String userID,
			Collection<String> propertyPaths) throws ProfileException;

/* PROFILE SETTERS : MERGE AND REPLACE */
	/**
	 * Some rules on how to use merge or replace on profile data
	 * 				|  functional property  |  not-functional property  |
	 * ------------------------------------------------------------------
	 * REPLACE		|       Overwrite       |          Overwrite        |
	 * ------------------------------------------------------------------
	 * MERGE		|       Overwrite       |          Append           |
	 */
	
	/**
	 * Ask for the merge between a current user profile stored in the database
	 * and a user profile specified in its JSON-LD serialization. If the user
	 * profile does not exist in the database, it will be created. This method
	 * could be used to simply create a user profile in the database.
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @param JSONLDProfile
	 *            The user profile to be merged with the stored profile
	 *            Obviously, those operations are submitted to sufficient access
	 *            rights
	 * @throws ProfileException
	 *             In case of session issue
	 * @throws ProfileException
	 *             if the provided profile has not the appropriate format
	 */
	void mergeProfile(	Session 					session, 
						String 						userID,
						String						JSONLDProfile) throws ProfileException;

	/**
	 * Ask for the merge between a current user profile stored in the database
	 * and a set of valued properties. If the user profile does not exist in the
	 * database, it will be created. This method could be used to simply create
	 * a user profile in the database.
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @param pairsPropertyPathValues
	 *            A set of pairs of
	 *            <code>&lt;PropertyPath&gt;/&lt;new Property Values&gt;</code>
	 *            <ol>
	 *            	<li>If the aimed property is functional(only one value per
	 *            	instance):
	 *            		<ul>
	 *            		<li>the values list must contain only one value</li>
	 *            		<li>the property value will be replaced by the value specified
	 *            		in the list</li>
	 *            		</ul>
	 *            	</li>
	 *            	<li>If the aimed property is not-functional (multiple values )
	 *            		<ul>
	 *            			<li>a new property will be added for all values in the list</li>
	 *            			</ul>
	 *            	</li>
	 *            </ol>
	 *            Obviously, those operations are submitted to sufficient access
	 *            rights
	 * @throws ProfileException
	 *             In case of session issue
	 * @throws ProfileException
	 *             if property paths have not the appropriate format
	 */
	void mergeProfileProperties(Session 					session, 
								String 						userID,
								Collection<ValuedProperty>	pairsPropertyPathValues)
			throws ProfileException;
	
	/**
	 * Ask for the replacement of some properties values in the current user
	 * profile stored in the database by others properties. The user profile
	 * must exist in the database.
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @param pairsPropertyPathValues
	 *            Map that contains pairs of
	 *            <code>&lt;PropertyPath&gt;/&lt;new Property Values&gt;</code>
	 *            <ol>
	 *            <li>If the aimed property is functional(only one value per
	 *            instance):
	 *            <ul>
	 *            <li>if the value list is empty => the property will be removed
	 *            </li>
	 *            <li>if the list contains one value => the property value will
	 *            be replaced by the new value</li>
	 *            <li>if the list contains more than one value => the
	 *            modification is cancelled</li>
	 *            </ul>
	 *            </li>
	 *            <li>If the aimed property is not-functional (multiple values )
	 *            <ul>
	 *            <li>if the value list is empty => all property values will be
	 *            removed</li>
	 *            <li>otherwise => all previous values will be removed and one
	 *            property value will be added for each new value</li>
	 *            </ul>
	 *            </li>
	 *            </ol>
	 *            Obviously, those operations are submitted to sufficient access
	 *            rights
	 * @throws ProfileException
	 *             In case of session issue
	 * @throws ProfileException
	 *             if property paths have not the appropriate format
	 */	
	void replaceProfileProperties(	Session 					session, 
									String 						userID, 
									Collection<ValuedProperty> 	pairsPropertyPathValues) throws ProfileException;


			
/* PROFILE DELETION */
	
	/**
	 * Ask for the deletion of the profile of a user
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @return true if the profile has been deleted, otherwise false (for
	 *         example if the requester has no sufficient rights to delete it)
	 * @throws ProfileException
	 *             In case of session issue
	 */
	boolean deleteProfile(Session session, String userID) throws ProfileException;

	/**
	 * Ask for the deletion of some properties of the profile of a user The
	 * properties values will be deleted according to the requester rights.
	 * 
	 * @param session
	 *            The current session of the requester
	 * @param userID
	 *            ID of the user in the database
	 * @param pairsPropertyPathValues
	 *            Map that contains pairs of PropertyPath/Values
	 *            <ol>
	 *            <li>If the aimed property is functional(only one value per
	 *            instance): 
	 *            	<ul><li>the values list will not be used : the property
	 *            			is automatically deleted</li></ul>
	 *            <li>If the aimed property is not-functional (multiple values )
	 *            	<ul><li>if the values list is null => all properties will be deleted</li>
	 *            		<li>if the values list is not empty => only the specified values will be removed</li>
	 *            	</ul>
	 *            </li>
	 *            </ol> 
	 *            Obviously, those deletions are submitted to sufficient access rights
	 * @throws ProfileException
	 *             In case of session issue
	 * @throws ProfileException 
	 *             If property paths have not the appropriate format
	 */
	void deleteProfileProperties(	Session 					session, 
									String 						userID, 
									Collection<ValuedProperty> 	pairsPropertyPathValues) throws ProfileException;

	
}

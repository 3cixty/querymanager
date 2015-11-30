package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.Tray.OrderType;

/**
 * This interface to manipulate with Tray items.
 */
public interface TrayManager {

	/**
	 * Adds a given tray element to the KB.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean addTray(Tray tray) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Deletes a given tray element out of the KB.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean deleteTray(Tray tray) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Replaces a given junk token with a given real Google UID.
	 * @param junkID
	 * @param uid
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean replaceUID(String junkID, String uid) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Updates a given tray element.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean updateTray(Tray tray) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Gets a tray element from a given UID and trayID.
	 * @param uid
	 * @param trayId
	 * @return
	 * @throws InvalidTrayElement
	 */
	Tray getTray(String uid, String trayId) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Gets a list of tray elements.
	 * @param uid
	 * @param offset
	 * @param limit
	 * @param orderType
	 * @param eventsPast
	 * @return
	 * @throws InvalidTrayElement
	 */
	List <Tray> getTrays(String uid, int offset, int limit,
			OrderType orderType, boolean eventsPast) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Cleans all tray elements with a given token.
	 * @param token
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean cleanTrays(String token) throws InvalidTrayElement, TooManyConnections;
	
	/**
	 * Gets a list of tray elements.
	 * @param uid
	 * @return
	 * @throws InvalidTrayElement
	 */
	List <Tray> getTrays(String uid) throws InvalidTrayElement, TooManyConnections;

	/**
	 * Gets all trays in the KB.
	 * @return
	 */
	List <Tray> getAllTrays() throws TooManyConnections;
}

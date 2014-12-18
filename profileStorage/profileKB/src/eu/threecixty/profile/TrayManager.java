package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.Tray.OrderType;

/**
 * This interface to switch between a simple text file and Virtuoso for Tray.
 */
public interface TrayManager {

	/**
	 * Adds a given tray element to the KB.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean addTray(Tray tray) throws InvalidTrayElement;
	
	/**
	 * Deletes a given tray element out of the KB.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean deleteTray(Tray tray) throws InvalidTrayElement;
	
	/**
	 * Replaces a given junk token with a given real Google UID.
	 * @param junkID
	 * @param uid
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean replaceUID(String junkID, String uid) throws InvalidTrayElement;
	
	/**
	 * Updates a given tray element.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean updateTray(Tray tray) throws InvalidTrayElement;
	
	/**
	 * Gets a tray element from a given UID and trayID.
	 * @param uid
	 * @param trayId
	 * @return
	 * @throws InvalidTrayElement
	 */
	Tray getTray(String uid, String trayId) throws InvalidTrayElement;
	
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
			OrderType orderType, boolean eventsPast) throws InvalidTrayElement;
	
	/**
	 * Cleans all tray elements with a given token.
	 * @param token
	 * @return
	 * @throws InvalidTrayElement
	 */
	boolean cleanTrays(String token) throws InvalidTrayElement;
	
	/**
	 * Gets a list of tray elements.
	 * @param uid
	 * @return
	 * @throws InvalidTrayElement
	 */
	List <Tray> getTrays(String uid) throws InvalidTrayElement;

	/**
	 * Gets all trays in the KB.
	 * @return
	 */
	List <Tray> getAllTrays();
}

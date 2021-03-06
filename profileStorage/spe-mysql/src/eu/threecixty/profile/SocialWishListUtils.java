/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is a utility class for listing a list of different PoIs and Events from
 * the friends list of a given user.
 *
 */
public class SocialWishListUtils {
	
	// The following values are taken from TrayServices
	private static final String EVENT_TYPE = "Event";
	private static final String POI_TYPE = "Poi";

	/**
	 * Lists all PoI IDs in the WishList from friends list of a given 3cixty UID.
	 * @param _3cixtyUID
	 * @return
	 * @throws TooManyConnections
	 */
	public static List <String> getPoIsFromFriendsWishList(String _3cixtyUID)
			throws TooManyConnections {
		return getTrayIdsFromFriendsWishList(_3cixtyUID, POI_TYPE);
	}
	
	/**
	 * Lists all Event IDs in the WishList from friends list of a given 3cixty UID.
	 * @param _3cixtyUID
	 * @return
	 * @throws TooManyConnections
	 */
	public static List <String> getEventsFromFriendsWishList(String _3cixtyUID)
			throws TooManyConnections {
		return getTrayIdsFromFriendsWishList(_3cixtyUID, EVENT_TYPE);
	}
	
	private static List <String> getTrayIdsFromFriendsWishList(String _3cixtyUID,
			String type) throws TooManyConnections {
		if (_3cixtyUID == null) return null;
		UserProfile profile = ProfileManagerImpl.getInstance().getProfile(_3cixtyUID);
		if (profile == null) return null;
		Set <String> knows = profile.getKnows();
		if (knows == null) return null;
		List <String> list = new LinkedList <String>();
		for (String know: knows) {
			List<Tray> trays;
			try {
				trays = MySQLTrayManager.getInstance().getTrays(know);
				if (trays == null) continue;
				for (Tray tray: trays) {
					if (!type.equalsIgnoreCase(tray.getElement_type())) continue;
					if (!list.contains(tray.getElement_id())) list.add(tray.getElement_id());
				}
			} catch (InvalidTrayElement e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	private SocialWishListUtils() {
	}
}

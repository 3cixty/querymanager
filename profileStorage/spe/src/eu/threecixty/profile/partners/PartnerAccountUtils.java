/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.partners;

import java.util.UUID;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;
import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.partners.PartnerUser;
import eu.threecixty.profile.ProfileManagerImpl;

/**
 * 
 * Utility class to create, retrieve Mobidot /Goflow user within 3cixty database.
 *
 */
public class PartnerAccountUtils {
	public static final String MOBIDOT_APP_ID = "MobidotAppID";
	
	private static final Logger LOGGER = Logger.getLogger(
			PartnerAccountUtils.class.getName());
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	public static PartnerAccount retrieveMobidotUser(String _3cixtyUID) {
		Partner partner = ProfileManagerImpl.getInstance().getPartner();
    	PartnerUser mobidotUser = partner.getUser(_3cixtyUID);
    	if (DEBUG_MOD) {
    		if (mobidotUser == null) LOGGER.info("Not found the corresponding partner of " + _3cixtyUID);
    		else LOGGER.info("Found the corresponding partner of " +  _3cixtyUID);
    	}
		PartnerAccount account = partner.findAccount(mobidotUser, MOBIDOT_APP_ID, null);
		return account;
	}
	
	/**
	 * Try to create a Mobidot user if it doesn't exist on Movesmarter server.
	 * @param _3cixty
	 * @param displayName
	 * @return Mobidot's account if existed
	 */
	public static PartnerAccount retrieveOrAddMobidotUser(String _3cixtyUID, String displayName) {
		Partner partner = ProfileManagerImpl.getInstance().getPartner();
    	PartnerUser mobidotUser = partner.getUser(_3cixtyUID);
    	if (DEBUG_MOD) {
    		if (mobidotUser == null) LOGGER.info("Not found the corresponding partner of " + _3cixtyUID);
    		else LOGGER.info("Found the corresponding partner of " +  _3cixtyUID);
    	}
		PartnerAccount account = partner.findAccount(mobidotUser, MOBIDOT_APP_ID, null);
		if (DEBUG_MOD) {
			if (account == null) LOGGER.info("Not found the corresponding Mobidot account");
			else LOGGER.info("Found the corresponding Mobidot account");
		}
		if (account != null) { // already exist in 3cixty's DB
			return account;
		}
		
		if (mobidotUser == null) {
			mobidotUser = new PartnerUser();
			mobidotUser.setUid(_3cixtyUID);
			partner.addUser(mobidotUser); // persist user in 3cixty's DB
		}
		String password = "3cixtyI$InExpo)!_" + UUID.randomUUID().toString();
		try {
			if (DEBUG_MOD) LOGGER.info("Start creating a Mobidot account");
			String mobidotUserName = Configuration.isForProdTarget() ? _3cixtyUID : _3cixtyUID + "DEV";
		    String mobidotID = MobidotUserUtils.createMobidotUser(mobidotUserName, displayName, password);
		    if (mobidotID == null || mobidotID.equals("")) return null;
		    account = new PartnerAccount();
			account.setAppId(MOBIDOT_APP_ID);
			account.setPassword(password);
			account.setUsername(mobidotUserName);
			account.setUser_id(mobidotID);
			account.setRole("User");
			account.setPartnerUser(mobidotUser);
			boolean ok = partner.addAccount(account); // persist account in 3cixty's DB
			if (ok) {
				if (DEBUG_MOD) LOGGER.info("Successful to persist account in DB");
			} else {
				if (DEBUG_MOD) LOGGER.info("Failed to persist account in DB");
			}
			if (ok) return account;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static PartnerAccount retrieveOrAddGoflowUser(String _3cixtyUID, String appId) {
		Partner partner = ProfileManagerImpl.getInstance().getPartner();
    	PartnerUser mobidotUser = partner.getUser(_3cixtyUID);
		PartnerAccount account = partner.findAccount(mobidotUser, appId, null);
		
		if (account != null) { // already exist in 3cixty's DB
			return account;
		}
		
		if (mobidotUser == null) {
			mobidotUser = new PartnerUser();
			mobidotUser.setUid(_3cixtyUID);
			partner.addUser(mobidotUser); // persist user in 3cixty's DB
		}
		String password = GoFlowServer.getInstance().createEndUser(appId, _3cixtyUID);
		if (password == null) {
			return null;
		}
		try {
		    account = new PartnerAccount();
			account.setAppId(appId);
			account.setPassword(password);
			account.setUsername(_3cixtyUID);
			account.setRole("User");
			account.setPartnerUser(mobidotUser);
			partner.addAccount(account); // persist account in 3cixty's DB
			return account;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private PartnerAccountUtils() {
	}
}

/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.CrawlerCron;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;
import eu.threecixty.MobilityCrawlerCron.MobilityCrawlerCron;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;

/**
 * Features runs at 3am Pulls Movesmarter data (Personal Places, Radius, Regular
 * trips, Accompany details) provided by Mobidot and writes it in to the profile
 * KB. No Direct API access to the component. The component connects to other
 * components in the 3cixty Platform via KB. if no connectivity then re-check
 * User Specific History of when the job was last executed successfully for the
 * user. accompany uses 3cixty IDs to related people inconsistent data field in
 * json format output is handled Multiple inferences using the mobility data and
 * personal data are made.
 * 
 * @author Rachit@inria
 * 
 */

public class CrawlerCron {

	private long fONCE_PER_DAY = 1000 * 60 * 60 * 24;
	
	 private static final Logger LOGGER = Logger.getLogger(
			 CrawlerCron.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	private int fONE_DAY = 1;
	private int fTHREE_AM = 3;
	private int fZERO_MINUTES = 0;

	private String MOBIDOT_BASEURL = "https://www.movesmarter.nl/external/";
	private String DOMAIN = "3cixty";
	
	public String MOBIDOT_API_KEY = Configuration.getMobidotKey();

	public CrawlerCron() {
	}

	private long getFoncePerDay() {
		return fONCE_PER_DAY;
	}

	private int getFoneDay() {
		return fONE_DAY;
	}

	private int getFthreeAm() {
		return fTHREE_AM;
	}

	private int getFzeroMinutes() {
		return fZERO_MINUTES;
	}

	private String getMobidotBaseurl() {
		return MOBIDOT_BASEURL;
	}

	private String getMobidotApiKey() {
		return MOBIDOT_API_KEY;
	}

	private String getDomain() {
		return DOMAIN;
	}

	/**
	 * Returns Date for next day 3am
	 * 
	 * @return Date
	 */
	private Date getTomorrowMorning3am() {
		Calendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DATE, getFoneDay());
		Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR),
				tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE),
				getFthreeAm(), getFzeroMinutes());
		return result.getTime();
	}

	/**
	 * Crawls Mobidot info.
	 */
	public void run() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					crawl();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, getTomorrowMorning3am(),getFoncePerDay());
		//timer.scheduleAtFixedRate(task, GregorianCalendar.getInstance().getTime(), getFoncePerDay());
	}

	/**
	 * Main entry to crawl info.
	 */
	private void crawl() {
		
		if (DEBUG_MOD) LOGGER.info("Start crawling Mobidot data");
		
		//CallGPlusProfileParser is not used as the crawling of the reviews is done by localidata.
		//only MobilityCrawlerCron is to be used.
		MobilityCrawlerCron mobilityCrawlerCron = new MobilityCrawlerCron();
		
		// get all 3cixtyIDs and mobidotIDs
		Set<IDMapping> idMapping = ProfileManagerImpl.getInstance()
				.getIDMappings();

		Iterator<IDMapping> iteratorMapping = idMapping.iterator();
		
		iteratorMapping = idMapping.iterator();
		// for each user in 3cixty, crawl
		while (iteratorMapping.hasNext()) {
			IDMapping map = iteratorMapping.next();
			if (DEBUG_MOD) LOGGER.info("UID = " + map.getThreeCixtyID() + ", Mobidot ID = " + map.getMobidotID());

			try {
				
				UserProfile user = ProfileManagerImpl.getInstance().getProfile(map.getThreeCixtyID());
								
				try {
					mobilityCrawlerCron.getmobility(map, user, idMapping,
							getMobidotBaseurl(), getDomain(), getMobidotApiKey());
					if (DEBUG_MOD) LOGGER.info("Finished crawling Mobidot data of user: "+ map.getThreeCixtyID());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// XXX: the following try-catch to avoid being caused by incorrect inference
				//      (two accompanyings can come from API& DEV database).
				try {
					ProfileManagerImpl.getInstance().saveProfile(user);
					if (DEBUG_MOD) LOGGER.info("Finished saving Mobidot data of user: "+ map.getThreeCixtyID());
				} catch (Exception e) {
					if (DEBUG_MOD) LOGGER.info(e.getMessage());
				}

			} catch (TooManyConnections e) {
				if (DEBUG_MOD) LOGGER.info(e.getMessage());
			}
		}
		if (DEBUG_MOD) LOGGER.info("Finished crawling Mobidot data");

	}
}

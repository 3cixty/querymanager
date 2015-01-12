package eu.threecixty.CrawlerCron;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;

//import eu.threecixty.CrawlSocialProfiles.CallGPlusProfileParser;
import eu.threecixty.MobilityCrawlerCron.MobilityCrawlerCron;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.IDCrawlTimeMapping;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Preference;

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
	private String MOBIDOT_API_KEY = "SRjHX5yHgqqpZyiYaHSXVqhlFWzIEoxUBmbFcSxiZn58Go02rqB9gKwFqsGx5dks";
	private String DOMAIN = "3cixty";

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
	 * Get seconds since 1970 in String
	 * 
	 * @return: String
	 */
	private Long getDateTime() {
		return GregorianCalendar.getInstance().getTimeInMillis() / 1000;
	}

	/**
	 * Get last crawl time for the user
	 * 
	 * @param: Set<IDCrawlTimeMapping> idCrawlTimeMapping
	 * @param: String uid
	 * @return: String
	 */
	private String getCrawltimeforUserID(
			Set<IDCrawlTimeMapping> idCrawlTimeMapping, String uid) {
		Iterator<IDCrawlTimeMapping> iteratorMapping = idCrawlTimeMapping
				.iterator();

		while (iteratorMapping.hasNext()) {
			IDCrawlTimeMapping map = iteratorMapping.next();
			if (map.getThreeCixtyID() == uid) {
				return map.getLastCrawlTime();
			}
		}
		return "0";
	}

	/**
	 * Main entry to crawl info.
	 */
	private void crawl() {
		if (DEBUG_MOD) LOGGER.info("Start crawling Mobidot data");
		//CallGPlusProfileParser is not used as the crawling of the reviews is done by localidata.
		//only MobilityCrawlerCron is to be used.
		MobilityCrawlerCron mobilityCrawlerCron = new MobilityCrawlerCron();
		
		//CallGPlusProfileParser callGPlusProfileParser = new CallGPlusProfileParser();
		
		// get all 3cixtyIDs, CrawlTimes and mobidotUserNames
		Set<IDMapping> idMapping = ProfileManagerImpl.getInstance()
				.getIDMappings();
		Set<IDCrawlTimeMapping> idCrawlTimeMapping = ProfileManagerImpl
				.getInstance().getIDCrawlTimeMappings();
		try {
			Iterator<IDMapping> iteratorMapping = idMapping.iterator();
			while (iteratorMapping.hasNext()) {
				IDMapping map = iteratorMapping.next();
				map.setMobidotID(mobilityCrawlerCron.getMobidotIDforUsername(
						map.getMobidotUserName(), getMobidotBaseurl(),
						getDomain(), getMobidotApiKey()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			crawl();
		}

		long currentTime = getDateTime();

		try {
			Iterator<IDMapping> iteratorMapping = idMapping.iterator();
			// for each user in 3cixty, crawl
			while (iteratorMapping.hasNext()) {
				IDMapping map = iteratorMapping.next();
				if (DEBUG_MOD) LOGGER.info("UID = " + map.getThreeCixtyID() + ", Mobidot username = " + map.getMobidotUserName() + ", Mobidot ID = " + map.getMobidotID());
				UserProfile user = ProfileManagerImpl.getInstance().getProfile(map.getThreeCixtyID());
				//UserProfile user = new UserProfile();
				//user.setHasUID(map.getThreeCixtyID());

				String lastCrawlTime = getCrawltimeforUserID(
						idCrawlTimeMapping, map.getThreeCixtyID());
				Preference pref = new Preference();
				//callGPlusProfileParser.getInfoAndReviews(currentTime, map, user, lastCrawlTime, pref);
				mobilityCrawlerCron.getmobility(map, user, idMapping,
						getMobidotBaseurl(), getDomain(), getMobidotApiKey(),
						lastCrawlTime, pref, currentTime);

				user.setHasLastCrawlTime(Long.toString(currentTime));

				ProfileManagerImpl.getInstance().saveProfile(user);
			}
			if (DEBUG_MOD) LOGGER.info("Finish crawling Mobidot data");
		} catch (Exception e) {
			e.printStackTrace();
			crawl();
		}
	}
}

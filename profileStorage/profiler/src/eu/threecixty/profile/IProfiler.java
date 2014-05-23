package eu.threecixty.profile;

import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Preference;

public interface IProfiler {

	/**
	 * Sets parameters to values by default for augmentation. 
	 */
	void initDefaultParametersForAugmentation();
	
	/**
	 * Populate KB of UserProfiles using Profiling Techniques
	 * 
	 */
	void PopulateProfile();

	/**
	 * Requires number of times visited at least.
	 * @param number
	 */
	void requireNumberOfTimesVisitedAtLeast(int number);
	
	/**
	 * Requires score rated at least.
	 * @param f
	 */
	void requireScoreRatedAtLeast(float f);

	/**
	 * Requires number of times visited for friends at least.
	 * @param number
	 */
	void requireNumberOfTimesVisitedForFriendsAtLeast(int number);
	
	/**
	 * Requires score rated for friends at least.
	 * @param f
	 */
	void requireScoreRatedForFriendsAtLeast(float f);

	/**
	 * Requires current country.
	 * @param currentCountryRequired
	 */
	void requireCurrentCountry(boolean currentCountryRequired);

	/**
	 * Requires current town.
	 * @param currentTownRequired
	 */
	void requireCurrentTown(boolean currentTownRequired);

	/**
	 * Requires an area within d kilometer.
	 * @param d
	 */
	void requireAreaWithin(double d);

	/**
	 * Requires a period of time from today to next <code>ndays</code> days
	 * @param ndays
	 */
	void requirePeriod(Period period);

	/**
	 * Requires event name.
	 * @param eventNameRequired
	 */
	void requireEventName(boolean eventNameRequired);

	/**
	 * Requires preferred event dates.
	 * @param preferredEventDates
	 */
	void requirePreferredEventDates(boolean preferredEventDates);

	/**
	 * Gets user ID.
	 * @return
	 */
	String getUID();

	/**
	 * Gets KB of UserProfile.
	 * @return
	 */
	UserProfile getKBUserProfile();

	/**
	 * Gets preferences.
	 * @return
	 */
	Preference getPreference();
}

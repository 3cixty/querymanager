package eu.threecixty.profile;

import eu.threecixty.profile.models.Preference;

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

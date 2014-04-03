package eu.threecixty.profile;

import eu.threecixty.profile.models.Preference;

public interface IProfiler {

	/**
	 * Populate KB of UserProfiles using Profiling Techniques
	 * 
	 */
	public void PopulateProfile();

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

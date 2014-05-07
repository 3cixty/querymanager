package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.*;

/**
 * Preferences of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Preference {
	@Description(hasText = "Collection of likes the user has.")
	private Set<Likes> hasLikes;
	@Description(hasText = "Collection of user Entered rating the user is associated to.")
	private Set<UserEnteredRating> hasUserEnteredRating; 
	@Description(hasText = "Collection of groups the user is associated to.")
	private Set<Group> hasGroups;
	@Description(hasText = "Collection of Agencies the user has used services of or would like to use.")
	private Set<Agency> hasTravelAgent;

	@ThalesInputs
	private Set<SmartPhoneSensorData> hasSmartPhoneSensorData;
	@ThalesInputs
	private Set<FoodPreferences> hasFoodPreferences;
	@ThalesInputs
	private Set<PlacePreference> hasPlacePreference;
	@ThalesInputs
	private Set<EventPreference> hasEventPreference;
	@ThalesInputs
	private Set<HotelPreference> hasHotelPreference;
	@ThalesInputs
	private Set<TripPreference> hasTripPreference;
	@Description(hasText = "Collection of social prefernces of the user.")
	private Set<SocialPreference> hasSocialPreference;
	
	
	
	@MobidotInputs
	private Set<Transport> hasTransport;


}

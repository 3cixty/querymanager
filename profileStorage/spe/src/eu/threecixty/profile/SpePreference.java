package eu.threecixty.profile;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.MobidotInputs;
import eu.threecixty.profile.annotations.ThalesInputs;
import eu.threecixty.profile.oldmodels.Agency;
import eu.threecixty.profile.oldmodels.EventPreference;
import eu.threecixty.profile.oldmodels.FoodPreferences;
import eu.threecixty.profile.oldmodels.Group;
import eu.threecixty.profile.oldmodels.HotelPreference;
import eu.threecixty.profile.oldmodels.Likes;
import eu.threecixty.profile.oldmodels.PlacePreference;
import eu.threecixty.profile.oldmodels.SmartPhoneSensorData;
import eu.threecixty.profile.oldmodels.SocialPreference;
import eu.threecixty.profile.oldmodels.Transport;
import eu.threecixty.profile.oldmodels.TripPreference;
import eu.threecixty.profile.oldmodels.UserEnteredRating;



public class SpePreference {

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


	public Set<Likes> getHasLikes() {
		return hasLikes;
	}


	public void setHasLikes(Set<Likes> hasLikes) {
		this.hasLikes = hasLikes;
	}


	public Set<UserEnteredRating> getHasUserEnteredRating() {
		return hasUserEnteredRating;
	}


	public void setHasUserEnteredRating(Set<UserEnteredRating> hasUserEnteredRating) {
		this.hasUserEnteredRating = hasUserEnteredRating;
	}


	public Set<Group> getHasGroups() {
		return hasGroups;
	}


	public void setHasGroups(Set<Group> hasGroups) {
		this.hasGroups = hasGroups;
	}


	public Set<Agency> getHasTravelAgent() {
		return hasTravelAgent;
	}


	public void setHasTravelAgent(Set<Agency> hasTravelAgent) {
		this.hasTravelAgent = hasTravelAgent;
	}


	public Set<SmartPhoneSensorData> getHasSmartPhoneSensorData() {
		return hasSmartPhoneSensorData;
	}


	public void setHasSmartPhoneSensorData(
			Set<SmartPhoneSensorData> hasSmartPhoneSensorData) {
		this.hasSmartPhoneSensorData = hasSmartPhoneSensorData;
	}


	public Set<FoodPreferences> getHasFoodPreferences() {
		return hasFoodPreferences;
	}


	public void setHasFoodPreferences(Set<FoodPreferences> hasFoodPreferences) {
		this.hasFoodPreferences = hasFoodPreferences;
	}


	public Set<PlacePreference> getHasPlacePreference() {
		return hasPlacePreference;
	}


	public void setHasPlacePreference(Set<PlacePreference> hasPlacePreference) {
		this.hasPlacePreference = hasPlacePreference;
	}


	public Set<EventPreference> getHasEventPreference() {
		return hasEventPreference;
	}


	public void setHasEventPreference(Set<EventPreference> hasEventPreference) {
		this.hasEventPreference = hasEventPreference;
	}


	public Set<HotelPreference> getHasHotelPreference() {
		return hasHotelPreference;
	}


	public void setHasHotelPreference(Set<HotelPreference> hasHotelPreference) {
		this.hasHotelPreference = hasHotelPreference;
	}


	public Set<TripPreference> getHasTripPreference() {
		return hasTripPreference;
	}


	public void setHasTripPreference(Set<TripPreference> hasTripPreference) {
		this.hasTripPreference = hasTripPreference;
	}


	public Set<SocialPreference> getHasSocialPreference() {
		return hasSocialPreference;
	}


	public void setHasSocialPreference(Set<SocialPreference> hasSocialPreference) {
		this.hasSocialPreference = hasSocialPreference;
	}


	public Set<Transport> getHasTransport() {
		return hasTransport;
	}


	public void setHasTransport(Set<Transport> hasTransport) {
		this.hasTransport = hasTransport;
	}
}

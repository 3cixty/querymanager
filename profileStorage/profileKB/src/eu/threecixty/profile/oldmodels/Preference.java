/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.*;

/**
 * Preferences of the user
 *
 */
public class Preference {
	@Description(hasText="UniqueID")
	private String hasPreferenceURI="";
	@Description(hasText = "Collection of likes the user has.")
	private Set<Likes> hasLikes;
	@Description(hasText = "Collection of user Entered rating the user is associated to.")
	private Set<UserEnteredRating> hasUserEnteredRating; 
	@Description(hasText = "Collection of groups the user is associated to.")
	private Set<Group> hasGroups;
	@Description(hasText = "Collection of Agencies the user has used services of or would like to use.")
	private Set<Agency> hasTravelAgent;

	/**Attribute which stores information about places populated*/
	private Set<Place> hasPlaces;

	/**Attribute which stores information about events populated*/
	private Set <Event> hasEvents;

	/**Attribute which stores information about periods which events take place*/
	private Set <Period> hasPeriods;
	
	private Set <Double> scoresRequired;

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

	

	public String getHasPreferenceURI() {
		return hasPreferenceURI;
	}


	public void setHasPreferenceURI(String hasPreferenceURI) {
		this.hasPreferenceURI = hasPreferenceURI;
	}


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


	public Set<Place> getHasPlaces() {
		return hasPlaces;
	}

	public void setHasPlaces(Set<Place> hasPlaces) {
		this.hasPlaces = hasPlaces;
	}


	public Set<Event> getHasEvents() {
		return hasEvents;
	}

	public void setHasEvents(Set<Event> hasEvents) {
		this.hasEvents = hasEvents;
	}

	public Set<Period> getHasPeriods() {
		return hasPeriods;
	}

	public void setHasPeriods(Set<Period> hasPeriods) {
		this.hasPeriods = hasPeriods;
	}

	public Set<Double> getScoresRequired() {
		return scoresRequired;
	}

	public void setScoresRequired(Set<Double> scoresRequired) {
		this.scoresRequired = scoresRequired;
	}
}

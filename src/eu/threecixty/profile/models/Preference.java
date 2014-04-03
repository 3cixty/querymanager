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
	Set<Likes> hasLikes;
	@Description(hasText = "Collection of groups the user is associated to.")
	Set<Group> hasGroups;
	@Description(hasText = "Collection of events the user is associated to.")
	Set<Event> hasEvents;
	@Description(hasText = "Collection of places the user is associated to.")
	Set<Place> hasPlaces;
	@Description(hasText = "Collection of hotels the user likes or has visited before.")
	Set<Hotel> hasHotels;
	@Description(hasText = "Collection of Agencies the user has used services of or would like to use.")
	Set<Agency> hasTravelAgent;

	@ThalesInputs
	Set<SmartPhoneSensorData> hasSmartPhoneSensorData;
	@ThalesInputs
	Set<FoodPreferences> hasFoodPreferences;

	@MobidotInputs
	Set<Transport> hasTransport;

	public Set<Transport> getHasTransport() {
		return hasTransport;
	}

	public void setHasTransport(Set<Transport> hasTransport) {
		this.hasTransport = hasTransport;
	}

	public Set<Event> getHasEvents() {
		return hasEvents;
	}

	public void setHasEvents(Set<Event> hasEvents) {
		this.hasEvents = hasEvents;
	}

	public Set<Place> getHasPlaces() {
		return hasPlaces;
	}

	public void setHasPlaces(Set<Place> hasPlaces) {
		this.hasPlaces = hasPlaces;
	}

}

package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

public class UserEnteredRating {

	@Description(hasText = "Collection of events the user is associated to.")
	private Set<UserEventRating> hasUserEventRating;
	@Description(hasText = "Collection of places the user is associated to.")
	private Set<UserPlaceRating> hasUserPlaceRating;
	@Description(hasText = "Collection of hotels the user likes or has visited before.")
	private Set<UserHotelRating> hasUserHotelRating;
	public Set<UserEventRating> getHasUserEventRating() {
		return hasUserEventRating;
	}
	public void setHasUserEventRatin(Set<UserEventRating> hasUserEventRatin) {
		this.hasUserEventRating = hasUserEventRatin;
	}
	public Set<UserPlaceRating> getHasUserPlaceRating() {
		return hasUserPlaceRating;
	}
	public void setHasUserPlaceRating(Set<UserPlaceRating> hasUserPlaceRating) {
		this.hasUserPlaceRating = hasUserPlaceRating;
	}
	public Set<UserHotelRating> getHasUserHotelRating() {
		return hasUserHotelRating;
	}
	public void setHasUserHotelRating(Set<UserHotelRating> hasUserHotelRating) {
		this.hasUserHotelRating = hasUserHotelRating;
	}
	
	
}

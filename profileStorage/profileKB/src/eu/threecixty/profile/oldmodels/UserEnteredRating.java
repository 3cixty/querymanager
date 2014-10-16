package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

public class UserEnteredRating {
	@Description(hasText="UniqueID")
	private String hasUserEnteredRatingURI="";
	@Description(hasText = "Collection of events the user is associated to.")
	private Set<UserEventRating> hasUserEventRating;
	@Description(hasText = "Collection of places the user is associated to.")
	private Set<UserPlaceRating> hasUserPlaceRating;
	@Description(hasText = "Collection of hotels the user likes or has visited before.")
	private Set<UserHotelRating> hasUserHotelRating;
	
	
	public String getHasUserEnteredRatingURI() {
		return hasUserEnteredRatingURI;
	}
	public void setHasUserEnteredRatingURI(String hasUserEnteredRatingURI) {
		this.hasUserEnteredRatingURI = hasUserEnteredRatingURI;
	}
	public void setHasUserEventRating(Set<UserEventRating> hasUserEventRating) {
		this.hasUserEventRating = hasUserEventRating;
	}
	public Set<UserEventRating> getHasUserEventRating() {
		return hasUserEventRating;
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

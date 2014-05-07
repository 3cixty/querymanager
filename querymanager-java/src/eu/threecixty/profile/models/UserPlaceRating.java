package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * place information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class UserPlaceRating {
	@Description(hasText="place details")
	private PlaceDetail hasPlaceDetail;
	@Description(hasText="User rating of the place")
	private Rating hasRating;
	@Description(hasText="Number of times User visited the place")
	private int hasNumberOfTimesVisited;
	public PlaceDetail getHasPlaceDetail() {
		return hasPlaceDetail;
	}
	public void setHasPlaceDetail(PlaceDetail hasPlaceDetail) {
		this.hasPlaceDetail = hasPlaceDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTimesVisited() {
		return hasNumberOfTimesVisited;
	}
	public void setHasNumberOfTimesVisited(int hasNumberOfTimesVisited) {
		this.hasNumberOfTimesVisited = hasNumberOfTimesVisited;
	}
	
}

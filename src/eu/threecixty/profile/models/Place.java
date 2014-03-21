package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * place information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Place {
	@Description(hasText="place details")
	private PlaceDetail hasPlaceDetail;
	@Description(hasText="User rating of the place")
	private Rating hasRating;
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
	
}

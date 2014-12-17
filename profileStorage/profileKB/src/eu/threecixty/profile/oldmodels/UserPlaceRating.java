package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * place information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class UserPlaceRating {
	@Description(hasText="UniqueID")
	private String hasUserPlaceRatingURI="";
	@Description(hasText="place details")
	private PlaceDetail hasPlaceDetail;
	@Description(hasText="User rating of the place")
	private Rating hasRating;
	@Description(hasText="Number of times User visited the place")
	private int hasNumberOfTimesVisited;
	
	// data is new 
	private Boolean newForKB = null;
	
	
	public String getHasUserPlaceRatingURI() {
		return hasUserPlaceRatingURI;
	}
	public void setHasUserPlaceRatingURI(String hasUserPlaceRatingURI) {
		this.hasUserPlaceRatingURI = hasUserPlaceRatingURI;
	}
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
	public Boolean getNewForKB() {
		return newForKB;
	}
	public void setNewForKB(Boolean newForKB) {
		this.newForKB = newForKB;
	}
	
}

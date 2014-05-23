package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Hotel information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Hotel {
	@Description(hasText="Hotel details")
	private HotelDetail hasHotelDetail;
	@Description(hasText="User rating of the hotel")
    private Rating hasRating; 
	@Description(hasText="number of times User visited the hotel")
    private int hasNumberOfTImesVisited;
	public HotelDetail getHasHotelDetail() {
		return hasHotelDetail;
	}
	public void setHasHotelDetail(HotelDetail hasHotelDetail) {
		this.hasHotelDetail = hasHotelDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTImesVisited() {
		return hasNumberOfTImesVisited;
	}
	public void setHasNumberOfTImesVisited(int hasNumberOfTImesVisited) {
		this.hasNumberOfTImesVisited = hasNumberOfTImesVisited;
	}
	
}

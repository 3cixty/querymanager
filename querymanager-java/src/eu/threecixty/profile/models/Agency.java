package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Agency information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Agency {
	@Description(hasText="Agency details")
	private TravelAgency hasTravelAgency;
	@Description(hasText="User rating of the Agency")
    private Rating hasRating;
	@Description(hasText="number of times User visited the Agency")
    private int hasNumberOfTimesServed;
	public TravelAgency getHasTravelAgency() {
		return hasTravelAgency;
	}
	public void setHasTravelAgency(TravelAgency hasTravelAgency) {
		this.hasTravelAgency = hasTravelAgency;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTimesServed() {
		return hasNumberOfTimesServed;
	}
	public void setHasNumberOfTimesServed(int hasNumberOfTimesServed) {
		this.hasNumberOfTimesServed = hasNumberOfTimesServed;
	}
}

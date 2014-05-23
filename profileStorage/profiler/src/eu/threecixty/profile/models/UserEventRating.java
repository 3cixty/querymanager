package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Event information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class UserEventRating {
	@Description(hasText="Event details")
	private EventDetail hasEventDetail;
	@Description(hasText="User rating of the event")
	private Rating hasRating;
	@Description(hasText="Number of times user visited the event")
	private int hasNumberOfTimesVisited;
	public EventDetail getHasEventDetail() {
		return hasEventDetail;
	}
	public void setHasEventDetail(EventDetail hasEventDetail) {
		this.hasEventDetail = hasEventDetail;
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

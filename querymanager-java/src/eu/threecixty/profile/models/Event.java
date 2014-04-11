package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Event information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Event {
	@Description(hasText="Event details")
	private EventDetail hasEventDetail;
	@Description(hasText="User rating of the event")
	private Rating hasRating;
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
	
}

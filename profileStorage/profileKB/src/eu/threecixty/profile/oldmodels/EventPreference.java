package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

public class EventPreference {
	@Description(hasText="preferred event details")
    private EventDetailPreference hasEventDetailPreference;
	@Description(hasText="the preferred rating for the searched event")
	private RatingPreference hasEventRatingPreference;
	public EventDetailPreference getHasEventDetailPreference() {
		return hasEventDetailPreference;
	}
	public void setHasEventDetailPreference(
			EventDetailPreference hasEventDetailPreference) {
		this.hasEventDetailPreference = hasEventDetailPreference;
	}
	public RatingPreference getHasEventRatingPreference() {
		return hasEventRatingPreference;
	}
	public void setHasEventRatingPreference(
			RatingPreference hasEventRatingPreference) {
		this.hasEventRatingPreference = hasEventRatingPreference;
	}
	
}

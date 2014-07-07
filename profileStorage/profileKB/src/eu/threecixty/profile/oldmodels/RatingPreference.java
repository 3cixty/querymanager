package eu.threecixty.profile.oldmodels;

import java.util.Date;

import eu.threecixty.profile.annotations.Description;

public class RatingPreference {
	@Description(hasText="minimal average rating accepted for the place, on scale of 1 to 10")
    private double hasMinRating;
    @Description(hasText="maximal date of the newest rating")
    private Date hasMaxLastRatingTime;
	public double getHasMinRating() {
		return hasMinRating;
	}
	public void setHasMinRating(double hasMinRating) {
		this.hasMinRating = hasMinRating;
	}
	public Date getHasMaxLastRatingTime() {
		return hasMaxLastRatingTime;
	}
	public void setHasMaxLastRatingTime(Date hasMaxLastRatingTime) {
		this.hasMaxLastRatingTime = hasMaxLastRatingTime;
	}
    
}

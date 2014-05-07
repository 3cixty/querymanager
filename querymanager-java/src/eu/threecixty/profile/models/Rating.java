package eu.threecixty.profile.models;

import java.util.Date;
import java.util.Set;

import eu.threecixty.profile.annotations.Description;
/**
 * Rating given by the User to an entity
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Rating {
	@Description(hasText="rating given by the user on scale of 1 to 10")
	private Double hasUseDefinedRating;
	@Description(hasText="user interaction mode")
    private UserInteractionMode hasUserInteractionMode;
	@Description(hasText="time at which the user rated")
    private Date hasRatingTime;
	@Description(hasText="comments by the user")
    private Set <String> hasKeyTags;
	public Double getHasUseDefinedRating() {
		return hasUseDefinedRating;
	}
	public void setHasUseDefinedRating(Double hasUseDefinedRating) {
		this.hasUseDefinedRating = hasUseDefinedRating;
	}
	public UserInteractionMode getHasUserInteractionMode() {
		return hasUserInteractionMode;
	}
	public void setHasUserInteractionMode(UserInteractionMode hasUserInteractionMode) {
		this.hasUserInteractionMode = hasUserInteractionMode;
	}
	public Date getHasRatingTime() {
		return hasRatingTime;
	}
	public void setHasRatingTime(Date hasRatingTime) {
		this.hasRatingTime = hasRatingTime;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	
}

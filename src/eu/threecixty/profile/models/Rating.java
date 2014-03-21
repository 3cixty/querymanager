package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
/**
 * Rating given by the User to an entity
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Rating {
	@Description(hasText="rating given by the user on scale of 1 to 10")
	private float rating=0;
	@Description(hasText="user interaction mode")
    private UserInteractionMode hasUserInteractionMode;
	@Description(hasText="comments by the user")
    private Set <String> hasKeyTags;
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public UserInteractionMode getHasUserInteractionMode() {
		return hasUserInteractionMode;
	}
	public void setHasUserInteractionMode(UserInteractionMode hasUserInteractionMode) {
		this.hasUserInteractionMode = hasUserInteractionMode;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.*;
import eu.threecixty.profile.annotations.Description;

/**
 * User Friend class. It holds the relation of a user with another user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Friend {
	@Description(hasText = "Other profile")
	private UserProfile hasUser;
	@Description(hasText = "relationship with the other user")
    private Relationship hasType;
	@Description(hasText = "comments on the relationship")
    private Set <String> hasKeyTags;
	public UserProfile getHasUser() {
		return hasUser;
	}
	public void setHasUser(UserProfile hasUser) {
		this.hasUser = hasUser;
	}
	public Relationship getHasType() {
		return hasType;
	}
	public void setHasType(Relationship hasType) {
		this.hasType = hasType;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}

}

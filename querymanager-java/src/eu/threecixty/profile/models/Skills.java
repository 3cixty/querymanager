package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * Skills a user has
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Skills {
	@Description(hasText="Name of the skill")
	private String hasName;
	@Description(hasText="Maturity Level of the user on the skill")
	private MaturityLevel hasMaturityLevel;
	@Description(hasText="comments")
	private Set <String> keyTags;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public MaturityLevel getHasMaturityLevel() {
		return hasMaturityLevel;
	}
	public void setHasMaturityLevel(MaturityLevel hasMaturityLevel) {
		this.hasMaturityLevel = hasMaturityLevel;
	}
	public Set<String> getKeyTags() {
		return keyTags;
	}
	public void setKeyTags(Set<String> keyTags) {
		this.keyTags = keyTags;
	}
	
}

package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * Skills a user has
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Skills {
	@Description(hasText="Name of the skill")
	private String hasSkillName;
	@Description(hasText="Maturity Level of the user on the skill")
	private MaturityLevel hasMaturityLevel;
	@Description(hasText="comments")
	private Set <String> keyTags;
	public String getHasSkillName() {
		return hasSkillName;
	}
	public void setHasSkillName(String hasSkillName) {
		this.hasSkillName = hasSkillName;
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
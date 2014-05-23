package eu.threecixty.profile.models;

import java.util.Set;
import java.util.Date;

import eu.threecixty.profile.annotations.Description;

/**
 * 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Information {
	@Description(hasText = "start date")
	private Date hasFrom;
	@Description(hasText = "end date. can be empty")
	private Date hasUntil;
	@Description(hasText = "Details")
	private String hasDetails="";
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	
	public Date getHasFrom() {
		return hasFrom;
	}
	public void setHasFrom(Date hasFrom) {
		this.hasFrom = hasFrom;
	}
	public Date getHasUntil() {
		return hasUntil;
	}
	public void setHasUntil(Date hasUntil) {
		this.hasUntil = hasUntil;
	}
	public String getHasDetails() {
		return hasDetails;
	}
	public void setHasDetails(String hasDetails) {
		this.hasDetails = hasDetails;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

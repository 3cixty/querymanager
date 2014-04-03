package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;
/**
 * School attended information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class SchoolAttended {
	@Description(hasText="School attended")
	private School hasSchool;
	@Description(hasText="Information about the school attended period")
	private Information hasInformation;
	public School getHasSchool() {
		return hasSchool;
	}
	public void setHasSchool(School hasSchool) {
		this.hasSchool = hasSchool;
	}
	public Information getHasInformation() {
		return hasInformation;
	}
	public void setHasInformation(Information hasInformation) {
		this.hasInformation = hasInformation;
	}
}

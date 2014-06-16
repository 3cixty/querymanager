package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;
/**
 * School attended information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class SchoolAttended extends EventDetail {
	@Description(hasText="School attended")
	private School hasSchool;
	public School getHasSchool() {
		return hasSchool;
	}
	public void setHasSchool(School hasSchool) {
		this.hasSchool = hasSchool;
	}
}

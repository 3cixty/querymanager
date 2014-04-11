package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Group Information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Group {
	@Description(hasText="Group details")
	private GroupDetail hasGroupDetail;
	@Description(hasText="User rating of the Group")
    private Rating hasRating;
	public GroupDetail getHasGroupDetail() {
		return hasGroupDetail;
	}
	public void setHasGroupDetail(GroupDetail hasGroupDetail) {
		this.hasGroupDetail = hasGroupDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	
}

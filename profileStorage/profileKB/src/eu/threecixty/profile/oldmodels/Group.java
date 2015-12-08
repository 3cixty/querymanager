/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Group Information
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

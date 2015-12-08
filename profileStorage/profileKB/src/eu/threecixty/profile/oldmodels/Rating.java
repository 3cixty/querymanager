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

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
/**
 * Rating given by the User to an entity
 *
 */
public class Rating {
	@Description(hasText="rating given by the user on scale of 1 to 10")
	private double hasUseDefinedRating;
	@Description(hasText="user interaction mode")
    private UserInteractionMode hasUserInteractionMode;
//	@Description(hasText="time at which the user rated")
//    private Date hasRatingTime;
	@Description(hasText="comments by the user")
    private Set <String> hasKeyTags;
	public double getHasUseDefinedRating() {
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
//	public Date getHasRatingTime() {
//		return hasRatingTime;
//	}
//	public void setHasRatingTime(Date hasRatingTime) {
//		this.hasRatingTime = hasRatingTime;
//	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof Rating)) return false;
		Rating rating = (Rating) object;
		if (hasUserInteractionMode == null) {
			if (rating.hasUserInteractionMode != null) return false;
		} else if (hasUserInteractionMode != rating.hasUserInteractionMode) return false;
//		if (hasRatingTime == null) {
//			if (rating.hasRatingTime != null) return false;
//		} else if (!hasRatingTime.equals(rating.hasRatingTime)) return false;
		
		return hasUseDefinedRating == rating.hasUseDefinedRating;
	}
}

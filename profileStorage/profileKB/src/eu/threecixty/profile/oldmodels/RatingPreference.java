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

import java.util.Date;

import eu.threecixty.profile.annotations.Description;

public class RatingPreference {
	@Description(hasText="minimal average rating accepted for the place, on scale of 1 to 10")
    private double hasMinRating;
    @Description(hasText="maximal date of the newest rating")
    private Date hasMaxLastRatingTime;
	public double getHasMinRating() {
		return hasMinRating;
	}
	public void setHasMinRating(double hasMinRating) {
		this.hasMinRating = hasMinRating;
	}
	public Date getHasMaxLastRatingTime() {
		return hasMaxLastRatingTime;
	}
	public void setHasMaxLastRatingTime(Date hasMaxLastRatingTime) {
		this.hasMaxLastRatingTime = hasMaxLastRatingTime;
	}
    
}

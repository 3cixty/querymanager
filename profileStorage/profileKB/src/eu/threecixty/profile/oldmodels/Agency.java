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
 * Agency information
 *
 */
public class Agency extends TravelAgency{
	@Description(hasText="User rating of the Agency")
    private Rating hasRating;
	@Description(hasText="number of times User visited the Agency")
    private int hasNumberOfTimesServed;
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTimesServed() {
		return hasNumberOfTimesServed;
	}
	public void setHasNumberOfTimesServed(int hasNumberOfTimesServed) {
		this.hasNumberOfTimesServed = hasNumberOfTimesServed;
	}
}

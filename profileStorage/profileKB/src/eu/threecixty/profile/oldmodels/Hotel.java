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
 * Hotel information
 *
 */
public class Hotel {
	@Description(hasText="Hotel details")
	private HotelDetail hasHotelDetail;
	@Description(hasText="User rating of the hotel")
    private Rating hasRating; 
	@Description(hasText="number of times User visited the hotel")
    private int hasNumberOfTImesVisited;
	public HotelDetail getHasHotelDetail() {
		return hasHotelDetail;
	}
	public void setHasHotelDetail(HotelDetail hasHotelDetail) {
		this.hasHotelDetail = hasHotelDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTImesVisited() {
		return hasNumberOfTImesVisited;
	}
	public void setHasNumberOfTImesVisited(int hasNumberOfTImesVisited) {
		this.hasNumberOfTImesVisited = hasNumberOfTImesVisited;
	}
	
}

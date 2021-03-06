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

public class HotelPreference {
	@Description(hasText=" Hotel details preferred  by user for his booking")
    private HotelDetailPreference hasHotelDetailPreference;
    @Description(hasText=" the preferred rating for the searched hotel")
    private RatingPreference hasPreferredHotelRating;
    @Description(hasText="minimal times the User himself visited the hotel")
    private int hasNumberOfTimesVisited;
	public HotelDetailPreference getHasHotelDetailPreference() {
		return hasHotelDetailPreference;
	}
	public void setHasHotelDetailPreference(
			HotelDetailPreference hasHotelDetailPreference) {
		this.hasHotelDetailPreference = hasHotelDetailPreference;
	}
	public RatingPreference getHasPreferredHotelRating() {
		return hasPreferredHotelRating;
	}
	public void setHasPreferredHotelRating(RatingPreference hasPreferredHotelRating) {
		this.hasPreferredHotelRating = hasPreferredHotelRating;
	}
	public int getHasNumberOfTimesVisited() {
		return hasNumberOfTimesVisited;
	}
	public void setHasNumberOfTimesVisited(int hasNumberOfTimesVisited) {
		this.hasNumberOfTimesVisited = hasNumberOfTimesVisited;
	}
    
}

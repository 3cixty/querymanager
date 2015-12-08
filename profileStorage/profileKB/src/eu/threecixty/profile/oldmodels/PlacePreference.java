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

public class PlacePreference {
	@Description(hasText="UniqueID")
	private String hasPlacePreferenceURI="";
	@Description(hasText="preferred place details")
    private PlaceDetailPreference hasPlaceDetailPreference;
	@Description(hasText=" the preferred rating for the searched place")
	private RatingPreference hasPlaceRatingPreference;
	
	
	public String getHasPlacePreferenceURI() {
		return hasPlacePreferenceURI;
	}
	public void setHasPlacePreferenceURI(String hasPlacePreferenceURI) {
		this.hasPlacePreferenceURI = hasPlacePreferenceURI;
	}
	public PlaceDetailPreference getHasPlaceDetailPreference() {
		return hasPlaceDetailPreference;
	}
	public void setHasPlaceDetailPreference(
			PlaceDetailPreference hasPlaceDetailPreference) {
		this.hasPlaceDetailPreference = hasPlaceDetailPreference;
	}
	public RatingPreference getHasPlaceRatingPreference() {
		return hasPlaceRatingPreference;
	}
	public void setHasPlaceRatingPreference(
			RatingPreference hasPlaceRatingPreference) {
		this.hasPlaceRatingPreference = hasPlaceRatingPreference;
	}

}

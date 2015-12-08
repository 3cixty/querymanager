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

public class EventPreference {
	@Description(hasText="preferred event details")
    private EventDetailPreference hasEventDetailPreference;
	@Description(hasText="the preferred rating for the searched event")
	private RatingPreference hasEventRatingPreference;
	public EventDetailPreference getHasEventDetailPreference() {
		return hasEventDetailPreference;
	}
	public void setHasEventDetailPreference(
			EventDetailPreference hasEventDetailPreference) {
		this.hasEventDetailPreference = hasEventDetailPreference;
	}
	public RatingPreference getHasEventRatingPreference() {
		return hasEventRatingPreference;
	}
	public void setHasEventRatingPreference(
			RatingPreference hasEventRatingPreference) {
		this.hasEventRatingPreference = hasEventRatingPreference;
	}
	
}

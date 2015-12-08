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
 * Event information
 *
 */
public class Event {
	@Description(hasText="Event details")
	private EventDetail hasEventDetail;
	@Description(hasText="User rating of the event")
	private Rating hasRating;
	public EventDetail getHasEventDetail() {
		return hasEventDetail;
	}
	public void setHasEventDetail(EventDetail hasEventDetail) {
		this.hasEventDetail = hasEventDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}

	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof Event)) return false;
		Event event = (Event) object;
		if (hasEventDetail == null) {
			if (event.hasEventDetail != null) return false;
		} else if (!hasEventDetail.equals(event.hasEventDetail)) return false;
		if (hasRating == null) {
			if (event.hasRating != null) return false;
		} else if (!hasRating.equals(event.hasRating)) return false;
		return true;
	}
}

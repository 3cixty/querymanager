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
 * place information
 *
 */
public class Place {
	@Description(hasText="place details")
	private PlaceDetail hasPlaceDetail;
	@Description(hasText="User rating of the place")
	private Rating hasRating;
	public PlaceDetail getHasPlaceDetail() {
		return hasPlaceDetail;
	}
	public void setHasPlaceDetail(PlaceDetail hasPlaceDetail) {
		this.hasPlaceDetail = hasPlaceDetail;
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
		if (!(object instanceof Place)) return false;
		Place place = (Place) object;
		if (hasPlaceDetail == null) {
			if (place.hasPlaceDetail != null) return false;
		} else if (!hasPlaceDetail.equals(place.hasPlaceDetail)) return false;
		if (hasRating == null) {
			if (place.hasRating != null) return false;
		} else if (!hasRating.equals(place.hasRating)) return false;
		return true;
	}
}

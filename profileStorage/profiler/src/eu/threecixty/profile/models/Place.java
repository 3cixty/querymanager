package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * place information
 * @author Rachit.Agarwal@inria.fr
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

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
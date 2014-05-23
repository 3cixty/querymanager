package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

public class HotelDetailPreference extends PlaceDetailPreference{
	@Description(hasText="preferred hotel chains")
	private Set <Address> haspreferredChains;
	@Description(hasText="maximal accepted lowest Price")
	private Double haMaxPriceLow;
	@Description(hasText="minimal accepted highest Price")
	private Double hasMinPriceHigh;
	@Description(hasText="preferred Room Types to be available eg. single, double, triple, quad, single and double, ...")
	private String hasPreferredRoomTypes="";
	@Description(hasText="preferred Nearby Public Transport facility")
	private String hasNearbyTransportMode="";
	@Description(hasText="preferred Star information, from 1 to 7")
	private int hasPreferredStarCategory;
	@Description(hasText="preferred Type of food served")
	private TypeOfFood preferredTypeofFood;
	@Description(hasText="preferred tags associated with the hotel")
	private Set <String> preferredKeyTags;
	public Set<Address> getHaspreferredChains() {
		return haspreferredChains;
	}
	public void setHaspreferredChains(Set<Address> haspreferredChains) {
		this.haspreferredChains = haspreferredChains;
	}
	public Double getHaMaxPriceLow() {
		return haMaxPriceLow;
	}
	public void setHaMaxPriceLow(Double haMaxPriceLow) {
		this.haMaxPriceLow = haMaxPriceLow;
	}
	public Double getHasMinPriceHigh() {
		return hasMinPriceHigh;
	}
	public void setHasMinPriceHigh(Double hasMinPriceHigh) {
		this.hasMinPriceHigh = hasMinPriceHigh;
	}
	public String getHasPreferredRoomTypes() {
		return hasPreferredRoomTypes;
	}
	public void setHasPreferredRoomTypes(String hasPreferredRoomTypes) {
		this.hasPreferredRoomTypes = hasPreferredRoomTypes;
	}
	public String getHasNearbyTransportMode() {
		return hasNearbyTransportMode;
	}
	public void setHasNearbyTransportMode(String hasNearbyTransportMode) {
		this.hasNearbyTransportMode = hasNearbyTransportMode;
	}
	public int getHasPreferredStarCategory() {
		return hasPreferredStarCategory;
	}
	public void setHasPreferredStarCategory(int hasPreferredStarCategory) {
		this.hasPreferredStarCategory = hasPreferredStarCategory;
	}
	public TypeOfFood getPreferredTypeofFood() {
		return preferredTypeofFood;
	}
	public void setPreferredTypeofFood(TypeOfFood preferredTypeofFood) {
		this.preferredTypeofFood = preferredTypeofFood;
	}
	public Set<String> getPreferredKeyTags() {
		return preferredKeyTags;
	}
	public void setPreferredKeyTags(Set<String> preferredKeyTags) {
		this.preferredKeyTags = preferredKeyTags;
	}
	
}

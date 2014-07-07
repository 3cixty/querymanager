package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * Hotel Details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class HotelDetail extends PlaceDetail {
	@Description(hasText="hotel chains")
	private Set <Address> hasHotelChains;
	@Description(hasText="lowest Price")
	private Double hasHotelPriceLow=0.0;
	@Description(hasText="highest Price")
	private Double hasHotelPriceHigh=0.0;
	@Description(hasText="Room Type available eg. single, double, triple, quad, single and double, ...")
	private String hasHotelRoomTypes="";
	@Description(hasText="Nearby Public Transport facility")
	private String hasNearbyTransportMode="";
	@Description(hasText="How to reach details")
	private String hasHowToReach="";
	@Description(hasText="Star information from 1 to 7")
	private int hasHotelStarCategory;
	@Description(hasText="Type of food served")
	private TypeOfFood hasTypeOfFood;
	@Description(hasText="Facilities available")
	private Set <String> hasFacilities;
	public Set<Address> getHasHotelChains() {
		return hasHotelChains;
	}
	public void setHasHotelChains(Set<Address> hasHotelChains) {
		this.hasHotelChains = hasHotelChains;
	}
	public Double getHasHotelPriceLow() {
		return hasHotelPriceLow;
	}
	public void setHasHotelPriceLow(Double hasHotelPriceLow) {
		this.hasHotelPriceLow = hasHotelPriceLow;
	}
	public Double getHasHotelPriceHigh() {
		return hasHotelPriceHigh;
	}
	public void setHasHotelPriceHigh(Double hasHotelPriceHigh) {
		this.hasHotelPriceHigh = hasHotelPriceHigh;
	}
	public String getHasHotelRoomTypes() {
		return hasHotelRoomTypes;
	}
	public void setHasHotelRoomTypes(String hasHotelRoomTypes) {
		this.hasHotelRoomTypes = hasHotelRoomTypes;
	}
	public String getHasNearbyTransportMode() {
		return hasNearbyTransportMode;
	}
	public void setHasNearbyTransportMode(String hasNearbyTransportMode) {
		this.hasNearbyTransportMode = hasNearbyTransportMode;
	}
	public String getHasHowToReach() {
		return hasHowToReach;
	}
	public void setHasHowToReach(String hasHowToReach) {
		this.hasHowToReach = hasHowToReach;
	}
	public int getHasHotelStarCategory() {
		return hasHotelStarCategory;
	}
	public void setHasHotelStarCategory(int hasHotelStarCategory) {
		this.hasHotelStarCategory = hasHotelStarCategory;
	}
	public TypeOfFood getHasTypeOfFood() {
		return hasTypeOfFood;
	}
	public void setHasTypeOfFood(TypeOfFood hasTypeOfFood) {
		this.hasTypeOfFood = hasTypeOfFood;
	}
	public Set<String> getHasFacilities() {
		return hasFacilities;
	}
	public void setHasFacilities(Set<String> hasFacilities) {
		this.hasFacilities = hasFacilities;
	}
	
	
}

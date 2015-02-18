package eu.threecixty.profile.oldmodels;

import eu.threecixty.ThreeCixtyExpression;
import eu.threecixty.profile.annotations.Description;

/**
 * Address class that holds residence address information of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Address {
	@Description(hasText="UniqueID")
	private String hasAddressURI="";
	@Description(hasText="Post office box")
	private Long postOfficeBox=0L;
	@Description(hasText="Street Address")
	private String streetAddress="";
	@Description(hasText="Name of the town")
	private String townName="";
	@Description(hasText="Postal code of the street")
	private String postalCode="";
	@Description(hasText="Country of residence")
	private String countryName="";
	@Description(hasText="HomeLocationURI")
	private String hasHomeLocationURI="";
	@Description(hasText="GeoCoordinatesURI")
	private String hasGeoCoordinatesURI="";
	@Description(hasText="longitude information. Uses http://www.w3.org/2006/vcard/ns#longitude")
	private double longitute=0.0;
	@Description(hasText="latitude information. Uses http://www.w3.org/2006/vcard/ns#latitude")
	private double latitude=0.0;
	
	// For generating expression filter
	private ThreeCixtyExpression threeCixtyExpr;
	
	
	public String getHasAddressURI() {
		return hasAddressURI;
	}
	public void setHasAddressURI(String hasAddressURI) {
		this.hasAddressURI = hasAddressURI;
	}
	public Long getPostOfficeBox() {
		return postOfficeBox;
	}
	public void setPostOfficeBox(Long postOfficeBox) {
		this.postOfficeBox = postOfficeBox;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getTownName() {
		return townName;
	}
	public void setTownName(String townName) {
		this.townName = townName;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getHasHomeLocationURI() {
		return hasHomeLocationURI;
	}
	public void setHasHomeLocationURI(String hasHomeLocationURI) {
		this.hasHomeLocationURI = hasHomeLocationURI;
	}
	public String getHasGeoCoordinatesURI() {
		return hasGeoCoordinatesURI;
	}
	public void setHasGeoCoordinatesURI(String hasGeoCoordinatesURI) {
		this.hasGeoCoordinatesURI = hasGeoCoordinatesURI;
	}
	
	
	public double getLongitute() {
		return longitute;
	}
	public void setLongitute(double longitute) {
		this.longitute = longitute;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public ThreeCixtyExpression getThreeCixtyExpr() {
		return threeCixtyExpr;
	}
	public void setThreeCixtyExpr(ThreeCixtyExpression threeCixtyExpr) {
		this.threeCixtyExpr = threeCixtyExpr;
	}
}
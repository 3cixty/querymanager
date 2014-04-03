package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Address class that holds residence address information of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Address {
	@Description(hasText="Post office box")
	private Long postOfficeBox;
	@Description(hasText="Street Address")
	private String streetAddress="";
	@Description(hasText="Name of the town")
	private String townName="";
	@Description(hasText="Postal code of the street")
	private String postalCode="";
	@Description(hasText="Country of residence")
	private String countryName="";
	@Description(hasText="longitude information. Uses http://www.w3.org/2006/vcard/ns#longitude")
	private Double longitute=0.0;
	@Description(hasText="latitude information. Uses http://www.w3.org/2006/vcard/ns#latitude")
	private Double lontitude=0.0;
	
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
	public Double getLongitute() {
		return longitute;
	}
	public void setLongitute(Double longitute) {
		this.longitute = longitute;
	}
	public Double getLontitude() {
		return lontitude;
	}
	public void setLontitude(Double lontitude) {
		this.lontitude = lontitude;
	}
}

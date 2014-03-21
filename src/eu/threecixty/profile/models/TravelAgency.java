package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;
import eu.threecixty.profile.annotations.Extend;
import eu.threecixty.profile.annotations.Uses;

/**
 * Agency Details
 * @author Rachit.Agarwal@inria.fr
 *
 */
@Extend(hasText= "http://www.w3.org/2006/vcard/ns#Organization")
public class TravelAgency {
	@Extend(hasText="http://www.w3.org/2006/vcard/ns#Kind")
	private String hasName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;  
	@Description(hasText="Agency details")
	private String hasDetail="";
	@Description(hasText="Agency chains")
	private Set <Address> hasChains;
	@Uses(hasText="http://www.w3.org/2006/vcard/ns#url")
	private String hasURL;
	@Description(hasText="lowest Price")
	private Double hasPriceLow=0.0;
	@Description(hasText="highest Price")
	private Double hasPriceHigh=0.0;
	@Description(hasText="Facilities available")
	private Set <String> hasFacilities;
	@Description(hasText="comments")
	private Set <String> hasKeyTags;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public Address getHasAddress() {
		return hasAddress;
	}
	public void setHasAddress(Address hasAddress) {
		this.hasAddress = hasAddress;
	}
	public String getHasDetail() {
		return hasDetail;
	}
	public void setHasDetail(String hasDetail) {
		this.hasDetail = hasDetail;
	}
	public Set<Address> getHasChains() {
		return hasChains;
	}
	public void setHasChains(Set<Address> hasChains) {
		this.hasChains = hasChains;
	}
	public String getHasURL() {
		return hasURL;
	}
	public void setHasURL(String hasURL) {
		this.hasURL = hasURL;
	}
	public Double getHasPriceLow() {
		return hasPriceLow;
	}
	public void setHasPriceLow(Double hasPriceLow) {
		this.hasPriceLow = hasPriceLow;
	}
	public Double getHasPriceHigh() {
		return hasPriceHigh;
	}
	public void setHasPriceHigh(Double hasPriceHigh) {
		this.hasPriceHigh = hasPriceHigh;
	}
	public Set<String> getHasFacilities() {
		return hasFacilities;
	}
	public void setHasFacilities(Set<String> hasFacilities) {
		this.hasFacilities = hasFacilities;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

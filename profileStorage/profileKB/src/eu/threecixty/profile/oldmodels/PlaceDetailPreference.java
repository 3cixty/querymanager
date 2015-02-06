package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;

public class PlaceDetailPreference {
	@Description(hasText="UniqueID")
	private String hasPlaceDetailPreferenceURI="";
	@Description(hasText="preferred approximate address of the searched place")
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
	private Address hasPreferredApproximateAddress;
	@Description(hasText="preferred Nature of the place")
	private NatureOfPlace hasNatureOfPlace;
	@Description(hasText = "preffered tags associated with the place")
	private Set <String> hasPreferredPlaceKeyTags;
	
	
	public String getHasPlaceDetailPreferenceURI() {
		return hasPlaceDetailPreferenceURI;
	}
	public void setHasPlaceDetailPreferenceURI(String hasPlaceDetailPreferenceURI) {
		this.hasPlaceDetailPreferenceURI = hasPlaceDetailPreferenceURI;
	}
	public Address getHasPreferredApproximateAddress() {
		return hasPreferredApproximateAddress;
	}
	public void setHasPreferredApproximateAddress(
			Address hasPreferredApproximateAddress) {
		this.hasPreferredApproximateAddress = hasPreferredApproximateAddress;
	}
	public NatureOfPlace getHasNatureOfPlace() {
		return hasNatureOfPlace;
	}
	public void setHasNatureOfPlace(NatureOfPlace hasNatureOfPlace) {
		this.hasNatureOfPlace = hasNatureOfPlace;
	}
	public Set<String> getHasPreferredPlaceKeyTags() {
		return hasPreferredPlaceKeyTags;
	}
	public void setHasPreferredPlaceKeyTags(Set<String> hasPreferredPlaceKeyTags) {
		this.hasPreferredPlaceKeyTags = hasPreferredPlaceKeyTags;
	}
	
}

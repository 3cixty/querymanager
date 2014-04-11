package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;
import eu.threecixty.profile.annotations.Extend;

/**
 * information about a Place
 * @author Rachit.Agarwal@inria.fr
 *
 */
@Extend(hasText="http://www.w3.org/2006/vcard/ns#Location")
public class PlaceDetail {
	@Description(hasText="name of the place")
	private String hasName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
	private Address hasAddress;
	@Description(hasText="Description of the place")
	private String hasDetails="";
	@Description(hasText="Nature of the place")
	private NatureOfPlace isTheNatureOfPlace;
	@Description(hasText = "comments")
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
	public String getHasDetails() {
		return hasDetails;
	}
	public void setHasDetails(String hasDetails) {
		this.hasDetails = hasDetails;
	}
	public NatureOfPlace getIsTheNatureOfPlace() {
		return isTheNatureOfPlace;
	}
	public void setIsTheNatureOfPlace(NatureOfPlace isTheNatureOfPlace) {
		this.isTheNatureOfPlace = isTheNatureOfPlace;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	
}

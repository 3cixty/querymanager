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
	@Extend(hasText="http://www.w3.org/2006/vcard/ns#Kind")
	String hasName;
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
	Address hasAddress;
	@Description(hasText="Description of the place")
	String hasDetails;
	@Description(hasText="Nature of the place")
	NatureOfPlace isTheNatureOfPlace;
	@Description(hasText = "comments")
	Set <String> hasKeyTags;
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

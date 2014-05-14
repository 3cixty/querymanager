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
	private String hasPlaceName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
	private Address hasAddress;
	@Description(hasText="Description of the place")
	private String hasDetails="";
	@Description(hasText="Nature of the place")
	private NatureOfPlace hasNatureOfPlace;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	// This attribute is used to deal with a region for a place
	private Area area;
	public String getHasPlaceName() {
		return hasPlaceName;
	}
	public void setHasPlaceName(String hasPlaceName) {
		this.hasPlaceName = hasPlaceName;
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
	public NatureOfPlace getHasNatureOfPlace() {
		return hasNatureOfPlace;
	}
	public void setHasNatureOfPlace(NatureOfPlace hasNatureOfPlace) {
		this.hasNatureOfPlace = hasNatureOfPlace;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	
}

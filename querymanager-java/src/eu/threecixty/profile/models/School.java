package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;
import eu.threecixty.profile.annotations.Uses;

/**
 * @Extend (hasText="http://www.w3.org/2006/vcard/ns#Organization")
 * School Information
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class School {
	@Description(hasText="name of the school")
	private String hasSchoolName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
	private Address hasAddress;
	@Description(hasText="Description of the school")
	private String hasDetails="";
	@Uses(hasText="http://www.w3.org/2006/vcard/ns#url")
	private String hasURL;
	@Description(hasText="Telephone number")
	private String hasTelephone;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	public String getHasSchoolName() {
		return hasSchoolName;
	}
	public void setHasSchoolName(String hasSchoolName) {
		this.hasSchoolName = hasSchoolName;
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
	public String getHasURL() {
		return hasURL;
	}
	public void setHasURL(String hasURL) {
		this.hasURL = hasURL;
	}
	public String getHasTelephone() {
		return hasTelephone;
	}
	public void setHasTelephone(String hasTelephone) {
		this.hasTelephone = hasTelephone;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	
}

/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;
import eu.threecixty.profile.annotations.Uses;

/**
 * @Extend (hasText="http://www.w3.org/2006/vcard/ns#Organization")
 * School Information
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

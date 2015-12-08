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
import eu.threecixty.profile.annotations.Extend;

/**
 * information about a Place
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
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof PlaceDetail)) return false;
		PlaceDetail pd = (PlaceDetail) object;
		if (hasPlaceName == null) {
			if (pd.hasPlaceName != null) return false;
		} else if (!hasPlaceName.equals(pd.hasPlaceName)) return false;
		if (hasAddress == null) {
			if (pd.hasAddress != null) return false;
		} else if (!hasAddress.equals(pd.hasAddress)) return false;
		if (hasNatureOfPlace == null) {
			if (pd.hasNatureOfPlace != null) return false;
		} else if (hasNatureOfPlace != pd.hasNatureOfPlace) return false;
		return true;
	}
}

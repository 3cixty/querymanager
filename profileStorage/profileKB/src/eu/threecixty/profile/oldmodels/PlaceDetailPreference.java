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

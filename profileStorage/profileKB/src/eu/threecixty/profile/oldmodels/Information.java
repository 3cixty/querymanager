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
import java.util.Date;

import eu.threecixty.profile.annotations.Description;

public class Information {
	@Description(hasText = "start date")
	private Date hasFrom;
	@Description(hasText = "end date. can be empty")
	private Date hasUntil;
	@Description(hasText = "Details")
	private String hasDetails="";
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	
	public Date getHasFrom() {
		return hasFrom;
	}
	public void setHasFrom(Date hasFrom) {
		this.hasFrom = hasFrom;
	}
	public Date getHasUntil() {
		return hasUntil;
	}
	public void setHasUntil(Date hasUntil) {
		this.hasUntil = hasUntil;
	}
	public String getHasDetails() {
		return hasDetails;
	}
	public void setHasDetails(String hasDetails) {
		this.hasDetails = hasDetails;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

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

public class TemporalDetails {
	@Description(hasText = "start date")
	private Date hasDateFrom;
	@Description(hasText = "end date. can be empty")
	private Date hasDateUntil;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	
	public Date getHasDateFrom() {
		return hasDateFrom;
	}
	public void setHasDateFrom(Date hasDateFrom) {
		this.hasDateFrom = hasDateFrom;
	}
	public Date getHasDateUntil() {
		return hasDateUntil;
	}
	public void setHasDateUntil(Date hasDateUntil) {
		this.hasDateUntil = hasDateUntil;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof TemporalDetails)) return false;
		TemporalDetails td = (TemporalDetails) object;
		if (hasDateFrom == null) {
			if (td.hasDateFrom != null) return false;
		} else if (!hasDateFrom.equals(td.hasDateFrom)) return false;
		if (hasDateUntil == null) {
			if (td.hasDateUntil != null) return false;
		} else if (!hasDateUntil.equals(td.hasDateUntil)) return false;
		return true;
	}
}

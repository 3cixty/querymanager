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

import java.util.Date;
import java.util.Set;

import eu.threecixty.profile.annotations.Description;

public class EventDetailPreference {
	@Description(hasText="the preferred Nature of the event")
    private NatureOfEvent hasNatureOfEvent;
    @Description(hasText = "preferred start date")
	private Date hasPreferredStartDate;
	@Description(hasText = "preferred end date. can be empty")
    private Date hasPreferredEndDate;
	@Description(hasText = "preferred tags associated with the event")
    private Set <String> hasPreferredEventKeyTags;
	public NatureOfEvent getHasNatureOfEvent() {
		return hasNatureOfEvent;
	}
	public void setHasNatureOfEvent(NatureOfEvent hasNatureOfEvent) {
		this.hasNatureOfEvent = hasNatureOfEvent;
	}
	public Date getHasPreferredStartDate() {
		return hasPreferredStartDate;
	}
	public void setHasPreferredStartDate(Date hasPreferredStartDate) {
		this.hasPreferredStartDate = hasPreferredStartDate;
	}
	public Date getHasPreferredEndDate() {
		return hasPreferredEndDate;
	}
	public void setHasPreferredEndDate(Date hasPreferredEndDate) {
		this.hasPreferredEndDate = hasPreferredEndDate;
	}
	public Set<String> getHasPreferredEventKeyTags() {
		return hasPreferredEventKeyTags;
	}
	public void setHasPreferredEventKeyTags(Set<String> hasPreferredEventKeyTags) {
		this.hasPreferredEventKeyTags = hasPreferredEventKeyTags;
	}
	
}

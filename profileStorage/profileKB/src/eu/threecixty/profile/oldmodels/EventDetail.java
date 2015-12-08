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

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;

/**
 * information about a Event
 *
 */
public class EventDetail{
	@Description(hasText="name of the event")
	private String hasEventName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;
	@Description(hasText = "Details")
	private String hasDetails="";
	@Description(hasText="Start and end information about the event")
	private TemporalDetails hasTemporalDetails;
	@Description(hasText="Nature of the event")
    private NatureOfEvent hasNatureOfEvent;
	public String getHasEventName() {
		return hasEventName;
	}
	public void setHasEventName(String hasEventName) {
		this.hasEventName = hasEventName;
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
	public TemporalDetails getHasTemporalDetails() {
		return hasTemporalDetails;
	}
	public void setHasTemporalDetails(TemporalDetails hasTemporalDetails) {
		this.hasTemporalDetails = hasTemporalDetails;
	}
	public NatureOfEvent getHasNatureOfEvent() {
		return hasNatureOfEvent;
	}
	public void setHasNatureOfEvent(NatureOfEvent hasNatureOfEvent) {
		this.hasNatureOfEvent = hasNatureOfEvent;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof EventDetail)) return false;
		EventDetail ed = (EventDetail) object;
		if (hasEventName == null) {
			if (ed.hasEventName != null) return false;
		} else if (!hasEventName.equals(ed.hasEventName)) return false;
		if (hasNatureOfEvent == null) {
			if (ed.hasNatureOfEvent != null) return false;
		} else if (hasNatureOfEvent != ed.hasNatureOfEvent) return false;
		if (hasTemporalDetails == null) {
			if (ed.hasTemporalDetails != null) return false;
		} else if (!hasTemporalDetails.equals(ed.hasTemporalDetails)) return false;
		return true;
	}
}

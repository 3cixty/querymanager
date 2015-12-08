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

/**
 * Event information
 *
 */
public class UserEventRating {
	@Description(hasText="UniqueID")
	private String hasUserEventRatingURI="";
	@Description(hasText="Event details")
	private EventDetail hasEventDetail;
	@Description(hasText="User rating of the event")
	private Rating hasRating;
	@Description(hasText="Number of times user visited the event")
	private int hasNumberOfTimesVisited;
	
	// data is new 
	private Boolean newForKB = null;
	
	public String getHasUserEventRatingURI() {
		return hasUserEventRatingURI;
	}
	public void setHasUserEventRatingURI(String hasUserEventRatingURI) {
		this.hasUserEventRatingURI = hasUserEventRatingURI;
	}
	public EventDetail getHasEventDetail() {
		return hasEventDetail;
	}
	public void setHasEventDetail(EventDetail hasEventDetail) {
		this.hasEventDetail = hasEventDetail;
	}
	public Rating getHasRating() {
		return hasRating;
	}
	public void setHasRating(Rating hasRating) {
		this.hasRating = hasRating;
	}
	public int getHasNumberOfTimesVisited() {
		return hasNumberOfTimesVisited;
	}
	public void setHasNumberOfTimesVisited(int hasNumberOfTimesVisited) {
		this.hasNumberOfTimesVisited = hasNumberOfTimesVisited;
	}
	public Boolean getNewForKB() {
		return newForKB;
	}
	public void setNewForKB(Boolean newForKB) {
		this.newForKB = newForKB;
	}
	
	
}

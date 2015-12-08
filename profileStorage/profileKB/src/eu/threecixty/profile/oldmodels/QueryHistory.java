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

import com.hp.hpl.jena.query.Query;


import eu.threecixty.profile.annotations.Description;

/**
 * History of user searched queries 
 *
 */
public class QueryHistory {
	@Description(hasText="user Input query")
	private Query query;
	@Description(hasText="time at which the user queried the KB")
    private Date hasQueringTime;
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public Date getHasQueringTime() {
		return hasQueringTime;
	}
	public void setHasQueringTime(Date hasQueringTime) {
		this.hasQueringTime = hasQueringTime;
	}
	
	
}

/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.oauth.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * 
 * For developers who register to be 3cixty developers.
 *
 */
@Entity
@DiscriminatorValue("Developer")
public class Developer extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set <App> apps = new HashSet <App>();
	
	public Developer() {
		super();
	}

	public Developer(Integer id, String uid) {
		super(id, uid);
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "developer")
	public Set<App> getApps() {
		return apps;
	}

	public void setApps(Set<App> apps) {
		this.apps = apps;
	}
}

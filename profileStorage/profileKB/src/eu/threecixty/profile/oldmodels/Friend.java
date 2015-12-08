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

import eu.threecixty.profile.*;
import eu.threecixty.profile.annotations.Description;

/**
 * User Friend class. It holds the relation of a user with another user
 *
 */
public class Friend {
	@Description(hasText = "Other profile")
	private UserProfile hasUser;
	@Description(hasText = "relationship with the other user")
    private Relationship hasType;
	@Description(hasText = "comments on the relationship")
    private Set <String> hasKeyTags;
	public UserProfile getHasUser() {
		return hasUser;
	}
	public void setHasUser(UserProfile hasUser) {
		this.hasUser = hasUser;
	}
	public Relationship getHasType() {
		return hasType;
	}
	public void setHasType(Relationship hasType) {
		this.hasType = hasType;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}

}

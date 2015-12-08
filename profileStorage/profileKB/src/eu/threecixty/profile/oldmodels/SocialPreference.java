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

public class SocialPreference {
	private Set <Accompanying> hasAccompany;

	public Set<Accompanying> getHasAccompany() {
		return hasAccompany;
	}

	public void setHasAccompany(Set<Accompanying> hasAccompany) {
		this.hasAccompany = hasAccompany;
	}
	
	
}

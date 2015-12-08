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

/**
 * Skills a user has
 *
 */
public class Skills {
	@Description(hasText="Name of the skill")
	private String hasSkillName;
	@Description(hasText="Maturity Level of the user on the skill")
	private MaturityLevel hasMaturityLevel;
	@Description(hasText="comments")
	private Set <String> keyTags;
	public String getHasSkillName() {
		return hasSkillName;
	}
	public void setHasSkillName(String hasSkillName) {
		this.hasSkillName = hasSkillName;
	}
	public MaturityLevel getHasMaturityLevel() {
		return hasMaturityLevel;
	}
	public void setHasMaturityLevel(MaturityLevel hasMaturityLevel) {
		this.hasMaturityLevel = hasMaturityLevel;
	}
	public Set<String> getKeyTags() {
		return keyTags;
	}
	public void setKeyTags(Set<String> keyTags) {
		this.keyTags = keyTags;
	}
	
}

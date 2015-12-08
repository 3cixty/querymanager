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
 * particular language a user speaks
 *
 */
public class Language extends LanguageDetail{
	@Description(hasText="user knowledge about the language")
    private UserLanguageState hasLanguageState;          
	@Description(hasText="user would like to gather what knowledge about the language")
    private UserLanguageState wantedLanguageState;         
	@Description(hasText="comments")
    private Set <String> hasKeyTags;
	public UserLanguageState getHasLanguageState() {
		return hasLanguageState;
	}
	public void setHasLanguageState(UserLanguageState hasLanguageState) {
		this.hasLanguageState = hasLanguageState;
	}
	public UserLanguageState getWantedLanguageState() {
		return wantedLanguageState;
	}
	public void setWantedLanguageState(UserLanguageState wantedLanguageState) {
		this.wantedLanguageState = wantedLanguageState;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}

    
}

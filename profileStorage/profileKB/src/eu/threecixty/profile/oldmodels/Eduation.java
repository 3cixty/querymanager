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
 * Educational Background of the user
 *
 */
public class Eduation {
	@Description(hasText="Higest Level Of Aquired Education")
	private HigestLevelOfAquiredEducation hasHigestLevelOfAquiredEducation;
	@Description(hasText="School Attended")
	private Set <SchoolAttended> hasSchoolAttended; 
	@Description(hasText="Courses Undertaken")
	private Set <Course> hasTakenCourse;
	@Description(hasText="comments")
	private Set <String> hasKeyTags;
	public HigestLevelOfAquiredEducation getHasHigestLevelOfAquiredEducation() {
		return hasHigestLevelOfAquiredEducation;
	}
	public void setHasHigestLevelOfAquiredEducation(
			HigestLevelOfAquiredEducation hasHigestLevelOfAquiredEducation) {
		this.hasHigestLevelOfAquiredEducation = hasHigestLevelOfAquiredEducation;
	}
	public Set<SchoolAttended> getHasSchool() {
		return hasSchoolAttended;
	}
	public void setHasSchoolAttended(Set<SchoolAttended> hasSchoolAttended) {
		this.hasSchoolAttended = hasSchoolAttended;
	}
	public Set<Course> getHasTakenCourse() {
		return hasTakenCourse;
	}
	public void setHasTakenCourse(Set<Course> hasTakenCourse) {
		this.hasTakenCourse = hasTakenCourse;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

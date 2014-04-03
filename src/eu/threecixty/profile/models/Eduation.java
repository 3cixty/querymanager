package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * Educational Background of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Eduation {
	@Description(hasText="Higest Level Of Aquired Education")
	private HigestLevelOfAquiredEducation hasHigestLevelOfAquiredEducation;
	@Description(hasText="School Attended")
	private Set <SchoolAttended> hasAttendedSchool; 
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
	public Set<SchoolAttended> getHasAttendedSchool() {
		return hasAttendedSchool;
	}
	public void setHasAttendedSchool(Set<SchoolAttended> hasAttendedSchool) {
		this.hasAttendedSchool = hasAttendedSchool;
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

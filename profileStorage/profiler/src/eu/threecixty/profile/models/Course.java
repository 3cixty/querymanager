package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Uses;
/**
 * Course details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Course extends EventDetail{
	@Description(hasText="Name of the instructor")
	private String hasCourseInstructor="";
	@Description(hasText="Duration of the course")
	private String hasCourseDuration="";
	@Description(hasText="is offered by")
	private School isOfferedby;
	@Uses(hasText="http://www.w3.org/2006/vcard/ns#url")
	private String hasURL="";
	@Description(hasText="type of course")
  	private CourseType hasCourseType;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	public String getHasCourseInstructor() {
		return hasCourseInstructor;
	}
	public void setHasCourseInstructor(String hasCourseInstructor) {
		this.hasCourseInstructor = hasCourseInstructor;
	}
	public String getHasCourseDuration() {
		return hasCourseDuration;
	}
	public void setHasCourseDuration(String hasCourseDuration) {
		this.hasCourseDuration = hasCourseDuration;
	}
	public School getIsOfferedby() {
		return isOfferedby;
	}
	public void setIsOfferedby(School isOfferedby) {
		this.isOfferedby = isOfferedby;
	}
	public String getHasURL() {
		return hasURL;
	}
	public void setHasURL(String hasURL) {
		this.hasURL = hasURL;
	}
	public CourseType getHasCourseType() {
		return hasCourseType;
	}
	public void setHasCourseType(CourseType hasCourseType) {
		this.hasCourseType = hasCourseType;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
}

package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Uses;
/**
 * Course details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Course {
	@Description(hasText="name of the cource")
	private String hasName="";
	@Description(hasText="Name of the instructor")
	private String hasCourseInstructor="";
	@Description(hasText="Duration of the course")
	private String hasCourseDuration="";
	@Description(hasText="is offered by")
	private School isOfferedby;
	@Description(hasText="Description of the place")
	private String hasDetails="";
	@Uses(hasText="http://www.w3.org/2006/vcard/ns#url")
	private String hasURL="";
	@Description(hasText="Start and end information about the event")
    private Information hasInformation;
	@Description(hasText="type of course")
  	private CourseType isCourseType;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
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
	public String getHasDetails() {
		return hasDetails;
	}
	public void setHasDetails(String hasDetails) {
		this.hasDetails = hasDetails;
	}
	public String getHasURL() {
		return hasURL;
	}
	public void setHasURL(String hasURL) {
		this.hasURL = hasURL;
	}
	public Information getHasInformation() {
		return hasInformation;
	}
	public void setHasInformation(Information hasInformation) {
		this.hasInformation = hasInformation;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
}

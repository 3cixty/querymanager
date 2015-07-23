package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.elements.ElementDetails;

public class UserRelatedInformation {

	private String firstName;
	private String lastName;
	private List <ElementDetails> wishesList;
	private List <Friend> knows;
	private List <Friend> peopleHaveMeInKnows;
	private String extraInfo;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<ElementDetails> getWishesList() {
		return wishesList;
	}
	public void setWishesList(List<ElementDetails> wishesList) {
		this.wishesList = wishesList;
	}
	public List<Friend> getKnows() {
		return knows;
	}
	public void setKnows(List<Friend> knows) {
		this.knows = knows;
	}
	public List<Friend> getPeopleHaveMeInKnows() {
		return peopleHaveMeInKnows;
	}
	public void setPeopleHaveMeInKnows(List<Friend> peopleHaveMeInKnows) {
		this.peopleHaveMeInKnows = peopleHaveMeInKnows;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
}

package eu.threecixty.profile;

import java.util.List;
import java.util.Set;

import eu.threecixty.profile.elements.ElementDetails;
import eu.threecixty.profile.oldmodels.Accompanying;

/**
 * 
 * This class is used to represent all personal information stored by 3cixty platform.
 * The class is used to address the question about user privacy.
 *
 */
public class UserRelatedInformation {

	private String firstName;
	private String lastName;
	/**Google account, Facebook account, Mobidot account*/
	private List <AssociatedAccount> accounts;
	private List <ElementDetails> wishesList;
	
	/**The attribute which contains information about what the user knows (about other users)*/
	private List <Friend> knows;
	private List <Friend> peopleHaveMeInKnows;
	private Set <Accompanying> accompanyings;
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
	public List<AssociatedAccount> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<AssociatedAccount> accounts) {
		this.accounts = accounts;
	}
	public Set<Accompanying> getAccompanyings() {
		return accompanyings;
	}
	public void setAccompanyings(Set<Accompanying> accompanyings) {
		this.accompanyings = accompanyings;
	}
}

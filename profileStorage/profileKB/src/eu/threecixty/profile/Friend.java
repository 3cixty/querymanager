package eu.threecixty.profile;

import java.util.List;

public class Friend {

	private String uid;
	private String firstName;
	private String lastName;
	private String source;
	private String accountId;
	private List <Friend> derivedFrom;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<Friend> getDerivedFrom() {
		return derivedFrom;
	}
	public void setDerivedFrom(List<Friend> derivedFrom) {
		this.derivedFrom = derivedFrom;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public Friend clone() {
		Friend clonedFriend = new Friend();
		clonedFriend.accountId = accountId;
		clonedFriend.firstName = firstName;
		clonedFriend.lastName = lastName;
		clonedFriend.source = source;
		clonedFriend.uid = uid;
		return clonedFriend;
	}
}

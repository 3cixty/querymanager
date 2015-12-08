/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.util.List;

/**
 * 
 * This class is used to export user-related information.
 *
 */
public class Friend {

	private String uid; // 3cixty UID
	private String firstName; // first name
	private String lastName; // last name
	private String source; // source
	private String accountId; // account ID (Google UID, Facebook UID)
	
	/*
	 * This attribute can be only used if the user has been merged their account.
	 * So, <code>source</code> and <code>accountId</code> in the instance of
	 * Friend (container instance to expose to the world) must be empty as the
	 * instance is just a container to contain    
	 */
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

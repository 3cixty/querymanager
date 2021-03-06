/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;

/**
 * 
 * This class is to represent the user profile.
 *
 */
@Entity
@Table(name = "3cixty_user_profile", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"uid"})})
public class UserModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312079254371061310L;
	private Integer id;
	private String firstName;
	private String lastName;
	private String uid;
	private String profileImage;
	
	
	private Set <String> knows;
	
	private long lastCrawlTimeToKB;
	private Set <AccountModel> accounts;

	private AddressModel address;
	private Set <AccompanyingModel> accompanyings;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "firstName", nullable = true, length = 255)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name = "lastName", nullable = true, length = 255)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name = "uid", unique = true, nullable = false, length = 100)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "profileImage", nullable = true, length = 255)
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
	@Column(name = "lastCrawlTimeToKB")
	public long getLastCrawlTimeToKB() {
		return lastCrawlTimeToKB;
	}
	public void setLastCrawlTimeToKB(long lastCrawlTimeToKB) {
		this.lastCrawlTimeToKB = lastCrawlTimeToKB;
	}

	@CollectionOfElements
	public Set<String> getKnows() {
		return knows;
	}
	public void setKnows(Set<String> knows) {
		this.knows = knows;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel")
	public Set<AccountModel> getAccounts() {
		return accounts;
	}
	public void setAccounts(Set<AccountModel> accounts) {
		this.accounts = accounts;
	}
	
	@OneToOne(mappedBy="userModel", cascade = CascadeType.ALL)
	public AddressModel getAddress() {
		return address;
	}
	public void setAddress(AddressModel address) {
		this.address = address;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel")
	public Set<AccompanyingModel> getAccompanyings() {
		return accompanyings;
	}
	public void setAccompanyings(Set<AccompanyingModel> accompanyings) {
		this.accompanyings = accompanyings;
	}
}

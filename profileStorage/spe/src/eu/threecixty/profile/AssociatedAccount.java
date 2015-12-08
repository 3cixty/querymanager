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

/**
 * 
 * This class is to easily export associated account for user-related information.
 * The class is used in the SPEServices.
 *
 */
public class AssociatedAccount {

	private String accountId;
	private String source;
	private String password;
	private String mobidotUserId;

	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobidotUserId() {
		return mobidotUserId;
	}
	public void setMobidotUserId(String mobidotUserId) {
		this.mobidotUserId = mobidotUserId;
	}
}

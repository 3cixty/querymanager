/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.partners;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This class is to represent a 3cixty user with Mobidot account.
 *
 */
@Entity
@Table(name = "3cixty_partner_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = "uid")})
public class PartnerUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6848955943203673444L;

	private String uid; // 3cixty user_id
	
	private List <PartnerAccount> partnerAccounts;
	private Integer id;
	
	public PartnerUser() {
	}
	
	@Column(name = "uid", unique = true, nullable = false, length = 64)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "partnerUser")
	public List<PartnerAccount> getPartnerAccounts() {
		return partnerAccounts;
	}

	public void setPartnerAccounts(List<PartnerAccount> partnerAccounts) {
		this.partnerAccounts = partnerAccounts;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}

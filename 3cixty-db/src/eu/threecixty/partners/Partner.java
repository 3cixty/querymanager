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

import java.util.List;


public interface Partner {

	boolean addUser(PartnerUser user);
	boolean updateUser(PartnerUser user);
	boolean deleteUser(PartnerUser user);
	boolean exist(String uid);
	PartnerUser getUser(String uid);
	
	PartnerAccount findAccount(PartnerUser user, String appid, String role);
	
	List <PartnerAccount> getPartnerAccounts(String uid);
	
	boolean addAccount(PartnerAccount account);
	
	boolean replaceAccount(PartnerAccount oldAccount, PartnerAccount newAccount);
}

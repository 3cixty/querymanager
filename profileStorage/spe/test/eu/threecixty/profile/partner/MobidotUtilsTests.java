/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.partner;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.partners.MobidotUserUtils;

public class MobidotUtilsTests {

	@Test
	public void testName() {
		int ID=0;
		try{
			 ID=MobidotUserUtils.getMaxMobidotID(10118, 9429);
			System.out.println(ID);
		}catch(Exception e){
			e.printStackTrace();
		}
		Assert.assertTrue(ID==9429);
	}

}

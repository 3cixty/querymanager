/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager;

import org.junit.Assert;
import org.junit.Test;

public class KeyServiceTests extends HTTPCall {

	private static final String KEY_SERVICE = "http://localhost:8080/querymanagerServlet-1.0/services/key";
	
	private static final String A_VALID_KEY = "MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIyNzAzMzIwMzpfIWdjAWhmLjpmXF9x";
	
	@Test
	public void testValidateKey() {
		try {

			// URL = http://localhost:8080/querymanagerServlet-1.0/services/key/validate?key=MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIyNzAzMzIwMzpfIWdjAWhmLjpmXF9x
			StringBuffer buffer = new StringBuffer();
			sendGET(KEY_SERVICE , "validate?key=" + A_VALID_KEY, buffer);
			Assert.assertTrue(buffer.toString().equalsIgnoreCase("ok"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

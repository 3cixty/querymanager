/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.cache;

import java.util.Calendar;

/**
 * 
 * This class is used to cache data which contains both content and expiration.
 *
 */
public class CacheElement {
	private long lastValidTime;
	public String content;
	
	public CacheElement() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59);
		lastValidTime = calendar.getTimeInMillis();
	}
	
	public boolean isValid() {
		long currentTime = System.currentTimeMillis();
		return (lastValidTime >= currentTime);
	}
}

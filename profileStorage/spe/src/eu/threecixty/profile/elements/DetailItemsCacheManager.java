/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class is used to cache detail about items (Event or PoI) in Tomcat memory.
 *
 */
public class DetailItemsCacheManager {
	
	private static final DetailItemsCacheManager INSTANCE = new DetailItemsCacheManager();
	
	private Map <String, ElementDetails> caches;
	
	public static DetailItemsCacheManager getInstance() {
		return INSTANCE;
	}
	
	public void put(ElementDetails element) {
		if (element == null) return;
		caches.put(element.getId(), element);
	}
	
	public void put(Collection <ElementDetails> elements) {
		for (ElementDetails element: elements) {
			caches.put(element.getId(), element);
		}
	}
	
	public ElementDetails get(String id) {
		if (id == null) return null;
		return caches.get(id);
	}
	
	private DetailItemsCacheManager() {
		caches = new HashMap<String, ElementDetails>();
	}
}

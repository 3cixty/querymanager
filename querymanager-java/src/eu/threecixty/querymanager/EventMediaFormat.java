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

/**
 * This class is used to define supported EventMedia format.
 *
 */
public class EventMediaFormat {
	private String format;
	
	public static final EventMediaFormat RDF = new EventMediaFormat("rdf");
	public static final EventMediaFormat JSON = new EventMediaFormat("json");
	
	public static EventMediaFormat parse(String format) {
		if (format == null) return null;
		if (format.equals(RDF.format)) return RDF;
		if (format.equals(JSON.format)) return JSON;
		
		return null;
	}
	
	private EventMediaFormat(String format) {
		this.format = format;
	}
}

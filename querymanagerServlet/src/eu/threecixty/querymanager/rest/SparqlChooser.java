/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager.rest;

import eu.threecixty.Configuration;
import eu.threecixty.querymanager.AuthorizationBypassManager;

/**
 * 
 * This utility class is to get SPARQL endpoint, graph based on application key, city.
 *
 */
public class SparqlChooser {
	
	private static String END_POINT_WITH_E015 = null;
	private static String END_POINT_WITHOUT_E015 = null;

	/**
	 * Gets Event graph based on a given application key.
	 * @param key
	 * @param city
	 * 			The city parameter null means all cities in the KB.
	 * @return
	 */
	public static String getEventGraph(String key, String city) {
		if (city == null) return Constants.GRAPH_EVENTS;
		else return "<http://3cixty.com/" + city + "/events>";
//		return Constants.GRAPH_EVENTS;
	}
	
	/**
	 * Gets PoI graph based on a given application key.
	 * @param key
	 * @param city
	 * 			The city parameter null means all cities in the KB.
	 * @return
	 */
	public static String getPoIGraph(String key, String city) {
		if (city == null) return Constants.GRAPH_POIS;
		else return "<http://3cixty.com/" + city + "/places>";
//		return Constants.GRAPH_POIS;
	}
	
	/**
	 * Gets end point URL from a given app key.
	 * @param key
	 * @return
	 */
	public static String getEndPointUrl(String key) {
		if (AuthorizationBypassManager.getInstance().isFound(key)) {
			if (END_POINT_WITH_E015 == null) END_POINT_WITH_E015 = Configuration.getVirtuosoServer() + "/sparql";
			return END_POINT_WITH_E015;
		}
		if (END_POINT_WITHOUT_E015 == null) {
			END_POINT_WITHOUT_E015 = Configuration.getVirtuosoServerForOutside() + "/sparql";
		}
		return END_POINT_WITHOUT_E015;
	}
	
	private SparqlChooser() {
	}
}

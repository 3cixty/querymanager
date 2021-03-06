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

public class Constants {

	public static final String PREFIX_NAME = "";
	public static final String VERSION_2 = "";
	public static final String OFFSET_LINK_TO_KEYS_FOLDER = "./keys/";
	
	public static final String OFFSET_LINK_TO_ERROR_PAGE = "./";
	
	public static final String OFFSET_LINK_TO_DASHBOARD_PAGE = OFFSET_LINK_TO_ERROR_PAGE;
	
	public static final String OFFSET_LINK_TO_SETTINGS_PAGE = OFFSET_LINK_TO_ERROR_PAGE;
	
	public static final String OFFSET_LINK_TO_AUTH_PAGE = OFFSET_LINK_TO_ERROR_PAGE;
	
	public static final String WISH_LIST_SCOPE_NAME = "WishList";
	public static final String PROFILE_SCOPE_NAME = "Profile";
	public static final String JSON = "json";
	public static final String RDF = "rdf";
	
	public static final String GRAPH_EVENTS = "<http://3cixty.com/events>";
	public static final String GRAPH_POIS = "<http://3cixty.com/places>";
	public static final String CITY_MILAN = "milan";
	public static final String CITY_LONDON = "london";

	private Constants() {
	}
}

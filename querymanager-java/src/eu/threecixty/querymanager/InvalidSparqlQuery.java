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

public class InvalidSparqlQuery extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3984295715165030035L;
	
	public InvalidSparqlQuery() {
		super("The query does not conform to SPARQL 1.1");
	}
	
	public InvalidSparqlQuery(String msg) {
		super(msg);
	}

	public InvalidSparqlQuery(Throwable thr) {
		super(thr);
	}
	
	public InvalidSparqlQuery(String msg, Throwable thr) {
		super(msg, thr);
	}
}

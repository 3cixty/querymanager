/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

public class UnknownException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7350289072331172794L;

	public UnknownException() {
		super();
	}
	
	public UnknownException(String msg) {
		super(msg);
	}
	
	public UnknownException(Throwable thr) {
		super(thr);
	}
	
	public UnknownException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

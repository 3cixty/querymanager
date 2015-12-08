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

public class ThreeCixtyPermissionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1898737284643191683L;

	public ThreeCixtyPermissionException() {
	}

	public ThreeCixtyPermissionException(String msg) {
		super(msg);
	}

	public ThreeCixtyPermissionException(Throwable thr) {
		super(thr);
	}

	public ThreeCixtyPermissionException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

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

public class InvalidTrayElement extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3598109648241998188L;

	public InvalidTrayElement() {
		super();
	}
	
	public InvalidTrayElement(String msg) {
		super(msg);
	}
	
	public InvalidTrayElement(Throwable thr) {
		super(thr);
	}
	
	public InvalidTrayElement(String msg, Throwable thr) {
		super(msg, thr);
	}
}

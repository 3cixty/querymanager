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

/**
 * 
 * The class represents an event in detail.
 *
 */
public class ElementEventDetails extends ElementDetails {

	
	private String time_beginning;
	private String time_end;

	public String getTime_beginning() {
		return time_beginning;
	}
	public void setTime_beginning(String time_beginning) {
		this.time_beginning = time_beginning;
	}
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
	
	public ElementEventDetails export(String language) {
		ElementEventDetails eed = new ElementEventDetails();
		this.cloneTo(eed, language);

		eed.setTime_beginning(this.getTime_beginning());
		eed.setTime_end(this.getTime_end());

		return eed;
	}
	
}

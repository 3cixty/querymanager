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

/**
 * 
 * This class is used to represent a review to be exported to JSON format.
 *
 */
public class Review {
	private String text;
	private boolean translated;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isTranslated() {
		return translated;
	}
	public void setTranslated(boolean translated) {
		this.translated = translated;
	}
	
	public int hashCode() {
		if (text == null) return -1;
		return text.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Review)) return false;
		if (obj == this) return true;
		Review review = (Review) obj;
		if (text == null) {
			if (review.text != null) return false;
		} else {
			if (!text.equals(review.text)) return false;
		}
		return true;
	}
}

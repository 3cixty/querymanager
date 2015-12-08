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

import java.io.Serializable;

/**
 * This class represents a tray item.
 *
 */
public class Tray implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7343223023348165957L;
	private String element_id;
	private String element_type;
	private String element_title;
	private long timestamp;
	// real UID or junkID
	private String token;
	private String source;
	
	private boolean attend;
	private String attend_datetime;
	private int rating = -1;
	
	private String image_url;
	private Long creationTimestamp;
	
	public String getElement_id() {
		return element_id;
	}

	public void setElement_id(String element_id) {
		this.element_id = element_id;
	}

	public String getElement_type() {
		return element_type;
	}

	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}

	public String getElement_title() {
		return element_title;
	}

	public void setElement_title(String element_title) {
		this.element_title = element_title;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isAttend() {
		return attend;
	}

	public void setAttend(boolean attend) {
		this.attend = attend;
	}

	public String getAttend_datetime() {
		return attend_datetime;
	}

	public void setAttend_datetime(String attend_datetime) {
		this.attend_datetime = attend_datetime;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public Long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public enum OrderType {
		Desc, Asc
	}
}

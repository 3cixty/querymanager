/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.oldmodels;
import eu.threecixty.profile.annotations.Description;

public class Likes {
	@Description(hasText="UniqueID")
	private String hasLikesURI="";
	@Description(hasText="Name of the like")
	private String hasLikeName=""; 
	@Description(hasText="type of the entity liked")
	private LikeType hasLikeType;
	
	public String getHasLikesURI() {
		return hasLikesURI;
	}
	public void setHasLikesURI(String hasLikesURI) {
		this.hasLikesURI = hasLikesURI;
	}
	public String getHasLikeName() {
		return hasLikeName;
	}
	public void setHasLikeName(String hasLikeName) {
		this.hasLikeName = hasLikeName;
	}
	public LikeType getHasLikeType() {
		return hasLikeType;
	}
	public void setHasLikeType(LikeType hasLikeType) {
		this.hasLikeType = hasLikeType;
	}
	
}

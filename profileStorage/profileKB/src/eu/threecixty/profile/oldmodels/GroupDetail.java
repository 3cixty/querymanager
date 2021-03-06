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

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Extend;

/**
 * Group Details
 *
 */
@Extend(hasText="http://www.w3.org/2006/vcard/ns#Group")
public class GroupDetail {
	@Description(hasText="Name of the group")
    private String hasName="";
	@Description(hasText="Group details")
    private String hasDetail="";
	@Description(hasText="Moderator name")
    private String hasModerator="";    
	@Description(hasText="comments")
    private Set <String> haskeyTags;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public String getHasDetail() {
		return hasDetail;
	}
	public void setHasDetail(String hasDetail) {
		this.hasDetail = hasDetail;
	}
	public String getHasModerator() {
		return hasModerator;
	}
	public void setHasModerator(String hasModerator) {
		this.hasModerator = hasModerator;
	}
	public Set<String> getHaskeyTags() {
		return haskeyTags;
	}
	public void setHaskeyTags(Set<String> haskeyTags) {
		this.haskeyTags = haskeyTags;
	}

}

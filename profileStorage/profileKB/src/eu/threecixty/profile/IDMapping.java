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

public class IDMapping {

		private String threeCixtyID;
		private String mobidotUserName;
		private String mobidotID;
		public String getThreeCixtyID() {
			return threeCixtyID;
		}
		public void setThreeCixtyID(String threeCixtyID) {
			this.threeCixtyID = threeCixtyID;
		}
		public String getMobidotUserName() {
			return mobidotUserName;
		}
		public void setMobidotUserName(String mobidotUserName) {
			this.mobidotUserName = mobidotUserName;
		}
		public String getMobidotID() {
			return mobidotID;
		}
		public void setMobidotID(String mobidotID) {
			this.mobidotID = mobidotID;
		}
		
}

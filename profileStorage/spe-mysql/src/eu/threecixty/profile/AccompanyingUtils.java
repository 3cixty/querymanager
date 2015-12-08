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

import java.util.Set;

import org.hibernate.Session;

import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.userprofile.AccompanyingModel;
import eu.threecixty.userprofile.UserModel;

/**
 * 
 * Utility class to persist and read Accompanying to/from 3cixty database.
 *
 */
public class AccompanyingUtils {

	public static boolean findAccompanying(AccompanyingModel am,
			Set<Accompanying> accompanyings) {
		for (Accompanying accompanying: accompanyings) {
			if (equals(am, accompanying)) return true;
		}
		return false;
	}

	public static boolean findAccompanying(Accompanying accompanying,
			Set<AccompanyingModel> accompanyingModels) {
		for (AccompanyingModel am: accompanyingModels) {
			if (equals(am, accompanying)) return true;
		}
		return false;
	}

	public static AccompanyingModel save(Accompanying accompanying, UserModel userModel, Session session) {
		AccompanyingModel am = new AccompanyingModel();
		am.setAccompanyScore(accompanying.getHasAccompanyScore());
		am.setAccompanyValidity(accompanying.getHasAccompanyValidity());
		am.setAccompanyTime(accompanying.getHasAccompanyTime());
		am.setHasAccompanyUserid1ST(accompanying.getHasAccompanyUserid1ST());
		am.setHasAccompanyUserid2ST(accompanying.getHasAccompanyUserid2ST());
		am.setAccompanyId(accompanying.getHasAccompanyId());
		am.setUserModel(userModel);
		session.save(am);
		return am;
	}

	public static Accompanying createAccompanying(AccompanyingModel am) {
		Accompanying accompanying =new Accompanying();
		accompanying.setHasAccompanyScore(am.getAccompanyScore());
		accompanying.setHasAccompanyValidity(am.getAccompanyValidity());
		accompanying.setHasAccompanyTime(am.getAccompanyTime());
		accompanying.setHasAccompanyUserid2ST(am.getHasAccompanyUserid2ST());
		accompanying.setHasAccompanyUserid1ST(am.getHasAccompanyUserid1ST());
		accompanying.setHasAccompanyId(am.getAccompanyId());
		return accompanying;
	}
	
	private static boolean equals(AccompanyingModel am, Accompanying accompanying) {
		if (am.getAccompanyScore() == null) {
			if (accompanying.getHasAccompanyScore() != null) return false;
		} else {
			if (accompanying.getHasAccompanyScore() == null
					|| (accompanying.getHasAccompanyScore().doubleValue() != am.getAccompanyScore().doubleValue())) return false;
		}
		if (am.getAccompanyTime() == null) {
			if (accompanying.getHasAccompanyTime() != null) return false;
		} else {
			if (accompanying.getHasAccompanyTime() == null
					|| (accompanying.getHasAccompanyTime().longValue() != am.getAccompanyTime().longValue())) return false;
		}
		if (am.getAccompanyValidity() == null) {
			if (accompanying.getHasAccompanyValidity() != null) return false;
		} else {
			if (accompanying.getHasAccompanyValidity() == null
					|| (accompanying.getHasAccompanyValidity().longValue() != am.getAccompanyValidity().longValue())) return false;
		}
		if (am.getHasAccompanyUserid1ST() == null) {
			if (accompanying.getHasAccompanyUserid1ST() != null) return false;
		} else {
			if (accompanying.getHasAccompanyUserid1ST() == null
					|| (!accompanying.getHasAccompanyUserid1ST().equals(am.getHasAccompanyUserid1ST()))) return false;
		}
		if (am.getHasAccompanyUserid2ST() == null) {
			if (accompanying.getHasAccompanyUserid2ST() != null) return false;
		} else {
			if (accompanying.getHasAccompanyUserid2ST() == null
					|| (!accompanying.getHasAccompanyUserid2ST().equals(am.getHasAccompanyUserid2ST()))) return false;
		}
		return true;
	}
	
	private AccompanyingUtils() {
	}
}

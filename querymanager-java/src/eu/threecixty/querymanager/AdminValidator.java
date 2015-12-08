/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager;

import java.io.FileNotFoundException;

import java.io.File;
import java.util.Scanner;

/**
 * 
 * This class is used to check whether or not a given user name and password match with predefined admin info.
 *
 */
public class AdminValidator {
	private static String adminPwd = null;
	
    public AdminValidator(){}
	public boolean validate(String username, String password,String path){
		if (username.equals("3cixtyAdmin") && password.equals(getDbPassword(path))) {
			return true;
		}
		return false;
	}
	private String getDbPassword(String path) {
		if (adminPwd != null) return adminPwd;
		try {
            Scanner scanner = new Scanner(new File(path+ File.separatorChar+"WEB-INF"+File.separatorChar+"admin.properties"));
			adminPwd = scanner.nextLine();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return adminPwd;
	}
}


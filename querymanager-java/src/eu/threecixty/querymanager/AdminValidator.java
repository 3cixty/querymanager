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


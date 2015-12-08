/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.oauth.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScopeUtils {

	public static List <String> getScopeNames() {
		List <String> scopes = new ArrayList <String>();
		findScopes(scopes);
		return scopes;
	}
	
	private static void findScopes(List <String> scopes) {
		InputStream input = ScopeUtils.class.getResourceAsStream("/scopes_3cixty.properties");
		if (input == null) return;
		Scanner scanner = new Scanner(input);
		while (true) {
			if (!scanner.hasNextLine()) break;
			String line = scanner.nextLine();
			scopes.add(line);
		}
		scanner.close();
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ScopeUtils() {
	}
}

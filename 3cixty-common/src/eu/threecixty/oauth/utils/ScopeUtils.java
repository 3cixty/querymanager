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

package eu.threecixty.querymanager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.threecixty.Configuration;

/**
 * 
 * This class is used to check whether or not an app key is ignored to show authorization dialog.
 *
 */
public class AuthorizationBypassManager {

	private static final AuthorizationBypassManager instance = new AuthorizationBypassManager();
	
	// Map for better performance than List
	private Map <String, Boolean> appkeys;
	
	public static AuthorizationBypassManager getInstance() {
		return instance;
	}
	
	public boolean isFound(String appkey) {
		return appkeys.containsKey(appkey);
	}
	
	public Map <String, Boolean> getAppkeys() {
		return appkeys;
	}
	
	public void load() {
		String path = Configuration.path;
		File file = new File(path + File.separatorChar + "WEB-INF" + File.separatorChar + "authBypass.list");
		if (!file.exists()) return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (true) {
				String line = br.readLine();
				if (line == null) break;
				String appkey = line.trim();
				if (appkey.equals("")) continue;
				appkeys.put(appkey, true);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private AuthorizationBypassManager() {
		appkeys = new HashMap<String, Boolean>();
	}
}

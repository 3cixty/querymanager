package eu.threecixty.oauth;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class is to check whether or not a given appkey is found in the list of appkeys which
 * are fully trusted. This means there is no authorization process for asking permission.
 *
 */
public class OAuthBypassedManager {

	private static final OAuthBypassedManager INSTANCE = new OAuthBypassedManager();
	
	/**app keys which are bypassed OAuth server*/
	private Map <String, Boolean> appkeys;
	
	public static OAuthBypassedManager getInstance() {
		return INSTANCE;
	}
	
	public boolean isFound(String appkey) {
		if (appkey == null) return false;
		return appkeys.containsKey(appkey);
	}
	
	public void addAppKeys(Map <String, Boolean> maps) {
		if (maps == null) return;
		appkeys.putAll(maps);
	}
	
	private OAuthBypassedManager() {
		appkeys = new HashMap<String, Boolean>();
	}
}

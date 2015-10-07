package eu.threecixty.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
	
	private static final CacheManager instance = new CacheManager();
	
	private Map <String, CacheElement> cacheElements = new HashMap<String, CacheElement>();
	
	public static CacheManager getInstance() {
		return instance;
	}
	
	public String getCacheData(String query) {
		CacheElement cacheElement = cacheElements.get(query);
		if (cacheElement == null) return null;
		if (!cacheElement.isValid()) {
			cacheElements.remove(query);
		}
		return cacheElement.content;
	}
	
	public void putCacheData(String query, String result) {
		CacheElement cacheElement = new CacheElement();
		cacheElement.content = result;
		cacheElements.put(query, cacheElement);
	}
	
	private CacheManager() {
	}
}

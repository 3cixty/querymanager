package eu.threecixty.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

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
		if (result == null || result.equals("")) return;
		JSONObject rootJson = new JSONObject(result);
		// empty result should not be cached
		if (rootJson.getJSONObject("results").getJSONArray("bindings").length() == 0) return;
		CacheElement cacheElement = new CacheElement();
		cacheElement.content = result;
		cacheElements.put(query, cacheElement);
	}
	
	private void cleanData() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				for (String key: cacheElements.keySet()) {
					CacheElement ce = cacheElements.get(key);
					if (!ce.isValid()) cacheElements.remove(key);
				}
			}
		};
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 24, TimeUnit.HOURS);
	}
	
	private CacheManager() {
		cleanData();
	}
}

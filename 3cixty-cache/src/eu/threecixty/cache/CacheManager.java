package eu.threecixty.cache;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.threecixty.profile.SparqlEndPointUtils;

public class CacheManager {

	 private static final Logger LOGGER = Logger.getLogger(
			 CacheManager.class.getName());
	
	private static final String JSON_APP_FORMAT = "application/sparql-results+json";
	private static final CacheManager instance = new CacheManager();
	
	private Map <String, CacheElement> cacheElements = new ConcurrentHashMap<String, CacheElement>();
	
	public static CacheManager getInstance() {
		return instance;
	}
	
	public String getContent(String query) {
		CacheElement cacheElement = cacheElements.get(query);
		if (cacheElement == null) return null;
		if (cacheElement.isValid()) return cacheElement.content;
		synchronized (this) {
			StringBuilder result = new StringBuilder();
			try {
				SparqlEndPointUtils.executeQueryViaSPARQL(query, JSON_APP_FORMAT,
						SparqlEndPointUtils.HTTP_POST, result);
				cacheElement = new CacheElement();
				cacheElement.content = result.toString();
				cacheElements.put(query, cacheElement);
				return cacheElement.content;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Checks whether or not a given query should be executed through cache.
	 * @param query
	 * @return
	 */
	public boolean isQueryShouldBeExecutedViaCache(String query) {
		return cacheElements.containsKey(query);
	}
	
	public CacheManager() {
	}
}

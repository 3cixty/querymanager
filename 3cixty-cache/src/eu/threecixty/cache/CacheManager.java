package eu.threecixty.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import eu.threecixty.profile.SparqlEndPointUtils;

public class CacheManager {

	 private static final Logger LOGGER = Logger.getLogger(
			 CacheManager.class.getName());
	 
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final String JSON_APP_FORMAT = "application/sparql-results+json";
	private static final CacheManager instance = new CacheManager();
	
	private Map <String, CacheElement> cacheElements = new HashMap<String, CacheElement>();
	
	public static CacheManager getInstance() {
		return instance;
	}
	
	public void loadQueries(String path) {
		if (path == null) return;
		File file = new File(path);
		if (!file.isDirectory()) return;
		for (File tmpFile: file.listFiles()) {
			String query = getQuery(tmpFile);
			if (query == null) continue;
			if (DEBUG_MOD) LOGGER.info("query preloaded: " + query);
			executeQuery(query);
		}
	}
	
	public String getContent(String query) {
		CacheElement cacheElement = cacheElements.get(query);
		if (cacheElement == null) return null;
		if (cacheElement.isValid()) return cacheElement.content;
		synchronized (cacheElements) {
			return executeQuery(query);
		}
	}
	
	/**
	 * Checks whether or not a given query should be executed through cache.
	 * @param query
	 * @return
	 */
	public boolean isQueryShouldBeExecutedViaCache(String query) {
		if (DEBUG_MOD) LOGGER.info("Query to check in memory: " + query);
		boolean found = cacheElements.containsKey(query);
		if (DEBUG_MOD) LOGGER.info("Query found in memory: " + found);
		return found;
	}
	
	private synchronized String executeQuery(String query) {
		StringBuilder result = new StringBuilder();
		try {
			SparqlEndPointUtils.executeQueryViaSPARQL(query, JSON_APP_FORMAT,
					SparqlEndPointUtils.HTTP_POST, result);
			CacheElement cacheElement = new CacheElement();
			cacheElement.content = result.toString();
			cacheElements.put(query, cacheElement);
			return cacheElement.content;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private String getQuery(File file) {
		if (file.isDirectory()) return null;
		try {
			InputStream input = new FileInputStream(file);
			byte [] b = new byte [1024];
			StringBuilder sb = new StringBuilder();
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				sb.append(new String(b, 0, readBytes, "UTF-8"));
			}
			input.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private CacheManager() {
	}
}

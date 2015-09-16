package eu.threecixty.querymanager.rest;

import eu.threecixty.Configuration;
import eu.threecixty.querymanager.AuthorizationBypassManager;

public class SparqlChooser {

	/**
	 * Gets Event graph based on a given application key.
	 * @param key
	 * @return
	 */
	public static String getEventGraph(String key) {
		if (AuthorizationBypassManager.getInstance().isFound(key)) return Constants.GRAPH_EVENT_EXPLORMI;
		return Constants.GRAPH_EVENT_OTHERS;
	}
	
	/**
	 * Gets PoI graph based on a given application key.
	 * @param key
	 * @return
	 */
	public static String getPoIGraph(String key) {
		if (AuthorizationBypassManager.getInstance().isFound(key)) return Constants.GRAPH_POI_EXPLORMI;
		return Constants.GRAPH_POI_OTHERS;
	}
	
	/**
	 * Gets end point URL from a given app key.
	 * @param key
	 * @return
	 */
	public static String getEndPointUrl(String key) {
		return Configuration.getVirtuosoServer();
	}
	
	private SparqlChooser() {
	}
}

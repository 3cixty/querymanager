package eu.threecixty.querymanager.rest;

import eu.threecixty.Configuration;
import eu.threecixty.querymanager.AuthorizationBypassManager;

public class SparqlChooser {
	
	private static String END_POINT_WITH_E015 = null;
	private static String END_POINT_WITHOUT_E015 = null;

	/**
	 * Gets Event graph based on a given application key.
	 * @param key
	 * @param city
	 * 			The city parameter null means all cities in the KB.
	 * @return
	 */
	public static String getEventGraph(String key, String city) {
		if (city == null) return Constants.GRAPH_EVENTS;
		else return "<http://3cixty.com/" + city + "/events>";
	}
	
	/**
	 * Gets PoI graph based on a given application key.
	 * @param key
	 * @param city
	 * 			The city parameter null means all cities in the KB.
	 * @return
	 */
	public static String getPoIGraph(String key, String city) {
		if (city == null) return Constants.GRAPH_POIS;
		else return "<http://3cixty.com/" + city + "/places>";
	}
	
	/**
	 * Gets end point URL from a given app key.
	 * @param key
	 * @return
	 */
	public static String getEndPointUrl(String key) {
		if (AuthorizationBypassManager.getInstance().isFound(key)) {
			if (END_POINT_WITH_E015 == null) END_POINT_WITH_E015 = Configuration.getVirtuosoServer() + "/sparql";
			return END_POINT_WITH_E015;
		}
		if (END_POINT_WITHOUT_E015 == null) {
			END_POINT_WITHOUT_E015 = Configuration.getVirtuosoServerForOutside() + "/sparql";
		}
		return END_POINT_WITHOUT_E015;
	}
	
	private SparqlChooser() {
	}
}

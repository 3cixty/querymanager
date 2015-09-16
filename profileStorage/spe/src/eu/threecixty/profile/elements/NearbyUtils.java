package eu.threecixty.profile.elements;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.profile.SparqlEndPointUtils;


//TODO: need to remove condition with new Virtuoso updated
public class NearbyUtils {
	
	private static final double MIN_LAT = 45.35668565341486;
	private static final double MIN_LON = 9.011490619692509;
	private static final double SIZE_LAT = 0.00211498;
	private static final double SIZE_LON = 0.00300033;
	private static final int NUMBER_CELLS_AS_RADIUS_WITHOUT_CATEGORY_EVENT = 15;
	private static final int NUMBER_CELLS_AS_RADIUS_WITHOUT_CATEGORY_POI = 2;
	private static final int NUMBER_CELLS_AS_RADIUS_WITH_CATEGORY = 15;
	private static final double CELL_SIZE =CellUtils.DX / 1000; // km
	
	 private static final Logger LOGGER = Logger.getLogger(
			 NearbyUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	public static List <ElementDetails> getNearbyEvents(String endPointUrl, String eventGraph, double lat, double lon, String[] categories, String[] languages,
			double distance, int offset, int limit, String notId,
			List <String> listEventsFromFriendsWishlist) throws IOException {
		
		StringBuilder builder = new StringBuilder("SELECT distinct ?event ?distance ?title \n");

		builder.append("WHERE { \n");
		builder.append("        { graph " + eventGraph + " {?event a lode:Event.} } \n");
		builder.append("?event rdfs:label ?title . \n");
		int numberOfCells = NUMBER_CELLS_AS_RADIUS_WITHOUT_CATEGORY_EVENT;
		if (categories != null && categories.length > 0) {
			builder.append("?event lode:hasCategory ?category . \n");
		
			filterCategories(categories, builder);
			numberOfCells = NUMBER_CELLS_AS_RADIUS_WITH_CATEGORY;
		}
		
		builder.append(" ?event ?p ?inSpace. \n");
		builder.append("              ?inSpace geo:lat ?eventLat .\n");
		builder.append("              ?inSpace geo:long ?eventLon . \n");
		builder.append("BIND(bif:st_point(xsd:decimal(?eventLon), xsd:decimal(?eventLat)) as ?geo) .\n");

		builder.append("BIND(bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) .\n");

		builder.append(" OPTIONAL{ ?event lode:atTime ?time. \n");
		builder.append("              { ?time time:hasEnd ?end .\n");
		builder.append("              ?end time:inXSDDateTime ?endTime . } \n");
		builder.append(" UNION {?time time:inXSDDateTime ?endTime . } \n");
		builder.append("BIND (xsd:dateTime(?endTime) as ?dtEndTime ) . } \n");
		builder.append("BIND (now() AS ?thisMillisecond) . \n");
		
		builder.append("?event locationOnt:cell ?cell .");
		
		if (distance >= 0) {
			builder.append("FILTER (?distance <= " + distance + ") \n");
			int floor = (int) Math.floor(distance / CELL_SIZE) + 1; 
			if (distance > 0) numberOfCells = floor < numberOfCells ? floor : numberOfCells;
		}
		builder.append("FILTER (?dtEndTime > ?thisMillisecond) \n");
		

		if (!isNullOrEmpty(notId)) {
			builder.append("FILTER (?event != <" + notId + ">) \n");
		}
		
		builder.append("VALUES ?cell {");
		List <Integer> cellIds = calcCellIds(lat, lon, numberOfCells);
		cellIds = CellUtils.calcEffectiveCellIds(cellIds, distance * 1000, lat, lon);
		if (cellIds.size() == 0) return Collections.emptyList();
		for (int cellId: cellIds) {
			builder.append("<http://data.linkedevents.org/cell/milano/" + cellId + ">");
		}
		builder.append("}. \n");
		
		builder.append("} \n");
		if (listEventsFromFriendsWishlist == null || listEventsFromFriendsWishlist.size() == 0) {
		    builder.append("ORDER BY ?distance \n");
		} else {
			builder.append("ORDER BY");
			for (String eventFromWishList: listEventsFromFriendsWishlist) {
				builder.append(" DESC(?event = <" + eventFromWishList + ">)");
			}
			builder.append(" ?distance \n");
		}
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		if (DEBUG_MOD) LOGGER.info(builder.toString());
		
		return getNearbyEvents(endPointUrl, eventGraph, builder.toString(), categories, languages, listEventsFromFriendsWishlist);
	}
	
	public static List <ElementDetails> getNearbyEvents(String endPointUrl, String eventGraph, String id, String[] categories, String[] languages,
			double distance, int offset, int limit) throws IOException {
		if (isNullOrEmpty(id)) return new LinkedList <ElementDetails>();
		StringBuilder builder = new StringBuilder("SELECT ?lat ?lon \n");

		builder.append("WHERE { \n");
		builder.append("{ \n");
		builder.append("?event a lode:Event . \n");
		builder.append("?event lode:inSpace ?inSpace. \n");
		builder.append("?inSpace geo:lat ?lat . \n");
		builder.append("?inSpace geo:long ?lon  . \n");
		builder.append("FILTER (?event = <"  + id + ">) \n");
		
		builder.append("} UNION { \n");
		builder.append(" ?poi a dul:Place . \n");
		// cannot use the pattern geo:location/locn:geometry to get lat, long
		builder.append("?poi geo:location ?geoLocation . \n");
		builder.append("?geoLocation geo:lat  ?lat . \n");
		builder.append("?geoLocation geo:long  ?lon . \n");
		builder.append("FILTER (?poi = <" + id + ">) \n");
		
		builder.append("}} \n");
		
        StringBuilder resultBuilder = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(builder.toString(),
				"application/sparql-results+json", SparqlEndPointUtils.HTTP_POST, endPointUrl, resultBuilder); 
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len == 0) return new LinkedList <ElementDetails>();
		double lat = 0, lon = 0;
		JSONObject jsonElement = jsonArrs.getJSONObject(0);
		String latStr = getAttributeValue(jsonElement, "lat");
		lat = Double.parseDouble(latStr);
		String lonStr = getAttributeValue(jsonElement, "lon");
		lon = Double.parseDouble(lonStr);
		
		return getNearbyEvents(endPointUrl, eventGraph, lat, lon, categories, languages, distance, offset, limit, id, null);
	}
	
	public static List <ElementDetails> getNearbyPoIElements(String endPointUrl, String poiGraph, double lat, double lon,
			String[] categories, String[] topCategories, String[] languages,
			double distance, int offset, int limit,
			List <String> listPoIsFromFriendsWishlist) throws IOException {
		StringBuilder builder = new StringBuilder("SELECT distinct ?poi ?distance ?name \n");
		int numberOfCells = NUMBER_CELLS_AS_RADIUS_WITHOUT_CATEGORY_POI;

		builder.append("WHERE { \n");
		builder.append(" { graph " + poiGraph + " {?poi a dul:Place.} }  \n");
		builder.append(" ?poi rdfs:label ?name .  \n");
		if (categories != null && categories.length > 0) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("?businessType skos:prefLabel ?category .\n");
			
			filterCategories(categories, builder);
			numberOfCells = NUMBER_CELLS_AS_RADIUS_WITH_CATEGORY;
		}
		
		if (topCategories != null && topCategories.length > 0) {
			builder.append("?poi locationOnt:businessTypeTop ?businessTypeTop. \n");
			builder.append("?businessTypeTop skos:prefLabel ?topCategory .\n");
			
			filterTopCategories(topCategories, builder);
			numberOfCells = NUMBER_CELLS_AS_RADIUS_WITH_CATEGORY;
		}
		
		builder.append("?poi locationOnt:cell ?cell .");
		
		builder.append("?poi geo:location ?loc . ?loc geo:lat ?lat . ?loc geo:long ?lon . BIND(bif:st_point(xsd:decimal(?lon), xsd:decimal(?lat)) as ?geo) . \n");

		builder.append(" BIND(bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) \n");
		if (distance >= 0) {
			builder.append("FILTER (?distance <= " + distance + ") .\n");
			int floor = (int) Math.floor(distance / CELL_SIZE) + 1; 
			if (distance > 0) numberOfCells = floor < numberOfCells ? floor : numberOfCells;
		}
		
		builder.append("VALUES ?cell {");
		List <Integer> cellIds = calcCellIds(lat, lon, numberOfCells);
		cellIds = CellUtils.calcEffectiveCellIds(cellIds, distance * 1000, lat, lon);
		if (cellIds.size() == 0) return Collections.emptyList();
		for (int cellId: cellIds) {
			builder.append("<http://data.linkedevents.org/cell/milano/" + cellId + ">");
		}
		builder.append("}. \n");
		
		builder.append("} \n");
		if (listPoIsFromFriendsWishlist == null || listPoIsFromFriendsWishlist.size() == 0) {
		    builder.append("ORDER BY ?distance \n");
		} else {
			builder.append("ORDER BY");
			for (String poiFromWishList: listPoIsFromFriendsWishlist) {
				builder.append(" DESC(?poi = <" + poiFromWishList + ">)");
			}
			builder.append(" ?distance \n");
		}
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		return getNearbyPoIs(endPointUrl, poiGraph, builder.toString(), categories, topCategories, languages, listPoIsFromFriendsWishlist);
	}
	
	/**
	 * Gets nearby locations based on a given location ID and other parameters.
	 * @param locId
	 * @param category
	 * @param distance
	 * @param offset
	 * @param limit
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> getNearbyPoIElements(String endPointUrl, String poiGraph, String locId, String[] categories, String[] topCategories,
			String[] languages,
			double distance, int offset, int limit) throws IOException {
		if (isNullOrEmpty(locId)) return new LinkedList <ElementDetails>();
		
		StringBuilder builder = new StringBuilder("SELECT distinct ?poi ?distance \n");

		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		
		if (categories != null && categories.length > 0) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("?businessType skos:prefLabel ?category .\n");
			
			filterCategories(categories, builder);
		}
		
		if (topCategories != null && topCategories.length > 0) {
			builder.append("?poi locationOnt:businessTypeTop ?businessTypeTop. \n");
			builder.append("?businessTypeTop skos:prefLabel ?topCategory .\n");
			
			filterTopCategories(topCategories, builder);
		}
		
		builder.append("?poi geo:location ?loc . ?loc geo:lat ?lat . ?loc geo:long ?lon . BIND(bif:st_point(xsd:decimal(?lon), xsd:decimal(?lat)) as ?geo) . \n");
		builder.append(" <" + locId +"> geo:location ?locFixed . ?locFixed geo:lat ?latFixed . ?locFixed geo:long ?lonFixed . BIND(bif:st_point(xsd:decimal(?lonFixed), xsd:decimal(?latFixed)) as ?geoFixed) . \n");

		builder.append("        FILTER ( <").append(locId).append("> != ?poi ) . \n");

		builder.append(" BIND(bif:st_distance(?geo, ?geoFixed) as ?distance) \n");
		if (distance >= 0) {
			builder.append("FILTER (?distance <= " + distance + ") \n");
		}
		
		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		return getNearbyPoIs(endPointUrl, poiGraph, builder.toString(), categories, topCategories, languages, null);

	}
	
	private static List <ElementDetails> getNearbyPoIs(String endPointUrl, String poiGraph, String query, String[] categories,
			String[] topCategories, String [] languages,
			List <String> listPoIsFromFriendsWishlist) throws IOException {
		if (DEBUG_MOD) LOGGER.info(query);
		Map <String, Double> maps = new HashMap <String, Double>();
        StringBuilder resultBuilder = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(query, "application/sparql-results+json",
				SparqlEndPointUtils.HTTP_POST, endPointUrl, resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			findNearbyElement(jsonElement, maps, "poi");
		}
		if (maps.size() == 0) return new LinkedList <ElementDetails>();
		
		List <ElementDetails> results = ElementDetailsUtils.createPoIsDetails(endPointUrl, poiGraph, maps.keySet(),
				categories, topCategories, languages);
		
		for (ElementDetails elementDetails: results) {
			elementDetails.setDistance(maps.get(elementDetails.getId()));
			
			// set highlighted field
			setHighlightedField(elementDetails, listPoIsFromFriendsWishlist);
		}
		Collections.sort(results, new ElementDistance());
		return results;
	}
	
	private static List <ElementDetails> getNearbyEvents(String endPointUrl, String eventGraph, String query, String[] categories, String [] languages,
			List <String> listEventsFromFriendsWishlist) throws IOException {
		Map <String, Double> maps = new HashMap <String, Double>();
        StringBuilder resultBuilder = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(query, "application/sparql-results+json",
				SparqlEndPointUtils.HTTP_POST, endPointUrl, resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			findNearbyElement(jsonElement, maps, "event");
		}
		if (maps.size() == 0) return new LinkedList <ElementDetails>();
		
		List <ElementDetails> results = ElementDetailsUtils.createEventsDetails(endPointUrl, eventGraph, maps.keySet(), categories, languages);
		
		for (ElementDetails elementDetails: results) {
			elementDetails.setDistance(maps.get(elementDetails.getId()));
			// set highlighted field
			setHighlightedField(elementDetails, listEventsFromFriendsWishlist);
		}
		Collections.sort(results, new ElementDistance());
		return results;
	}
	
	private static void setHighlightedField(ElementDetails elementDetails,
			List <String> listItemsFromFriendsWishlist) {
		if (listItemsFromFriendsWishlist != null && listItemsFromFriendsWishlist.size() > 0) {
			String elementId = elementDetails.getId();
			boolean found = false;
			for (String tmpId: listItemsFromFriendsWishlist) {
				if (elementId.equals(tmpId)) {
					found = true;
					break;
				}
			}
			elementDetails.setHighlighted(found);
		}
	}
	
	private static void filterCategories(String[] categories, StringBuilder result) {
		if (categories.length == 0) return;
		result.append("FILTER (");
		int index = 0;
		for (String category: categories) {
			if (category != null) category = category.trim();
			if (category == null || category.equals("")) continue;
			if (index > 0) {
				result.append(" || ");
			}
			index++;
			result.append("STR(?category) = \"").append(category).append("\"");

		}
		result.append(") \n");
	}
	
	private static void filterTopCategories(String[] topCategories, StringBuilder result) {
		if (topCategories.length == 0) return;
		result.append("FILTER (");
		int index = 0;
		for (String topCategory: topCategories) {
			if (topCategory != null) topCategory = topCategory.trim();
			if (topCategory == null || topCategory.equals("")) continue;
			if (index > 0) {
				result.append(" || ");
			}
			index++;
			result.append("STR(?topCategory) = \"").append(topCategory).append("\"");

		}
		result.append(") \n");
	}

	/**
	 * This method only creates PoI ID + distance.
	 * @param jsonElement
	 * @return
	 */
	private static void findNearbyElement(JSONObject jsonElement, Map <String, Double> maps, String attributeID) {
		String elementId = getAttributeValue(jsonElement, attributeID);
		
		String distanceStr = getAttributeValue(jsonElement, "distance");
		if (!isNullOrEmpty(distanceStr)) {
			maps.put(elementId, Double.valueOf(distanceStr));
		}
	}
	
	private static String getAttributeValue(JSONObject jsonObject, String attr) throws JSONException {
		if (jsonObject.has(attr)) {
			return jsonObject.getJSONObject(attr).get("value").toString();
		}
		return null;
	}
	
	private static List <Integer> calcCellIds(double lat, double lon, int numberOfCellsAsRadius) {
		List <Integer> rets = new LinkedList <Integer>();
		for (int i = - numberOfCellsAsRadius; i <= numberOfCellsAsRadius; i++) {
			for (int j = - numberOfCellsAsRadius; j <= numberOfCellsAsRadius; j++) {
				double newLat = lat + i * SIZE_LAT;
				double newLon = lon + j * SIZE_LON;
				int cellId = calcCellId(newLat, newLon);
				rets.add(cellId);
			}
		}
		return rets;
	}
	
	private static int calcCellId(double lat, double lon) {
		int tmpLat = (int) Math.floor((lat - MIN_LAT)/(SIZE_LAT));
		int tmpLon = (int) Math.floor(((lon - MIN_LON)/(SIZE_LON)));
		int ret = tmpLat * 100 + tmpLon;
		return ret;
	}
	
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}
	
	private NearbyUtils() {
	}
	
	private static class ElementDistance implements Comparator<ElementDetails> {

		@Override
		public int compare(ElementDetails o1, ElementDetails o2) {
			Boolean highlighted1 = o1.getHighlighted();
			Boolean highlighted2 = o2.getHighlighted();
			if (highlighted1 != null && highlighted1.booleanValue() == true) {
				if (highlighted2 == null || highlighted2.booleanValue() == false) return -1;
			}
			if (highlighted2 != null && highlighted2.booleanValue() == true) {
				if (highlighted1 == null || highlighted1.booleanValue() == false) return 1;
			}
			double d1 = o1.getDistance() == null ? 0 : o1.getDistance().doubleValue();
			double d2 = o2.getDistance() == null ? 0 : o2.getDistance().doubleValue();
			if (d1 == d2) return 0;
			if (d1 > d2) return 1;
			return -1;
		}
		
	}
}

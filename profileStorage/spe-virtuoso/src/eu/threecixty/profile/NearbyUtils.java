package eu.threecixty.profile;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//TODO: need to remove condition with new Virtuoso updated
public class NearbyUtils {

	public static List <ElementDetails> getNearbyEvents(double lat, double lon, String category,
			double distance, int offset, int limit, String notId) throws IOException {
		
		StringBuilder builder = null;
		if (distance < 0) {
			builder = new StringBuilder("SELECT distinct ?event (bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) ((?distance >= 0) as ?condition) ((?dtEndTime > ?thisMillisecond) as ?timeCondition) \n");
		} else {
			builder = new StringBuilder("SELECT distinct ?event (bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) ((?distance <= " + distance + ") as ?condition) ((?dtEndTime > ?thisMillisecond) as ?timeCondition) \n");
		}

		builder.append("WHERE { \n");
		builder.append("        ?event a lode:Event . \n");
		
		if (!isNullOrEmpty(category)) {
			
			builder.append("OPTIONAL { ?event lode:hasCategory ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
			builder.append("OPTIONAL { ?event lode:hasCategory ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
			builder.append("OPTIONAL { ?event lode:hasCategory ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\")) }\n");
			builder.append("FILTER ((STR(?category_en) = \"" + category + "\") || (STR(?category_it) = \"" + category + "\") || (STR(?category_empty) = \"" + category + "\"))");
		}
		
		// should get title, description in another query to avoid consuming a lot of time for filter first results		
		
		builder.append("OPTIONAL { ?event ?p ?inSpace. \n");
		builder.append("              ?inSpace geo:lat ?eventLat .\n");
		builder.append("              ?inSpace geo:long ?eventLon . }\n");
		builder.append("BIND(bif:st_point(xsd:decimal(?eventLon), xsd:decimal(?eventLat)) as ?geo) .\n");

		builder.append(" OPTIONAL{ ?event lode:atTime ?time. \n");
		builder.append("              ?time time:hasEnd ?end .\n");
		builder.append("              ?end time:inXSDDateTime ?endTime .\n");
		builder.append("BIND (xsd:dateTime(?endTime) as ?dtEndTime ) . } \n");
		builder.append("BIND (now() AS ?thisMillisecond) . \n");
		
		builder.append("FILTER (?dtEndTime > ?thisMillisecond) \n");

		if (!isNullOrEmpty(notId)) {
			builder.append("FILTER (?event != <" + notId + ">) \n");
		}
		
		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		System.out.println(builder.toString());
		
		return getNearbyEvents(builder.toString());
	}
	
	public static List <ElementDetails> getNearbyEvents(String id, String category,
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
		builder.append("?poi schema:geo ?geoPoi . \n");
		builder.append("?geoPoi schema:latitude ?lat. \n");
		builder.append("?geoPoi schema:longitude ?lon. \n");
		builder.append("FILTER (?poi = <" + id + ">) \n");
		
		builder.append("}} \n");
		
        StringBuilder resultBuilder = new StringBuilder();
		VirtuosoManager.getInstance().executeQueryViaSPARQL(builder.toString(), "application/sparql-results+json", resultBuilder); 
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
		
		return getNearbyEvents(lat, lon, category, distance, offset, limit, id);
	}
	
	public static List <ElementDetails> getNearbyPoIElements(double lat, double lon, String category,
			double distance, int offset, int limit) throws IOException {
		// TODO: need to remove condition with new Virtuoso updated
		StringBuilder builder = null;
		if (distance < 0) {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) ((?distance >= 0) as ?condition) \n");
		} else {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, bif:st_point(" + Double.toString(lon) + ", " + Double.toString(lat) + ")) as ?distance) ((?distance <= " + distance + ") as ?condition) \n");
		}

		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		
		if (!isNullOrEmpty(category)) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\")) } \n");
			builder.append("FILTER ((STR(?category_en) = \"" + category + "\") || (STR(?category_it) = \"" + category + "\") || (STR(?category_empty) = \"" + category + "\"))");

		}
		
		// should get title, description in another query to avoid consuming a lot of time for filter first results		
		
		builder.append("        ?poi schema:geo ?geoPoi . \n");
		builder.append("        ?geoPoi geo:geometry ?geo. \n");

		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		return getNearbyPoIs(builder.toString());
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
	public static List <ElementDetails> getNearbyPoIElements(String locId, String category,
			double distance, int offset, int limit) throws IOException {
		if (isNullOrEmpty(locId)) return new LinkedList <ElementDetails>();
		
		StringBuilder builder = null;
		if (distance < 0) {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, ?geoFixed) as ?distance) ((?distance >= 0) as ?condition) \n");
		} else {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, ?geoFixed) as ?distance) ((?distance <= " + distance + ") as ?condition) \n");
		}

		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		
		if (!isNullOrEmpty(category)) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\")) } \n");
			builder.append("FILTER ((STR(?category_en) = \"" + category + "\") || (STR(?category_it) = \"" + category + "\") || (STR(?category_empty) = \"" + category + "\"))");

		}
		
		// should get title, description in another query to avoid consuming a lot of time for filter first results		
		
		builder.append("        ?poi schema:geo ?geoPoi . \n");
		builder.append("        ?geoPoi geo:geometry ?geo. \n");
		builder.append("        <").append(locId).append("> schema:geo ?geoPoiFixed . \n");
		builder.append("        ?geoPoiFixed geo:geometry ?geoFixed . \n");
		builder.append("        FILTER ( <").append(locId).append("> != ?poi ) . \n");
		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		return getNearbyPoIs(builder.toString());

	}
	
	public static int countNearbyPoIs(String locId, String category, double distance) {
		if (isNullOrEmpty(locId)) return 0;
		
		StringBuilder builder = null;
		if (distance < 0) {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, ?geoFixed) as ?distance) ((?distance >= 0) as ?condition) \n");
		} else {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, ?geoFixed) as ?distance) ((?distance <= " + distance + ") as ?condition) \n");
		}

		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		
		if (!isNullOrEmpty(category)) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\")) } \n");
			builder.append("FILTER ((STR(?category_en) = \"" + category + "\") || (STR(?category_it) = \"" + category + "\") || (STR(?category_empty) = \"" + category + "\"))");
		}
		
		// should get title, description in another query to avoid consuming a lot of time for filter first results		
		
		builder.append("        ?poi schema:geo ?geoPoi . \n");
		builder.append("        ?geoPoi geo:geometry ?geo. \n");
		builder.append("        <").append(locId).append("> schema:geo ?geoPoiFixed . \n");
		builder.append("        ?geoPoiFixed geo:geometry ?geoFixed . \n");
		builder.append("        FILTER ( <").append(locId).append("> != ?poi ) . \n");
		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		
		return 0;
	}
	
	private static List <ElementDetails> getNearbyPoIs(String query) throws IOException {
		Map <String, Double> maps = new HashMap <String, Double>();
        StringBuilder resultBuilder = new StringBuilder();
		VirtuosoManager.getInstance().executeQueryViaSPARQL(query, "application/sparql-results+json", resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			findNearbyElement(jsonElement, maps, "poi");
		}
		if (maps.size() == 0) return new LinkedList <ElementDetails>();
		
		List <ElementDetails> results = ElementDetailsUtils.createPoIsDetails(maps.keySet());
		
		for (ElementDetails elementDetails: results) {
			elementDetails.setDistance(maps.get(elementDetails.getId()));
		}
		Collections.sort(results, new ElementDistance());
		return results;
	}
	
	private static List <ElementDetails> getNearbyEvents(String query) throws IOException {
		Map <String, Double> maps = new HashMap <String, Double>();
        StringBuilder resultBuilder = new StringBuilder();
		VirtuosoManager.getInstance().executeQueryViaSPARQL(query, "application/sparql-results+json", resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			findNearbyElement(jsonElement, maps, "event");
		}
		if (maps.size() == 0) return new LinkedList <ElementDetails>();
		
		List <ElementDetails> results = ElementDetailsUtils.createEventsDetails(maps.keySet());
		
		for (ElementDetails elementDetails: results) {
			elementDetails.setDistance(maps.get(elementDetails.getId()));
		}
		Collections.sort(results, new ElementDistance());
		return results;
	}
	
	
//	/**
//	 * Find information (title, description, category, latitude, longitude)
//	 * @param results
//	 * @throws IOException 
//	 */
//	private static void findOtherInformationForNearbyPoIs(
//			List<NearbyElement> results) throws IOException {
//		StringBuilder builder = new StringBuilder("SELECT DISTINCT ?poi ?element_title ?description ?category ?lat ?lon ?image_url \n");
//		builder.append("WHERE { \n");
//		builder.append("        ?poi a dul:Place . \n");
//		builder.append("        OPTIONAL { ?poi schema:name ?title_en.  FILTER (langMatches(lang(?title_en), \"en\")) } \n");
//		builder.append("        OPTIONAL { ?poi schema:name ?title_it.  FILTER (langMatches(lang(?title_it), \"it\")) } \n");
//		builder.append("        OPTIONAL { ?poi schema:name ?title_empty.  FILTER (langMatches(lang(?title_empty), \"\")) } \n");
//		builder.append("        BIND(COALESCE(?title_en, ?title_it, ?title_empty) AS ?element_title) \n");
//		builder.append("        OPTIONAL { ?poi schema:description ?description_en.  FILTER (langMatches(lang(?description_en), \"en\"))} \n");
//		builder.append("        OPTIONAL { ?poi schema:description ?description_it.  FILTER (langMatches(lang(?description_it), \"it\"))} \n");
//		builder.append("        OPTIONAL { ?poi schema:description ?description_empty.  FILTER (langMatches(lang(?description_empty), \"\"))} \n");
//		builder.append("        BIND(COALESCE(?description_en, ?description_it, ?description_empty) AS ?description) \n");
//
//		builder.append("OPTIONAL { ?poi locationOnt:businessType ?businessType . } \n");
//		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
//		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
//		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\"))} \n");
//		builder.append("BIND(COALESCE(?category_en, ?category_it, ?category_empty) AS ?category) .\n");
//		
//		builder.append("        ?poi schema:geo ?geo . \n");
//		builder.append("        ?geo schema:latitude ?lat . \n");
//		builder.append("        ?geo schema:longitude ?lon . \n");
//		builder.append("OPTIONAL { ?poi lode:poster ?image_url.} \n");
//		builder.append("FILTER (");
//		boolean firstTime = true;
//		for (NearbyElement element: results) {
//			if (firstTime) {
//				firstTime = false;
//				builder.append("(?poi = <").append(element.getId()).append(">)");
//			} else {
//				builder.append(" || (?poi = <").append(element.getId()).append(">)");
//			}
//		}
//		builder.append(") \n");
//		builder.append("}");
//		
//		
//        StringBuilder resultBuilder = new StringBuilder();
//		VirtuosoManager.getInstance().executeQueryViaSPARQL(builder.toString(), "application/sparql-results+json", resultBuilder);
//		
//		JSONObject json = new JSONObject(resultBuilder.toString());
//		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
//		int len = jsonArrs.length();
//		for (int i = 0; i < len; i++) {
//			JSONObject jsonElement = jsonArrs.getJSONObject(i);
//			findOtherInformationForNearbyPoIs(jsonElement, results);
//		}
//	}
//
//
//
//	private static void findOtherInformationForNearbyPoIs(
//			JSONObject jsonElement, List<NearbyElement> results) {
//		String poiId = getAttributeValue(jsonElement, "poi");
//		if (isNullOrEmpty(poiId)) return;
//		for (NearbyElement nearbyElement: results) {
//			if (poiId.equals(nearbyElement.getId())) {
//				String title = getAttributeValue(jsonElement, "element_title");
//				if (!isNullOrEmpty(title)) nearbyElement.setTitle(title);
//				String desc = getAttributeValue(jsonElement, "description");
//				if (!isNullOrEmpty(desc)) nearbyElement.setDescription(desc);
//				String cat = getAttributeValue(jsonElement, "category");
//				if (!isNullOrEmpty(cat)) nearbyElement.setCategory(cat);
//				String latStr = getAttributeValue(jsonElement, "lat");
//				if (!isNullOrEmpty(latStr)) nearbyElement.setLat(Double.valueOf(latStr));
//				String lonStr = getAttributeValue(jsonElement, "lon");
//				if (!isNullOrEmpty(lonStr)) nearbyElement.setLon(Double.valueOf(lonStr));
//				String imageUrl = getAttributeValue(jsonElement, "image_url");
//				if (!isNullOrEmpty(imageUrl)) nearbyElement.setImage_url(imageUrl);
//			    break;
//			}
//		}
//	}


	/**
	 * This method only creates PoI ID + distance.
	 * @param jsonElement
	 * @return
	 */
	private static void findNearbyElement(JSONObject jsonElement, Map <String, Double> maps, String attributeID) {
		String conditionStr = getAttributeValue(jsonElement, "condition");
		int condition = Integer.parseInt(conditionStr);
		if (condition == 0) return;
		
		String timeConditionStr = getAttributeValue(jsonElement, "timeCondition");
		int timecondition = Integer.parseInt(timeConditionStr);
		if (timecondition == 0) return;
		
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
	
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}
	
	private NearbyUtils() {
	}
	
	private static class ElementDistance implements Comparator<ElementDetails> {

		@Override
		public int compare(ElementDetails o1, ElementDetails o2) {
			double d1 = o1.getDistance() == null ? 0 : o1.getDistance().doubleValue();
			double d2 = o2.getDistance() == null ? 0 : o2.getDistance().doubleValue();
			if (d1 == d2) return 0;
			if (d1 > d2) return 1;
			return -1;
		}
		
	}
}

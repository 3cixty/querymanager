package eu.threecixty.profile.nearby;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.profile.VirtuosoManager;

public class NearbyUtils {

	public static List <NearbyElement> getNearbyPoIElements(double lat, double lon, String category,
			double distance, int offset, int limit) throws IOException {
		List <NearbyElement> results = new LinkedList <NearbyElement>();
		
		StringBuilder builder = null;
		if (distance < 0) {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, bif:st_point(" + Double.toString(lat) + ", " + Double.toString(lon) + ")) as ?distance) ((?distance >= 0) as ?condition) \n");
		} else {
			builder = new StringBuilder("SELECT distinct ?poi (bif:st_distance(?geo, bif:st_point(" + Double.toString(lat) + ", " + Double.toString(lon) + ")) as ?distance) ((?distance <= " + distance + ") as ?condition) \n");
		}

		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		
		if (!isNullOrEmpty(category)) {
			builder.append("?poi locationOnt:businessType ?businessType. \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\"))} \n");
			builder.append("BIND(COALESCE(?category_en, ?category_it, ?category_empty) AS ?category) .\n");
			builder.append("FILTER (STR(?category) = \"" + category + "\")");
		}
		
		// should get title, description in another query to avoid consuming a lot of time for filter first results		
		
		builder.append("        ?poi schema:geo ?geoPoi . \n");
		builder.append("        ?geoPoi geo:geometry ?geo. \n");

		builder.append("} \n");
		builder.append("ORDER BY ?distance \n");
		builder.append("OFFSET ").append(offset <= 0 ? 0 : offset).append(" \n");
		builder.append("LIMIT ").append(limit <= 0 ? 0 : limit);
		
		findNearbyPoIs(builder.toString(), results);
		
		return results;
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
	public static List <NearbyElement> getNearbyPoIElements(String locId, String category,
			double distance, int offset, int limit) throws IOException {
		List <NearbyElement> results = new LinkedList <NearbyElement>();
		if (isNullOrEmpty(locId)) return results;
		
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
			builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\"))} \n");
			builder.append("BIND(COALESCE(?category_en, ?category_it, ?category_empty) AS ?category) .\n");
			builder.append("FILTER (STR(?category) = \"" + category + "\")");
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
		
		findNearbyPoIs(builder.toString(), results);
		
		return results;
	}
	
	private static void findNearbyPoIs(String query, List <NearbyElement> results) throws IOException {
        StringBuilder resultBuilder = new StringBuilder();
		VirtuosoManager.getInstance().executeQueryViaSPARQL(query, "application/sparql-results+json", resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			NearbyElement tmp = createNearbyPoI(jsonElement);
			if (tmp != null) results.add(tmp);
		}
		if (results.size() == 0) return;
		
		findOtherInformationForNearbyPoIs(results);
	}
	
	
	/**
	 * Find information (title, description, category, latitude, longitude)
	 * @param results
	 * @throws IOException 
	 */
	private static void findOtherInformationForNearbyPoIs(
			List<NearbyElement> results) throws IOException {
		StringBuilder builder = new StringBuilder("SELECT DISTINCT ?poi ?element_title ?description ?category ?lat ?lon ?image_url \n");
		builder.append("WHERE { \n");
		builder.append("        ?poi a dul:Place . \n");
		builder.append("        OPTIONAL { ?poi schema:name ?title_en.  FILTER (langMatches(lang(?title_en), \"en\")) } \n");
		builder.append("        OPTIONAL { ?poi schema:name ?title_it.  FILTER (langMatches(lang(?title_it), \"it\")) } \n");
		builder.append("        OPTIONAL { ?poi schema:name ?title_empty.  FILTER (langMatches(lang(?title_empty), \"\")) } \n");
		builder.append("        BIND(COALESCE(?title_en, ?title_it, ?title_empty) AS ?element_title) \n");
		builder.append("        OPTIONAL { ?poi schema:description ?description_en.  FILTER (langMatches(lang(?description_en), \"en\"))} \n");
		builder.append("        OPTIONAL { ?poi schema:description ?description_it.  FILTER (langMatches(lang(?description_it), \"it\"))} \n");
		builder.append("        OPTIONAL { ?poi schema:description ?description_empty.  FILTER (langMatches(lang(?description_empty), \"\"))} \n");
		builder.append("        BIND(COALESCE(?description_en, ?description_it, ?description_empty) AS ?description) \n");

		builder.append("OPTIONAL { ?poi locationOnt:businessType ?businessType . } \n");
		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_en.  FILTER (langMatches(lang(?category_en), \"en\"))} \n");
		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_it.  FILTER (langMatches(lang(?category_it), \"it\"))} \n");
		builder.append("OPTIONAL { ?businessType skos:prefLabel ?category_empty.  FILTER (langMatches(lang(?category_empty), \"\"))} \n");
		builder.append("BIND(COALESCE(?category_en, ?category_it, ?category_empty) AS ?category) .\n");
		
		builder.append("        ?poi schema:geo ?geo . \n");
		builder.append("        ?geo schema:latitude ?lat . \n");
		builder.append("        ?geo schema:longitude ?lon .} \n");
		builder.append("OPTIONAL { ?poi lode:poster ?image_url.} \n");
		builder.append("FILTER (");
		boolean firstTime = true;
		for (NearbyElement element: results) {
			if (firstTime) {
				firstTime = false;
				builder.append("(?poi = <").append(element.getId()).append(">)");
			} else {
				builder.append(" || (?poi = <").append(element.getId()).append(">)");
			}
		}
		builder.append(") \n");
		builder.append("}");
		
		
        StringBuilder resultBuilder = new StringBuilder();
		VirtuosoManager.getInstance().executeQueryViaSPARQL(builder.toString(), "application/sparql-results+json", resultBuilder);
		
		JSONObject json = new JSONObject(resultBuilder.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			findOtherInformationForNearbyPoIs(jsonElement, results);
		}
	}



	private static void findOtherInformationForNearbyPoIs(
			JSONObject jsonElement, List<NearbyElement> results) {
		String poiId = getAttributeValue(jsonElement, "poi");
		if (isNullOrEmpty(poiId)) return;
		for (NearbyElement nearbyElement: results) {
			if (poiId.equals(nearbyElement.getId())) {
				String title = getAttributeValue(jsonElement, "element_title");
				if (!isNullOrEmpty(title)) nearbyElement.setTitle(title);
				String desc = getAttributeValue(jsonElement, "description");
				if (!isNullOrEmpty(desc)) nearbyElement.setDescription(desc);
				String cat = getAttributeValue(jsonElement, "category");
				if (!isNullOrEmpty(cat)) nearbyElement.setCategory(cat);
				String latStr = getAttributeValue(jsonElement, "lat");
				if (!isNullOrEmpty(latStr)) nearbyElement.setLat(Double.valueOf(latStr));
				String lonStr = getAttributeValue(jsonElement, "lon");
				if (!isNullOrEmpty(lonStr)) nearbyElement.setLon(Double.valueOf(lonStr));
				String imageUrl = getAttributeValue(jsonElement, "image_url");
				if (!isNullOrEmpty(imageUrl)) nearbyElement.setImage_url(imageUrl);
			    break;
			}
		}
	}


	/**
	 * This method only creates PoI ID + distance.
	 * @param jsonElement
	 * @return
	 */
	private static NearbyElement createNearbyPoI(JSONObject jsonElement) {
		String conditionStr = getAttributeValue(jsonElement, "condition");
		int condition = Integer.parseInt(conditionStr);
		if (condition == 0) return null;
		NearbyElement nearbyElement = new NearbyElement();
		
		String poiId = getAttributeValue(jsonElement, "poi");
		if (!isNullOrEmpty(poiId)) nearbyElement.setId(poiId);
		
		String distanceStr = getAttributeValue(jsonElement, "distance");
		if (!isNullOrEmpty(distanceStr)) nearbyElement.setDistance(Double.valueOf(distanceStr));
		
		return nearbyElement;
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
}

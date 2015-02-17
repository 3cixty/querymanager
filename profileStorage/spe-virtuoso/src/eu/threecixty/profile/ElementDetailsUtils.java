package eu.threecixty.profile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ElementDetailsUtils {
	
	public static List <ElementDetails> createEventsDetails(List <String> eventIds) throws IOException {
		if (eventIds == null || eventIds.size() == 0) return null;

		StringBuffer queryBuff = new StringBuffer("SELECT DISTINCT *\n");
		queryBuff.append("WHERE {\n");
		queryBuff.append("OPTIONAL{ ?item dc:title ?title.} \n");
		queryBuff.append("OPTIONAL{ ?item dc:description ?description.} \n");
		queryBuff.append("OPTIONAL{ ?item lode:hasCategory ?category.} \n");
		queryBuff.append("OPTIONAL{ ?item ?p ?inSpace. \n");
		queryBuff.append("              ?inSpace geo:lat ?lat .\n");
		queryBuff.append("              ?inSpace geo:long ?lon . }\n");
		queryBuff.append("OPTIONAL{ ?item lode:atPlace ?place. \n");
		queryBuff.append("              ?place vcard2006:hasAddress ?address .\n");
		queryBuff.append("              ?address vcard2006:street-address ?street .\n");
		queryBuff.append("              ?address vcard2006:locality ?locality . }\n");
		queryBuff.append(" OPTIONAL{ ?item lode:atTime ?time.");
		queryBuff.append("              ?time time:hasBeginning ?beginning .\n");
		queryBuff.append("              ?beginning time:inXSDDateTime ?beginTime .\n");
		queryBuff.append("              ?time time:hasEnd ?end .\n");
		queryBuff.append("              ?end time:inXSDDateTime ?endTime .}\n");
		queryBuff.append("OPTIONAL{ ?item lode:poster ?image_url .}\n");
		queryBuff.append("OPTIONAL{ ?item dc:publisher ?source .}\n");
		
		queryBuff.append("FILTER (");
		boolean first = true;
		for (String eventId: eventIds) {
			if (first) {
				first = false;
				queryBuff.append("(?item = <").append(eventId).append(">)");
			} else {
				queryBuff.append("|| (?item = <").append(eventId).append(">)");
			}
		}
		queryBuff.append(") \n");
		queryBuff.append("}");

		StringBuilder result = new StringBuilder();
		
		VirtuosoManager.getInstance().executeQueryViaSPARQL(queryBuff.toString(), "application/sparql-results+json", result);

		JSONObject json = new JSONObject(
				VirtuosoManager.getInstance().cleanResultReceivedFromVirtuoso(result.toString()));
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len < 1) return null;

		List <ElementDetails> elementsDetails = new LinkedList <ElementDetails>();
		for (int i = 0; i  < len; i++) {
			JSONObject tmpObj = jsonArrs.getJSONObject(i);
			ElementEventDetails tmpEventDetails = createEventDetails(tmpObj);
			if (tmpEventDetails == null) continue;
			if (!elementsDetails.contains(tmpEventDetails)) elementsDetails.add(tmpEventDetails);
		}
		return elementsDetails;
	}
	
	
	public static List <ElementDetails> createPoIsDetails(List <String> poiIds) throws IOException {
		// TODO
		return null;
	}
	
	private static ElementPoIDetails createPoIDetails(String itemId) {
		// TODO
		return null;
	}
	
	private static ElementEventDetails createEventDetails(JSONObject json) {
		ElementEventDetails eventDetails = new ElementEventDetails();
		String id = getAttributeValue(json, "item");
		if (isNullOrEmpty(id)) return null;
		eventDetails.setId(id);
		String title = getAttributeValue(json, "title");
		if (!isNullOrEmpty(title)) eventDetails.setName(title);
		String desc = getAttributeValue(json, "description");
		if (!isNullOrEmpty(desc)) eventDetails.setDescription(desc);
		String category = getAttributeValue(json, "category");
		if (!isNullOrEmpty(category)) eventDetails.setCategory(category);
		String lat = getAttributeValue(json, "lat");
		if (!isNullOrEmpty(lat)) eventDetails.setLat(lat);
		String lon = getAttributeValue(json, "lon");
		if (!isNullOrEmpty(lon)) eventDetails.setLon(lon);
		String street = getAttributeValue(json, "street");
		if (!isNullOrEmpty(street)) eventDetails.setAddress(street);
		String locality = getAttributeValue(json, "locality");
		if (!isNullOrEmpty(locality)) eventDetails.setLocality(locality);
		String beginTime = getAttributeValue(json, "beginTime");
		if (!isNullOrEmpty(beginTime)) eventDetails.setTime_beginning(beginTime);
		String endTime = getAttributeValue(json, "endTime");
		if (!isNullOrEmpty(endTime)) eventDetails.setTime_end(endTime);
		String image_url = getAttributeValue(json, "image_url");
		if (!isNullOrEmpty(image_url)) eventDetails.setImage_url(image_url);
		String source = getAttributeValue(json, "source");
		if (!isNullOrEmpty(source)) eventDetails.setSource(source);
		return eventDetails;
	}

	private static String getAttributeValue(JSONObject jsonObject, String attr) throws JSONException {
		if (jsonObject.has(attr)) {
			return jsonObject.getJSONObject(attr).getString("value");
		}
		return null;
	}
	
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}
	
	private ElementDetailsUtils() {
	}
}

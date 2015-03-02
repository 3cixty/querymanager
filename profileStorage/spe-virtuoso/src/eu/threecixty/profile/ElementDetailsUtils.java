package eu.threecixty.profile;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a utility class to get a list of events or PoIs in details as requested by TI team.
 * @author Cong-Kinh Nguyen
 *
 */
public class ElementDetailsUtils {
	
	private static final String COMMENT_ATTRIBUTE = "reviewBody"; // to get comment
	
	/**
	 * Creates a list of events with info in details from a given list of IDs.
	 * @param eventIds
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createEventsDetails(Collection <String> eventIds) throws IOException {
		if (eventIds == null || eventIds.size() == 0) return null;

		StringBuffer queryBuff = new StringBuffer("SELECT DISTINCT *\n");
		queryBuff.append("WHERE {\n");
		queryBuff.append("?item dc:title ?title. \n");
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
	
	/**
	 * Creates a list of PoIs with info in details from a given list of IDs.
	 * @param poiIds
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createPoIsDetails(Collection <String> poiIds) throws IOException {
		if (poiIds == null || poiIds.size() == 0) return null;

		StringBuffer queryBuff = new StringBuffer("SELECT DISTINCT *\n");
		queryBuff.append("WHERE {\n");
		queryBuff.append(" ?poi schema:name ?name. \n");
		queryBuff.append("OPTIONAL{ ?poi locationOnt:businessType ?businessType. \n");
		queryBuff.append("          ?businessType skos:prefLabel ?category . } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:location ?location . \n");
		queryBuff.append("          ?location schema:streetAddress ?address .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:geo ?geo . \n");
		queryBuff.append("          ?geo schema:latitude ?lat . \n");
		queryBuff.append("          ?geo schema:longitude ?lon .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:review ?review . \n");
		queryBuff.append("          ?review schema:reviewBody ?reviewBody .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating ?aggregateRating . \n");
		queryBuff.append("          ?aggregateRating schema:reviewRating ?reviewRating . \n");
		queryBuff.append("          ?reviewRating schema:ratingValue ?ratingValue .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:interactionCount ?reviewCounts .} \n");
		queryBuff.append("OPTIONAL{ ?poi lode:poster ?image_url .} \n");
		queryBuff.append("OPTIONAL{ ?poi dc:publisher ?source .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:telephone ?telephone .} \n");
		
		queryBuff.append("FILTER (");
		boolean first = true;
		for (String poiId: poiIds) {
			if (first) {
				first = false;
				queryBuff.append("(?poi = <").append(poiId).append(">)");
			} else {
				queryBuff.append("|| (?poi = <").append(poiId).append(">)");
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

		Map <String, ElementDetails> maps = new HashMap <String, ElementDetails>();
		
		ElementDetails tmpPoIDetails = null;
		for (int i = 0; i  < len; i++) {
			JSONObject tmpObj = jsonArrs.getJSONObject(i);
			String currentId = tmpObj.get("poi").toString();
			if (currentId == null) continue;
			tmpPoIDetails = maps.get(currentId);
			if (tmpPoIDetails == null) {
				tmpPoIDetails = createPoIDetails(tmpObj);
				maps.put(currentId, tmpPoIDetails);
			} else {
				String comment = getAttributeValue(tmpObj, COMMENT_ATTRIBUTE);
				if (!isNullOrEmpty(comment)) {
					List <String> comments = ((ElementPoIDetails) tmpPoIDetails).getReviews();
					if (!comments.contains(comment)) comments.add(comment);
				}
			}
		}
		
		List <ElementDetails> elementsDetails = new LinkedList <ElementDetails>();
		elementsDetails.addAll(maps.values());
		return elementsDetails;
	}
	
	private static ElementPoIDetails createPoIDetails(JSONObject json) {
		ElementPoIDetails poiDetails = new ElementPoIDetails();
		String id = getAttributeValue(json, "poi");
		if (isNullOrEmpty(id)) return null;
		poiDetails.setId(id);
		String name = getAttributeValue(json, "name");
		if (!isNullOrEmpty(name)) poiDetails.setName(name);
		String category = getAttributeValue(json, "category");
		if (!isNullOrEmpty(category)) poiDetails.setCategory(category);
		String lat = getAttributeValue(json, "lat");
		if (!isNullOrEmpty(lat)) poiDetails.setLat(lat);
		String lon = getAttributeValue(json, "lon");
		if (!isNullOrEmpty(lon)) poiDetails.setLon(lon);
		String street = getAttributeValue(json, "address");
		if (!isNullOrEmpty(street)) poiDetails.setAddress(street);
		String locality = getAttributeValue(json, "locality");
		if (!isNullOrEmpty(locality)) poiDetails.setLocality(locality);
		String image_url = getAttributeValue(json, "image_url");
		if (!isNullOrEmpty(image_url)) poiDetails.setImage_url(image_url);
		String source = getAttributeValue(json, "source");
		if (!isNullOrEmpty(source)) poiDetails.setSource(source);
		String aggregateRatingStr = getAttributeValue(json, "ratingValue");
		try {
		    if (!isNullOrEmpty(aggregateRatingStr)) poiDetails.setAggregate_rating(
		    		Double.parseDouble(aggregateRatingStr));
		} catch (Exception e) {}
		String reviewCountsStr = getAttributeValue(json, "reviewCounts");
		try {
			if (!isNullOrEmpty(reviewCountsStr)) {
				String [] reviews = reviewCountsStr.trim().split(" ");
				poiDetails.setReview_counts(Integer.parseInt(reviews[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String telephone = getAttributeValue(json, "telephone");
		if (!isNullOrEmpty(telephone)) poiDetails.setTelephone(telephone);
		List <String> comments = new LinkedList <String>();
		String comment = getAttributeValue(json, COMMENT_ATTRIBUTE);
		if (!isNullOrEmpty(comment) && !comments.contains(comment)) comments.add(comment);
		poiDetails.setReviews(comments);
		return poiDetails;
		
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
			return jsonObject.getJSONObject(attr).get("value").toString();
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

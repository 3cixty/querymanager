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

		StringBuffer queryBuff = new StringBuffer("SELECT DISTINCT * \n");
		queryBuff.append("WHERE {\n");
		queryBuff.append("?item a lode:Event . \n");
		queryBuff.append("OPTIONAL { ?item dc:title  ?title_en.  FILTER (langMatches(lang(?title_en), \"en\"))  } \n");
		queryBuff.append("OPTIONAL { ?item dc:title  ?title_it.  FILTER (langMatches(lang(?title_it), \"it\"))  } \n");
		queryBuff.append("OPTIONAL { ?item dc:title  ?title_empty.  FILTER (langMatches(lang(?title_empty), \"\"))  } \n");

		queryBuff.append("OPTIONAL { ?item dc:description ?description_en. FILTER (langMatches(lang(?description_en), \"en\")) } \n");
		queryBuff.append("OPTIONAL { ?item dc:description ?description_it. FILTER (langMatches(lang(?description_it), \"it\")) } \n");
		queryBuff.append("OPTIONAL { ?item dc:description ?description_empty. FILTER (langMatches(lang(?description_empty), \"\")) } \n");

		queryBuff.append("OPTIONAL { ?item lode:hasCategory ?category.} \n");
		queryBuff.append("OPTIONAL { ?item ?p ?inSpace. \n");
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
		queryBuff.append(" ?poi a dul:Place .  \n");
		queryBuff.append("OPTIONAL { ?poi schema:name ?name_en. FILTER (langMatches(lang(?name_en), \"en\")) } \n");
		queryBuff.append("OPTIONAL { ?poi schema:name ?name_it. FILTER (langMatches(lang(?name_it), \"it\")) } \n");
		queryBuff.append("OPTIONAL { ?poi schema:name ?name_empty. FILTER (langMatches(lang(?name_empty), \"\")) } \n");
		
		queryBuff.append("OPTIONAL{ ?poi locationOnt:businessType ?businessType. \n");
		queryBuff.append("          ?businessType skos:prefLabel ?category . } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:location ?location . \n");
		queryBuff.append("          ?location schema:streetAddress ?address .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:geo ?geo . \n");
		queryBuff.append("          ?geo schema:latitude ?lat . \n");
		queryBuff.append("          ?geo schema:longitude ?lon .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:review ?review . \n");
		queryBuff.append("          ?review schema:reviewBody ?reviewBody .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating ?ratingValue1 . } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating ?aggregateRating2 . \n");
		queryBuff.append("          ?aggregateRating2 schema:ratingValue ?ratingValue2 . } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating ?aggregateRating3 . \n");
		queryBuff.append("          ?aggregateRating3 schema:reviewRating ?reviewRating3 .  \n");
		queryBuff.append("           ?reviewRating3 schema:ratingValue ?ratingValue3 .}  \n");
		
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
		
		System.out.println(queryBuff.toString());
		
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
		
		for (String key: maps.keySet()) {
			ElementDetails tmp = maps.get(key);
			ElementPoIDetails poi = (ElementPoIDetails) tmp;
			if (poi.getReview_counts() == 0) {
				if (poi.getReviews() != null) poi.setReview_counts(poi.getReviews().size());
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
		
		String name = getAttributeValue(json, "name_en");
		if (isNullOrEmpty(name)) name = getAttributeValue(json, "name_it");
		if (isNullOrEmpty(name)) name = getAttributeValue(json, "name_empty");
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

		String aggregateRatingStr = getAttributeValue(json, "ratingValue1");
		if (isNullOrEmpty(aggregateRatingStr)) aggregateRatingStr = getAttributeValue(json, "ratingValue2");
		if (isNullOrEmpty(aggregateRatingStr)) aggregateRatingStr = getAttributeValue(json, "ratingValue3");

		try {
		    if (!isNullOrEmpty(aggregateRatingStr)) poiDetails.setAggregate_rating(
		    		Double.parseDouble(aggregateRatingStr.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// seems incorrect
//		String reviewCountsStr = getAttributeValue(json, "reviewCounts");
//		try {
//			if (!isNullOrEmpty(reviewCountsStr)) {
//				String [] reviews = reviewCountsStr.trim().split(" ");
//				poiDetails.setReview_counts(Integer.parseInt(reviews[0]));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
		
		String title = getAttributeValue(json, "title_en");
		if (isNullOrEmpty(title)) title = getAttributeValue(json, "title_it");
		if (isNullOrEmpty(title)) title = getAttributeValue(json, "title_empty");
		if (!isNullOrEmpty(title)) eventDetails.setName(title);
		
		String desc = getAttributeValue(json, "description_en");
		if (isNullOrEmpty(desc)) desc = getAttributeValue(json, "description_it");
		if (isNullOrEmpty(desc)) desc = getAttributeValue(json, "description_empty");
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

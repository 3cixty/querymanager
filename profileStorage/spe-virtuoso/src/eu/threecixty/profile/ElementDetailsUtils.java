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
	private static final String CATEGORY_ATTRIBUTE = "category";
	
	/**
	 * Creates a list of events with info in details from a given list of IDs.
	 * @param eventIds
	 * @param categories
	 * 			The categories, null for all categories.
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createEventsDetails(Collection <String> eventIds, String[] categories, String[] languages) throws IOException {
		if (eventIds == null || eventIds.size() == 0) return null;

		StringBuilder queryBuff = new StringBuilder("SELECT DISTINCT * \n");
		queryBuff.append("WHERE {\n");
		queryBuff.append("?item a lode:Event . \n");
		
		addInfoOptional("?item", "dc:title", "?title", LanguageUtils.getAllLanguages(), true, queryBuff);
		
		addInfoOptional("?item", "dc:description", "?description", languages, true, queryBuff);

		if (categories == null) {
			queryBuff.append("OPTIONAL { ?item lode:hasCategory ?category . } \n");
		} else {
			queryBuff.append("?item lode:hasCategory ?category . \n");
			if (categories.length > 0) {
				queryBuff.append("FILTER (");
				int index = 0;
				for (String tmpCat: categories) {
					if (index > 0) {
						queryBuff.append(" || ");
					}
					index++;
					queryBuff.append("STR(?category) = \"" + tmpCat + "\"");
				}
				queryBuff.append(") .\n");
			}
		}
		
		queryBuff.append("OPTIONAL { ?item ?p ?inSpace. \n");
		queryBuff.append("              ?inSpace geo:lat ?lat .\n");
		queryBuff.append("              ?inSpace geo:long ?lon . }\n");
		queryBuff.append("OPTIONAL{ ?item lode:atPlace ?place. \n");
		queryBuff.append("              ?place vcard2006:hasAddress ?address .\n");
		queryBuff.append("              ?address vcard2006:street-address ?street .\n");
		queryBuff.append("              ?address vcard2006:locality ?locality . }\n");
		queryBuff.append(" OPTIONAL{ ?item lode:atTime ?time.");
		queryBuff.append("              { ?time time:hasBeginning ?beginning .\n");
		queryBuff.append("              ?beginning time:inXSDDateTime ?beginTime .\n");
		queryBuff.append("              ?time time:hasEnd ?end .\n");
		queryBuff.append("              ?end time:inXSDDateTime ?endTime .}\n");
		queryBuff.append(" UNION { ?time time:inXSDDateTime ?beginTime .  } \n");
		queryBuff.append("}");
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
		
		SparqlEndPointUtils.executeQueryViaSPARQL(queryBuff.toString(), "application/sparql-results+json", result);

		JSONObject json = new JSONObject(
				SparqlEndPointUtils.cleanResultReceivedFromVirtuoso(result.toString()));
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len < 1) return null;
		
		Map <String, ElementDetails> maps = new HashMap <String, ElementDetails>();

		for (int i = 0; i  < len; i++) {
			JSONObject tmpObj = jsonArrs.getJSONObject(i);
			String currentId = tmpObj.get("item").toString();
			if (currentId == null) continue;
			ElementDetails tmpEventDetails = maps.get(currentId); 
			if (tmpEventDetails == null) {
				tmpEventDetails = createEventDetails(tmpObj, languages);
				maps.put(currentId, tmpEventDetails);
			} else {
				String category = getAttributeValue(tmpObj, CATEGORY_ATTRIBUTE);
				if (!isNullOrEmpty(category)) {
					if (!tmpEventDetails.getCategories().contains(category))
						    tmpEventDetails.getCategories().add(category);
				}
			}
		}
		List <ElementDetails> elementsDetails = new LinkedList <ElementDetails>();
		elementsDetails.addAll(maps.values());
		processCategories(elementsDetails);
		return elementsDetails;
	}

	/**
	 * Creates a list of PoIs with info in details from a given list of IDs.
	 * @param poiIds
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createPoIsDetails(Collection <String> poiIds, String[] categories, String[] languages) throws IOException {
		if (poiIds == null || poiIds.size() == 0) return null;

		StringBuilder queryBuff = new StringBuilder("SELECT DISTINCT *\n");
		queryBuff.append("WHERE {\n");
		queryBuff.append(" ?poi a dul:Place .  \n");
		
		addInfoOptional("?poi", "schema:name", "?name", LanguageUtils.getAllLanguages(), true, queryBuff);
		addInfoOptional("?poi", "schema:description", "?description", languages, true, queryBuff);
		
		if (categories == null) {
			queryBuff.append("OPTIONAL {?poi locationOnt:businessType ?businessType. \n ?businessType skos:prefLabel ?category . } \n");
		} else {
			queryBuff.append("?poi locationOnt:businessType ?businessType. \n ?businessType skos:prefLabel ?category . \n");
			if (categories.length > 0) {
				queryBuff.append("FILTER (");
				int index = 0;
				for (String tmpCat: categories) {
					if (index > 0) {
						queryBuff.append(" || ");
					}
					index++;
					queryBuff.append("STR(?category) = \"" + tmpCat + "\"");
				}
				queryBuff.append(") .\n");
			}
		}
		
		queryBuff.append("OPTIONAL{ ?poi schema:location ?location . \n");
		queryBuff.append("          ?location schema:streetAddress ?address .} \n");
		queryBuff.append("OPTIONAL {\n");
		queryBuff.append("?poi geo:location ?geoLocation . \n");
		queryBuff.append("?geoLocation geo:lat  ?lat . \n");
		queryBuff.append("?geoLocation geo:long  ?lon . \n");
		queryBuff.append("} \n");
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
		
		SparqlEndPointUtils.executeQueryViaSPARQL(queryBuff.toString(), "application/sparql-results+json", result);

		JSONObject json = new JSONObject(
				SparqlEndPointUtils.cleanResultReceivedFromVirtuoso(result.toString()));
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
				tmpPoIDetails = createPoIDetails(tmpObj, languages);
				maps.put(currentId, tmpPoIDetails);
			} else {
				String comment = getAttributeValue(tmpObj, COMMENT_ATTRIBUTE);
				if (!isNullOrEmpty(comment)) {
					List <String> comments = ((ElementPoIDetails) tmpPoIDetails).getReviews();
					if (!comments.contains(comment)) comments.add(comment);
				}
				String category = getAttributeValue(tmpObj, CATEGORY_ATTRIBUTE);
				if (!isNullOrEmpty(category)) {
					if (!tmpPoIDetails.getCategories().contains(category))
						tmpPoIDetails.getCategories().add(category);
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
		processCategories(elementsDetails);
		return elementsDetails;
	}
	
	private static ElementPoIDetails createPoIDetails(JSONObject json, String [] languages) {
		ElementPoIDetails poiDetails = new ElementPoIDetails();
		String id = getAttributeValue(json, "poi");
		if (isNullOrEmpty(id)) return null;
		poiDetails.setId(id);
		
		for (String language: LanguageUtils.getAllLanguages()) {
		    String name = getAttributeValue(json, "name_" + language);
		    if (!isNullOrEmpty(name)) poiDetails.setName(name);
		}
		
		for (String language: languages) {
		    String desc = getAttributeValue(json, "description_" + language);
		    if (!isNullOrEmpty(desc)) poiDetails.setDescription(desc);
		}
		
		List <String> categories = new LinkedList <String>();
		poiDetails.setCategories(categories);
		String category = getAttributeValue(json, CATEGORY_ATTRIBUTE);
		if (!isNullOrEmpty(category)) categories.add(category);
		
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

		double ratingValue = getRatingValue(json, "ratingValue1");
		if (ratingValue == 0) ratingValue = getRatingValue(json, "ratingValue2");
		if (ratingValue == 0) ratingValue = getRatingValue(json, "ratingValue3");
		if (ratingValue > 0) poiDetails.setAggregate_rating(ratingValue);
		
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
	
	private static ElementEventDetails createEventDetails(JSONObject json, String [] languages) {
		ElementEventDetails eventDetails = new ElementEventDetails();
		String id = getAttributeValue(json, "item");
		if (isNullOrEmpty(id)) return null;
		eventDetails.setId(id);
		
		for (String language: LanguageUtils.getAllLanguages()) {
		    String title = getAttributeValue(json, "title_" + language);
		    if (!isNullOrEmpty(title)) eventDetails.setName(title);
		}
		
		for (String language: languages) {
		    
		    String desc = getAttributeValue(json, "description_" + language);
		    if (!isNullOrEmpty(desc)) eventDetails.setDescription(desc);
		}
		
		List <String> categories = new LinkedList <String>();
		eventDetails.setCategories(categories);
		String category = getAttributeValue(json, CATEGORY_ATTRIBUTE);
		if (!isNullOrEmpty(category)) categories.add(category);

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
		if (eventDetails.getTime_end() == null) eventDetails.setTime_end(eventDetails.getTime_beginning());
		String image_url = getAttributeValue(json, "image_url");
		if (!isNullOrEmpty(image_url)) eventDetails.setImage_url(image_url);
		String source = getAttributeValue(json, "source");
		if (!isNullOrEmpty(source)) eventDetails.setSource(source);
		return eventDetails;
	}
	
	private static void addInfoOptional(String subject, String predicate, String object, String[] languages,
			boolean emptyFilter, StringBuilder result) {
		for (String language: languages) {
			result.append("OPTIONAL { ").append(subject).append(" ").append(predicate).append(" ").append(object).append("_").append(
					language).append(".  FILTER (langMatches(lang(").append(object).append("_").append(language).append("), \"").append(
							language.equalsIgnoreCase("empty") ? "" : language).append("\"))} \n");
		}
		if (emptyFilter) {
			result.append("FILTER (");
			int index = 0;
			for (String language: languages) {
				if (index > 0) {
					result.append(" || ");
				}
				result.append("(").append(object).append("_").append(language).append(" != \"\")");
				index++;
			}
			result.append(")\n");
		}
	}
	
	private static void processCategories(List<ElementDetails> elementsDetails) {
		StringBuilder catBuilder = new StringBuilder();
		for (ElementDetails ed: elementsDetails) {
			catBuilder.setLength(0);
			for (String category: ed.getCategories()) {
				if (catBuilder.length() > 0) catBuilder.append(", ");
				catBuilder.append(category);
			}
			ed.setCategory(catBuilder.toString());
			ed.setCategories(null);
		}
	}
	
	private static double getRatingValue(JSONObject json, String attributeName) {
		String aggregateRatingStr = getAttributeValue(json, attributeName);
		try {
		    if (!isNullOrEmpty(aggregateRatingStr)) return Double.parseDouble(aggregateRatingStr);
		} catch (Exception e) {
		}
		return 0;
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

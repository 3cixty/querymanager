package eu.threecixty.profile.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.profile.Review;
import eu.threecixty.profile.SparqlEndPointUtils;


/**
 * This is a utility class to get a list of events or PoIs in details as requested by TI team.
 * @author Cong-Kinh Nguyen
 *
 */
public class ElementDetailsUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 ElementDetailsUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final String COMMENT_ATTRIBUTE = "reviewBody"; // to get comment
	private static final String CATEGORY_ATTRIBUTE = "category";
	private static final String TOP_CATEGORY_ATTRIBUTE = "topCategory";
	
	private static final String TRANSLATION_TAG = "-tr";
	private static final String REVIEW_LANG = "reviewLang";
	
	/**
	 * Creates a list of events with info in details from a given list of IDs.
	 * @param eventIds
	 * @param categories
	 * 			The categories, null for all categories.
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createEventsDetails(Collection <String> eventIds, String[] categories, String[] languages) throws IOException {
		if (eventIds == null || eventIds.size() == 0) return Collections.emptyList();
		
		List <ElementDetails> finalList = new ArrayList <ElementDetails>();

		StringBuilder queryBuff = new StringBuilder("SELECT DISTINCT ?item ?title ?description ?category ?beginTime ?endTime ?lat ?lon ?street ?locality ?image_url ?source (lang(?description)  as ?language) ?url ?url1 ?url2 \n");
		queryBuff.append("WHERE {\n");
		queryBuff.append("{ graph <http://3cixty.com/events> {?item a lode:Event.} } \n");
		queryBuff.append("?item rdfs:label ?title . \n");
		queryBuff.append(" OPTIONAL { ?item rdfs:seeAlso ?url . } \n");
		queryBuff.append(" OPTIONAL { ?item owl:sameAs ?url2 . } \n");
		queryBuff.append(" OPTIONAL { ?item dc:description ?description . \n");
		//addLanguageFilter("description", languages, queryBuff);
		queryBuff.append(" } \n");

		if (categories == null) {
			queryBuff.append("OPTIONAL { ?item lode:hasCategory ?category . } \n");
		} else {
			queryBuff.append("?item lode:hasCategory ?category . \n");
			appendCategoriesFilter(queryBuff, categories);
		}
		
		queryBuff.append("OPTIONAL { ?item ?p ?inSpace. \n");
		queryBuff.append("              ?inSpace geo:lat ?lat .\n");
		queryBuff.append("              ?inSpace geo:long ?lon . }\n");
		queryBuff.append("OPTIONAL{ ?item lode:atPlace ?place1. \n");
		queryBuff.append("          ?place1 schema:url ?url1 . } \n");
		queryBuff.append("OPTIONAL{ ?item lode:atPlace ?place. \n");
		queryBuff.append("              ?place schema:location ?address .\n");
		queryBuff.append("              ?address schema:streetAddress ?street .\n");
		queryBuff.append("              ?address schema:addressLocality ?locality . }\n");
		queryBuff.append(" OPTIONAL{ ?item lode:atTime ?time.");
		queryBuff.append("              { ?time time:hasBeginning/time:inXSDDateTime ?beginTime .\n");
		queryBuff.append("                ?time time:hasEnd/time:inXSDDateTime ?endTime .\n");
		queryBuff.append("              }\n");
		queryBuff.append(" UNION { ?time time:inXSDDateTime ?beginTime .  } \n");
		queryBuff.append("}");
		queryBuff.append("OPTIONAL{ ?item lode:poster/ma-ont:locator ?image_url .}\n");
		queryBuff.append("OPTIONAL{ ?item dc:publisher ?source .}\n");
		
		queryBuff.append("VALUES ?item {");
		for (String eventId: eventIds) {
			ElementDetails tmp = DetailItemsCacheManager.getInstance().get(eventId);
			if (tmp != null) {
				for (String language: languages) {
				    if (!language.contains(TRANSLATION_TAG)) {
				    	ElementEventDetails eed = (ElementEventDetails) tmp;
				    	if (eed.containsDescIn(language)) finalList.add((eed).export(language));
				    	else if (eed.containsDescIn("en")) finalList.add((eed).export("en"));
				    	else finalList.add((eed).export(language));
				    }
				}
				continue;
			}
			queryBuff.append("<").append(eventId).append(">");

		}
		queryBuff.append("} \n");
		queryBuff.append("}");
		
		if (finalList.size() == eventIds.size()) return finalList;
		
		if (DEBUG_MOD) LOGGER.info("Get events in detail: " + queryBuff.toString());

		StringBuilder result = new StringBuilder();
		
		SparqlEndPointUtils.executeQueryViaSPARQL(queryBuff.toString(), "application/sparql-results+json", 
				SparqlEndPointUtils.HTTP_POST, result);

		JSONObject json = new JSONObject(
				SparqlEndPointUtils.cleanResultReceivedFromVirtuoso(result.toString()));
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len < 1) return finalList;
		
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
				String language = getAttributeValue(tmpObj, "language");
				
				String desc = getAttributeValue(tmpObj, "description");
				if (!isNullOrEmpty(desc) && !isNullOrEmpty(language)) {
					tmpEventDetails.putDescription(language, desc);
				}
			}
		}
		List <ElementDetails> elementsDetails = new LinkedList <ElementDetails>();
		elementsDetails.addAll(maps.values());
		processCategories(elementsDetails);
		
		DetailItemsCacheManager.getInstance().put(elementsDetails);
		for (ElementDetails tmp: elementsDetails) {
			for (String language: languages) {
			    if (!language.contains(TRANSLATION_TAG)) {
			    	ElementEventDetails eed = (ElementEventDetails) tmp;
			    	if (eed.containsDescIn(language)) finalList.add((eed).export(language));
			    	else if (eed.containsDescIn("en")) finalList.add((eed).export("en"));
			    	else finalList.add((eed).export(language));
			    }
			}
		}
		
		// reOrder final list
		reorder(finalList, eventIds);
		return finalList;
	}

	/**
	 * Reorder the list to keep the same order compared to input.
	 * @param finalList
	 * @param itemIds
	 */
	private static void reorder(List<ElementDetails> finalList,
			Collection<String> itemIds) {
		String [] ids = new String [itemIds.size()];
		itemIds.toArray(ids);
		int len = finalList.size();
		for (int i = 0; i < len; i++) {
			ElementDetails ed = finalList.get(i);
			String itemId = ids[i];
			if (itemId.equals(ed.getId())) continue;
			for (int j = i + 1; j < len; j++) {
				ElementDetails ed2 = finalList.get(j);
				if (itemId.equals(ed2.getId())) {
					finalList.set(i, ed2);
					finalList.set(j, ed);
					break;
				}
			}
		}
		
	}

	/**
	 * Creates a list of PoIs with info in details from a given list of IDs.
	 * @param poiIds
	 * @return
	 * @throws IOException
	 */
	public static List <ElementDetails> createPoIsDetails(Collection <String> poiIds,
			String[] categories, String[] topCategories, String[] languages) throws IOException {
		if (poiIds == null || poiIds.size() == 0) return Collections.emptyList();

		List <ElementDetails> finalList = new LinkedList <ElementDetails>();
		
		StringBuilder queryBuff = new StringBuilder("SELECT DISTINCT  ?poi ?name ?description (lang(?description)  as ?descLang) ?category ?topCategory  ?lat ?lon ?address ?reviewBody (lang(?reviewBody)  as ?reviewLang) ?ratingValue1 ?ratingValue2 ?ratingValue3 ?image_url ?source  ?telephone ?url ?url1 ?url2  \n");
		queryBuff.append("WHERE {\n");
		queryBuff.append(" { graph <http://3cixty.com/places> {?poi a dul:Place.} }  \n");
		
		queryBuff.append(" ?poi rdfs:label ?name .  \n");
		queryBuff.append(" OPTIONAL { ?poi owl:sameAs ?url . } \n");
		queryBuff.append(" OPTIONAL { ?poi schema:url ?url1 . } \n");
		queryBuff.append(" OPTIONAL { ?poi rdfs:seeAlso ?url2 . } \n");
		queryBuff.append(" OPTIONAL { ?poi schema:description ?description . \n");
		//addLanguageFilter("description", languages, queryBuff);
		queryBuff.append(" } \n");
		
		if (categories != null && categories.length > 0) {
			queryBuff.append("?poi locationOnt:businessType/skos:prefLabel ?category . \n");
			appendCategoriesFilter(queryBuff, categories);
		} else {
			queryBuff.append("OPTIONAL {?poi locationOnt:businessType/skos:prefLabel ?category . }\n");
		}
		
		if (topCategories != null && topCategories.length > 0) {
			queryBuff.append("?poi locationOnt:businessTypeTop/skos:prefLabel ?topCategory . \n");
			appendTopCategoriesFilter(queryBuff, topCategories);
		} else {
			queryBuff.append("OPTIONAL {?poi locationOnt:businessTypeTop/skos:prefLabel ?topCategory . }\n");
		}
		
		queryBuff.append("OPTIONAL{ ?poi schema:location/schema:streetAddress ?address . } \n");
		queryBuff.append("OPTIONAL {\n");
		queryBuff.append("?poi geo:location ?geoLocation . \n");
		queryBuff.append("?geoLocation geo:lat  ?lat . \n");
		queryBuff.append("?geoLocation geo:long  ?lon . \n");
		queryBuff.append("} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:review ?review .  \n");
		queryBuff.append("?review schema:reviewBody ?reviewBody .  \n");
		//addLanguageFilter("reviewBody", languages, queryBuff);
		queryBuff.append(" } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating ?ratingValue1 . } \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating/schema:ratingValue ?ratingValue2  .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:aggregateRating/schema:reviewRating/schema:ratingValue ?ratingValue3  .} \n");
		
		queryBuff.append("OPTIONAL{ ?poi schema:interactionCount ?reviewCounts .} \n");
		queryBuff.append("OPTIONAL{ ?poi lode:poster/ma-ont:locator ?image_url .} \n");
		queryBuff.append("OPTIONAL{ ?poi dc:publisher ?source .} \n");
		queryBuff.append("OPTIONAL{ ?poi schema:telephone ?telephone .} \n");
		
		queryBuff.append("VALUES ?poi {");
		for (String poiId: poiIds) {
			ElementDetails tmp = DetailItemsCacheManager.getInstance().get(poiId);
			if (tmp != null) {
				for (String language: languages) {
				    if (!language.contains(TRANSLATION_TAG)) {
				    	ElementPoIDetails epd = (ElementPoIDetails) tmp;
				    	if (epd.containsDescIn(language)) finalList.add((epd).export(language));
				    	else if (epd.containsDescIn("en")) finalList.add((epd).export("en"));
				    	else finalList.add((epd).export(language));
				    }
				}
				continue;
			}

			queryBuff.append("<").append(poiId).append(">");
		}
		queryBuff.append("} \n");
		queryBuff.append("}");
		
		if (finalList.size() == poiIds.size()) return finalList;
		
		if (DEBUG_MOD) LOGGER.info("Get PoIs in detail: " + queryBuff.toString());
		
		StringBuilder result = new StringBuilder();
		
		SparqlEndPointUtils.executeQueryViaSPARQL(queryBuff.toString(),
				"application/sparql-results+json", SparqlEndPointUtils.HTTP_POST, result);

		JSONObject json = new JSONObject(
				SparqlEndPointUtils.cleanResultReceivedFromVirtuoso(result.toString()));
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len < 1) return finalList;

		Map <String, ElementDetails> maps = new HashMap <String, ElementDetails>();
		
		ElementPoIDetails tmpPoIDetails = null;
		for (int i = 0; i  < len; i++) {
			JSONObject tmpObj = jsonArrs.getJSONObject(i);
			String currentId = tmpObj.get("poi").toString();
			if (currentId == null) continue;
			tmpPoIDetails = (ElementPoIDetails) maps.get(currentId);
			if (tmpPoIDetails == null) {
				tmpPoIDetails = createPoIDetails(tmpObj, languages);
				maps.put(currentId, tmpPoIDetails);
			} else {
				String comment = getAttributeValue(tmpObj, COMMENT_ATTRIBUTE);
				if (!isNullOrEmpty(comment)) {
					Review review = new Review();
					review.setText(comment);
					String reviewLanguage = getAttributeValue(tmpObj, REVIEW_LANG);
					if (!isNullOrEmpty(reviewLanguage)) review.setTranslated(reviewLanguage.contains(TRANSLATION_TAG));
					else review.setTranslated(false);
					((ElementPoIDetails) tmpPoIDetails).putReview(reviewLanguage, review);
				}
				String category = getAttributeValue(tmpObj, CATEGORY_ATTRIBUTE);
				if (!isNullOrEmpty(category)) {
					if (!tmpPoIDetails.getCategories().contains(category))
						tmpPoIDetails.getCategories().add(category);
				}
				String topCategory = getAttributeValue(tmpObj, TOP_CATEGORY_ATTRIBUTE);
				if (!isNullOrEmpty(topCategory)) {
					if (!tmpPoIDetails.getTopCategories().contains(topCategory))
						tmpPoIDetails.getTopCategories().add(topCategory);
				}
				String descLang = getAttributeValue(tmpObj, "descLang");
				
				String desc = getAttributeValue(tmpObj, "description");
				if (!isNullOrEmpty(desc) && !isNullOrEmpty(descLang)) {
					tmpPoIDetails.putDescription(descLang, desc);
				}
			}
		}
		
		List <ElementDetails> elementsDetails = new LinkedList <ElementDetails>();
		elementsDetails.addAll(maps.values());
		processCategories(elementsDetails);
		processTopCategories(elementsDetails);
		
		DetailItemsCacheManager.getInstance().put(elementsDetails);
		for (ElementDetails tmp: elementsDetails) {
			for (String language: languages) {
			    if (!language.contains(TRANSLATION_TAG)) {
			    	ElementPoIDetails epd = (ElementPoIDetails) tmp;
			    	if (epd.containsDescIn(language)) finalList.add((epd).export(language));
			    	else if (epd.containsDescIn("en")) finalList.add((epd).export("en"));
			    	else finalList.add((epd).export(language));
			    }
			}
		}
		
		for (ElementDetails tmp: finalList) {
			ElementPoIDetails poi = (ElementPoIDetails) tmp;
			if (poi.getReview_counts() == 0) {
				if (poi.getReviews() != null) poi.setReview_counts(poi.getReviews().size());
			}
		}
		
		// reOrder final list
		reorder(finalList, poiIds);
		return finalList;
	}
	
	private static ElementPoIDetails createPoIDetails(JSONObject json, String [] languages) {
		ElementPoIDetails poiDetails = new ElementPoIDetails();
		String id = getAttributeValue(json, "poi");
		if (isNullOrEmpty(id)) return null;
		poiDetails.setId(id);
		
		String name = getAttributeValue(json, "name");
		if (!isNullOrEmpty(name)) poiDetails.setName(name);
		
		String descLang = getAttributeValue(json, "descLang");
		
		String desc = getAttributeValue(json, "description");
		if (!isNullOrEmpty(desc) && !isNullOrEmpty(descLang)) {
			poiDetails.putDescription(descLang, desc);
		}
		
		List <String> categories = new LinkedList <String>();
		poiDetails.setCategories(categories);
		String category = getAttributeValue(json, CATEGORY_ATTRIBUTE);
		if (!isNullOrEmpty(category)) categories.add(category);
		
		List <String> topCategories = new LinkedList <String>();
		poiDetails.setTopCategories(topCategories);
		String topCategory = getAttributeValue(json, TOP_CATEGORY_ATTRIBUTE);
		if (!isNullOrEmpty(topCategory)) topCategories.add(topCategory);
		
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
		
		String telephone = getAttributeValue(json, "telephone");
		if (!isNullOrEmpty(telephone)) poiDetails.setTelephone(telephone);
		String comment = getAttributeValue(json, COMMENT_ATTRIBUTE);
		if (!isNullOrEmpty(comment)) {
			Review review = new Review();
			review.setText(comment);
			String reviewLanguage = getAttributeValue(json, REVIEW_LANG);
			if (!isNullOrEmpty(comment)) {
			    if (!isNullOrEmpty(reviewLanguage)) review.setTranslated(reviewLanguage.contains(TRANSLATION_TAG));
			    else review.setTranslated(false);
			}
			poiDetails.putReview(reviewLanguage, review);
		}
		
		String url = getAttributeValue(json, "url");
		if (isNullOrEmpty(url)) url = getAttributeValue(json, "url1");
		if (isNullOrEmpty(url)) url = getAttributeValue(json, "url2");
		if (!isNullOrEmpty(url)) poiDetails.setUrl(url);
		
		return poiDetails;
		
	}
	
	private static ElementEventDetails createEventDetails(JSONObject json, String [] languages) {
		ElementEventDetails eventDetails = new ElementEventDetails();
		String id = getAttributeValue(json, "item");
		if (isNullOrEmpty(id)) return null;
		eventDetails.setId(id);
		
		String title = getAttributeValue(json, "title");
		if (!isNullOrEmpty(title)) eventDetails.setName(title);
		 
		String language = getAttributeValue(json, "language");
		
		String desc = getAttributeValue(json, "description");
		if (!isNullOrEmpty(desc) && !isNullOrEmpty(language)) {
			eventDetails.putDescription(language, desc);
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
		String url = getAttributeValue(json, "url");
		if (isNullOrEmpty(url)) url = getAttributeValue(json, "url1");
		if (isNullOrEmpty(url)) url = getAttributeValue(json, "url2");
		if (!isNullOrEmpty(url)) eventDetails.setUrl(url);
		return eventDetails;
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
	
	private static void processTopCategories(List<ElementDetails> elementsDetails) {
		StringBuilder topCatBuilder = new StringBuilder();
		for (ElementDetails ed: elementsDetails) {
			topCatBuilder.setLength(0);
			for (String topCategory: ((ElementPoIDetails) ed).getTopCategories()) {
				if (topCatBuilder.length() > 0) topCatBuilder.append(", ");
				topCatBuilder.append(topCategory);
			}
			((ElementPoIDetails) ed).setTopCategory(topCatBuilder.toString());
			((ElementPoIDetails) ed).setTopCategories(null);
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
	
	private static void appendCategoriesFilter(StringBuilder sb, String[] categories) {
		if (categories.length > 0) {
			sb.append("FILTER (");
			int index = 0;
			for (String tmpCat: categories) {
				if (index > 0) {
					sb.append(" || ");
				}
				index++;
				sb.append("STR(?category) = \"" + tmpCat + "\"");
			}
			sb.append(") .\n");
		}
	}
	
	private static void appendTopCategoriesFilter(StringBuilder sb, String[] topCategories) {
		if (topCategories.length > 0) {
			sb.append("FILTER (");
			int index = 0;
			for (String tmpCat: topCategories) {
				if (index > 0) {
					sb.append(" || ");
				}
				index++;
				sb.append("STR(?topCategory) = \"" + tmpCat + "\"");
			}
			sb.append(") .\n");
		}
	}
	
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}
	
	private ElementDetailsUtils() {
	}
}

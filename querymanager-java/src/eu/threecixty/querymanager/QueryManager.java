package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.util.FileManager;

import eu.threecixty.ThreeCixtyExpression;
import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.UnknownException;
import eu.threecixty.profile.VirtuosoManager;
import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Place;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.Rating;

 public class QueryManager implements IQueryManager {
	 private static final String PREFIX_PROFILE_ADDED = "PREFIX profile:<http://www.eu.3cixty.org/profile#> ";
	 
	 private static final Logger LOGGER = Logger.getLogger(
			 QueryManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	 /**Original query*/
	 private ThreeCixtyQuery originalQuery;
	 
	 /**Current query*/
	private ThreeCixtyQuery query;

	/**Current augmented query*/
	private AugmentedQuery augmentedQuery;
	
	/**Attribute which is related to user profile*/
	private Model rdfModel;

	/**User ID*/
	private String uid;
	
	/**User's preferences*/
	private Preference preference;
	
	private String augmentedQueryStr;
	private boolean isForEvents;
	private boolean isForDateRanges;
	
	private int numberOfOrders;
	
	public QueryManager(String uid) {
		this(uid, null);
	}

	public QueryManager(String uid, Model userProfileModel) {
		this.rdfModel = userProfileModel;
	}
	
	@Override
	public AugmentedQuery getAugmentedQuery() {
		return augmentedQuery;
	}

	public String getAugmentedQueryWithoutPrefixes() {
		return augmentedQueryStr;
	}

	public ThreeCixtyQuery getQuery() {
		return query;
	}

	public void setQuery(ThreeCixtyQuery query){
		if (query == null) return;
		this.query = query;
		this.originalQuery = query.cloneQuery();
		augmentedQuery = new AugmentedQuery(query);
	}
	
	@Override
	public String askForExecutingAugmentedQueryAtEventMedia(AugmentedQuery augmentedQuery,
			EventMediaFormat format) throws IOException, UnknownException {
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		augmentedQuery.getQuery().getQuery().setDistinct(true);
		if (!augmentedQuery.getQuery().getQuery().hasLimit()) {
			if (!augmentedQuery.getQuery().getQuery().hasAggregators()) {
				augmentedQuery.getQuery().getQuery().setLimit(20);
			}
		}
		
		augmentedQueryStr = augmentedQuery.getQuery().getQuery().toString();
		
		String originalQueryStr = originalQuery.convert2String();
		try {
			if (originalQuery != null && originalQueryStr.contains("http://schema.org/")) {
			    augmentedQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
			            + removePrefixes(augmentedQueryStr);
			    originalQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
			    		+ removePrefixes(originalQueryStr);
			} else {
				augmentedQueryStr = removePrefixes(augmentedQueryStr);
				originalQueryStr = removePrefixes(originalQueryStr);
			}
			
			logInfo("Augmented query: " + augmentedQueryStr);
			
			StringBuilder sb = new StringBuilder();
			
			boolean ok = hasElementsForBindings(augmentedQueryStr, format, formatType, sb, uid, numberOfOrders);
			if (ok) return sb.toString();
			
			hasElementsForBindings(originalQueryStr, format, formatType, sb, uid, numberOfOrders);
			
			return sb.toString();

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
			throw new UnknownException(e.getMessage());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
			throw new UnknownException(e.getMessage());
		}
	}
	
	public Map<String, Boolean> askForExecutingAugmentedQuery(AugmentedQuery augmentedQuery) throws IOException {
		String formatType = "application/sparql-results+json";
		augmentedQuery.getQuery().getQuery().setDistinct(true);
		if (!augmentedQuery.getQuery().getQuery().hasLimit()) {
			if (!augmentedQuery.getQuery().getQuery().hasAggregators()) {
				augmentedQuery.getQuery().getQuery().setLimit(20);
			}
		}
		
		augmentedQueryStr = augmentedQuery.getQuery().getQuery().toString();
		
		String originalQueryStr = originalQuery.convert2String();
		if (originalQuery != null && originalQueryStr.contains("http://schema.org/")) {
		    augmentedQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
		            + removePrefixes(augmentedQueryStr);
		    originalQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
		    		+ removePrefixes(originalQueryStr);
		} else {
			augmentedQueryStr = removePrefixes(augmentedQueryStr);
			originalQueryStr = removePrefixes(originalQueryStr);
		}
		
		logInfo("Augmented query: " + augmentedQueryStr);
		
		StringBuilder sb = new StringBuilder();

		
		Map <String, Boolean> maps = new HashMap <String, Boolean>();
		
		VirtuosoManager.getInstance().executeQueryViaSPARQL(augmentedQueryStr, formatType, sb);
		JSONObject json = new JSONObject(sb.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");

		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			boolean hightLighted = isItemAugmented(jsonElement, numberOfOrders);
			String elementId = null;
			if (jsonElement.has("event")) {
				elementId = jsonElement.getJSONObject("event").get("value").toString();
			} else if (jsonElement.has("venue")) {
				elementId = jsonElement.getJSONObject("venue").get("value").toString();
			}
			if (elementId != null) {
				maps.put(elementId, hightLighted);
			}
		}

		return maps;
	}

	/**
	 * Executes query without creating a new instance of QueryManager.
	 * <br>
	 * Note that this method doesn't augment the given query.
	 * @param query
	 * @param format
	 * @return
	 */
	public static String executeQuery(String query, EventMediaFormat format) throws IOException {
		if (query == null || format == null) return "";
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		StringBuilder builder = new StringBuilder();
		hasElementsForBindings(query, format, formatType, builder, null, 0);
		return builder.toString();
	}
	
	/**
	 * Get a list of Element IDs (Events or PoIs) from Virtuoso for a given SPARQL query. This method supposes that the
	 * given query was created by QueryManagerServices from line 742-769.
	 *
	 * @param query
	 * @return
	 */
	public static List <String> getElementIDs(String query) throws IOException {

		String formatType = "application/sparql-results+json";

		StringBuilder sb = new StringBuilder();
		
		List <String> elementIds = new LinkedList <String>();

		VirtuosoManager.getInstance().executeQueryViaSPARQL(query, formatType, sb);
		JSONObject json = new JSONObject(sb.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");

		int len = jsonArrs.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			String elementId = null;
			if (jsonElement.has("event")) {
				elementId = jsonElement.getJSONObject("event").get("value").toString();
			} else if (jsonElement.has("venue")) {
				elementId = jsonElement.getJSONObject("venue").get("value").toString();
			}
			if (elementId != null) elementIds.add(elementId);
		}
		return elementIds;
	}

	/**
	 * Checks whether or not there is one element at least for result.
	 * @param query
	 * @param buffer
	 * @return
	 * @throws IOException 
	 */
	private static boolean hasElementsForBindings(String query, EventMediaFormat format, String formatType,
			StringBuilder sb, String uid, int numberOfOrders) throws IOException {
		sb.setLength(0);

		logInfo("Query to be executed: " + query);

		boolean ok = true;
		// only make queries to public graphs
//		if (uid == null) { // only public graphs
		VirtuosoManager.getInstance().executeQueryViaSPARQL(query, formatType, sb);

			if (EventMediaFormat.JSON == format) {
				try {
				    ok = hasElement(sb, numberOfOrders);
				} catch (JSONException e) { // check if there are some backslashes
					String newString = VirtuosoManager.getInstance().cleanResultReceivedFromVirtuoso(sb.toString());
					//newString = newString.replace("\\","\\\\");
					//newString = newString.replace("\\\\\"", "\\\\\\\"");
					if (DEBUG_MOD) LOGGER.info(newString);
					sb.setLength(0);
					sb.append(newString);
					ok = hasElement(sb, numberOfOrders);
				}
			}
//		} else {
//			JSONObject result = VirtuosoManager.getInstance().executeQuery(query, uid);
//			if (result.getJSONObject("results").getJSONArray("bindings").length() < 1) {
//				ok = false;
//			}
//			sb.append(result.toString());
//		}
		
		logInfo("Finished executing the query on Virtuoso: ok = " + ok);

		return ok;
	}
	
	private static boolean hasElement(StringBuilder sb, int numberOfOrders) throws JSONException {
		// check if there is one element at least
		JSONObject json = new JSONObject(sb.toString());
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		if (jsonArrs.length() < 1) {
			return false;
		} else {
			if (DEBUG_MOD) LOGGER.info("Result received from Virtuoso: " + sb.toString());
			// add augmented to vars
			if (numberOfOrders > 0) {
				JSONObject jsonHead = json.getJSONObject("head");
				JSONArray newArrs = new JSONArray();
				JSONArray subHeadArrs = jsonHead.getJSONArray("vars");
				for (int i = 0; i < subHeadArrs.length(); i++) {
					String varName = subHeadArrs.get(i).toString();
					boolean found = false;
					for (int index = 0; index <= 10; index++) {
						if (varName.equals("callret-" + index)) {
							found = true;
							break;
						}
					}
					if (!found) {
						newArrs.put(varName);
					}
				}
				newArrs.put("augmented");
				jsonHead.put("vars", newArrs);
			}
			
			// add augmented to item
			for (int i = 0; i < jsonArrs.length(); i++) {
				JSONObject jsonElement = jsonArrs.getJSONObject(i);
				cleanResultAndAddAugmented(jsonElement, numberOfOrders);
			}
			sb.setLength(0);
			sb.append(json.toString());
			if (DEBUG_MOD) LOGGER.info("Result to send back to request: " + sb.toString());
		}
		return true;
	}
	
	private static void cleanResultAndAddAugmented(JSONObject jsonElement, int numberOfOrders) throws JSONException {
		boolean augmented = false;
		augmented = checkPropertyTrueAndRemove(jsonElement, "callret");
		for (int index = 0; index <= numberOfOrders; index++) {
			if (augmented) checkPropertyTrueAndRemove(jsonElement, "callret-" + index); // only remove
			else augmented = checkPropertyTrueAndRemove(jsonElement, "callret-" + index);
		}
		if (numberOfOrders > 0) jsonElement.put("augmented", augmented);
	}
	
	private static boolean isItemAugmented(JSONObject jsonElement, int numberOfOrders) {
		boolean augmented = checkPropertyTrue(jsonElement, "callret");
		for (int index = 0; index <= numberOfOrders; index++) {
			if (augmented) break;
			else augmented = checkPropertyTrue(jsonElement, "callret-" + index);
		}
		return augmented;
	}
	
	private static boolean checkPropertyTrueAndRemove(JSONObject jsonObject, String property) throws JSONException {
		if (jsonObject.has(property)) {
			String val = jsonObject.getJSONObject(property).getString("value");
			jsonObject.remove(property);
			return val.equals("1");
		}
		return false;
	}
	
	private static boolean checkPropertyTrue(JSONObject jsonObject, String property) {
		if (jsonObject.has(property)) {
			String val = jsonObject.getJSONObject(property).getString("value");
			return val.equals("1");
		}
		return false;
	}

	@Override
	public void requestPreferences(IProfiler profiler) {
		if (profiler != null) {
			this.preference = profiler.getPreference();
		}
	}

	@Override
	public void storeResultInUserQueryProfile(Query inputQuery, Query augmentedQuery,String updateQueryString) {
		// TODO: update KB of UserProfile. Write the update query.
		//runnning on the server with multiple instances so concurrency control has to be there.
		
/*		Query updateQuery=QueryFactory.create(updateQueryString);
		UpdateRequest request=UpdateFactory.create(updateQueryString);
		
		updateProcessRemote.execute();//(updateQueryString);
		//GraphStore graphStore = GraphStoreFactory.create();
		UpdateAction.execute(request, this.connection);*/
	}

	@Override
	public void performAugmentingTask() {
		if (preference == null || query == null) return;
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList <Expr>();
		addTriplesAndExprsToLists(triples, exprs);
		performORAugmentation(triples, exprs); // perform ORAugmentation by default
	}

//	public void performANDAugmentation(List<Triple> triples, List<Expr> exprs) {
//		if (preference == null || query == null) return;
//		if (triples.size() == 0 && exprs.size() == 0) return;
//		augmentedQuery = new AugmentedQuery(query.cloneQuery());
//		QueryUtils.addTriplesIntoQuery(triples, augmentedQuery.getQuery().getQuery());
//		QueryUtils.addAND_ExprsIntoQuery(exprs, augmentedQuery.getQuery().getQuery());
//	}

	public void performORAugmentation(List<Triple> triples, List<Expr> exprs) {
		numberOfOrders = 0;
		if (preference == null || query == null) return;
		if (triples.size() == 0 && exprs.size() == 0) return;
		if (DEBUG_MOD) {
			logInfo("Enter into the performORAugmentation method");
			logInfo("Information about triples added: ----------------------");
			for (Triple triple: triples) {
				logInfo(triple.toString());
			}
			logInfo("-------------------------------------------------------");
			logInfo("Information about expressions added: ----------------------");
			for (Expr expr: exprs) {
				logInfo(expr.toString());
			}
			logInfo("-------------------------------------------------------");
		}
		augmentedQuery = new AugmentedQuery(query.cloneQuery());
		if (augmentedQuery.getQuery().getQuery().hasAggregators()) return;
		QueryUtils.addTriplesIntoQuery(triples, augmentedQuery.getQuery().getQuery());
		QueryUtils.addOrderToQuery(exprs, augmentedQuery.getQuery().getQuery());
		QueryUtils.addVarNameResultsToQuery(exprs, augmentedQuery.getQuery().getQuery());
		numberOfOrders = exprs.size() + originalQuery.getQuery().getResultVars().size();
	}

	@Override
	public void addTriplesAndExprsToLists(
			List<Triple> triples, List<Expr> exprs) {
		if (query == null || preference == null) return;
		
		logInfo("Enter into the addTriplesAndExprsToLists method");
		
		Set <Place> places = preference.getHasPlaces();
		if (places != null && places.size() > 0) {
			logInfo("List of places: --------------------");
			for (Place place: places) {
				logInfo("Place name: " + place.getHasPlaceDetail().getHasPlaceName()
						+ ", NatureOfPlace: " + place.getHasPlaceDetail().getHasNatureOfPlace());
				query.addExpressionsAndTriples(place, exprs, triples, isForEvents);
			}
			logInfo("------------------------------------");
		}

		Set <Event> events = preference.getHasEvents();
		if (events != null && events.size() > 0) {
			logInfo("List of events: --------------------");
			for (Event event: events) {
				logInfo("Event name: " + event.getHasEventDetail().getHasEventName()
						+ ", NatureOfEvent: " + event.getHasEventDetail().getHasNatureOfEvent());
				query.addExpressionsAndTriples(event, exprs, triples, isForEvents);
			}
			logInfo("------------------------------------");
		}

		Set <Period> periods = preference.getHasPeriods();
		if (periods != null && periods.size() > 0) {
			logInfo("List of periods: -------------------");
			addPeriodsToTriplesAndExprsList(periods, query, triples, exprs, isForEvents);
			logInfo("------------------------------------");
		}

		Set <Double> scoresRequired = preference.getScoresRequired();
		if (scoresRequired != null && scoresRequired.size() > 0) {
			logInfo("List of scores: --------------------");
			for (Double score: scoresRequired) {
				logInfo("Score: " + score);
				Rating rating = new Rating();
				rating.setHasUseDefinedRating(score);
				query.addExpressionsAndTriples(rating, exprs, triples, isForEvents);
			}
			logInfo("------------------------------------");
		}
	}

	@Override
	public Model getModel() {
		return rdfModel;
	}

	@Override
	public void setModel(Model model) {
		this.rdfModel = model;
	}

	@Override
	public void setModel(InputStream modelStream) {
		if (modelStream == null) return;
		rdfModel = ModelFactory.createDefaultModel();
		rdfModel = rdfModel.read(modelStream, "UTF-8");
	}

	@Override
	public void setModel(String rdfContent) {
		if (rdfContent == null) return;
		rdfModel = ModelFactory.createDefaultModel();
		rdfModel = rdfModel.read(rdfContent);
	}

	@Override
	public void setModelFromFileOrUri(String filenameOrURI) {
		if (filenameOrURI == null) return;
		FileManager.get().addLocatorClassLoader(QueryManager.class.getClassLoader());
		rdfModel = FileManager.get().loadModel(filenameOrURI);
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public Query createJenaQuery(String queryStr) {
		if (queryStr == null) return null;
		return QueryFactory.create(queryStr);
	}

	@Override
	public QResult executeQuery(AugmentedQuery query) {
		if (query == null) return null;
		if (rdfModel == null) return null;
		QueryExecution qe = QueryExecutionFactory.create(query.getQuery().getQuery(), rdfModel);
		
		ResultSet rs = qe.execSelect();
		
		return new QResult(rs, qe);
	}

	@Override
	public QResult executeAugmentedQuery() {
		return executeQuery(augmentedQuery);
	}

	public boolean isForEvents() {
		return isForEvents;
	}

	public void setForEvents(boolean isForEvents) {
		this.isForEvents = isForEvents;
	}

	private void addPeriodsToTriplesAndExprsList(Set<Period> periods, ThreeCixtyQuery query, List <Triple> triples, List <Expr> exprs, boolean isForEvents) {
		for (Period period: periods) {
			logInfo("start date: " + period.getStartDate() + ", end date: " + period.getEndDate());
			query.addExprsAndTriplesFromAttributeNameAndPropertyName(period, "startDate", "datetime",
					exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
			query.addExprsAndTriplesFromAttributeNameAndPropertyName(period, "endDate", "datetime",
					exprs, triples, ThreeCixtyExpression.LessThanOrEqual, isForEvents);
		}
	}

	public static String removePrefixes(String query) {
		if (query == null) return null;
		int lastPrefixIndex = query.lastIndexOf("PREFIX");
		if (lastPrefixIndex < 0) return query;
		int index = query.indexOf('\n', lastPrefixIndex);
		if (index >= lastPrefixIndex) {
			return query.substring(index + 2);
		}
		return PREFIX_PROFILE_ADDED + query;
	}
	
	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}

	@Override
	public boolean isForDateRanges() {
		return isForDateRanges;
	}

	@Override
	public void setForDateRanges(boolean isForDateRanges) {
		this.isForDateRanges = isForDateRanges;
	}
}
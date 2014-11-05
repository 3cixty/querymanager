package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Place;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.Rating;

 public class QueryManager implements IQueryManager {
	 //private static final String EVENTMEDIA_URL_PREFIX = "http://eventmedia.eurecom.fr/sparql?default-graph-uri=&query=";
	 //private static final String EVENTMEDIA_URL_PREFIX = "http://3cixty.eurecom.fr/sparql?default-graph-uri=&query=";
	 private static final String SPARQL_ENDPOINT_URL = ProfileManagerImpl.SPARQL_ENDPOINT_URL;
	 
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

	private  String getAugmentedQueryWithoutPrefixes(AugmentedQuery augmentedQuery) {
		if (augmentedQuery == null) return "";
		augmentedQuery.getQuery().getQuery().setDistinct(true);
		String augmentedQueryStr = removePrefixes(augmentedQuery.convert2String());
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
			EventMediaFormat format, boolean augmentedQueryIncluded) {
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		augmentedQueryStr = "";
		String originalQueryStr = originalQuery.convert2String();
		try {
			if (originalQuery != null && originalQueryStr.contains("http://schema.org/")) {
			    augmentedQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
			            + getAugmentedQueryWithoutPrefixes(augmentedQuery);
			    originalQueryStr = "PREFIX schema: <http://schema.org/>\n PREFIX locationOnt: <http://data.linkedevents.org/def/location#> \n "
			    		+ removePrefixes(originalQueryStr);
			} else {
				augmentedQueryStr = getAugmentedQueryWithoutPrefixes(augmentedQuery);
				originalQueryStr = removePrefixes(originalQueryStr);
			}
			
			logInfo("Augmented query: " + augmentedQueryStr);
			
			StringBuilder sb = new StringBuilder();
			
			boolean ok = hasElementsForBindings(augmentedQueryStr, format, formatType, augmentedQueryIncluded, sb);
			if (ok) return sb.toString();
			
			hasElementsForBindings(originalQueryStr, format, formatType, augmentedQueryIncluded, sb);
			
			return sb.toString();

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
			return "ERROR:" + e.getMessage();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
			return "ERROR:" + e.getMessage();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return "ERROR:" + e.getMessage();
		}
	}

	/**
	 * Executes query without creating a new instance of QueryManager.
	 * <br>
	 * Note that this method doesn't augment the given query.
	 * @param query
	 * @param format
	 * @return
	 */
	public static String executeQuery(String query, EventMediaFormat format) {
		if (query == null || format == null) return "";
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		StringBuilder builder = new StringBuilder();
		try {
			hasElementsForBindings(query, format, formatType, false, builder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * Checks whether or not there is one element at least for result.
	 * @param query
	 * @param buffer
	 * @return
	 * @throws IOException 
	 */
	private static boolean hasElementsForBindings(String query, EventMediaFormat format, String formatType,
			boolean augmentedQueryIncluded, StringBuilder sb) throws IOException {
		sb.setLength(0);

		logInfo("Query to be executed: " + query);

		String urlStr = SPARQL_ENDPOINT_URL + URLEncoder.encode(query, "UTF-8");
		urlStr += "&format=" + URLEncoder.encode(formatType, "UTF-8");

		URL url = new URL(urlStr);

		InputStream input = url.openStream();
		byte [] b = new byte[1024];
		int readBytes = 0;
		while ((readBytes = input.read(b)) >= 0) {
			sb.append(new String(b, 0, readBytes));
		}
		input.close();
		boolean ok = true;
		if (augmentedQueryIncluded) {
			if (EventMediaFormat.JSON == format) {
				// check if there is one element at least
				JSONObject json = new JSONObject(sb.toString());
				if (json.getJSONObject("results").getJSONArray("bindings").length() < 1) {
					ok = false;
				}
				
				int lastIndex = sb.lastIndexOf("}");
				if (lastIndex >= 0) {
					JSONArray jsonArr = new JSONArray();
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("AugmentedQuery", query);
					jsonArr.put(jsonObj);
					String augmentedQueryJson = ", " + "\"AugmentedQueries\": " + jsonArr.toString();
					sb.insert(lastIndex, augmentedQueryJson);
				}
			}
		}
		return ok;
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
		QueryUtils.addTriplesIntoQuery(triples, augmentedQuery.getQuery().getQuery());
		QueryUtils.addFilterWithOrOperandForExprsIntoQuery(exprs, augmentedQuery.getQuery().getQuery());
		if (augmentedQuery.getQuery().getQuery().hasAggregators()) return;
		QueryUtils.addOrderToQuery(exprs, augmentedQuery.getQuery().getQuery());
		QueryUtils.addVarNameResultsToQuery(exprs, augmentedQuery.getQuery().getQuery());
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

	private String removePrefixes(String query) {
		int lastPrefixIndex = query.lastIndexOf("PREFIX");
		if (lastPrefixIndex < 0) return query;
		int index = query.indexOf('\n', lastPrefixIndex);
		if (index >= lastPrefixIndex) {
			return query.substring(index + 2);
		}
		return query;
	}
	
	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}
}
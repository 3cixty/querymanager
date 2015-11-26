package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import eu.threecixty.Configuration;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.SparqlEndPointUtils;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.elements.ElementDetails;
import eu.threecixty.profile.elements.ElementDetailsUtils;
import eu.threecixty.profile.elements.LanguageUtils;
import eu.threecixty.querymanager.EventMediaFormat;
import eu.threecixty.querymanager.QueryAugmentationUtils;
import eu.threecixty.querymanager.QueryAugmenterFilter;
import eu.threecixty.querymanager.QueryAugmenterImpl;

/**
 * The class is an end point for QA RestAPIs to expose to other components.
 * @author Cong-Kinh Nguyen
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class QueryManagerServices {
	private static final String LOCALITY_TRIPLES = "?event lode:atPlace ?place . \n ?place vcard:adr ?address . \n ?address vcard:locality ?locality .\n";
	
	 private static final Logger LOGGER = Logger.getLogger(
			 QueryManagerServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final Map <String, String> groupTriples = new HashMap <String, String>();
	
	static {
		groupTriples.put("locality", LOCALITY_TRIPLES);
		groupTriples.put("category", "?event lode:hasCategory ?category .\n");
		groupTriples.put("country", "?event lode:atPlace ?place .\n ?place vcard:adr ?address.	\n ?address vcard:country-name ?country.\n");
		groupTriples.put("publisher", "?event lode:involvedAgent ?involvedAgent .\n ?involvedAgent dc:publisher ?publisher .\n");
		groupTriples.put("placeName", "?event rdfs:label ?title;	lode:atPlace ?place. \n	?place rdfs:label ?placeName.\n");
		groupTriples.put("artist", "?event lode:involvedAgent ?involvedAgent .\n ?involvedAgent rdfs:label ?artist .\n");
	}

	/**The attribute which is real path to Servlet*/
	public static String realPath;
	
	/**
	 * This API is used to augment a given query based on the user's reviews or his/her friends/travelmates
	 * crawled from Google places. Then, the augmented query is sent to KB to execute. The result received
	 * from KB will be sent back to the requester of this API.
	 * <br>
	 * To augment the query, the formula <code>totalScore = coef * socialScore + editorialScore</code> is used
	 * to order items by <code>totalScore</code>. To change the order of result, third party developers just
	 * need to change a different <code>coef</code>. Check documentation for Query Augmentation to get more
	 * detail about the algorithm used to calculate <code>socialScore</code> (task 2).
	 * 
	 * @param access_token
	 * 				The 3cixty access token
	 * @param format
	 * 				Output format (json)
	 * @param query
	 * 				The SPARQL query
	 * @param coef
	 * 				The coefficient value
	 * @param filter
	 * 				The filter which is currently either <code>friends</code> or <code>enteredrating</code>. Note that
	 * 				the value <code>null</code> means that the query doesn't need to be augmented.
	 * @param debug
	 * 				The flag for debug
	 * @param turnOffQA
	 * 				The flag to turn off query augmentation.
	 * @return
	 */
	@POST
	@Path("/augmentAndExecute2")
	public Response executeQueryPOST(@HeaderParam("access_token") String access_token,
			@FormParam("format") String format, @FormParam("query") String query,
			@DefaultValue("1") @FormParam("coef") double coef,
			@FormParam("filter") String filter, @DefaultValue("off") @FormParam("debug") String debug,
			@DefaultValue("false") @QueryParam("turnOffQA") String turnOffQA) {
		
		//return executeQueryWithHttpMethod(access_token, format, query, filter, debug, SparqlEndPointUtils.HTTP_POST);
		if (!"true".equalsIgnoreCase(turnOffQA)) return executeQueryWithHttpMethod(access_token, format, query, coef, filter, debug, SparqlEndPointUtils.HTTP_POST);
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String key = userAccessToken.getAppkey();
			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
			if (eventMediaFormat == null || query == null) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNSUPPORTED_FORMAT);
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("The format is not supported or query is null")
						.type(MediaType.TEXT_PLAIN)
						.build();
			} else {

				try {
					String result = executeQuery(query, eventMediaFormat, SparqlEndPointUtils.HTTP_POST, key, false);

					// log calls
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);

					return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
							MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		}
		return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Invalid access token")
		        .type(MediaType.TEXT_PLAIN)
		        .build();
	}

	/**
	 * This API is a GET version of {@link augmentAndExecute2}.
	 * @see augmentAndExecute2
	 * 
	 * @param access_token
	 * @param format
	 * @param query
	 * @param coef
	 * @param filter
	 * @param debug
	 * @param turnOffQA
	 * @return
	 */
	@GET
	@Path("/augmentAndExecute")
	public Response executeQuery(@HeaderParam("access_token") String access_token,
			@QueryParam("format") String format, @QueryParam("query") String query,
			@DefaultValue("1") @QueryParam("coef") double coef,
			@QueryParam("filter") String filter, @DefaultValue("off") @QueryParam("debug") String debug,
			@DefaultValue("false") @QueryParam("turnOffQA") String turnOffQA) {
		// check whether or not the flag for turning off QA is true
		if (!"true".equalsIgnoreCase(turnOffQA)) return executeQueryWithHttpMethod(access_token, format, query, coef, filter, debug, SparqlEndPointUtils.HTTP_GET);
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String key = userAccessToken.getAppkey();
			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
			if (eventMediaFormat == null || query == null) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNSUPPORTED_FORMAT);
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("The format is not supported or query is null")
						.type(MediaType.TEXT_PLAIN)
						.build();
			} else {

				try {
					String result = executeQuery(query, eventMediaFormat, SparqlEndPointUtils.HTTP_POST, key, false);

					// log calls
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);

					return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
							MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		}
		return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Invalid access token")
		        .type(MediaType.TEXT_PLAIN)
		        .build();
	}
	
	/**
	 * This method is used to augment a query for both GET and POST methods.
	 *
	 * @param access_token
	 * @param format
	 * @param query
	 * @param coef
	 * @param filter
	 * @param debug
	 * @param httpMethod
	 * @return
	 */
	private Response executeQueryWithHttpMethod(String access_token,
			String format, String query, double coef,
			String filter, String debug, String httpMethod) {
		if (DEBUG_MOD) LOGGER.info("Start augmentAndExecute method ----------------------");
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			if (DEBUG_MOD) LOGGER.info("Found a valid access token");
			String user_id =  userAccessToken.getUid();
			if ("on".equals(debug)) {
				user_id = "107217557295681360318";
			}
			String key = userAccessToken.getAppkey();

			try {
				if (DEBUG_MOD) LOGGER.info("Before augmenting and executing a query");

				String result = executeQuery(user_id, query, filter, format, httpMethod, coef, key);

				// log calls

				if (filter == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				} else if (filter.equals("location")) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_WITH_LOCATION_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				} else if (filter.equals("enteredrating")) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_WITH_USERENTERED_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				} else if (filter.equals("preferred")) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_WITH_PREFERRED_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				} else if (filter.equals("friends")) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_WITH_FRIENDS_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				} else {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				}

				return Response.ok(result, Constants.JSON.equals(format) ?
						MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
			} catch (IOException e) {
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
						.entity(e.getMessage())
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} else {
			if (access_token != null && !access_token.equals("")) CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}

	/**
	 * This API is used to execute a given query with Virtuoso KB through HTTP POST.
	 *
	 * @param key
	 * @param format
	 * @param query
	 * @param filter
	 * @return
	 */
	@POST
	@Path("/executeQuery2")
	public Response executeQueryNoAccessTokenPost(@HeaderParam("key") String key, 
			@FormParam("format") String format, @FormParam("query") String query) {
		return executeQueryNoAccessTokenWithHttpMethod(key, format, query, SparqlEndPointUtils.HTTP_POST);
	}
	
	/**
	 * This API is used to execute a given query with Virtuoso KB through HTTP POST.
	 *
	 * @param key
	 * @param format
	 * @param query
	 * @return
	 */
	@GET
	@Path("/executeQueryNoKey")
	public Response executeQueryNoAccessToken( 
			@QueryParam("format") String format, @QueryParam("query") String query) {
		EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
		String result;
		try {
			result = executeQuery(query, eventMediaFormat, SparqlEndPointUtils.HTTP_GET, true);
			return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
					MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
			        .entity(e.getMessage())
			        .type(MediaType.TEXT_PLAIN)
			        .build();
		}
	}
	
	@GET
	@Path("/returnInput")
	public Response returnInput(String input) {
			return Response.ok(input).type(MediaType.TEXT_PLAIN_TYPE).build();
	}
	
	@GET
	@Path("/executeQuery")
	public Response executeQueryNoAppKey(@HeaderParam("key") String key, 
			@QueryParam("format") String format, @QueryParam("query") String query) {
		return executeQueryNoAccessTokenWithHttpMethod(key, format, query, SparqlEndPointUtils.HTTP_GET);
	}
	
	/**
	 * This method is used to execute a given query with Virtuoso through either HTTP GET or POST.
	 *
	 * @param key
	 * @param format
	 * @param query
	 * @param httpMethod
	 * @return
	 */
	private Response executeQueryNoAccessTokenWithHttpMethod(String key, 
			String format, String query, String httpMethod) {
		logInfo("Start executeQuery method ----------------------");
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			logInfo("App key is validated");
			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
			if (eventMediaFormat == null || query == null) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNSUPPORTED_FORMAT);
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("The format is not supported or query is null")
						.type(MediaType.TEXT_PLAIN)
						.build();
			} else {

				try {
					String result = executeQuery(query, eventMediaFormat, httpMethod, key, false);

					// log calls
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);

					return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
							MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		} else {
			if (key != null && !key.equals(""))  CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to get events or places in detail.
	 *
	 * @param key
	 * 				The application key
	 * @param languages
	 * 				The language code (two characters)
	 * @param events
	 * 				The list of event IDs separated by comma
	 * @param pois
	 * 				The list of place IDs separated by comma
	 * @param city
	 * 				The city
	 * @return
	 */
	@GET
	@Path("/getElementsInDetails")
	public Response getElementsInDetails(@HeaderParam("key") String key,
			@HeaderParam("Accept-Language") String languages, 
			@QueryParam("events") String events, @QueryParam("pois") String pois,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			logInfo("App key is validated");

			String [] tmpLanguages = LanguageUtils.getLanguages(languages);
			
			try {
				JSONObject result = new JSONObject();
				if (events != null && !events.equals("")) {
					List <String> eventIds = createList(events);
					List <ElementDetails> eventsDetails = ElementDetailsUtils.createEventsDetails(
							SparqlChooser.getEndPointUrl(key),
							SparqlChooser.getEventGraph(key, city), eventIds, null, tmpLanguages);
					if (eventsDetails != null) {
						// use Events key for all events
						result.put("Events", eventsDetails);
					}

				}
				if (pois != null && !pois.equals("")) {
					List <String> poiIds = createList(pois);

					List <ElementDetails> poisDetails = ElementDetailsUtils.createPoIsDetails(
							SparqlChooser.getEndPointUrl(key), SparqlChooser.getPoIGraph(key, city),
							poiIds, null, null, tmpLanguages);
					if (poisDetails != null) {
						// use POIs key for all places
						result.put("POIs", poisDetails);
					}
				}
				
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				
				return Response.ok(result.toString(), MediaType.APPLICATION_JSON_TYPE ).build();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals("")) CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to count number of events.
	 *
	 * @param key
	 * 				The application key.
	 * @return
	 */
	@GET
	@Path("/countItems")
	public Response countItems(@HeaderParam("key") String key,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			String query = "SELECT (COUNT(*) AS ?count) \n WHERE { \n { graph " + SparqlChooser.getEventGraph(key, city) + " { ?event a lode:Event. } } \n } ";
			try {
				String ret = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);

				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals("")) CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}

	/**
	 * This API is used to count number of PoIs.
	 *
	 * @param key
	 * 				The application key
	 * @return
	 */
	@GET
	@Path("/countPoIs")
	public Response countPoIs(@HeaderParam("key") String key,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			String query = "SELECT DISTINCT  (count(*) AS ?count)\nWHERE\n  { { graph " + SparqlChooser.getPoIGraph(key, city) + "  {?venue rdf:type dul:Place.} } }";

			try {
				String ret = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);

				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals(""))  CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}

	/**
	 * This API is used to get aggregated information about events.
	 * @param group
	 * @param offset
	 * @param limit
	 * @param filter1
	 * @param filter2
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getAggregatedItems/{group}")
	public Response getAggregatedItems(@PathParam("group") String group,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			if (groupTriples.containsKey(group)) {
				Gson gson = new Gson();
				KeyValuePair pair1 = null;
				KeyValuePair pair2 = null;
				try {
					pair1 = gson.fromJson(filter1, KeyValuePair.class);
				} catch (Exception e) {
				}
				try {
					pair2 = gson.fromJson(filter2, KeyValuePair.class);
				} catch (Exception e) {
				}
				boolean existed1 = pair1 != null && pair1.getGroupBy() != null
						&& groupTriples.containsKey(pair1.getGroupBy());
				boolean existed2 = pair2 != null && pair2.getGroupBy() != null
						&& groupTriples.containsKey(pair2.getGroupBy());
				String query = createGroupQuery(group, offset, limit,
						existed1 ? pair1.getGroupBy() : null, pair1.getValue(),
						existed2 ? pair2.getGroupBy() : null, pair2.getValue(), key, city);
				try {
					String ret = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
					return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_PARAMS + group);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The group is invalid '" + group + "'. The group is one of locality, category, country, publishe, placeName, and artist")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		} else {
			if (key != null && !key.equals(""))  CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}

	/**
	 * This API is used to get aggregated information about PoIs.
	 *
	 * @param group
	 * @param offset
	 * @param limit
	 * @param filter1
	 * @param filter2
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getAggregatedPoIs")
	public Response getAggregatedPoIs(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			int tmpOffset = offset < 0 ? 0 : offset;
			String query ="SELECT DISTINCT  (?catRead AS ?category) (count(*) AS ?count)\nWHERE\n  {  { graph "
			+ SparqlChooser.getPoIGraph(key, city) + " { ?venue rdf:type dul:Place .} }\n    ?venue <http://data.linkedevents.org/def/location#businessType> ?cat .\n    ?cat skos:prefLabel ?catRead\n }\nGROUP BY ?catRead\nORDER BY DESC(?count)\nOFFSET  "
			+ tmpOffset +( limit < 0 ? "" : "\nLIMIT  " + limit);
			try {
				String ret = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals(""))  CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_POIS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to get information (ID, title, description) about events.
	 *
	 * @param access_token
	 * @param offset
	 * @param limit
	 * @param preference
	 * @param filter1
	 * @param filter2
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getItems")
	public Response getItems(@HeaderParam("access_token") String access_token,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit, @DefaultValue("") @QueryParam("preference") String preference,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		
		long starttime = System.currentTimeMillis();

		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String user_id =  userAccessToken.getUid();
			String key = userAccessToken.getAppkey();

			Gson gson = new Gson();
			KeyValuePair pair1 = null;
			KeyValuePair pair2 = null;
			try {
				pair1 = gson.fromJson(filter1, KeyValuePair.class);
			} catch (Exception e) {}
			try {
				pair2 = gson.fromJson(filter2, KeyValuePair.class);
			} catch (Exception e) {}

			String query = createSelectSparqlQuery(offset, limit,
					(pair1 == null ? null : pair1.getGroupBy()),
					(pair1 == null ? null : pair1.getValue()),
					(pair2 == null ? null : pair2.getGroupBy()),
					(pair2 == null ? null : pair2.getValue()), key, city);

			try {
				String result = executeQuery(user_id, query, preference, Constants.JSON, SparqlEndPointUtils.HTTP_GET, 1, key);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.FAILED);
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (access_token != null && !access_token.equals("")) CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}
	
	/**
	 * This API is used to get information (ID, title - name) about PoIs.
	 *
	 * @param access_token
	 * @param offset
	 * @param limit
	 * @param preference
	 * @param category
	 * @return
	 */
	@GET
	@Path("/getPoIs")
	public Response getPoIs(@HeaderParam("access_token") String access_token,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit, @DefaultValue("") @QueryParam("preference") String preference,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("0") @QueryParam("minRating") int minRating,
			@DefaultValue("5") @QueryParam("maxRating") int maxRating,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		
		long starttime = System.currentTimeMillis();

		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String user_id =  userAccessToken.getUid();
			String key = userAccessToken.getAppkey();

			String query = createSelectSparqlQueryForPoI(offset, limit, category, minRating, maxRating, key, city);

			try {
				String result = executeQuery(user_id, query, preference, Constants.JSON, SparqlEndPointUtils.HTTP_GET, 1, key);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (access_token != null && !access_token.equals("")) CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}
	
	/**
	 * This API is used to get information (ID, title, description) about events without using 3cixty token.
	 *
	 * @param offset
	 * @param limit
	 * @param filter1
	 * @param filter2
	 * @param city
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getItemsWithoutAccessToken")
	public Response getItemsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key) {
		
		long starttime = System.currentTimeMillis();

		if (OAuthWrappers.validateAppKey(key)) {

				Gson gson = new Gson();
				KeyValuePair pair1 = null;
				KeyValuePair pair2 = null;
				try {
					pair1 = gson.fromJson(filter1, KeyValuePair.class);
				} catch (Exception e) {}
				try {
					pair2 = gson.fromJson(filter2, KeyValuePair.class);
				} catch (Exception e) {}

				String query = createSelectSparqlQuery(offset, limit,
						(pair1 == null ? null : pair1.getGroupBy()),
						(pair1 == null ? null : pair1.getValue()),
						(pair2 == null ? null : pair2.getGroupBy()),
						(pair2 == null ? null : pair2.getValue()), key, city);

				try {
					String result = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
					return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
		} else {
			if (key != null && !key.equals("")) CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}

	/**
	 * This API is used to get information (ID, title - name) about PoIs without using 3cixty token.
	 * @param offset
	 * @param limit
	 * @param category
	 * @param minRating
	 * @param maxRating
	 * @param city
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getPoIsWithoutAccessToken")
	public Response getPoIsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("0") @QueryParam("minRating") int minRating,
			@DefaultValue("5") @QueryParam("maxRating") int maxRating,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key) {
		
		long starttime = System.currentTimeMillis();

		if (OAuthWrappers.validateAppKey(key)) {
			String query = createSelectSparqlQueryForPoI(offset, limit, category, minRating, maxRating, key, city);

			try {
				String result = executeQuery(query, EventMediaFormat.JSON, SparqlEndPointUtils.HTTP_GET, key, false);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals("")) CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to get information in detail about events.
	 *
	 * @param offset
	 * @param limit
	 * @param filter1
	 * @param filter2
	 * @param city
	 * @param key
	 * @param languages
	 * @return
	 */
	@GET
	@Path("/getEventsInDetailsWithoutAccessToken")
	public Response getEventsInDetailsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key,
			@HeaderParam("Accept-Language") String languages) {
		
		long starttime = System.currentTimeMillis();

		if (OAuthWrappers.validateAppKey(key)) {

				Gson gson = new Gson();
				KeyValuePair pair1 = null;
				KeyValuePair pair2 = null;
				try {
					pair1 = gson.fromJson(filter1, KeyValuePair.class);
				} catch (Exception e) {}
				try {
					pair2 = gson.fromJson(filter2, KeyValuePair.class);
				} catch (Exception e) {}

				String query = createSelectSparqlQuery(offset, limit,
						(pair1 == null ? null : pair1.getGroupBy()),
						(pair1 == null ? null : pair1.getValue()),
						(pair2 == null ? null : pair2.getGroupBy()),
						(pair2 == null ? null : pair2.getValue()), key, city);

				try {
					List <String> eventIds = getElementIDs(query, SparqlEndPointUtils.HTTP_GET,
							SparqlChooser.getEndPointUrl(key));
				
					String [] tmpLanguages = LanguageUtils.getLanguages(languages);
					List<ElementDetails> eventsDetails = ElementDetailsUtils.createEventsDetails(SparqlChooser.getEndPointUrl(key),
							SparqlChooser.getEventGraph(key, city), eventIds, null, tmpLanguages);
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
					String content = JSONObject.wrap(eventsDetails).toString();
					return Response.ok(content, MediaType.APPLICATION_JSON_TYPE).build();
				} catch (IOException e) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.FAILED);
					LOGGER.error(e.getMessage());
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity(e.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
		} else {
			if (key != null && !key.equals("")) CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to get information in detail about PoIs.
	 *
	 * @param offset
	 * @param limit
	 * @param category
	 * @param minRating
	 * @param maxRating
	 * @param city
	 * @param key
	 * @param languages
	 * @return
	 */
	@GET
	@Path("/getPoIsInDetailsWithoutAccessToken")
	public Response getPoIsInDetailsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("0") @QueryParam("minRating") int minRating,
			@DefaultValue("5") @QueryParam("maxRating") int maxRating,
			@DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city,
			@HeaderParam("key") String key,
			@HeaderParam("Accept-Language") String languages) {
		
		long starttime = System.currentTimeMillis();

		if (OAuthWrappers.validateAppKey(key)) {
			String query = createSelectSparqlQueryForPoI(offset, limit, category, minRating, maxRating, key, city);

			try {
				List <String> poiIds = getElementIDs(query, SparqlEndPointUtils.HTTP_GET, SparqlChooser.getEndPointUrl(key));
				String[] tmpLanguages = LanguageUtils.getLanguages(languages);
				List <ElementDetails> poisInDetails = ElementDetailsUtils.createPoIsDetails(SparqlChooser.getEndPointUrl(key),
						SparqlChooser.getPoIGraph(key, city), poiIds, null, null, tmpLanguages);
				
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				String content = JSONObject.wrap(poisInDetails).toString();
				return Response.ok(content, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (IOException e) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.FAILED);
				LOGGER.error(e.getMessage());
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (key != null && !key.equals(""))  CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return createResponseForInvalidKey(key);
		}
	}
	
	/**
	 * This API is used to check whether or not a given query conforms to SPARQL 1.1.
	 *
	 * @param query
	 * @return
	 */
	@GET
	@Path("/validateQuery")
	public Response validateSPARLQuery(@DefaultValue("") @QueryParam("query") String query) {
		String fullQuery = getAllPrefixes() + " " + Configuration.PREFIXES + " " + query;
		try {
		    Query q = QueryFactory.create(fullQuery);
		    if (q != null) return Response.ok().entity("The given query conforms to SPARQL 1.1!!!").build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).entity("incomprehensive query").build();
	}
	
	/**
	 * This API is used to get social scores for either the user themselves or the user's friends.
	 *
	 * @param query
	 * @return
	 */
	@GET
	@Path("/getSocialScores")
	public Response getSocialScores(@HeaderParam("access_token") String access_token,
			@DefaultValue("3.0") @QueryParam("rating") double rating,
			@DefaultValue("true") @QueryParam("ratedByFriends") boolean ratedByFriends) {
		long starttime = System.currentTimeMillis();

		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String user_id =  userAccessToken.getUid();
			String key = userAccessToken.getAppkey();
			String endpointUrl = SparqlChooser.getEndPointUrl(key);
			try {
				UserProfile userProfile = ProfileManagerImpl.getInstance().getProfile(user_id, null);
				List <String> placeIds = new LinkedList <String>();
				List <Double> socialScores = new LinkedList <Double>();
				if (ratedByFriends) { // my friends / travel-mates rated places
					ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScoreForFriends(userProfile,
							(float) rating, placeIds, socialScores, endpointUrl);
				} else { // I rated places
					ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScore(userProfile,
							(float) rating, placeIds, socialScores, endpointUrl);
				}
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_SOCIAL_SCORE_SERVICE, CallLoggingConstants.SUCCESSFUL);
				JSONArray jsonArray = createJSONArray(placeIds, socialScores);
				return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON_TYPE).build();
			} catch (TooManyConnections e) {
				return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
				        .entity(e.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			if (access_token != null && !access_token.equals(""))
				CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}

	/**
	 * Execute a given query with KB.
	 * <br>
	 * If the given query is for events, the given query will not be augmented.
	 * <br>
	 * If the given query is for PoIs, and the given <code>filter</code> is null, the given query
	 * will not be augmented; otherwise, the given query will be augmented based on either friends / travel-mate
	 * rating or your own rating.
	 *
	 * @param uid
	 * @param query
	 * @param filter
	 * @param format
	 * @param httpMethod
	 * @param coef
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private String executeQuery(String uid, String query, String filter, String format,
			String httpMethod, double coef, String key) throws IOException {

		if (QueryAugmenterImpl.allPrefixes == null) QueryAugmenterImpl.allPrefixes = getAllPrefixes();
		if (QueryAugmentationUtils.allPrefixes == null) QueryAugmentationUtils.allPrefixes = getAllPrefixes();
		
		// XXX: is for events
		boolean isForEvents = (query.indexOf("lode:Event") > 0);
//		StringBuilder sb = new StringBuilder();
//		String formatType = Constants.JSON.equalsIgnoreCase(format) ? "application/sparql-results+json"
//				: (Constants.RDF.equals(format) ? "application/rdf+xml" : "application/sparql-results+json");
//		if (isForEvents) {
//			SparqlEndPointUtils.executeQueryViaSPARQL(query, formatType, httpMethod, sb);
//		} else {
//			QueryAugmenterFilter qaf = eu.threecixty.querymanager.Constants.FRIENDS.equalsIgnoreCase(filter)
//					? QueryAugmenterFilter.FriendsRating : eu.threecixty.querymanager.Constants.ENTERED_RATING.equalsIgnoreCase(filter)
//							? QueryAugmenterFilter.MyRating : null;
//			try {
//				String augmentedQuery = new QueryAugmenterImpl().createQueryAugmented(query, qaf, uid, coef);
//				if (DEBUG_MOD) LOGGER.info(augmentedQuery);
//				SparqlEndPointUtils.executeQueryViaSPARQL(augmentedQuery, formatType, httpMethod, sb);
//			} catch (InvalidSparqlQuery e) {
//				if (DEBUG_MOD) LOGGER.info(e.getMessage());
//				// try with original query
//				SparqlEndPointUtils.executeQueryViaSPARQL(query, formatType, httpMethod, sb);
//			}
//		}
//		return sb.toString();
		
		
		String formatType = Constants.JSON.equalsIgnoreCase(format) ? "application/sparql-results+json"
				: (Constants.RDF.equals(format) ? "application/rdf+xml" : "application/sparql-results+json");
		String endPointUrl = SparqlChooser.getEndPointUrl(key);
		if (isForEvents) {
			StringBuilder sb = new StringBuilder();
			SparqlEndPointUtils.executeQueryViaSPARQL(query, formatType, httpMethod, endPointUrl, sb);
			return sb.toString();
		} else {
			QueryAugmenterFilter qaf = eu.threecixty.querymanager.Constants.FRIENDS.equalsIgnoreCase(filter)
					? QueryAugmenterFilter.FriendsRating : eu.threecixty.querymanager.Constants.ENTERED_RATING.equalsIgnoreCase(filter)
							? QueryAugmenterFilter.MyRating : null;
			return QueryAugmentationUtils.augmentAndExecuteQuery(query, qaf, uid, coef, httpMethod, endPointUrl);
		}
	}

	/**
	 * Gets the list of element IDs (event or place ID) received by executing a given query.
	 *
	 * @param query
	 * @param httpMethod
	 * @param endPointUrl
	 * @return
	 * @throws IOException
	 */
	public static List <String> getElementIDs(String query, String httpMethod, String endPointUrl) throws IOException {

		String formatType = "application/sparql-results+json";

		StringBuilder sb = new StringBuilder();
		
		List <String> elementIds = new LinkedList <String>();

		// execute the given query and store results into StringBuilder
		SparqlEndPointUtils.executeQueryViaSPARQL(query, formatType, httpMethod, endPointUrl, sb);
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
	 * Creates SPARQL query for grouping category, publisher, etc.
	 * @param group
	 * @param offset
	 * @param limit
	 * @param groupname1
	 * @param groupvalue1
	 * @param groupname2
	 * @param groupvalue2
	 * @param key
	 * @param city
	 * @return
	 */
	private String createGroupQuery(String group, int offset, int limit,
			String groupname1, String groupvalue1, String groupname2, String groupvalue2, String key, String city) {
		StringBuffer buffer = new StringBuffer("select ?" + group + " (COUNT(*) as ?count) \n WHERE {\n { graph "
			+ SparqlChooser.getEventGraph(key, city) + " {?event a lode:Event . } }\n" + getTriples(group));
		if (groupname1 != null && groupname2 == null) {
			if (!group.equals(groupvalue1)) {
				buffer.append(getTriples(groupname1));
				buffer.append("FILTER(STR(?" + groupname1 + ") = \"" + groupvalue1 + "\") .\n");
			}
		} else if (groupname1 != null && groupname2 != null) {
			if (!group.equals(groupvalue1)) {
				buffer.append(getTriples(groupname1));
				buffer.append("FILTER(STR(?" + groupname1 + ") = \"" + groupvalue1 + "\") .\n");
			}
			if (!group.equals(groupvalue2)) {
				buffer.append(getTriples(groupname2));
				buffer.append("FILTER(STR(?" + groupname2 + ") = \"" + groupvalue2 + "\") .\n");
			}
		}
		buffer.append("} GROUP BY ?" + group + " ORDER BY DESC (?count) ");
		
		return createSelectSparqlQuery(buffer.toString(), offset, limit);
	}

	private String getTriples(String group) {
		if (groupTriples.containsKey(group)) return groupTriples.get(group);
		return "";
	}
	
	/**
	 * Create SPARQL query for getting information about events.
	 *
	 * @param offset
	 * @param limit
	 * @param groupname1
	 * @param groupvalue1
	 * @param groupname2
	 * @param groupvalue2
	 * @param key
	 * @param city
	 * @return
	 */
	private String createSelectSparqlQuery(int offset, int limit, String groupname1, String groupvalue1,
			String groupname2, String groupvalue2, String key, String city) {
		StringBuffer buffer = new StringBuffer("SELECT ?event ?title ?description \n	WHERE {\n { graph "
			+ SparqlChooser.getEventGraph(key, city) + "	{ ?event a lode:Event. } } \n	OPTIONAL{?event rdfs:label ?title.}\n	OPTIONAL{?event dc:description ?description.} \n");
		if (groupTriples.containsKey(groupname1)) {
			buffer.append(groupTriples.get(groupname1));
			buffer.append("FILTER(STR(?" + groupname1 + ") = \"" + groupvalue1 + "\") .\n");
		}
		if (groupTriples.containsKey(groupname2)) {
			buffer.append(groupTriples.get(groupname2));
			buffer.append("FILTER(STR(?" + groupname2 + ") = \"" + groupvalue2 + "\") .\n");
		}
		buffer.append("} \n");
		return createSelectSparqlQuery(buffer.toString(),
				offset, limit);
	}

	/**
	 * Create SPARQL query for getting information about PoIs.
	 * @param offset
	 * @param limit
	 * @param category
	 * @param minRating
	 * @param maxRating
	 * @param key
	 * @param city
	 * @return
	 */
	private String createSelectSparqlQueryForPoI(int offset, int limit,
			String category, int minRating, int maxRating, String key, String city) {
		StringBuffer buffer = new StringBuffer();
		if (category != null && !category.equals("")) {
			buffer.append("SELECT DISTINCT  ?venue ?title\nWHERE\n  { { graph " + SparqlChooser.getPoIGraph(key, city) + " {?venue a dul:Place.} } .\n    ?venue rdfs:label ?title .\n    ?venue schema:location ?location .\n    ?venue <http://data.linkedevents.org/def/location#businessType> ?cat .\n    ?cat skos:prefLabel ?catRead .\n   ?venue schema:aggregateRating ?rating .\n    ?rating schema:ratingValue ?ratingValue .\n    FILTER ( str(?catRead) = \""
		            + category + "\" )\n  FILTER ( xsd:decimal(?ratingValue) >= " 
					+ minRating + " )\n    FILTER ( xsd:decimal(?ratingValue) < " + maxRating + " )\n  }\n");
		} else {
			buffer.append("SELECT DISTINCT  ?venue ?title\nWHERE\n  { { graph " + SparqlChooser.getPoIGraph(key, city) + " {?venue a dul:Place.} } .\n    ?venue rdfs:label ?title .\n    ?venue schema:location ?location .\n  ?venue schema:aggregateRating ?rating .\n    ?rating schema:ratingValue ?ratingValue .\n  FILTER ( xsd:decimal(?ratingValue) >= " 
		                + minRating + " )\n    FILTER ( xsd:decimal(?ratingValue) < "  + maxRating + " )\n  }");
		}
		return createSelectSparqlQuery(buffer.toString(), offset, limit);
	}

	/**
	 * Adds <code>LIMIT</code> and <code>OFFSET</code> to the given query.
	 *
	 * @param initQuery
	 * @param offset
	 * @param limit
	 * @return
	 */
	private String createSelectSparqlQuery(String initQuery, int offset, int limit) {
		StringBuffer query =  new StringBuffer(initQuery);
		if (offset > 0) {
			query.append("OFFSET ").append(offset).append("\n");
		}
		if (limit > 0) {
			query.append("LIMIT ").append(limit).append("\n");
		}
		return query.toString();
	}

	/**
     * Get all prefixes predefined. Those prefixes are defined by Virtuoso KB.
     *
     * @return string
     */
    private String getAllPrefixes() {
		try {
			InputStream inStream = new FileInputStream(realPath + File.separatorChar 
					+ "WEB-INF" + File.separatorChar + "prefix.properties");
			StringBuilder sb = new StringBuilder();
			Properties props = new Properties();
			props.load(inStream);
			inStream.close();
			for (java.util.Map.Entry<Object, Object> entry: props.entrySet()) {
				sb.append("PREFIX " + entry.getKey() + ":\t");
				sb.append('<');
				sb.append(entry.getValue());
				sb.append(">\n");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    private List <String> createList(String idsStr) {
    	String [] arrIds = idsStr.split(",");
    	List <String> results = new LinkedList <String>();
    	for (String id: arrIds) {
    		results.add(id.trim());
    	}
    	return results;
    }
    
    private Response createResponseForAccessToken(String access_token) {
    	return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("The access token is invalid, access_token = " + access_token)
		        .type(MediaType.TEXT_PLAIN)
		        .build();
    }
    
    private Response createResponseForInvalidKey(String key) {
		return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("The key is invalid, key = " + key)
		        .type(MediaType.TEXT_PLAIN)
		        .build();
    }
    
	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}
	
	/**
	 * Executes query without using 3cixty access token.
	 * <br>
	 * Note that this method doesn't augment the given query.
	 * @param query
	 * @param format
	 * @return
	 */
	private String executeQuery(String query, EventMediaFormat format, String httpMethod, String key, boolean stressTestOn) throws IOException {
		if (query == null || format == null) return "";
		String ret = null;
		// get format to send to Virtuoso KB
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		long startTime = System.currentTimeMillis();
		
		long time = System.currentTimeMillis();
		if (DEBUG_MOD) LOGGER.info("Time to get data from map: " + (time - startTime));
		if (ret == null) {
			StringBuilder builder = new StringBuilder();
			// execute the query at Virtuoso KB, then store results into StringBuilder
			SparqlEndPointUtils.executeQueryViaSPARQL(query, formatType, httpMethod, SparqlChooser.getEndPointUrl(key), builder);
			ret = builder.toString();
			long endTime = System.currentTimeMillis();
			if (DEBUG_MOD) {
				//LOGGER.info("Query: " + query);
				LOGGER.info("Time to make the query: " + (endTime - startTime) + " ms");
			}
		}
		return ret;
	}
	
	/**
	 * Creates a JSONArray to represent a list of JSON objects made of a placeID and its corresponding social score.
	 *
	 * @param placeIds
	 * @param socialScores
	 * @return
	 */
	private JSONArray createJSONArray(List <String> placeIds, List <Double> socialScores) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < placeIds.size(); i++) {
			String placeId = placeIds.get(i);
			Double socialScore = socialScores.get(i);
			JSONObject json = new JSONObject();
			json.put("placeId", placeId);
			json.put("socialScore", socialScore);
			jsonArray.put(0, json);
		}
		return jsonArray;
	}

    public class KeyValuePair {

    	private String groupBy;
    	private String value;

    	public String getGroupBy() {
    		return groupBy;
    	}

    	public void setGroupBy(String groupBy) {
    		this.groupBy = groupBy;
    	}

    	public String getValue() {
    		return value;
    	}

    	public void setValue(String value) {
    		this.value = value;
    	}
    }

}

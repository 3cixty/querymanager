package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;
import eu.threecixty.querymanager.EventMediaFormat;
import eu.threecixty.querymanager.FromClauseUtils;
import eu.threecixty.querymanager.IQueryManager;
import eu.threecixty.querymanager.QueryManager;
import eu.threecixty.querymanager.QueryManagerDecision;
import eu.threecixty.querymanager.ThreeCixtyQuery;

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
		groupTriples.put("placeName", "?event dc:title ?title;	lode:atPlace ?place. \n	?place rdfs:label ?placeName.\n");
		groupTriples.put("artist", "?event lode:involvedAgent ?involvedAgent .\n ?involvedAgent rdfs:label ?artist .\n");
	}

	public static String realPath;
	private static String allPrefixes;
	
	/**
	 * This method firstly augments a given query, then sends to Eurecom to execute and receives data back.
	 *
	 * @param key
	 * 				Application key
	 * @param access_token
	 * 				Google access token
	 * @param format
	 * 				JSON or RDF format
	 * @param query
	 * 				Sparql query
	 * @param filter
	 * 				Filter to augment the query
	 * @return Data received from Eurecom when executing a query augmented. 
	 */
	@GET
	@Path("/augmentAndExecute")
	public Response executeQuery(@HeaderParam("access_token") String access_token,
			@QueryParam("format") String format, @QueryParam("query") String query,
			@QueryParam("filter") String filter, @DefaultValue("off") @QueryParam("debug") String debug) {
		logInfo("Start augmentAndExecute method ----------------------");
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			logInfo("Found a valid access token");
			String user_id =  userAccessToken.getUid();
			if ("on".equals(debug)) {
				user_id = "107217557295681360318";
			}
			String key = userAccessToken.getAppkey();

			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
			if (eventMediaFormat == null || query == null) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNSUPPORTED_FORMAT);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("The format is not supported or query is null")
						.type(MediaType.TEXT_PLAIN)
						.build());
			} else {
				logInfo("Before reading user profile");
				IProfiler profiler = new Profiler(user_id);
				QueryManager qm = new QueryManager(user_id);

				try {
					logInfo("Before augmenting and executing a query");
					String result = executeQuery(profiler, qm, query, filter, eventMediaFormat);

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

					return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
							MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
				} catch (ThreeCixtyPermissionException tcpe) {
					CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.ILLEGAL_QUERY + query);
					return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity(tcpe.getMessage())
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	/**
	 * Make query without information about 3cixty access token
	 * @param key
	 * @param format
	 * @param query
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/executeQuery")
	public Response executeQueryNoAccessToken(@HeaderParam("key") String key, 
			@QueryParam("format") String format, @QueryParam("query") String query) {
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

				Query jenaQuery = createJenaQuery(query);
				if (jenaQuery == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNPARSED_QUERY);
					return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity(CallLoggingConstants.UNPARSED_QUERY)
							.type(MediaType.TEXT_PLAIN)
							.build();
				}
				if (FromClauseUtils.containFromProfile(jenaQuery)) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.ILLEGAL_QUERY + " " + query);
					return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity(CallLoggingConstants.ILLEGAL_QUERY)
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
				FromClauseUtils.addFromGraphs(jenaQuery);
				
				String queryToBeExecuted = QueryManager.removePrefixes(jenaQuery.toString());
				
				String result = QueryManager.executeQuery(queryToBeExecuted, eventMediaFormat);

				// log calls
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);

				return Response.ok(result, EventMediaFormat.JSON.equals(eventMediaFormat) ?
						MediaType.APPLICATION_JSON_TYPE : MediaType.TEXT_PLAIN_TYPE).build();
			}
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build();
		}
	}
	
	/**
	 * Counts the number of items in the KB at EventMedia.
	 * @param key
	 * @return
	 */
	@GET
	@Path("/countItems")
	public Response countItems(@HeaderParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			String query = "SELECT (COUNT(*) AS ?count) \n WHERE { \n ?event a lode:Event. \n } ";
			
			String ret = QueryManager.executeQuery(query, EventMediaFormat.JSON);
			
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	/**
	 * Counts the number of PoIs in the KB.
	 * @param key
	 * @return
	 */
	@GET
	@Path("/countPoIs")
	public Response countPoIs(@HeaderParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			String query = "SELECT DISTINCT  (count(*) AS ?count)\nWHERE\n  { ?venue rdf:type dul:Place }";
			
			String ret = QueryManager.executeQuery(query, EventMediaFormat.JSON);
			
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	/**
	 * Gets aggregated information of a given group in the KB at EventMedia.
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
						existed2 ? pair2.getGroupBy() : null, pair2.getValue());
				String ret = QueryManager.executeQuery(query, EventMediaFormat.JSON);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
			}
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_PARAMS + group);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The group is invalid '" + group + "'. The group is one of locality, category, country, publishe, placeName, and artist")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	/**
	 * Gets aggregated information of PoIs categories in the KB at EventMedia.
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
			@HeaderParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (OAuthWrappers.validateAppKey(key)) {
			int tmpOffset = offset < 0 ? 0 : offset;
			String query ="SELECT DISTINCT  (?catRead AS ?category) (count(*) AS ?count)\nWHERE\n  { ?venue rdf:type dul:Place .\n    ?venue <http://data.linkedevents.org/def/location#businessType> ?cat .\n    ?cat skos:prefLabel ?catRead\n }\nGROUP BY ?catRead\nORDER BY DESC(?count)\nOFFSET  "
			+ tmpOffset +( limit < 0 ? "" : "\nLIMIT  " + limit);
			String ret = QueryManager.executeQuery(query, EventMediaFormat.JSON);
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_POIS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Gets items in details.
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
			@DefaultValue("{}") @QueryParam("filter2") String filter2) {
		
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

			IProfiler profiler = new Profiler(user_id);
			QueryManager qm = new QueryManager(user_id);

			String query = createSelectSparqlQuery(offset, limit,
					(pair1 == null ? null : pair1.getGroupBy()),
					(pair1 == null ? null : pair1.getValue()),
					(pair2 == null ? null : pair2.getGroupBy()),
					(pair2 == null ? null : pair2.getValue()));

			try {
				String result = executeQuery(profiler, qm, query, preference, EventMediaFormat.JSON);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (ThreeCixtyPermissionException tcpe) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.ILLEGAL_QUERY);
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity(tcpe.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Gets PoIs in details.
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
			@DefaultValue("5") @QueryParam("maxRating") int maxRating) {
		
		long starttime = System.currentTimeMillis();

		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			String user_id =  userAccessToken.getUid();
			String key = userAccessToken.getAppkey();

			IProfiler profiler = new Profiler(user_id);
			QueryManager qm = new QueryManager(user_id);

			String query = createSelectSparqlQueryForPoI(offset, limit, category, minRating, maxRating);

			try {
				String result = executeQuery(profiler, qm, query, preference, EventMediaFormat.JSON);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (ThreeCixtyPermissionException tcpe) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.ILLEGAL_QUERY );
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity(tcpe.getMessage())
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			}
		} else {
			CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The access token is invalid '" + access_token + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	

	@GET
	@Path("/getItemsWithoutAccessToken")
	public Response getItemsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2, @HeaderParam("key") String key) {
		
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
						(pair2 == null ? null : pair2.getValue()));

				String result = QueryManager.executeQuery(query, EventMediaFormat.JSON);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	@GET
	@Path("/getPoIsWithoutAccessToken")
	public Response getPoIsWithoutUserInfo(
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("0") @QueryParam("minRating") int minRating,
			@DefaultValue("5") @QueryParam("maxRating") int maxRating,
			@HeaderParam("key") String key) {
		
		long starttime = System.currentTimeMillis();

		if (OAuthWrappers.validateAppKey(key)) {
			String query = createSelectSparqlQueryForPoI(offset, limit, category, minRating, maxRating);

			String result = QueryManager.executeQuery(query, EventMediaFormat.JSON);
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
			return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_POIS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	private String executeQuery(IProfiler profiler, IQueryManager qm,
			String query, String filter, EventMediaFormat eventMediaFormat) throws ThreeCixtyPermissionException {

		Query jenaQuery = createJenaQuery(query);
		
		if (FromClauseUtils.containFromProfile(jenaQuery)) throw new ThreeCixtyPermissionException(
				"Illegal to make a query to get private information");
		
		FromClauseUtils.addFromGraphs(jenaQuery);
		
		// XXX: is for events
		boolean isForEvents = (query.indexOf("lode:Event") > 0);
		qm.setForEvents(isForEvents);

		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new ThreeCixtyQuery(jenaQuery);


		qm.setQuery(placeQuery);
		
		String result = QueryManagerDecision.run(profiler, qm, filter, eventMediaFormat);
		return  result;
	}

	private String createGroupQuery(String group, int offset, int limit,
			String groupname1, String groupvalue1, String groupname2, String groupvalue2) {
		StringBuffer buffer = new StringBuffer("select ?" + group + " (COUNT(*) as ?count) \n WHERE {\n ?event a lode:Event .\n" + getTriples(group));
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
	
	private String createSelectSparqlQuery(int offset, int limit, String groupname1, String groupvalue1, String groupname2, String groupvalue2) {
		StringBuffer buffer = new StringBuffer("SELECT ?event ?title ?description \n	WHERE {\n	?event a lode:Event. \n	OPTIONAL{?event dc:title ?title.}\n	OPTIONAL{?event dc:description ?description.} \n");
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

	private String createSelectSparqlQueryForPoI(int offset, int limit,
			String category, int minRating, int maxRating) {
		StringBuffer buffer = new StringBuffer();
		if (category != null && !category.equals("")) {
			buffer.append("PREFIX schema: <http://schema.org/>\n SELECT DISTINCT  ?venue ?title\nWHERE\n  { ?venue rdf:type dul:Place .\n    ?venue schema:name ?title .\n    ?venue schema:location ?location .\n    ?venue rdf:type dul:Place .\n    ?venue <http://data.linkedevents.org/def/location#businessType> ?cat .\n    ?cat skos:prefLabel ?catRead .\n   ?venue schema:aggregateRating ?rating .\n    ?rating schema:ratingValue ?ratingValue .\n    FILTER ( str(?catRead) = \""
		            + category + "\" )\n  FILTER ( xsd:decimal(?ratingValue) >= " 
					+ minRating + " )\n    FILTER ( xsd:decimal(?ratingValue) < " + maxRating + " )\n  }\n");
		} else {
			buffer.append("PREFIX schema: <http://schema.org/>\n SELECT DISTINCT  ?venue ?title\nWHERE\n  { ?venue rdf:type dul:Place .\n    ?venue schema:name ?title .\n    ?venue schema:location ?location .\n  ?venue schema:aggregateRating ?rating .\n    ?rating schema:ratingValue ?ratingValue .\n  FILTER ( xsd:decimal(?ratingValue) >= " 
		                + minRating + " )\n    FILTER ( xsd:decimal(?ratingValue) < "  + maxRating + " )\n  }");
		}
		return createSelectSparqlQuery(buffer.toString(), offset, limit);
	}

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

	private Query createJenaQuery(String queryStr) {
		if (queryStr == null) return null;
		if (allPrefixes == null) {
			allPrefixes = getAllPrefixes() + " ";
		}

		Query jenaQuery = QueryFactory.create(allPrefixes + queryStr);
		return jenaQuery;
	}

	/**
     * To validate the sparql query, we need prefixes. These prefixes are same as those used by EventMedia.
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
    
	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
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

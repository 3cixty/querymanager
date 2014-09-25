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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;

import eu.threecixty.keys.KeyManager;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;
import eu.threecixty.querymanager.EventMediaFormat;
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
	
	@Context 
	private HttpServletRequest httpRequest;
	
	/**
	 * This method firstly augments a given query, then sends to Eurecom to execute and receives data back.
	 *
	 * @param key
	 * 				Application key
	 * @param accessToken
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
	public Response executeQuery(@QueryParam("key") String key, @QueryParam("accessToken") String accessToken,
			@QueryParam("format") String format, @QueryParam("query") String query,
			@QueryParam("filter") String filter) {
		long starttime = System.currentTimeMillis();
		if (KeyManager.getInstance().checkAppKey(key)) {
			boolean isAccessTokenFalse = "false".equals(accessToken);
			String user_id =  null;
			if (!isAccessTokenFalse) {
				HttpSession session = httpRequest.getSession();
				if (session.getAttribute("uid") == null) {
				    user_id = GoogleAccountUtils.getUID(accessToken); // which corresponds with Google user_id (from Google account)
				    session.setMaxInactiveInterval(GoogleAccountUtils.getValidationTime(accessToken));
				    session.setAttribute("uid", user_id);
				} else {
					user_id = (String) session.getAttribute("uid"); 
				}
			}
			if ((user_id == null || user_id.equals("")) && (!isAccessTokenFalse)) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNAUTHORIZED);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Access token is incorrect or expired")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			} else {
				EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
				if (eventMediaFormat == null || query == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.UNSUPPORTED_FORMAT);
					throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity("The format is not supported or query is null")
					        .type(MediaType.TEXT_PLAIN)
					        .build());
				} else {
				    IProfiler profiler = isAccessTokenFalse ? null : new Profiler(user_id);
				    QueryManager qm = isAccessTokenFalse ? new QueryManager("false") : new QueryManager(user_id);
				    				    
					String result = executeQuery(profiler, qm, query, filter, eventMediaFormat, true);
					
				    // log calls
				    if (isAccessTokenFalse) {
				    	CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_NO_FILTER_SERVICE, CallLoggingConstants.SUCCESSFUL);
				    } else {
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
				    }

					
					return Response.status(HttpURLConnection.HTTP_OK)
			        .entity(result)
			        .type(MediaType.APPLICATION_JSON)
			        .build();
				}
			}
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_SPARQL_SERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * Counts the number of items in the KB at EventMedia.
	 * @param key
	 * @return
	 */
	@GET
	@Path("/countItems")
	@Produces("application/json")
	public String countItems(@QueryParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (KeyManager.getInstance().checkAppKey(key)) {
			String query = "SELECT (COUNT(*) AS ?count) \n WHERE { \n ?event a lode:Event. \n } ";
			QueryManager qm = new QueryManager("false");
			String ret = executeQuery(null, qm, query, null, EventMediaFormat.JSON, false);
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_COUNT_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
			return ret;
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
	@Produces("application/json")
	public String getAggregatedItems(@PathParam("group") String group,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2,
			@QueryParam("key") String key) {
		long starttime = System.currentTimeMillis();
		if (KeyManager.getInstance().checkAppKey(key)) {
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
				QueryManager qm = new QueryManager("false");
				String ret = executeQuery(null, qm, query, null, EventMediaFormat.JSON, false);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_AGGREGATE_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return ret;
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
	 * Gets items in details.
	 *
	 * @param accessToken
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
	@Produces("application/json")
	public String getItems(@DefaultValue("false") @QueryParam("accessToken") String accessToken,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit, @DefaultValue("") @QueryParam("preference") String preference,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2, @QueryParam("key") String key) {
		
		long starttime = System.currentTimeMillis();

		if (KeyManager.getInstance().checkAppKey(key)) {
			IProfiler profiler = null;
			boolean isAccessTokenFalse = "false".equals(accessToken);
			String user_id =  null;
			if (!isAccessTokenFalse) {
				HttpSession session = httpRequest.getSession();
				if (session.getAttribute("uid") == null) {
				    user_id = GoogleAccountUtils.getUID(accessToken); // which corresponds with Google user_id (from Google account)
				    session.setMaxInactiveInterval(GoogleAccountUtils.getValidationTime(accessToken));
				    session.setAttribute("uid", user_id);
				} else {
					user_id = (String) session.getAttribute("uid"); 
				}
			}

			if ((user_id == null || user_id.equals("")) && (!isAccessTokenFalse)) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.UNAUTHORIZED);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("The access token is invalid: '" + accessToken + "'")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			} else {
				Gson gson = new Gson();
				KeyValuePair pair1 = null;
				KeyValuePair pair2 = null;
				try {
					pair1 = gson.fromJson(filter1, KeyValuePair.class);
				} catch (Exception e) {}
				try {
					pair2 = gson.fromJson(filter2, KeyValuePair.class);
				} catch (Exception e) {}

				profiler = isAccessTokenFalse ? null : new Profiler(user_id);
				QueryManager qm = isAccessTokenFalse ? new QueryManager("false") : new QueryManager(user_id);

				String query = createSelectSparqlQuery(offset, limit,
						(pair1 == null ? null : pair1.getGroupBy()),
						(pair1 == null ? null : pair1.getValue()),
						(pair2 == null ? null : pair2.getGroupBy()),
						(pair2 == null ? null : pair2.getValue()));

				String result = executeQuery(profiler, qm, query, preference, EventMediaFormat.JSON, false);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.SUCCESSFUL);
				return result;
			}
		} else {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.QA_GET_ITEMS_RESTSERVICE, CallLoggingConstants.INVALID_APP_KEY + key);
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

	private String executeQuery(IProfiler profiler, IQueryManager qm,
			String query, String filter, EventMediaFormat eventMediaFormat, boolean augmentedQueryIncluded) {

		if (allPrefixes == null) {
			allPrefixes = getAllPrefixes() + " ";
		}

		Query jenaQuery = qm.createJenaQuery(allPrefixes + query);
		
		// XXX: is for events
		boolean isForEvents = (query.indexOf("lode:Event") > 0);
		qm.setForEvents(isForEvents);

		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new ThreeCixtyQuery(jenaQuery);


		qm.setQuery(placeQuery);
		
		String result = QueryManagerDecision.run(profiler, qm, filter, eventMediaFormat, augmentedQueryIncluded);
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

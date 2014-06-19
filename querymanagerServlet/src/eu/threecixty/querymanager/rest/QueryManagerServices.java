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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;
import eu.threecixty.querymanager.EventMediaFormat;
import eu.threecixty.querymanager.IQueryManager;
import eu.threecixty.querymanager.QueryManager;
import eu.threecixty.querymanager.QueryManagerDecision;
import eu.threecixty.querymanager.ThreeCixtyQuery;

@Path("/queryManager")
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
	
	@GET
	@Path("/countItems")
	@Produces("text/plain")
	public String countItems() {
		String query = "SELECT (COUNT(*) AS ?count) \n WHERE { \n ?event a lode:Event. \n } ";
		QueryManager qm = new QueryManager("false");
		return executeQuery(null, qm, query, null);
	}

	@GET
	@Path("/getAggregatedItems/{group}")
	@Produces("text/plain")
	public String getAggregatedItems(@PathParam("group") String group) {
		return getAggregatedItems(group, 0, 20, null, null, null, null);
	}

	@GET
	@Path("/getAggregatedItems/{group}/{offset}/{limit}")
	@Produces("text/plain")
	public String getAggregatedItems(@PathParam("group") String group, @PathParam("offset") int offset,
			@PathParam("limit") int limit) {
		return getAggregatedItems(group, offset, limit, null, null, null, null);
	}

	@GET
	@Path("/getAggregatedItems/{group}/{offset}/{limit}/{groupname1}/{groupvalue1}")
	@Produces("text/plain")
	public String getAggregatedItems(@PathParam("group") String group, @PathParam("offset") int offset,
			@PathParam("limit") int limit, @PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1) {
		return getAggregatedItems(group, offset, limit, groupname1, groupvalue1, null, null);
	}

	@GET
	@Path("/getAggregatedItems/{group}/{offset}/{limit}/{groupname1}/{groupvalue1}/{groupname2}/{groupvalue2}")
	@Produces("text/plain")
	public String getAggregatedItems(@PathParam("group") String group, @PathParam("offset") int offset,
			@PathParam("limit") int limit, @PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1,
			@PathParam("groupname2") String groupname2, @PathParam("groupvalue2") String groupvalue2) {
		if (groupTriples.containsKey(group)) {
			boolean existed1 = groupTriples.containsKey(groupname1);
			boolean existed2 = groupTriples.containsKey(groupname2);
			String query = createGroupQuery(group, offset, limit, existed1 ? groupname1 : null, groupvalue1,
					existed2 ? groupname2 : null, groupvalue2);
			QueryManager qm = new QueryManager("false");
			return executeQuery(null, qm, query, null);
		}
		throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
	
	@GET
	@Path("/getItems")
	@Produces("text/plain")
	public String getItems() {
		return getItems("false");
	}

	@GET
	@Path("/getItems/{accessToken}")
	@Produces("text/plain")
	public String getItems(@PathParam("accessToken") String accessToken) {
		return getItems(accessToken, null);
	}
	
	@GET
	@Path("/getItems/{accessToken}/{filter}")
	@Produces("text/plain")
	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("filter") String filter) {
		return getItems(accessToken, 0, 20, filter);
	}
	
	@GET
	@Path("/getItems/{accessToken}/{offset}/{limit}/{filter}")
	@Produces("text/plain")
	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("offset") int offset,
			@PathParam("limit") int limit, @PathParam("filter") String filter) {
		return getItems(accessToken, offset, limit, filter, null, null, null, null);
	}

	@GET
	@Path("/getItems/{accessToken}/{offset}/{limit}/{filter}/{groupname1}/{groupvalue1}")
	@Produces("text/plain")
	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("offset") int offset,
			@PathParam("limit") int limit, @PathParam("filter") String filter,
			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1) {
		return getItems(accessToken, offset, limit, filter, groupname1, groupvalue1, null, null);
	}

//	@GET
//	@Path("/getItems/{accessToken}/{filter}/{groupname1}/{groupvalue1}/{groupname2}/{groupvalue2}")
//	@Produces("text/plain")
//	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("filter") String filter,
//			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1,
//			@PathParam("groupname2") String groupname2, @PathParam("groupvalue2") String groupvalue2) {
//		return getItems(accessToken, 0, 20, filter, groupname1, groupvalue1, groupname2, groupvalue2);
//	}
//
//	@GET
//	@Path("/getItems/{accessToken}/{filter}/{groupname1}/{groupvalue1}")
//	@Produces("text/plain")
//	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("filter") String filter,
//			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1) {
//		return getItems(accessToken, filter, groupname1, groupvalue1, null, null);
//	}
//
//	@GET
//	@Path("/getItems/{accessToken}/{groupname1}/{groupvalue1}/{groupname2}/{groupvalue2}")
//	@Produces("text/plain")
//	public String getItems(@PathParam("accessToken") String accessToken,
//			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1,
//			@PathParam("groupname2") String groupname2, @PathParam("groupvalue2") String groupvalue2) {
//		return getItems(accessToken, 0, 20, null, groupname1, groupvalue1, groupname2, groupvalue2);
//	}
//
//	@GET
//	@Path("/getItems/{accessToken}/{groupname1}/{groupvalue1}")
//	@Produces("text/plain")
//	public String getItems(@PathParam("accessToken") String accessToken,
//			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1) {
//		return getItems(accessToken, groupname1, groupvalue1, null, null);
//	}

	@GET
	@Path("/getItems/{accessToken}/{offset}/{limit}/{filter}/{groupname1}/{groupvalue1}/{groupname2}/{groupvalue2}")
	@Produces("text/plain")
	public String getItems(@PathParam("accessToken") String accessToken, @PathParam("offset") int offset,
			@PathParam("limit") int limit, @PathParam("filter") String filter,
			@PathParam("groupname1") String groupname1, @PathParam("groupvalue1") String groupvalue1,
			@PathParam("groupname2") String groupname2, @PathParam("groupvalue2") String groupvalue2) {

	    IProfiler profiler = null;
		boolean isAccessTokenFalse = "false".equals(accessToken);
		String user_id =  null;
		if (!isAccessTokenFalse) {
			user_id = GoogleAccountUtils.updateInfo(accessToken); // which corresponds with Google user_id (from Google account)
		}
		
		if ((user_id == null || user_id.equals("")) && (!isAccessTokenFalse)) {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		} else {
			profiler = isAccessTokenFalse ? null : new Profiler(user_id);
			QueryManager qm = isAccessTokenFalse ? new QueryManager("false") : new QueryManager(user_id);

			String query = createSelectSparqlQuery(offset, limit, groupname1, groupvalue1, groupname2, groupvalue2);

			String result = executeQuery(profiler, qm, query, filter);
			return result;
		}
	}

	private String executeQuery(IProfiler profiler, IQueryManager qm,
			String query, String filter) {

		if (allPrefixes == null) {
			allPrefixes = getAllPrefixes() + " ";
		}

		Query jenaQuery = qm.createJenaQuery(allPrefixes + query);

		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new ThreeCixtyQuery(jenaQuery);

		qm.setQuery(placeQuery);
		
		String result = QueryManagerDecision.run(profiler, qm, filter, EventMediaFormat.JSON, false);
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
}

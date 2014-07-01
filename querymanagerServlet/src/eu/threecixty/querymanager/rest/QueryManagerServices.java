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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;

import eu.threecixty.keys.KeyManager;
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
	public String countItems(@QueryParam("key") String key) {
		if (KeyManager.getInstance().checkAppKey(key)) {
			String query = "SELECT (COUNT(*) AS ?count) \n WHERE { \n ?event a lode:Event. \n } ";
			QueryManager qm = new QueryManager("false");
			return executeQuery(null, qm, query, null);
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}

	@GET
	@Path("/getAggregatedItems/{group}")
	@Produces("text/plain")
	public String getAggregatedItems(@PathParam("group") String group, @DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit, @DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2, @QueryParam("key") String key) {
		if (KeyManager.getInstance().checkAppKey(key)) {
			if (groupTriples.containsKey(group)) {
				Gson gson = new Gson();
				KeyValuePair pair1 = null;
				KeyValuePair pair2 = null;
				try {
					pair1 = gson.fromJson(filter1, KeyValuePair.class);
				} catch (Exception e) {}
				try {
					pair2 = gson.fromJson(filter2, KeyValuePair.class);
				} catch (Exception e) {}
				boolean existed1 = pair1 != null && pair1.getGroupBy() != null && groupTriples.containsKey(pair1.getGroupBy());
				boolean existed2 = pair2 != null && pair2.getGroupBy() != null && groupTriples.containsKey(pair2.getGroupBy());
				String query = createGroupQuery(group, offset, limit, existed1 ? pair1.getGroupBy() : null, pair1.getValue(),
						existed2 ? pair2.getGroupBy() : null, pair2.getValue());
				QueryManager qm = new QueryManager("false");
				return executeQuery(null, qm, query, null);
			}
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
	
	@GET
	@Path("/getItems")
	@Produces("text/plain")
	public String getItems(@DefaultValue("false") @QueryParam("accessToken") String accessToken,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("20") @QueryParam("limit") int limit, @DefaultValue("") @QueryParam("preference") String preference,
			@DefaultValue("{}") @QueryParam("filter1") String filter1,
			@DefaultValue("{}") @QueryParam("filter2") String filter2, @QueryParam("key") String key) {

		if (KeyManager.getInstance().checkAppKey(key)) {
			IProfiler profiler = null;
			boolean isAccessTokenFalse = "false".equals(accessToken);
			String user_id =  null;
			if (!isAccessTokenFalse) {
				user_id = GoogleAccountUtils.getUID(accessToken); // which corresponds with Google user_id (from Google account)
			}

			if ((user_id == null || user_id.equals("")) && (!isAccessTokenFalse)) {
				throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
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

				String result = executeQuery(profiler, qm, query, preference);
				return result;
			}
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
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

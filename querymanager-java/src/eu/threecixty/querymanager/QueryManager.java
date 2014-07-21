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
import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Place;
import eu.threecixty.profile.oldmodels.Preference;

 public class QueryManager implements IQueryManager {
	 
	 //private static final String EVENTMEDIA_URL_PREFIX = "http://eventmedia.eurecom.fr/sparql?default-graph-uri=&query=";
	 private static final String EVENTMEDIA_URL_PREFIX = "http://3cixty.eurecom.fr/sparql?default-graph-uri=&query=";

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
		augmentedQuery = new AugmentedQuery(query);
	}
	
	@Override
	public String askForExecutingAugmentedQueryAtEventMedia(AugmentedQuery augmentedQuery,
			EventMediaFormat format, boolean augmentedQueryIncluded) {
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		augmentedQueryStr = "";
		try {
			augmentedQueryStr = getAugmentedQueryWithoutPrefixes(augmentedQuery);
			
			String urlStr = EVENTMEDIA_URL_PREFIX + URLEncoder.encode(augmentedQueryStr, "UTF-8");
			urlStr += "&format=" + URLEncoder.encode(formatType, "UTF-8");

			URL url = new URL(urlStr);

			InputStream input = url.openStream();
			StringBuilder sb = new StringBuilder();
			byte [] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				sb.append(new String(b, 0, readBytes));
			}
			input.close();
			if (augmentedQueryIncluded) {
				if (EventMediaFormat.JSON == format) {
					int lastIndex = sb.lastIndexOf("}");
					if (lastIndex >= 0) {
						JSONArray jsonArr = new JSONArray();
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("AugmentedQuery", augmentedQueryStr);
						jsonArr.put(jsonObj);
						String augmentedQueryJson = ", " + "\"AugmentedQueries\": " + jsonArr.toString();
						sb.insert(lastIndex, augmentedQueryJson);
					}
				}
			}
			return sb.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "ERROR:" + e.getMessage();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "ERROR:" + e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR:" + e.getMessage();
		}
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
		Set <Place> places = preference.getHasPlaces();
		if (places != null) {
			for (Place place: places) {
				query.addExpressionsAndTriples(place, exprs, triples);
			}
		}

		Set <Event> events = preference.getHasEvents();
		if (events != null) {
			for (Event event: events) {
				query.addExpressionsAndTriples(event, exprs, triples);
			}
		}

		Set <Period> periods = preference.getHasPeriods();
		if (periods != null) {
			addPeriodsToTriplesAndExprsList(periods, query, triples, exprs);
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

	private void addPeriodsToTriplesAndExprsList(Set<Period> periods, ThreeCixtyQuery query, List <Triple> triples, List <Expr> exprs) {
		for (Period period: periods) {
			query.addExprsAndTriplesFromAttributeNameAndPropertyName(period, "startDate", "datetime",
					exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual);
			query.addExprsAndTriplesFromAttributeNameAndPropertyName(period, "endDate", "datetime",
					exprs, triples, ThreeCixtyExpression.LessThanOrEqual);
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
}
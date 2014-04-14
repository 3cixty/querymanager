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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.models.Event;
import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.Preference;

 class QueryManager implements IQueryManager {
	 
	 private static final String EVENTMEDIA_URL_PREFIX = "http://eventmedia.eurecom.fr/sparql?default-graph-uri=&query=";

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
	
	public QueryManager(String uid) {
		this(uid, null);
	}

	public QueryManager(String uid, Model userProfileModel) {
		this.rdfModel = userProfileModel;
	}
	
	@Override
	public AugmentedQuery getAugmentedQuery() {
		if (augmentedQuery == null) {
			augmentedQuery = new AugmentedQuery(query);
		}
		return augmentedQuery;
	}

	public ThreeCixtyQuery getQuery() {
		return query;
	}

	public void setQuery(ThreeCixtyQuery query){
		if (query == null) return;
		this.query = query;
	}
	
	@Override
	public String askForExecutingAugmentedQueryAtEventMedia(AugmentedQuery augmentedQuery,
			EventMediaFormat format) {
		// TODO: call the EventMedia component
		String formatType = EventMediaFormat.JSON == format ? "application/sparql-results+json"
				: (EventMediaFormat.RDF == format ? "application/rdf+xml" : "");
		try {
			String urlStr = EVENTMEDIA_URL_PREFIX + URLEncoder.encode(augmentedQuery.convert2String(), "UTF-8");
			urlStr += "&format=" + URLEncoder.encode(formatType, "UTF-8");
			URL url = new URL(urlStr);
			if (url != null) {
				InputStream input = url.openStream();
				StringBuilder sb = new StringBuilder();
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					 sb.append(new String(b, 0, readBytes));
				}
				input.close();
				return sb.toString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		if (preference == null) return;
		if (query == null) return;
		List<AugmentedQuery> possibleAugmentedQueries = new ArrayList<AugmentedQuery>();
		findPossibleAugmentedQueries(possibleAugmentedQueries);
		if (possibleAugmentedQueries.size() > 0) {
			augmentedQuery = getBestAugmentedQuery(possibleAugmentedQueries);
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
		if (rdfModel == null || queryStr == null) return null;
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

	/**
	 * This method find all the possible augmented queries.
	 * @param possibleAugmentedQueries
	 */
	private void findPossibleAugmentedQueries(
			List<AugmentedQuery> possibleAugmentedQueries) {
		// TODO: how to augment a query from a given preference
		// How to validate whether a query was augmented, what are criteria?
		// How to validate that a query is not augmented from a given query and preference
		
		// for sake of simplicity, take all preferences relevant to places and events
		if (preference == null) return;
		
		AugmentedQuery tmpAugmentedQuery = new AugmentedQuery(query.cloneQuery());
		
		if (query instanceof PlaceQuery) {
			addPlaces((PlaceQuery) tmpAugmentedQuery.getQuery());
		} else if (query instanceof EventQuery) {
			addEvent((EventQuery) tmpAugmentedQuery.getQuery());
		}

		possibleAugmentedQueries.add(tmpAugmentedQuery);
	}

	/**
	 * This method finds the best augmented query from a list of possible augmented queries.
	 * @param possibleAugmentedQueries
	 * @return
	 */
	private AugmentedQuery getBestAugmentedQuery(List<AugmentedQuery> possibleAugmentedQueries) {
		// TODO: make decision about selecting the best one from a list of possible augmented queries
		// for sake of simplicity: pick the first one
		if (possibleAugmentedQueries.size() > 0) {
			return possibleAugmentedQueries.get(0);
		}
		
		return null;
	}

	/**
	 * Adds places preferences to the query.
	 * @param pq
	 */
	private void addPlaces(PlaceQuery pq) {
		Set <Place> places = preference.getHasPlaces();
		if (places == null) return;
		for (Place place: places) {
			pq.addPlace(place);
		}
	}

	/**
	 * Adds event preferences to the query.
	 * @param eQuery
	 */
	private void addEvent(EventQuery eQuery) {
		Set <Event> events = preference.getHasEvents();
		if (events == null) return;
		for (Event event: events) {
			eQuery.addEvent(event);
		}
	}
}
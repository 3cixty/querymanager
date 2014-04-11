package eu.threecixty.querymanager;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSetFormatter;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;

public class TestQM {
	
	@Test
	public void testMakeAQuery() {
		String uid = "kinh";
		String filenameOrURI = "data.rdf";
		String queryString = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>" +
				" SELECT ?name ?rate WHERE { }";
		
		IProfiler profiler = new Profiler(uid);
		
		IQueryManager qm = new QueryManager(uid);
		qm.setModelFromFileOrUri(filenameOrURI);
		
		Query query = qm.createJenaQuery(queryString);
		
		// take preferences into account to augment queries (only fade place preferences are available)
		qm.requestPreferences(profiler);
		
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new PlaceQuery(query);
		
		qm.setQuery(placeQuery);
		
		// perform query augmentation
		qm.performAugmentingTask();
		
		QResult qResult = qm.executeAugmentedQuery();		
		
		// query was already augmented
		Assert.assertFalse(queryString.equals(qm.getAugmentedQuery().getQuery().getQuery()));

		System.out.println("Original Query: " + queryString + "\n");
		
		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery());
				
		// 
		ResultSetFormatter.out(System.out, qResult.getResultSet());
		
		// release all resources used for executing the query
		qResult.releaseBuffer();
	}
}

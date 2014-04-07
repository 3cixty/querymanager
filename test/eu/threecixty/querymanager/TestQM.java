package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSetFormatter;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;

public class TestQM {

	public static void sparqlTest(String filenameOrURI,
			String queryString, String uid) {

		System.out.println("Original Query: " + queryString);
		System.out.println("");

		// select ?likeID ?predicate ?object where
		// { UID profile:PID ?prefID .
		// ?prefID preferences:LID ?likeID .
		// ?likeID ?predicate ?object. }
		// UID profile:ID ?blabla
		// ?blabla profile:preferences:likes ?likes
		
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
		

		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery());
				
		// 
		ResultSetFormatter.out(System.out, qResult.getResultSet());
		
		// release all resources used for executing the query
		qResult.releaseBuffer();
		
	}

	public static void main(String args[]) throws Exception {

		String queryString = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>" +
								" SELECT ?name ?rate WHERE { }";
		String kbPath = "D:/github/querymanager/test/eu/threecixty/querymanager/data.rdf";// "C:/Users/ragarwal/Desktop/INRIA/postdoc work/3cixty work/data.rdf";
		String UID = "rachit";

		sparqlTest(kbPath, queryString, UID);
			/*while (result.hasNext()) {
				QuerySolution soln = result.nextSolution();
				Literal name = soln.getLiteral("name");
				System.out.println(name);*/
			//}
			//System.out.println(result.listProperties().);
		
	}
}

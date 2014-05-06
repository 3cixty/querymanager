package eu.threecixty.querymanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;

public class TestQM {
	
	@Test
	public void testMakeAQuery() {
		String uid = "100900047095598983805";
		String queryString = "SELECT ?category ( COUNT(*) AS ?count )"
				+ " WHERE "
				+ "{"
				+ " ?event a lode:Event; lode:hasCategory ?category ."
				+ "} "
				+ "GROUP BY ?category "
				+ "ORDER BY DESC ( ?count ) "
				+ "LIMIT 20";
		String allPrefixes="";
		try {
			InputStream inStream = System.class.getResourceAsStream("/prefix.properties");
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
			allPrefixes=sb.toString() + " ";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		Query query = qm.createJenaQuery(allPrefixes+queryString);
		
		// take preferences into account to augment queries (only fade place preferences are available)
		qm.requestPreferences(profiler);
		
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new PlaceQuery(query);
		
		qm.setQuery(placeQuery);
		
		// perform query augmentation
		qm.performAugmentingTask();
		

		
		// query was already augmented
		Assert.assertFalse(queryString.equals(qm.getAugmentedQuery().getQuery().getQuery()));

		System.out.println("Original Query: " + queryString + "\n"+ "\n");
		
		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery()+ "\n"+ "\n");

		System.out.println("--------------------------------------------------------------------------\n");
	}
	
	@Test
	public void testMakeAQuery2() {
		String format="json";
		String uid = "100900047095598983805";
		EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
		String queryString = "SELECT  ?event ?title ?description "
				+ "WHERE"
				+ "  { ?event <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> lode:Event ."
				+ "    ?event dc:title ?title ."
				+ "    ?event dc:description ?description ."
				+ "    ?event <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> lode:Event ."
				+ "    ?event lode:involvedAgent ?involvedAgent ."
				+ "    ?involvedAgent dc:publisher ?publisher"
				+ "    FILTER ( str(?publisher) = <http://www.last.fm> )"
				+ "  }"
				+ "LIMIT   20";
		String allPrefixes="";
		try {
			InputStream inStream = System.class.getResourceAsStream("/prefix.properties");
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
			allPrefixes=sb.toString() + " ";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		Query query = qm.createJenaQuery(allPrefixes+queryString);
		
		qm.requestPreferences(profiler);
		
		ThreeCixtyQuery placeQuery = new PlaceQuery(query);
		
		qm.setQuery(placeQuery);
		
		Boolean isUsingPreferences = false;
		if (isUsingPreferences) {
		    qm.performAugmentingTask();
		}
		System.out.println("Original Query: " + queryString + "\n");
		
		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery()+ "\n");
		
		System.out.println(qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), eventMediaFormat)+ "\n"+ "\n");
	}
}

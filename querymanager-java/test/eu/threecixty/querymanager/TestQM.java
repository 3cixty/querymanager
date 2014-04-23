package eu.threecixty.querymanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

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

		try {
			String tmp = URLEncoder.encode(queryString, "UTF-8");
			System.out.println(tmp);
			File file = new File("tmp.txt");
			FileOutputStream out = new FileOutputStream(file);
			out.write(tmp.getBytes());
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IProfiler profiler = new Profiler(uid);
		
		IQueryManager qm = new QueryManager(uid);
		qm.setModelFromFileOrUri(filenameOrURI);
		
		Query query = qm.createJenaQuery(allPrefixes+queryString);
		
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

		System.out.println("Original Query: " + queryString + "\n"+ "\n");
		
		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery()+ "\n"+ "\n");
				
		// 
		ResultSetFormatter.out(System.out, qResult.getResultSet());
		
		// release all resources used for executing the query
		qResult.releaseBuffer();
		System.out.println("--------------------------------------------------------------------------\n");
	}
	
	@Test
	public void testMakeAQuery2() {
		String format="json";
		String uid = "kinh";
		String filenameOrURI = "data.rdf";
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

		try {
			String tmp = URLEncoder.encode(queryString, "UTF-8");
			System.out.println(tmp);
			File file = new File("tmp.txt");
			FileOutputStream out = new FileOutputStream(file);
			out.write(tmp.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IProfiler profiler = new Profiler(uid);
		
		IQueryManager qm = new QueryManager(uid);
		qm.setModelFromFileOrUri(filenameOrURI);
		
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

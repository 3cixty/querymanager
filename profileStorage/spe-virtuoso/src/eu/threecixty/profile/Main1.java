package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

public class Main1 {

	private static final String PROFILE_URI = "http://data.linkedevents.org/person/";

	private static final String SCHEMA_URI = "http://schema.org/";
	private static final String PROFILE_PREFIX = "prefix profile:    <http://test.com/ontology/profile/> ";
	private static final String PREFIXES = "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+"prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
			+"prefix foaf:  <http://xmlns.com/foaf/0.1/> "
			+"prefix schema:    <" + SCHEMA_URI + "> "
			+"prefix xsd:   <http://www.w3.org/2001/XMLSchema#> "
			+ PROFILE_PREFIX
			+"prefix frap:  <http://purl.org/frap#> "
			+"prefix dc:    <http://purl.org/dc/elements/1.1/> "
			+ "prefix fn: <http://www.w3.org/2005/xpath-functions#>";
	
	public static void main(String[] args) {

		createUsers();
	}

	private static void createUsers() {
		final int numberOfThreads = 15;
		final int loopCounts = 1;
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < loopCounts; i++) {
					createUser1();
				}
			}
		};

		for (int i = 0; i < numberOfThreads; i++) {
			new Thread(runnable).start();
		}
	}


	private static void createUser1() {
		String uid = String.valueOf(System.nanoTime()); // random uid
		String profileImage = "https://www.google.fr/images/srpr/logo11w.png";
		String givenName = "GN" + uid;
		String familyName = uid + "FN";

		Random random = new Random();
		int val = random.nextInt(2);
		String gender = null;
		if (val == 0) {
			gender = "Female";
		} else {
			gender = "Male";
		}

		saveUserProfile(uid, givenName, familyName, profileImage, gender);
	}


	private static void saveUserProfile(String uid, String firstName, String lastName, String profileImage, String gender) {
		VirtGraph graph = new VirtGraph ("jdbc:virtuoso://localhost:1111/",
				"dba", "dba");
		List <String> queries = new ArrayList <String>();
		queries.add(setUser(uid));
		queries.add(setGender(uid, gender));
		queries.add(setName(uid, firstName, lastName));

		VirtuosoUpdateRequest vurToInsertData = null;

		for (String query: queries) {
			if (vurToInsertData == null) vurToInsertData = VirtuosoUpdateFactory.create(query, graph);
			else vurToInsertData.addUpdate(query);
		}

		if (vurToInsertData != null) vurToInsertData.exec();

		graph.close();
	}

	private static String makeUser(String uid) {
		String query= " <"+ PROFILE_URI+uid+"> profile:userID \""+uid+"\" . ";
		return query;
	}

	private static String setUser(String uid){
		String query = PREFIXES
				+ " INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
				+ " { ";
		query+= makeUser(uid);
		query+= "}}";
		return query;
	}

	private static String setGender(String uid, String gender ){
		String query=PREFIXES
				+ "INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
				+ "{ ";
		if (gender==null || gender.isEmpty())
			gender="unknown";
		query+= "<"+PROFILE_URI+uid+"> schema:gender \""+gender+"\" ."
				+ "}}";
		return query;
	}

	private static String makeNameQuery(String uid,
			String firstName, String lastName) {
		String query = "  <"+PROFILE_URI+uid+"> schema:givenName \""+ firstName +"\".";
		query+= "  <"+PROFILE_URI+uid+"> schema:familyName \""+lastName+"\".";
		return query;
	}


	private static String setName(String uid, String firstName, String lastName ){
		String query=PREFIXES
				+ "   INSERT DATA { GRAPH <"+ getGraphName(uid) +">"
				+ "  { ";
		query+= makeNameQuery(uid, firstName, lastName);
		query+= "}}";
		return query;
	}

	private static String getGraphName(String uid) {
		return "http://test.com/private/";
	}	
}

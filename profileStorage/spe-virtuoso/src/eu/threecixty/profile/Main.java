package eu.threecixty.profile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.shared.JenaException;

import eu.threecixty.Configuration;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;

public class Main {

//	public static final String PROFILE_PREFIX = "prefix profile:	<http://3cixty.com/ontology/profile/> ";
//	public static final String PREFIXES = "prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
//			+"prefix rdfs:	<http://www.w3.org/2000/01/rdf-schema#> "
//			+"prefix foaf:	<http://xmlns.com/foaf/0.1/> "
//			+"prefix schema:	<http://schema.org/> "
//			+"prefix xsd:	<http://www.w3.org/2001/XMLSchema#> "
//			+ PROFILE_PREFIX
//			+"prefix frap:	<http://purl.org/frap#> "
//			+"prefix dc:	<http://purl.org/dc/elements/1.1/> ";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Configuration.setPath("/Users/kinh/github/querymanager/querymanagerServlet/WebContent/");
		
		//String query = "select (count (*) as ?count) where {?s  ?p ?o }";
		String query = PREFIXES + " SELECT DISTINCT  * WHERE { <http://data.linkedevents.org/location/d7776357-6a15-431b-b782-cf2c3bda83bf> ?p ?o MINUS { <http://data.linkedevents.org/location/d7776357-6a15-431b-b782-cf2c3bda83bf> rdf:type ?o } OPTIONAL { ?o ?p2 ?o2 MINUS { ?o rdf:type ?o2 }} }";
		
		Query q = QueryFactory.create(query);
		
		System.out.println(q.hasAggregators());
		
//		VirtuosoManager.getInstance().createAccount("103918130978226832690");
//		VirtuosoManager.getInstance().createAccount("112126033242468644827");
//		VirtuosoManager.getInstance().createAccount("117895882057702509461");
//		System.out.println(VirtuosoManager.getInstance().existsAccount("112126033242468644827"));
//		
//		String uid = GoogleAccountUtils.getUID("ya29.3gACh8dUnmQzCukMa9d6_rWu8GSfxVgRPdjRrzWeY56V86G05oGNfh_p7uG6bcwyEsdJvqmeztPNVQ");
//		System.out.println(uid);
//		
		
//		VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
//		String strQuery = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
// + "prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> " 
// + "prefix foaf:   <http://xmlns.com/foaf/0.1/> " 
// + "prefix schema: <http://schema.org/> " 
// + "prefix xsd:    <http://www.w3.org/2001/XMLSchema#> " 
// + "prefix profile:    <http://3cixty.com/ontology/profile/>" 
// + "prefix frap:   <http://purl.org/frap#>" 
// + "prefix dc: <http://purl.org/dc/elements/1.1/>" 
// + "  INSERT DATA {" 
// + "GRAPH <http://3cixtyfake.com> {"  
//+ "<http://data.linkedevents.org/person/junkid> frap:holds <http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c> ."  
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c> rdf:type frap:Preference ."
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c> frap:about <http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/aboutblankNode> ." 
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/aboutblankNode>  rdf:type frap:Pattern ." 
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/aboutblankNode>  frap:filter <http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/filterblankNode> ." 
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/filterblankNode> rdf:type frap:Filter ." 
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/filterblankNode> profile:hasPreferredCity \"Milan\" ." 
//+ "<http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c/filterblankNode>  profile:hasPreferredCountry \"Italy\" . "
// + "<http://data.linkedevents.org/person/junkid> frap:holds <http://data.linkedevents.org/person/junkid/TripPreference/69054ca1-4087-4685-8b32-190d4a89f37c> ." 
//+ "}}";
//		
//		//Query query = QueryFactory.create(strQuery);
//		VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
//				.create(strQuery, virtGraph);
//		
//		vur.exec();
//		
//		virtGraph.close();
		
//		for (int i = 0; i < 100; i++)
//		VirtuosoManager.getInstance().dropGraphs();
//		
//		UserProfile userProfile = ProfileManagerImpl.getInstance().getProfile("103918130978226832690");
//		Address address = new Address();
//		address.setCountryName("France");
//		userProfile.setHasAddress(address);
//		ProfileManagerImpl.getInstance().saveProfile(userProfile);
//		ThreeCixtySettings settings = SettingsStorage.load("103918130978226832690");
//		System.out.println(settings);
//		String countryName = ProfilerPlaceUtilsVirtuoso.getCountryName("103918130978226832690");
//		System.out.println(countryName);
//		System.out.println(ProfileManagerImpl.getInstance().getProfile("103918130978226832690").getHasName().getFamilyName());
//		System.out.println(ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromRatingOfFriends("103918130978226832690", 3));
		
		//System.out.println(userProfile);
		
		
		//System.out.println(containValidCharacters("ADBcD0hhjn-+"));
		
		
		Tray tray = new Tray();
		tray.setUid("103918130978226832690");
		tray.setElement_title("My elmeent title");
		tray.setItemType("Events");
		tray.setItemId("000012");
		/*
		try {
			VirtuosoTrayStorage.deleteTray(tray);
		} catch (InvalidTrayElement e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
//		tray.setTimestamp(System.currentTimeMillis());
//		try {
//			System.out.print(VirtuosoTrayStorage.getInstance().addTray(tray));
//		} catch (InvalidTrayElement e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List<Tray> trays;
//		try {
//			trays = VirtuosoTrayStorage.getInstance().getTrays("103918130978226832690");
//			System.out.println(trays.size());
//		} catch (InvalidTrayElement e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		dropGraphs();
//		System.out.println(ProfileManagerImpl.getInstance().getTrayManager().getAllTrays());
//		
//		Gson gson = new Gson();
//		
//		System.out.println(gson.toJson(ProfileManagerImpl.getInstance().getTrayManager().getAllTrays()));
//		
//		System.out.println("start".startsWith("start"));
//		
//		insertRating();
//		
//		//List <String> list = ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromRating("103411760688868522737", 1);
//		
//		List <String> list = ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromRatingOfFriends("103918130978226832690", 1);
//		
//		System.out.println(list);
//http://data.linkedevents.org/person/		
		
		
		
		//List <String> friends = ProfilerPlaceUtilsVirtuoso.getFriendUIDs("103918130978226832690");
		//System.out.println(friends.size());
		
		//doJSON();
		
		
		System.out.println("Available permits = " + VirtuosoManager.getInstance().getAvailablePermits());
		
		long startTime = System.currentTimeMillis();
		
		createUsers();
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("total time = " + (endTime - startTime) / 1000 + " seconds");
		
		System.out.println("Available permits = " + VirtuosoManager.getInstance().getAvailablePermits());
	}

	private static void dropGraphs() {
		
		try {
			
			URL url = new  URL("http://dev.3cixty.com/v2/dropGraphs");
			trustAllHosts();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("google_access_token", "ya29.2QAqUZmjQpek7diqFMgmwfd0erIMJe63l752gpKSU7F1oSn65aL7n6j-xgtGABD4GBqpy-HBber5kA");
			conn.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	   private static void trustAllHosts()
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
	        {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers()
	            {
	                return new java.security.cert.X509Certificate[] {};
	            }

	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	            {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	            {
	            }
	        } };

	        // Install the all-trusting trust manager
	        try
	        {
	            SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	   
	   
		private static boolean containValidCharacters(String str) {
			Pattern pattern = Pattern.compile("([a-z]*[A-Z]*[0-9]*[-]*)*");
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		}
		
		private static void insertRating() {
			StringBuffer buf = new StringBuffer();
			buf.append("INSERT DATA { GRAPH <http://3cixty.com/googleplaces> \n");
			buf.append("{\n");
			String locationUri = "http://data.linkedevents.org/location/72c9cb35-9e45-463d-a2fc-94e7026a36b8";
			String reviewUri = locationUri + "/review/" + UUID.randomUUID();
			String creatorUri = "http://data.linkedevents.org/user/" + UUID.randomUUID();
			buf.append("<" + locationUri + "> schema:review <" + reviewUri + "> .\n");
			buf.append("<" + reviewUri + "> schema:creator <" + creatorUri + "> . \n");
			//buf.append("<" + creatorUri + "> schema:url <https://plus.google.com/103411760688868522737>.\n");
			//buf.append("<" + creatorUri + "> schema:url <https://plus.google.com/103918130978226832690>.\n"); // kinh
			buf.append("<" + creatorUri + "> schema:url <https://plus.google.com/117895882057702509461>.\n"); // tony
			//buf.append("<" + creatorUri + "> schema:url <https://plus.google.com/115137127931365079177>.\n"); // rachit
			buf.append("<" + reviewUri + "> schema:reviewRating	<http://data.linkedevents.org/def/location#rating5> .\n");
			buf.append("}}");
			
			System.out.println(buf.toString());
		}
		
		
		private static void doJSON() {
			String tmp = "{ \"head\":{\"vars\":[\"venue\",\"title\", \"callret-1\", \"callret-2\"] }}";
			
			try {
				JSONObject jsonObject = new JSONObject(tmp);
				JSONObject jsonHead = jsonObject.getJSONObject("head");
				JSONArray newArrs = new JSONArray();
				JSONArray subHeadArrs = jsonHead.getJSONArray("vars");
				for (int i = 0; i < subHeadArrs.length(); i++) {
					String varName = subHeadArrs.get(i).toString();
					boolean found = false;
					for (int index = 0; index <= 10; index++) {
						if (varName.equals("callret-" + index)) {
							found = true;
							break;
						}
					}
					if (!found) {
						newArrs.put(varName);
					}
				}
				newArrs.put("augmented");
				jsonHead.put("vars", newArrs);
				
				
				System.out.println(jsonObject);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		private static void createUser() throws TooManyConnections {
			String uid = String.valueOf(System.nanoTime()); // random uid
			UserProfile profile = new UserProfile();
			profile.setHasUID(uid);
			String picture = "https://www.google.fr/images/srpr/logo11w.png";
			profile.setProfileImage(picture);
			Name name = new Name();
			profile.setHasName(name);
			String givenName = "GN" + RandomStringUtils.random(20);
			String familyName = RandomStringUtils.random(20) + "FN";
			name.setGivenName(givenName);
			name.setFamilyName(familyName);
			
			Random random = new Random();
			int val = random.nextInt(2);
			if (val == 0) {
				profile.setHasGender("Female");
			} else {
				profile.setHasGender("Male");
			}
			
			Set<String> knows = new HashSet<String>();
			
			knows.add("103411760688868522737"); // this would be useful to test augmentation query
			
			ProfileManagerImpl.getInstance().saveProfile(profile);
		}
		
		public static final String PROFILE_URI = "http://data.linkedevents.org/person/";
		
		public static final String SCHEMA_URI = "http://schema.org/";
		//public static final String PROFILE_GRAPH = "http://3cixty.com/fakeprofile";
		public static final String PROFILE_PREFIX = "prefix profile:	<http://3cixty.com/ontology/profile/> ";
		public static final String PREFIXES = "prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+"prefix rdfs:	<http://www.w3.org/2000/01/rdf-schema#> "
				+"prefix foaf:	<http://xmlns.com/foaf/0.1/> "
				+"prefix schema:	<" + SCHEMA_URI + "> "
				+"prefix xsd:	<http://www.w3.org/2001/XMLSchema#> "
				+ PROFILE_PREFIX
				+"prefix frap:	<http://purl.org/frap#> "
				+"prefix dc:	<http://purl.org/dc/elements/1.1/> "
				+ "prefix fn: <http://www.w3.org/2005/xpath-functions#>";
		
		private static void createUsers() {
			final int numberOfThreads = 200;
			final int loopCounts = 10;
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
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
			try {
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
			} catch (JenaException e) {
				e.printStackTrace();
			}
		}
		
		private static String makeUser(String uid) {
			String query= " <"+ PROFILE_URI+uid+"> profile:userID \""+uid+"\" . ";
			return query;
		}
		/**
		 * insert User in the KB
		 * @param uid
		 * @return
		 */
		public static String setUser(String uid){
			String query = PREFIXES
				+ " INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
				+ " { ";
					query+= makeUser(uid);
				query+= "}}";
				return query;
		}
		
		public static String setGender(String uid, String gender ){
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
		
		public static String getGraphName(String uid) {
			return "http://3cixty.com/private/";
		}
}

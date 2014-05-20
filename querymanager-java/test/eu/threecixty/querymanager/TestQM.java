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
	public void testAugmentQueryWithCurrentCountry() {
		String uid = "100900047095598983805";
		String queryString = "SELECT ?category ( COUNT(*) AS ?count )"
				+ " WHERE "
				+ "{"
				+ " ?event a lode:Event; lode:hasCategory ?category ."
				+ "} "
				+ "GROUP BY ?category "
				+ "ORDER BY DESC ( ?count ) "
				+ "LIMIT 20";
		String allPrefixes =  getPrefixes();
		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireCurrentCountry(true);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);

		String currentCountry = "Italy";
		// query was already augmented
		Assert.assertFalse(queryString.contains(currentCountry));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(currentCountry));

//		System.out.println("Original Query: " + queryString + "\n"+ "\n");
//		
//		System.out.println("Query after being augmented: " + qm.getAugmentedQuery().getQuery().getQuery()+ "\n"+ "\n");
	}
	
	@Test
	public void testAugmentQueryWithCurrentTown() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireCurrentTown(true);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String currentTown = "Milano";
		
		Assert.assertFalse(queryString.contains(currentTown));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(currentTown));
	}

	@Test
	public void testAugmentQueryWithoutInformationAboutPlace() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String currentTown = "Paris";
		
		Assert.assertFalse(queryString.contains(currentTown));
		// the target query does not contain the current town
		Assert.assertFalse(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(currentTown));
	}

	@Test
	public void testAugmentQueryWithPlaceNameRating() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireScoreRatedAtLeast(5);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String placeName = "Hilton Milan Hotel";
		
		Assert.assertFalse(queryString.contains(placeName));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(placeName));
	}

	@Test
	public void testAugmentQueryWithPlaceNameVisited() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireNumberOfTimesVisitedAtLeast(3);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String placeName = "Hilton Milan Hotel";
		
		Assert.assertFalse(queryString.contains(placeName));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(placeName));
	}

	@Test
	public void testAugmentQueryWithPlaceNameVisitedBasedOnMyFriends() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireNumberOfTimesVisitedForFriendsAtLeast(2);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String placeName = "Hilton Milan Hotel";
		
		Assert.assertFalse(queryString.contains(placeName));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(placeName));
	}

	@Test
	public void testAugmentQueryWithPlaceNameRatingBasedOnMyFriends() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireScoreRatedForFriendsAtLeast(4);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String placeName = "Hilton Milan Hotel";
		
		Assert.assertFalse(queryString.contains(placeName));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(placeName));
	}

	@Test
	public void testAugmentQueryWithCurrentGpsCoordinates() {
		String uid = "100900047095598983805";
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
		String allPrefixes = getPrefixes();

		
		IProfiler profiler = new Profiler(uid);
		if (profiler != null) {
			profiler.requireAreaWithin(10);
			profiler.PopulateProfile();
		}
		
		IQueryManager qm = new QueryManager(uid);
		
		performAugmentQuery(qm, profiler, allPrefixes+queryString, true);
		
		String latStr = "51.39886741923267";
		
		Assert.assertFalse(queryString.contains(latStr));
		Assert.assertTrue(qm.getAugmentedQuery().getQuery().getQuery().toString().contains(latStr));
	}

	private void performAugmentQuery(IQueryManager qm, IProfiler profiler,
			String queryStr, boolean isUsingPreferences) {
		Query query = qm.createJenaQuery(queryStr);
		
		qm.requestPreferences(profiler);
		
		ThreeCixtyQuery placeQuery = new ThreeCixtyQuery(query);
		
		qm.setQuery(placeQuery);
		
		if (isUsingPreferences) {
		    qm.performAugmentingTask();
		}
	}

	private String getPrefixes() {
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
			return sb.toString() + " ";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return " ";
	}
}

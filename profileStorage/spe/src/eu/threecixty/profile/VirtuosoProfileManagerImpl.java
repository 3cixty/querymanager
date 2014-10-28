package eu.threecixty.profile;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.IDCrawlTimeMapping;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.ProfileManager;
import eu.threecixty.profile.RdfFileManager;
import eu.threecixty.profile.UserProfile;

/**
 * This is an implementation version for ProfileManager using a RDF model file.
 * @author Cong Kinh Nguyen
 *
 */
class VirtuosoProfileManagerImpl implements ProfileManager {
	
	@Override
	public UserProfile getProfile(String uid) {
		return VirtuosoUserProfileStorage.loadProfile(uid);
	}

	@Override
	public boolean saveProfile(UserProfile userProfile,String type) {
		return VirtuosoUserProfileStorage.saveProfile(userProfile,type);
	}

	@Override
	public boolean existUID(String uid) {
		return VirtuosoUserProfileStorage.existUID(uid);
	}

	@Override
	public int getMinimumNumberOfTimesVisited(String uid) {
		return PreferencesUtils.getMinimumNumberOfTimesVisited(uid);
	}

	@Override
	public float getMinimumScoreRated(String uid) {
		return PreferencesUtils.getMinimumScoreRated(uid);
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		return PreferencesUtils.getMinimumNumberOfTimesVisitedForFriends(uid);
	}

	@Override
	public float getMinimumScoreRatedForFriends(String uid) {
		return PreferencesUtils.getMinimumScoreRatedForFriends(uid);
	}

	@Override
	public String getCountryName(String uid) {
		return ProfilerPlaceUtils.getCountryName(uid);
	}

	@Override
	public String getTownName(String uid) {
		return ProfilerPlaceUtils.getTownName(uid);
	}

	@Override
	public List<String> getPlaceNamesFromRating(String uid, float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRating(uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getPlaceNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRatingOfFriends(uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(String uid) {
		return ProfilerEventUtils.getEventNamesFromEventPreference(uid);
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(String uid) {
		return ProfilerEventUtils.getPreferredStartAndEndDates(uid);
	}

	@Override
	public List<String> getEventNamesFromRating(String uid, float rating) {
		return ProfilerEventUtils.getEventNamesFromRating(uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisited(uid, number);
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerEventUtils.getEventNamesFromRatingOfFriends(uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(String uid) {
		return ProfilerEventUtils.getEventNamesWhichFriendsLikeToVisit( uid);
	}

	@Override
	public GpsCoordinate getCoordinate(String uid) {
		return ProfilerPlaceUtils.getCoordinates( uid);
	}
	
	private Model getModel() {
		return RdfFileManager.getInstance().getRdfModel();
	}

	@Override
	public Set<IDMapping> getIDMappings() {
		return getMobidotIDsForUsers();
	}

	@Override
	public Set<IDCrawlTimeMapping> getIDCrawlTimeMappings(){
		return getCrawlTimesForUsers();
	}
	
	/**
	 * get MobidotIDs For the 3cixty Users
	 * @return
	 */
	private Set<IDMapping> getMobidotIDsForUsers() {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
    			+"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
    			+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
    			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
    			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n"
    			+ "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n"
    			+ "SELECT ?uid ?mobidotID\n"
    			+ "WHERE {\n\n"
    			+ "?root a owl:NamedIndividual .\n"
    			+ "?root profile:hasUID ?uid .\n"
    			+ "?root profile:hasProfileIdentities ?profileidentities .\n"
    			+ "?profileidentities profile:hasUserAccountID ?mobidotID. \n"
    			+ "?profileidentities profile:hasSource ?source. \n"
    			+ "Filter(STR(?source) =\"" + "https://www.movesmarter.nl/portal" + "\"). \n"
    			+ "\n"
    			+ "}";
		Set<IDMapping> idMapping=new HashSet<IDMapping>();
		
		Connection conn = null;
		Statement stmt = null;
	    
		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return null;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(queryString);

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				String UID = qs.getLiteral("uid").getString();
				String mobidotUserName = qs.getLiteral("mobidotID").getString();
				//Long mobidotID= getMobidotIDforUsername(mobidotUserName);
				IDMapping mapper=new IDMapping();
				mapper.setThreeCixtyID(UID);
				mapper.setMobidotUserName(mobidotUserName);
				//mapper.setMobidotID(mobidotID);
				idMapping.add(mapper);
			}
						
			return idMapping;


		} catch ( IOException  ex) {
			ex.printStackTrace();
		} catch ( SQLException ex){
			ex.printStackTrace();
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return idMapping;
	}
	/**
	 * get Crawl Times For the 3cixty Users
	 * @return
	 */
	private Set<IDCrawlTimeMapping> getCrawlTimesForUsers() {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
    			+"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
    			+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
    			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
    			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n"
    			+ "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n"
    			+ "SELECT ?uid ?lastCrawlTime\n"
    			+ "WHERE {\n\n"
    			+ "?root a owl:NamedIndividual .\n"
    			+ "?root profile:hasUID ?uid .\n"
    			+ "?root profile:hasLastCrawlTime ?lastCrawlTime .\n"
    			+ "\n"
    			+ "}";
		Set<IDCrawlTimeMapping> idCrawlTimeMapping=new HashSet<IDCrawlTimeMapping>();
		
		Connection conn = null;
		Statement stmt = null;
	    
		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return null;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(queryString);

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				String UID = qs.getLiteral("uid").getString();
				String lastCrawlTime = qs.getLiteral("lastCrawlTime").getString();
				
				IDCrawlTimeMapping mapper=new IDCrawlTimeMapping();
				mapper.setThreeCixtyID(UID);
				mapper.setLastCrawlTime(lastCrawlTime);
				idCrawlTimeMapping.add(mapper);
			}
						
			return idCrawlTimeMapping;


		} catch ( IOException  ex) {
			ex.printStackTrace();
		} catch ( SQLException ex){
			ex.printStackTrace();
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return idCrawlTimeMapping;
	}
}

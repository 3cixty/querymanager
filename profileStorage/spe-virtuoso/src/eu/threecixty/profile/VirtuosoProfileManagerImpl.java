package eu.threecixty.profile;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.IDCrawlTimeMapping;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.ProfileManager;
import eu.threecixty.profile.UserProfile;

/**
 * This is an implementation version for ProfileManager using a RDF model file.
 * @author Cong Kinh Nguyen
 *
 */
class VirtuosoProfileManagerImpl implements ProfileManager {
	
	@Override
	public UserProfile getProfile(String uid) {
		return VirtuosoUserProfileStorage.getInstance(uid).loadProfile();
	}

	@Override
	public boolean saveProfile(UserProfile userProfile) {
		return VirtuosoUserProfileStorage.getInstance(userProfile.getHasUID()).saveProfile(userProfile);
	}

	@Override
	public boolean existUID(String uid) {
		return VirtuosoUserProfileStorage.getInstance(uid).existUID();
	}

	@Override
	public int getMinimumNumberOfTimesVisited(String uid) {
		return PreferencesUtilsVirtuoso.getMinimumNumberOfTimesVisited(uid);
	}

	@Override
	public float getMinimumScoreRated(String uid) {
		return PreferencesUtilsVirtuoso.getMinimumScoreRated(uid);
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(String uid) {
		return PreferencesUtilsVirtuoso.getMinimumNumberOfTimesVisitedForFriends(uid);
	}

	@Override
	public float getMinimumScoreRatedForFriends(String uid) {
		return PreferencesUtilsVirtuoso.getMinimumScoreRatedForFriends(uid);
	}

	@Override
	public String getCountryName(String uid) {
		return ProfilerPlaceUtilsVirtuoso.getCountryName(uid);
	}

	@Override
	public String getTownName(String uid) {
		return ProfilerPlaceUtilsVirtuoso.getTownName(uid);
	}

	@Override
	public List<String> getPlaceNamesFromRating(String uid, float rating) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceIdsFromRating(uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getPlaceNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceIdsFromRatingOfFriends(uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(String uid) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromEventPreference(uid);
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(String uid) {
		return ProfilerEventUtilsVirtuoso.getPreferredStartAndEndDates(uid);
	}

	@Override
	public List<String> getEventNamesFromRating(String uid, float rating) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromRating(uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromNumberOfTimesVisited(uid, number);
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromRatingOfFriends(uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(String uid) {
		return ProfilerEventUtilsVirtuoso.getEventNamesWhichFriendsLikeToVisit( uid);
	}

	@Override
	public GpsCoordinate getCoordinate(String uid) {
		return ProfilerPlaceUtilsVirtuoso.getCoordinates( uid);
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
        String queryString = "prefix foaf:	<http://xmlns.com/foaf/0.1/> "
			+"prefix profile:	<http://3cixty.com/ontology/profile/> \n"
			+"prefix fn:	<http://www.w3.org/2005/xpath-functions#> \n"
    			+ "SELECT ?uid ?mobidotID "
    			+ "WHERE { "
    			+ "?root profile:userID ?uid . "
    			+ "?root foaf:account ?pi . "
			+ "?pi foaf:accountName ?mobidotID ."
			+ "Filter(fn:ends-with(STR(?pi), \"Mobidot\")) . "
    			+ "}";
		Set<IDMapping> idMapping=new HashSet<IDMapping>();
		
		QueryReturnClass qRC=VirtuosoConnection.query(queryString);

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
		qRC.closeConnection();
		return idMapping;
	}
	/**
	 * get Crawl Times For the 3cixty Users
	 * @return
	 */
	private Set<IDCrawlTimeMapping> getCrawlTimesForUsers() {
        String queryString = "prefix foaf:	<http://xmlns.com/foaf/0.1/> "
			+"prefix profile:	<http://3cixty.com/ontology/profile/> "
    			+ "SELECT ?uid ?lastCrawlTime "
    			+ "WHERE { "
    			+ "?root profile:userID ?uid . "
    			+ "?root profile:hasLastCrawlTime ?lastCrawlTime . "
      			+ "}";
		Set<IDCrawlTimeMapping> idCrawlTimeMapping=new HashSet<IDCrawlTimeMapping>();
	    
			
		QueryReturnClass qRC=VirtuosoConnection.query(queryString);

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
		qRC.closeConnection();
		return idCrawlTimeMapping;
	}

	@Override
	public Partner getMobidot() {
		return MobidotImpl.getInstance();
	}

	@Override
	public Partner getGoFlow() {
		return GoFlowImpl.getInstance();
	}

	@Override
	public TrayManager getTrayManager() {
		return VirtuosoTrayStorage.getInstance();
	}

	@Override
	public List<UserProfile> getAllUserProfiles() {
		//return VirtuosoUserProfileStorage.getAllUserProfiles();
		return null;
	}
}

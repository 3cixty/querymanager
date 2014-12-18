package eu.threecixty.profile;


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

/**
 * This is an implementation version for ProfileManager using a RDF model file.
 * @author Cong Kinh Nguyen
 *
 */
class SimpleProfileManagerImpl implements ProfileManager {
	
	@Override
	public UserProfile getProfile(String uid) {
		return UserProfileStorage.loadProfile(uid);
	}

	@Override
	public boolean saveProfile(UserProfile userProfile) {
		return UserProfileStorage.saveProfile(userProfile);
	}

	@Override
	public boolean existUID(String uid) {
		return UserProfileStorage.existUID(uid);
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
		return ProfilerPlaceUtils.getCountryName(getModel(), uid);
	}

	@Override
	public String getTownName(String uid) {
		return ProfilerPlaceUtils.getTownName(getModel(), uid);
	}

	@Override
	public List<String> getPlaceNamesFromRating(String uid, float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRating(getModel(), uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getModel(), uid, number);
	}

	@Override
	public List<String> getPlaceNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRatingOfFriends(getModel(), uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getModel(), uid, number);
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(String uid) {
		return ProfilerEventUtils.getEventNamesFromEventPreference(getModel(), uid);
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(String uid) {
		return ProfilerEventUtils.getPreferredStartAndEndDates(getModel(), uid);
	}

	@Override
	public List<String> getEventNamesFromRating(String uid, float rating) {
		return ProfilerEventUtils.getEventNamesFromRating(getModel(), uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisited(getModel(), uid, number);
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(String uid,
			float rating) {
		return ProfilerEventUtils.getEventNamesFromRatingOfFriends(getModel(), uid, rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			String uid, int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisitedOfFriends(getModel(), uid, number);
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(String uid) {
		return ProfilerEventUtils.getEventNamesWhichFriendsLikeToVisit(getModel(), uid);
	}

	@Override
	public GpsCoordinate getCoordinate(String uid) {
		return ProfilerPlaceUtils.getCoordinates(getModel(), uid);
	}
	
	private Model getModel() {
		return RdfFileManager.getInstance().getRdfModel();
	}

	@Override
	public Set<IDMapping> getIDMappings() {
		return getMobidotIDsForUsers();
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
		return TrayStorage.getInstance();
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
		Query query = QueryFactory.create(queryString);


			QueryExecution qe = QueryExecutionFactory.create(query, getModel());
			ResultSet rs = qe.execSelect();
			for ( ; rs.hasNext(); ) {
				QuerySolution qs = rs.next();
				String UID = qs.getLiteral("uid").getString();
				String mobidotUserName = qs.getLiteral("mobidotID").getString();
				//Long mobidotID= getMobidotIDforUsername(mobidotUserName);
				IDMapping mapper=new IDMapping();
				mapper.setThreeCixtyID(UID);
				mapper.setMobidotUserName(mobidotUserName);
				//mapper.setMobidotID(mobidotID);
				idMapping.add(mapper);
			}
			qe.close();
			return idMapping;
	}

	/**
	 * XXX: We never integrate Crawler for RDF version.
	 */
	@Override
	public Set<IDCrawlTimeMapping> getIDCrawlTimeMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserProfile> getAllUserProfiles() {
		return UserProfileStorage.getAllProfiles();
	}
}

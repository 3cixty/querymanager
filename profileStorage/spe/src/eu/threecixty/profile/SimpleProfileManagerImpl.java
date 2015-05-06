package eu.threecixty.profile;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerImpl;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;

/**
 * This is an implementation version for ProfileManager using a RDF model file.
 * @author Cong Kinh Nguyen
 *
 */
class SimpleProfileManagerImpl implements ProfileManager {
	
	@Override
	public UserProfile getProfile(String uid, Map <String, Boolean> attributes) {
		return UserProfileStorage.loadProfile(uid, attributes);
	}

	@Override
	public boolean saveProfile(UserProfile userProfile, Map <String, Boolean> attributes) {
		return UserProfileStorage.saveProfile(userProfile, attributes);
	}

	@Override
	public boolean existUID(String uid) {
		return UserProfileStorage.existUID(uid);
	}

	@Override
	public int getMinimumNumberOfTimesVisited(UserProfile userProfile) {
		return PreferencesUtils.getMinimumNumberOfTimesVisited(getUID(userProfile));
	}

	@Override
	public float getMinimumScoreRated(UserProfile userProfile) {
		return PreferencesUtils.getMinimumScoreRated(getUID(userProfile));
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile) {
		return PreferencesUtils.getMinimumNumberOfTimesVisitedForFriends(getUID(userProfile));
	}

	@Override
	public float getMinimumScoreRatedForFriends(UserProfile userProfile) {
		return PreferencesUtils.getMinimumScoreRatedForFriends(getUID(userProfile));
	}

	@Override
	public String getCountryName(UserProfile userProfile) {
		return ProfilerPlaceUtils.getCountryName(getModel(), getUID(userProfile));
	}

	@Override
	public String getTownName(UserProfile userProfile) {
		return ProfilerPlaceUtils.getTownName(getModel(), getUID(userProfile));
	}

	@Override
	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRating(getModel(), getUID(userProfile), rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getModel(), getUID(userProfile), number);
	}

	@Override
	public List<String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float rating) {
		return ProfilerPlaceUtils.getPlaceNamesFromRatingOfFriends(getModel(), getUID(userProfile), rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		return ProfilerPlaceUtils.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getModel(), getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(UserProfile userProfile) {
		return ProfilerEventUtils.getEventNamesFromEventPreference(getModel(), getUID(userProfile));
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile) {
		return ProfilerEventUtils.getPreferredStartAndEndDates(getModel(), getUID(userProfile));
	}

	@Override
	public List<String> getEventNamesFromRating(UserProfile userProfile, float rating) {
		return ProfilerEventUtils.getEventNamesFromRating(getModel(), getUID(userProfile), rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisited(getModel(), getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(UserProfile userProfile,
			float rating) {
		return ProfilerEventUtils.getEventNamesFromRatingOfFriends(getModel(), getUID(userProfile), rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		return ProfilerEventUtils.getEventNamesFromNumberOfTimesVisitedOfFriends(getModel(), getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile) {
		return ProfilerEventUtils.getEventNamesWhichFriendsLikeToVisit(getModel(), getUID(userProfile));
	}

	@Override
	public GpsCoordinate getCoordinate(UserProfile userProfile) {
		return ProfilerPlaceUtils.getCoordinates(getModel(), getUID(userProfile));
	}
	
	private Model getModel() {
		return RdfFileManager.getInstance().getRdfModel();
	}

	@Override
	public Set<IDMapping> getIDMappings() {
		return getMobidotIDsForUsers();
	}
	
	@Override
	public Partner getPartner() {
		return PartnerImpl.getInstance();
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
				String uid = qs.getLiteral("uid").getString();
				String mobidotUserName = qs.getLiteral("mobidotID").getString();
				//Long mobidotID= getMobidotIDforUsername(mobidotUserName);
				IDMapping mapper=new IDMapping();
				mapper.setThreeCixtyID(uid);
				mapper.setMobidotID(mobidotUserName);
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

	@Override
	public boolean checkAttributeToStore(Map<String, Boolean> attributes, String attribute) {
		// XXX: always use code from ProfileManagerImpl for this method.
		return false;
	}

	@Override
	public String find3cixtyUID(String arg0, String sourc) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getUID(UserProfile userProfile) {
		if (userProfile == null) return null;
		return userProfile.getHasUID();
	}

	@Override
	public Set<String> find3cixtyUIDs(List<String> arg0, String arg1, List <String> unfoundAccountIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createProfiles(List<UserProfile> arg0) throws IOException,
			UnknownException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UserProfile findUserProfile(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateKnows(UserProfile arg0, Set<String> arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}

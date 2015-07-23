package eu.threecixty.profile;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.threecixty.Configuration;
import eu.threecixty.partners.Partner;
import eu.threecixty.partners.PartnerImpl;
import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.IDCrawlTimeMapping;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.ProfileManager;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils.UidSource;

/**
 * This is an implementation version for ProfileManager using a RDF model file.
 * @author Cong Kinh Nguyen
 *
 */
class VirtuosoProfileManagerImpl implements ProfileManager {
	
	@Override
	public UserProfile getProfile(String uid, Map <String, Boolean> attrs) throws TooManyConnections {
		try {
			return VirtuosoUserProfileStorage.getInstance(uid).loadProfile(attrs);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}

	@Override
	public boolean saveProfile(UserProfile userProfile, Map <String, Boolean> attributes) throws TooManyConnections {
		try {
			return VirtuosoUserProfileStorage.getInstance(userProfile.getHasUID()).saveProfile(userProfile, attributes);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}

	@Override
	public boolean existUID(String uid) throws TooManyConnections {
		try {
			return VirtuosoUserProfileStorage.getInstance(uid).existUID();
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}

	@Override
	public int getMinimumNumberOfTimesVisited(UserProfile userProfile) {
		return PreferencesUtilsVirtuoso.getMinimumNumberOfTimesVisited(getUID(userProfile));
	}

	@Override
	public float getMinimumScoreRated(UserProfile userProfile) {
		return PreferencesUtilsVirtuoso.getMinimumScoreRated(getUID(userProfile));
	}

	@Override
	public int getMinimumNumberOfTimesVisitedForFriends(UserProfile userProfile) {
		return PreferencesUtilsVirtuoso.getMinimumNumberOfTimesVisitedForFriends(getUID(userProfile));
	}

	@Override
	public float getMinimumScoreRatedForFriends(UserProfile userProfile) {
		return PreferencesUtilsVirtuoso.getMinimumScoreRatedForFriends(getUID(userProfile));
	}

	@Override
	public String getCountryName(UserProfile userProfile) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getCountryName(getUID(userProfile));
	}

	@Override
	public String getTownName(UserProfile userProfile) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getTownName(getUID(userProfile));
	}

	@Override
	public List<String> getPlaceIdsFromRating(UserProfile userProfile, float rating) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getPlaceIdsFromRating(getUID(userProfile), rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getUID(userProfile), number);
	}

	@Override
	public List<String> getPlaceIdsFromRatingOfFriends(UserProfile userProfile,
			float rating) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getPlaceIdsFromRatingOfFriends(getUID(userProfile), rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromNumberOfTimesVisitedOfFriends(getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesFromEventPreferences(UserProfile userProfile) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromEventPreference(getUID(userProfile));
	}

	@Override
	public List<StartAndEndDate> getPreferredStartAndEndDates(UserProfile userProfile) {
		return ProfilerEventUtilsVirtuoso.getPreferredStartAndEndDates(getUID(userProfile));
	}

	@Override
	public List<String> getEventNamesFromRating(UserProfile userProfile, float rating) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromRating(getUID(userProfile), rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisited(UserProfile userProfile,
			int number) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromNumberOfTimesVisited(getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesFromRatingOfFriends(UserProfile userProfile,
			float rating) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromRatingOfFriends(getUID(userProfile), rating);
	}

	@Override
	public List<String> getEventNamesFromNumberOfTimesVisitedOfFriends(
			UserProfile userProfile, int number) {
		return ProfilerEventUtilsVirtuoso.getEventNamesFromNumberOfTimesVisitedOfFriends(getUID(userProfile), number);
	}

	@Override
	public List<String> getEventNamesWhichFriendsLikeToVisit(UserProfile userProfile) {
		return ProfilerEventUtilsVirtuoso.getEventNamesWhichFriendsLikeToVisit(getUID(userProfile));
	}

	@Override
	public GpsCoordinate getCoordinate(UserProfile userProfile) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getCoordinates(getUID(userProfile));
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
        String queryString = " prefix foaf: <http://xmlns.com/foaf/0.1/> \n"
        +" prefix profile: <http://3cixty.com/ontology/profile/> \n"
        +" prefix fn: <http://www.w3.org/2005/xpath-functions#> \n"
        + " SELECT ?uid ?mobidotID \n"
        + " FROM <" + VirtuosoManager.getInstance().getGraph("root") + "> \n"
        + " WHERE { \n"
        + " ?root profile:userID ?uid . \n"
        + " ?root foaf:account ?pi . \n"
        + " ?pi foaf:accountName ?mobidotID . \n"
        + " Filter(fn:ends-with(STR(?pi), \"Mobidot\")) . \n"
        + " }";
        Set<IDMapping> idMapping=new HashSet<IDMapping>();
        
        QueryReturnClass qRC;
		try {
			qRC = VirtuosoManager.getInstance().query(queryString);
			
	        ResultSet results = qRC.getReturnedResultSet();
	        
	        for ( ; results.hasNext(); ) {
	            QuerySolution qs = results.next();
	            RDFNode UID = qs.get("uid");
	            RDFNode mobidotID = qs.get("mobidotID");
	            if (UID!=null && mobidotID!=null){
	                IDMapping mapper=new IDMapping();
	                mapper.setThreeCixtyID(UID.toString());
	                mapper.setMobidotID(mobidotID.toString());
	                idMapping.add(mapper);
	            }
	        }
	        qRC.closeConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return idMapping;
	}
	/**
	 * get Crawl Times For the 3cixty Users
	 * @return
	 */
	private Set<IDCrawlTimeMapping> getCrawlTimesForUsers() {
        String queryString = " prefix foaf: <http://xmlns.com/foaf/0.1/> \n"
        +" prefix profile: <http://3cixty.com/ontology/profile/> \n"
        + " SELECT ?uid ?lastCrawlTime \n"
        + " FROM <" + VirtuosoManager.getInstance().getGraph("root") + "> \n"
        + " WHERE { \n"
        + " ?root profile:userID ?uid . \n"
        + " ?root profile:lastCrawlTime ?lastCrawlTime . \n"
        + " }";
        Set<IDCrawlTimeMapping> idCrawlTimeMapping=new HashSet<IDCrawlTimeMapping>();
        
        
        QueryReturnClass qRC;
		try {
			qRC = VirtuosoManager.getInstance().query(queryString);
			
	        ResultSet results = qRC.getReturnedResultSet();
	        
	        for ( ; results.hasNext(); ) {
	            QuerySolution qs = results.next();
	            RDFNode UID = qs.get("uid");
	            RDFNode lastCrawlTime = qs.get("lastCrawlTime");
	            if (UID!=null && lastCrawlTime!=null){
	                //UserProfile userProfile = qs.getLiteral("uid").getString();
	                //String lastCrawlTime = qs.getLiteral("lastCrawlTime").getString();
	                
	                IDCrawlTimeMapping mapper=new IDCrawlTimeMapping();
	                mapper.setThreeCixtyID(UID.toString());
	                mapper.setLastCrawlTime(lastCrawlTime.toString());
	                idCrawlTimeMapping.add(mapper);
	            }
	        }
	        qRC.closeConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return idCrawlTimeMapping;
	}

	@Override
	public Partner getPartner() {
		return PartnerImpl.getInstance();
	}

	@Override
	public TrayManager getTrayManager() {
		return VirtuosoTrayStorage.getInstance();
	}

	@Override
	public List<UserProfile> getAllUserProfiles() {
//		try {
//			return VirtuosoUserProfileStorage.getAllUserProfiles();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return null;
	}
	
	@Override
	public boolean checkAttributeToStore(Map<String, Boolean> attributes, String attribute) {
		// XXX: always use code from ProfileManagerImpl for this method.
		return false;
	}

	@Override
	public String find3cixtyUID(String uid, String source) {
		StringBuilder qStr = new StringBuilder(Configuration.PREFIXES);
	    qStr.append("\nSELECT  DISTINCT *\n");
	    qStr.append("FROM <" + VirtuosoManager.getInstance().getGraph(uid) + "> \n");
	    qStr.append("WHERE {\n\n");
	    qStr.append("?root profile:userID ?_3cixtyUid .\n");
    	
	    qStr.append("OPTIONAL { \n");
    	qStr.append("           ?root foaf:account ?pi. \n");
    	qStr.append("           ?pi foaf:accountName ?uid . \n");
    	qStr.append("         } \n");
    	
    	qStr.append("OPTIONAL { \n");
    	qStr.append("           ?root foaf:img ?profileImage . \n");
    	qStr.append("         } \n");

    	qStr.append("FILTER (str(?uid) = \"" + uid + "\") ");


	    qStr.append("}");
	    System.out.println(qStr.toString());
	    QueryReturnClass qRC = null;
	    String _3cixtyUID = null;
		try {
			qRC = VirtuosoManager.getInstance().query(qStr.toString());
			ResultSet results = qRC.getReturnedResultSet();
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				RDFNode tmpuid = qs.get("_3cixtyUid");
				if (tmpuid != null && !tmpuid.asLiteral().getString().equals("")) {
					_3cixtyUID = tmpuid.asLiteral().getString();
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (qRC != null) qRC.closeConnection();
		}
		return _3cixtyUID;
	}
	
	private String getUID(UserProfile userProfile) {
		if (userProfile == null) return null;
		return userProfile.getHasUID();
	}

	@Override
	public Set<String> find3cixtyUIDs(List<String> accountIds, String source,
			List <String> unfoundAccountIds) {
		//XXX: This function only works for Google & Facebook account
		// so, unfoundAccountIds list is always empty
		Set <String> _3cixtyUIDs = new HashSet <String>();
		for (String accountId: accountIds) {
			String _3cixtyUid = Utils.gen3cixtyUID(accountId,
					SPEConstants.GOOGLE_SOURCE.equals(source) ?
							UidSource.GOOGLE : UidSource.FACEBOOK);
			_3cixtyUIDs.add(_3cixtyUid);
		}
		return _3cixtyUIDs;
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

	@Override

	public void findPlaceIdsAndSocialScore(UserProfile arg0, float arg1,
			List<String> arg2, List<Double> arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findPlaceIdsAndSocialScoreForFriends(UserProfile arg0,
			float arg1, List<String> arg2, List<Double> arg3) {
		// TODO Auto-generated method stub
	}

	public List<Friend> findAll3cixtyFriendsHavingMyUIDInKnows(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Friend> findAllFriends(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

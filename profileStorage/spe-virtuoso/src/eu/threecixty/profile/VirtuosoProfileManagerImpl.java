package eu.threecixty.profile;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import virtuoso.jena.driver.VirtGraph;

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
	public String getCountryName(String uid) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getCountryName(uid);
	}

	@Override
	public String getTownName(String uid) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getTownName(uid);
	}

	@Override
	public List<String> getPlaceIdsFromRating(String uid, float rating) throws TooManyConnections {
		return ProfilerPlaceUtilsVirtuoso.getPlaceIdsFromRating(uid, rating);
	}

	@Override
	public List<String> getPlaceNamesFromNumberOfTimesVisited(String uid,
			int number) {
		return ProfilerPlaceUtilsVirtuoso.getPlaceNamesFromNumberOfTimesVisitedOfFriends(uid, number);
	}

	@Override
	public List<String> getPlaceIdsFromRatingOfFriends(String uid,
			float rating) throws TooManyConnections {
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
	public GpsCoordinate getCoordinate(String uid) throws TooManyConnections {
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
			VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			qRC = VirtuosoManager.getInstance().query(queryString, virtGraph);
			
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
	        VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
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
			VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			qRC = VirtuosoManager.getInstance().query(queryString, virtGraph);
			
	        ResultSet results = qRC.getReturnedResultSet();
	        
	        for ( ; results.hasNext(); ) {
	            QuerySolution qs = results.next();
	            RDFNode UID = qs.get("uid");
	            RDFNode lastCrawlTime = qs.get("lastCrawlTime");
	            if (UID!=null && lastCrawlTime!=null){
	                //String UID = qs.getLiteral("uid").getString();
	                //String lastCrawlTime = qs.getLiteral("lastCrawlTime").getString();
	                
	                IDCrawlTimeMapping mapper=new IDCrawlTimeMapping();
	                mapper.setThreeCixtyID(UID.toString());
	                mapper.setLastCrawlTime(lastCrawlTime.toString());
	                idCrawlTimeMapping.add(mapper);
	            }
	        }
	        qRC.closeConnection();
	        VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
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
	public String find3cixtyUID(String uid, String profileImage) {
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
    	if (profileImage != null && !profileImage.equals("")) {
    	    qStr.append("FILTER (str(?uid) = \"" + uid + "\" || str(?profileImage) = \"" + profileImage + "\") ");
    	} else {
    		qStr.append("FILTER (str(?uid) = \"" + uid + "\") ");
    	}

	    qStr.append("}");
	    System.out.println(qStr.toString());
	    QueryReturnClass qRC = null;
	    VirtGraph virtGraph = null;
	    String _3cixtyUID = null;
		try {
			virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			qRC = VirtuosoManager.getInstance().query(qStr.toString(), virtGraph);
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
			if (virtGraph != null) VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
		}
		return _3cixtyUID;
	}
}

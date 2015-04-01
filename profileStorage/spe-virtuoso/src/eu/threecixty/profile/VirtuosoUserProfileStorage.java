package eu.threecixty.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.threecixty.Configuration;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;

public class VirtuosoUserProfileStorage {
	
	private static final int EXPIRATION = 1000 * 60 * 60 * 4; // 4 hours, number in millisecond
	
	private static final Logger LOGGER = Logger.getLogger(
			 VirtuosoUserProfileStorage.class.getName());

	/**Attribute which is used to improve performance for logging out information*/
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	
	private static final String PROFILE_URI = "http://data.linkedevents.org/person/";
	
	private static volatile Map <String, VirtuosoUserProfileStorage> loadedStorages = new HashMap <String, VirtuosoUserProfileStorage>();
	private static volatile Map <String, Long> lastUsedTimes = new HashMap <String, Long>();
	
	private String uid;
	
	
	public static VirtuosoUserProfileStorage getInstance(String uid) {
		VirtuosoUserProfileStorage storage = loadedStorages.get(uid);
		if (storage == null) {
			synchronized (loadedStorages) {
				storage = loadedStorages.get(uid);
				if (storage == null) {
					storage = new VirtuosoUserProfileStorage();
					storage.uid = uid;
					loadedStorages.put(uid, storage);
				}
			}
		}
		synchronized (lastUsedTimes) {
			lastUsedTimes.put(uid, System.currentTimeMillis());
		}
		return storage;
	}

//	/**
//	 * Loads all user profiles. This API is intended to list all end users in the KB.
//	 * <br>
//	 * This API is not regularly called.
//	 * @return
//	 */
//	public List <UserProfile> getAllUserProfiles() {
//		List <UserProfile> allProfiles = new LinkedList <UserProfile>();
//		String query = Configuration.PREFIXES + " SELECT ?uid from <" + VirtuosoManager.getInstance().getGraph("") 
//				+ ">\n"
//				+ " WHERE { \n"
//				+ "?s profile:userID ?uid"
//				+ " }";
//		
//		
//		QueryReturnClass qrc = VirtuosoConnection.query(query);
//		ResultSet rs = qrc.getReturnedResultSet();
//		for ( ; rs.hasNext(); ) {
//			QuerySolution qs = rs.next();
//			String uid = qs.getLiteral("uid").toString();
//			if (uid != null && !uid.trim().equals("")) {
//				UserProfile userProfile = loadProfile(uid);
//				allProfiles.add(userProfile);
//			}
//		}
//		qrc.closeConnection();
//		return allProfiles;
//	}
	
	/**
	 * Loads profile information from the KB.
	 * @param uid
	 * @return
	 * @throws InterruptedException 
	 */	
	public synchronized eu.threecixty.profile.UserProfile loadProfile(Map <String, Boolean> attributes) throws InterruptedException {
		if (uid == null || uid.equals("")) return null;		
		//try {
			if (DEBUG_MOD) LOGGER.info("Start loading user profile");

			VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			
			eu.threecixty.profile.UserProfile toUserProfile = new eu.threecixty.profile.UserProfile();
			toUserProfile.setHasUID(uid);
			
			// load unique info for each profile: use a query instead of 5 ones to improve performance
			// since the following method loads five kinds of information, so we always load these kinds
			loadGenderNameImageAddressLastcrawl(virtGraph, uid, attributes, toUserProfile);

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PROFILE_IDENTITIES)) {
			    loadProfileIdentitiesFromUserProfile(virtGraph, uid, toUserProfile);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_KNOWS)) {
			    loadKnowsFromKBToUserProfile(virtGraph, uid, toUserProfile);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PREFERENCE)) {
			    loadPreferencesFromKBToUserProfile(virtGraph, uid, toUserProfile);
			}
			
			if (DEBUG_MOD) LOGGER.info("Finish loading user profile");
			
			VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
			
			return toUserProfile;
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error(e.getMessage());
//		}
//		return null;
	}

	/**
	 * Saves profile information to the KB.
	 * @param profile
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized boolean saveProfile(eu.threecixty.profile.UserProfile profile, Map <String, Boolean> attributes) throws InterruptedException {
		if (profile == null) return false;
		//try {

			if (DEBUG_MOD) LOGGER.info("begin saving user profile");
			
			List <String> queriesToInsertData = new LinkedList <String>();
			List <String> queriesToRemoveData = new LinkedList <String>();
			
			VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			VirtuosoUpdateRequest vurToInsertData = null;
			
			saveUIDInfoTOKB(profile.getHasUID(), queriesToInsertData);

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_GENDER)) {
			    saveGenderToKB(profile.getHasUID(),profile.getHasGender(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_NAME)) {
			    saveNameInfoToKB(profile.getHasUID(),profile.getHasName(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PROFILE_IMAGE)) {
			    saveProfileImage(profile.getHasUID(), profile.getProfileImage(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_ADDRESS)) {
			    saveAddressInfoToKB(profile.getHasUID(),profile.getHasAddress(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_LAST_CRAWL_TIME)) {
			    saveLastCrawlTimeToKB(profile.getHasUID(), profile.getHasLastCrawlTime(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PROFILE_IDENTITIES)) {
			    saveProfileIdentitiesToKB(profile.getHasUID(), profile.getHasProfileIdenties(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_KNOWS)) {
			    saveKnowsToKB(virtGraph, profile.getHasUID(), profile.getKnows(), queriesToRemoveData, queriesToInsertData);
			}

			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PREFERENCE)) {
			    savePreferenceToKB(profile.getHasUID(), profile.getPreferences(), queriesToRemoveData, queriesToInsertData);
			}
			
			if (ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_TRANSPORT)) {
			    if (profile.getPreferences()==null)
				    saveTransportToKB(virtGraph, profile.getHasUID(), null, queriesToRemoveData, queriesToInsertData);
			    else
				    saveTransportToKB(virtGraph, profile.getHasUID(), profile.getPreferences().getHasTransport(), queriesToRemoveData, queriesToInsertData);
			}

			VirtuosoUpdateRequest vurToRemoveData = null;
			for (String query: queriesToRemoveData) {
				if (DEBUG_MOD) LOGGER.info("query to remove data: " + query);
				if (vurToRemoveData == null) vurToRemoveData = VirtuosoUpdateFactory.create(query, virtGraph);
				else vurToRemoveData.addUpdate(query);
			}
			if (vurToRemoveData != null) vurToRemoveData.exec();

			for (String query: queriesToInsertData) {
				if (DEBUG_MOD) LOGGER.info("query to insert data: " + query);
				if (vurToInsertData == null) vurToInsertData = VirtuosoUpdateFactory.create(query, virtGraph);
				else vurToInsertData.addUpdate(query);
			}

			if (vurToInsertData != null) vurToInsertData.exec();

			VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
			
			if (DEBUG_MOD) LOGGER.info("end saving user profile");
			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error(e.getMessage());
//		}
		//return false;
	}

	private void saveUIDInfoTOKB( String uid, List <String> queries) throws InterruptedException {

		if (!existUID()) {
			// comment the following line as we only have one private graph
			//VirtuosoManager.getInstance().createAccount(uid);
			String insertQuery = GetSetQueryStrings.setUser(uid);
			queries.add(insertQuery);
		}
	}

	/**
	 * Adds knows information to KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 * @throws InterruptedException 
	 */
	private void saveKnowsToKB(VirtGraph virtGraph, String uid, Set <String> knows, List <String> queriesToRemoveData,
			List <String> queriesToInsertData)  throws InterruptedException {

		String str = GetSetQueryStrings.removeAllKnows(uid);
		queriesToRemoveData.add(str);
		
		if (knows == null || knows.size() == 0) return;
		
		//add new knows profiles
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String know=iterators.next();

			QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getUserURI(know), virtGraph); // need to refactor here

			ResultSet results = qRC.getReturnedResultSet();
			if (!results.hasNext()){

				try {

					str=GetSetQueryStrings.setUser(know);
					queriesToInsertData.add(str);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			qRC.closeConnection();
		}

		str = GetSetQueryStrings.setMultipleKnows(uid,knows);
		queriesToInsertData.add(str);
	}

	/**
	 * Adds profile identities found in a given instance of userprofile into the KB.
	 * @param fromUserProfile
	 * @param mf
	 * @param kbUserProfile
	 */
	private void saveProfileIdentitiesToKB(String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
		
		String str = GetSetQueryStrings.removeAllProfileIdentitiesOfUser(uid);
		queriesToRemoveData.add(str);
		
		if (profileIdentities!=null&&!profileIdentities.isEmpty()){
			str = GetSetQueryStrings.setMultipleProfileIdentities(uid, profileIdentities);
			queriesToInsertData.add(str);
		}
		return;
	}

	/**
	 * add gender
	 * @param uid
	 * @param time
	 */
	private void saveGenderToKB(String uid, String gender,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
		String str = GetSetQueryStrings.removeGender(uid);
		queriesToRemoveData.add(str);
		if (gender!=null){
			str = GetSetQueryStrings.setGender(uid, gender);
			queriesToInsertData.add(str);
		}
	}
	
	/**
	 * add last crawl time
	 * @param uid
	 * @param time
	 */
	private void saveLastCrawlTimeToKB(String uid, String time, List <String> queriesToRemoveData,
			List <String> queriesToInsertData) {
		
		String str = GetSetQueryStrings.removeLastCrawlTime(uid);
		queriesToRemoveData.add(str);
		
		str = GetSetQueryStrings.setLastCrawlTime(uid, time);
		queriesToInsertData.add(str);
	}
	
	/**
	 * add likes to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param likes
	 */
	private void saveLikesToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference,
			List <String> queriesToRemoveData, List <String> queriesToInsertData){//Set<eu.threecixty.profile.oldmodels.Likes> likes) {
		
		String str = GetSetQueryStrings.removeAllLikesOfUser(uid);
		queriesToRemoveData.add(str);

		if (preference!=null){//likes!=null&&!likes.isEmpty()){
			Set<eu.threecixty.profile.oldmodels.Likes> likes=preference.getHasLikes();
			if (likes == null || likes.size() == 0) return;
			str = GetSetQueryStrings.setMultipleLikes(uid, likes);
			queriesToInsertData.add(str);
		}
		return;
	}
	
	/**
	 * TODO: Note that this method checks the newForKB attribute for each corresponding class to
	 * decide whether or not an instance is new to store.
	 *
	 * Saves preferences into the KB.
	 * @param preference
	 * @param kbUserProfile
	 * @param mf
	 */
	private void savePreferenceToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
			//if (preference!=null){
				
				saveLikesToKB(uid,  preference, queriesToRemoveData, queriesToInsertData);//.getHasLikes());
				
				saveTripPreferenceToKB(uid, preference, queriesToRemoveData, queriesToInsertData);//.getHasTripPreference());
				 
				savePlacePreferenceToKB(uid, preference, queriesToRemoveData, queriesToInsertData);//.getHasPlacePreference());
			//}
	}

	/**
	 * add place preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param placePreferences
	 */
	private void savePlacePreferenceToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference,
			List <String> queriesToRemoveData, List <String> queriesToInsertData){//Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences) {
		
		String str = GetSetQueryStrings.removePlacePreferences(uid);
		queriesToRemoveData.add(str);
		if(preference!=null){
			Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences=preference.getHasPlacePreference();
			if (placePreferences!=null&&!placePreferences.isEmpty()){
				Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators=placePreferences.iterator();
				for ( ; iterators.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
					str = GetSetQueryStrings.setPlacePreferences(uid, placePreference);
					queriesToInsertData.add(str);
				}
			}						
		}
		return;
	}

	/**
	 * add trip preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param tripPreferences
	 */
	private void saveTripPreferenceToKB(String uid,eu.threecixty.profile.oldmodels.Preference preference,
			List <String> queriesToRemoveData, List <String> queriesToInsertData){// Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences) {
		
		String str = GetSetQueryStrings.removeMultipleTripPreferences(uid);
		queriesToRemoveData.add(str);
		if (preference!=null){
		
			Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences=preference.getHasTripPreference();
			if (tripPreferences!=null&& tripPreferences.size() > 0){
				str = GetSetQueryStrings.setMultipleTripPreferences(uid, tripPreferences);
				
				queriesToInsertData.add(str);
			}
		}
		return;
	}
	
	/**
	 * add transport in the KB.
	 * @param uid
	 * @param transport
	 * @param kbUserProfile
	 * @param mf
	 * @throws InterruptedException 
	 */
	private void saveTransportToKB(VirtGraph virtGraph, String uid, Set<eu.threecixty.profile.oldmodels.Transport> transports,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) throws InterruptedException {

		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getTransport(uid), virtGraph);
		
		ResultSet results = qRC.getReturnedResultSet();

		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			RDFNode transport = qs.get("transport");

			if (transport==null) break;


			String str = GetSetQueryStrings.removeMultiplePersonalPlacesAssociatedToATransport(uid, transport.asResource().getURI());
            		queriesToRemoveData.add(str);
			
			str = GetSetQueryStrings.removeMultipleRegularTripsAssociatedToSpecificTransport(uid, transport.asResource().getURI());

			queriesToRemoveData.add(str);

			str = GetSetQueryStrings.removeMultipleAccompanyingAssociatedToSpecificTransport(uid, transport.asResource().getURI());
			queriesToRemoveData.add(str);

		}
		qRC.closeConnection();
		
		String str = GetSetQueryStrings.removeTransport(uid);
		queriesToRemoveData.add(str);
		
		if (transports!=null&&!transports.isEmpty()){
			Iterator<eu.threecixty.profile.oldmodels.Transport> iterators=transports.iterator();
			for ( ; iterators.hasNext(); ){ 
				eu.threecixty.profile.oldmodels.Transport transport=iterators.next();
				if (transport.getHasTransportURI()==null||transport.getHasTransportURI().isEmpty()) {
					transport.setHasTransportURI(PROFILE_URI+uid+"/Mobility/"+UUID.randomUUID().toString());
				}
				
				if (transport.getHasAccompanyings()!=null&&!transport.getHasAccompanyings().isEmpty()){
					str = GetSetQueryStrings.setMultipleAccompanyingAssociatedToSpecificTransport(uid, transport.getHasTransportURI(), transport.getHasAccompanyings());
					queriesToInsertData.add(str);
				}
				
				Set <eu.threecixty.profile.oldmodels.RegularTrip> setRegTrip=transport.getHasRegularTrip();
				
				Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iteratorsRegularTrip=setRegTrip.iterator();
				for ( ; iteratorsRegularTrip.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.RegularTrip regularTrip=iteratorsRegularTrip.next();
					if (regularTrip.getHasRegularTripURI()==null||regularTrip.getHasRegularTripURI().isEmpty()) {
						regularTrip.setHasRegularTripURI(transport.getHasTransportURI()+"/RegularTrip/"+UUID.randomUUID().toString());
					}
					if (regularTrip.getHasPersonalPlacesNew()!=null&&!regularTrip.getHasPersonalPlacesNew().isEmpty()){
						str = GetSetQueryStrings.setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(uid, regularTrip.getHasRegularTripURI(), regularTrip.getHasPersonalPlacesNew());
						queriesToInsertData.add(str);
					}
				}
				if ( transport.getHasRegularTrip()!=null&&!transport.getHasRegularTrip().isEmpty()){
					str = GetSetQueryStrings.setMultipleRegularTripsAssociatedToSpecificTransport(uid, transport.getHasTransportURI(), transport.getHasRegularTrip());
					queriesToInsertData.add(str);
				}
				
				str = GetSetQueryStrings.setTransport(uid, transport.getHasTransportURI());
				queriesToInsertData.add(str);
			}		
		}
	}

	/**
	 * Saves name information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private void saveNameInfoToKB(String uid, eu.threecixty.profile.oldmodels.Name name,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
		
		String str = GetSetQueryStrings.removeName(uid);
		queriesToRemoveData.add(str);
		
		if (name!=null){
			//name.setHasNameURI(PROFILE_URI+uid+"/Name");
			str = GetSetQueryStrings.setName(uid, name);
			queriesToInsertData.add(str);
		}
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private void saveAddressInfoToKB(String uid,	eu.threecixty.profile.oldmodels.Address address,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
	
		String str = GetSetQueryStrings.removeAddress(uid);
		queriesToRemoveData.add(str);
		
		if (address!=null){
			address.setHasAddressURI(PROFILE_URI+uid+"/Address");
			str = GetSetQueryStrings.setAddress(uid, address);
			queriesToInsertData.add(str);
		}
	}

	/**
	 * Loads likes from the KB to a preference instance.
	 * @param from
	 * @param to
	 * @throws InterruptedException 
	 */
	private void loadLikesFromKBToPreference(VirtGraph virtGraph, String uid, eu.threecixty.profile.oldmodels.Preference to) throws InterruptedException {

		Set <eu.threecixty.profile.oldmodels.Likes> toLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();

		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getLikes(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				//?likes ?likeName ?liketype
				eu.threecixty.profile.oldmodels.Likes oldLikes = new eu.threecixty.profile.oldmodels.Likes();
				RDFNode likeURI = qs.get("likes");
				RDFNode likeName = qs.get("likeName");
				RDFNode liketype = qs.get("liketype");
			    if (likeURI!=null)
			    	oldLikes.setHasLikesURI(likeURI.asResource().getURI());
			    if (likeName!=null)
			    	oldLikes.setHasLikeName(likeName.toString());
			    if (liketype!=null)
			    	oldLikes.setHasLikeType(eu.threecixty.profile.oldmodels.LikeType.valueOf(liketype.toString()));	
			   toLikes.add(oldLikes);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		to.setHasLikes(toLikes);
		qRC.closeConnection();
		return;
	}
	
	/**
	 * Loads unique info (gender, name, profile image, address, last crawl time) for each user profile to
	 * avoid using 5 queries for performance improvement.
	 * @param uid
	 * @param toUserProfile
	 * @throws InterruptedException 
	 */
	private void loadGenderNameImageAddressLastcrawl(VirtGraph virtGraph, String uid, Map <String, Boolean> attributes,
			UserProfile toUserProfile) throws InterruptedException {
		if (!ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_GENDER)
				&& !ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_NAME)
				&& !ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_PROFILE_IMAGE)
				&& !ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_ADDRESS)
				&& !ProfileManagerImpl.getInstance().checkAttributeToStore(attributes, ProfileManager.ATTRIBUTE_LAST_CRAWL_TIME)) {
			return;
		}
		if (DEBUG_MOD) LOGGER.info(GetSetQueryStrings.createQueryToGetGenderNameImageAddressLastcrawl(uid));
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(
				GetSetQueryStrings.createQueryToGetGenderNameImageAddressLastcrawl(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		Address toAddress = null;
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				// Gender info
				RDFNode gender = qs.get("gender");
			    if (gender!=null) toUserProfile.setHasGender(gender.toString());	
				
			    // Name info
				RDFNode gn = qs.get("givenname");
				RDFNode fn = qs.get("familyname");
				if (gn != null || fn != null) {
					Name toName = new Name();
					toUserProfile.setHasName(toName);
					if (gn != null) toName.setGivenName(gn.toString());
					if (fn != null) toName.setFamilyName(fn.toString());
				}
				
				// Profile Image info
				RDFNode in = qs.get("profileImage");
				if (in != null) toUserProfile.setProfileImage(in.toString());
			    
				// Address info
				RDFNode addressuri = qs.get("address");
				RDFNode cname = qs.get("countryname");
			    RDFNode tname = qs.get("townname");
			    RDFNode lon = qs.get("longitude");
			    RDFNode lat = qs.get("lat");
			    if (addressuri!=null) {
			    	toAddress = new Address();
			    	toUserProfile.setHasAddress(toAddress);
			    	toAddress.setHasAddressURI(addressuri.asResource().getURI());
			    }
			    if (cname!=null) toAddress.setCountryName(cname.toString());
			    if (tname!=null) toAddress.setTownName(tname.toString());
			    try {
			        if (lon!=null) toAddress.setLongitute(Double.parseDouble(lon.asLiteral().getString()));
			    } catch (Exception e) {}
			    try {
			        if (lat!=null) toAddress.setLatitude(Double.parseDouble(lat.asLiteral().getString()));
			    } catch (Exception e) {}
			    
			    // Last crawl info
			    RDFNode lcn = qs.get("lastCrawlTime");
			    if (lcn != null) toUserProfile.setHasLastCrawlTime(lcn.toString());	
			    
			    break;
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		qRC.closeConnection();
	}
	
	/**
	 * Loads profile identities from a given user profile to a given settings instance.
	 * @param fromUserProfile
	 * @param toUserProfile
	 * @throws InterruptedException 
	 */
	private void loadProfileIdentitiesFromUserProfile(VirtGraph virtGraph, String uid, eu.threecixty.profile.UserProfile toUserProfile) throws InterruptedException {
		
		Set <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = new HashSet <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getProfileIdentities(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				
				eu.threecixty.profile.oldmodels.ProfileIdentities tmpProfile = new eu.threecixty.profile.oldmodels.ProfileIdentities();
				RDFNode uri = qs.get("pi");
				RDFNode piID = qs.get("piID");
				RDFNode uIM = qs.get("uIM");
			    
			    if (uri!=null)
			    	tmpProfile.setHasProfileIdentitiesURI(uri.asResource().getURI());
			    if (piID!=null)
			    	tmpProfile.setHasUserAccountID(piID.toString());	
			    if (uIM!=null)
			    	tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uIM.toString()));	
			    
			    oldProfiles.add(tmpProfile); 
			
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
			
		}
		toUserProfile.setHasProfileIdenties(oldProfiles);
		qRC.closeConnection();
		return;
	}

	/**
	 * Loads knows information in the KB to a given instance of user profile.
	 * @param fromUserProfile
	 * @param toUserProfile
	 * @throws InterruptedException 
	 */
	private void loadKnowsFromKBToUserProfile(VirtGraph virtGraph, String uid, eu.threecixty.profile.UserProfile toUserProfile) throws InterruptedException {
		
		Set <String> knows = new HashSet <String>();
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getKnows(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				
			    RDFNode uidknows = qs.get("uidknows");
			    
			    if (uidknows!=null) {
			    	String tmp = uidknows.toString();
			    	int index = tmp.indexOf(PROFILE_URI);
			    	if (index < 0) knows.add(tmp);
			    	else knows.add(tmp.substring(index + PROFILE_URI.length()));
			    }
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
			
		}
		toUserProfile.setKnows(knows);
		qRC.closeConnection();
		return;
	}

	private void loadPreferencesFromKBToUserProfile(VirtGraph virtGraph, String uid, eu.threecixty.profile.UserProfile toUserProfile) throws InterruptedException {
		eu.threecixty.profile.oldmodels.Preference toPrefs = new eu.threecixty.profile.oldmodels.Preference();

		loadLikesFromKBToPreference(virtGraph, uid,toPrefs);
		loadTripPreferencesFromKBToPreferences(virtGraph, uid, toPrefs);
		loadPlacePreferencesFromKBToPreferences(virtGraph, uid, toPrefs);
		loadTransportFromKBToPreferences(virtGraph, uid,toPrefs);
						
		toUserProfile.setPreferences(toPrefs);
						
		}
	/**
	 * Loads transport from the KB.
	 * @param userProfile
	 * @param toPrefs
	 * @throws InterruptedException 
	 */
	private void loadTransportFromKBToPreferences(VirtGraph virtGraph, String uid, eu.threecixty.profile.oldmodels.Preference toPrefs) throws InterruptedException {
		
		Set <eu.threecixty.profile.oldmodels.Transport> toTransports = new HashSet <eu.threecixty.profile.oldmodels.Transport>();
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getTransport(uid), virtGraph);
		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				RDFNode transport = qs.get("transport");

				if (transport==null) return;
				
				eu.threecixty.profile.oldmodels.Transport toTransport = new eu.threecixty.profile.oldmodels.Transport();
				
				toTransport.setHasTransportURI(transport.asResource().getURI());
				
				QueryReturnClass qRCRegularTrips = VirtuosoManager.getInstance().query(
						GetSetQueryStrings.getRegularTripsForTransport(uid,transport.asResource().getURI()), virtGraph);
				ResultSet resultsRegularTrips = qRCRegularTrips.getReturnedResultSet();
				
				Set <eu.threecixty.profile.oldmodels.RegularTrip> toRegularTrips = new HashSet <eu.threecixty.profile.oldmodels.RegularTrip>();
				
				for ( ; resultsRegularTrips.hasNext(); ) {
					QuerySolution qsRegularTrips = resultsRegularTrips.next();
					try {
						eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip = new eu.threecixty.profile.oldmodels.RegularTrip();
						loadRegularTripFromKB(virtGraph, qsRegularTrips, toRegularTrip);
				    	toRegularTrips.add(toRegularTrip);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.getMessage());
					}
				}
				
				qRCRegularTrips.closeConnection();
				
				QueryReturnClass qRCAccompanying = VirtuosoManager.getInstance().query(
						GetSetQueryStrings.getAccompanyingForTransport(uid,transport.asResource().getURI()), virtGraph);
				ResultSet resultsAccompanying = qRCAccompanying.getReturnedResultSet();
				
				Set <eu.threecixty.profile.oldmodels.Accompanying> toAccompanyings = new HashSet <eu.threecixty.profile.oldmodels.Accompanying>();
				
				for ( ; resultsAccompanying.hasNext(); ) {
					QuerySolution sAccompanying = resultsAccompanying.next();
					try {
						eu.threecixty.profile.oldmodels.Accompanying toAccompanying = new eu.threecixty.profile.oldmodels.Accompanying();
						loadAccompanyingFromKB(sAccompanying,uid,toAccompanying);
				    	toAccompanyings.add(toAccompanying);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.getMessage());
					}
					
				}
				qRCAccompanying.closeConnection();
			
				toTransport.setHasAccompanyings(toAccompanyings);
				toTransport.setHasRegularTrip(toRegularTrips);
				toTransports.add(toTransport);
				
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		
		toPrefs.setHasTransport(toTransports);
		qRC.closeConnection();
		return;
	}

	/**
	 * Loads regular trip from the KB.
	 * @param uid
	 * @param qs
	 * @param toRegularTrip
	 * @throws InterruptedException 
	 */
	private void loadRegularTripFromKB(VirtGraph virtGraph, QuerySolution qs,
			eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip) throws InterruptedException {
		
		RDFNode regularTripURI = qs.get("regularTrip");
		RDFNode tripID = qs.get("tripID");
		RDFNode name = qs.get("name");
		RDFNode departureTime = qs.get("departureTime");
		RDFNode departureTimeSD = qs.get("departureTimeSD");
		RDFNode travelTime = qs.get("travelTime");
		RDFNode travelTimeSD = qs.get("travelTimeSD");
		//RDFNode fastestTravelTime = qs.get("fastestTravelTime");
		RDFNode lastChanged = qs.get("lastChanged");
		RDFNode totalDistance = qs.get("totalDistance");
		RDFNode totalCount = qs.get("totalCount");
		RDFNode modalityType = qs.get("modalityType");
		RDFNode weekdayPattern = qs.get("weekdayPattern");
		RDFNode dayhourPattern = qs.get("dayhourPattern");
		//RDFNode timePattern = qs.get("timePattern");
		RDFNode weatherPattern = qs.get("weatherPattern");
	    
	    if (regularTripURI!=null)
    		toRegularTrip.setHasRegularTripURI(regularTripURI.asResource().getURI());
	    if (tripID!=null)
	    	toRegularTrip.setHasRegularTripId(tripID.asLiteral().getLong());
    	if (name!=null)
    		toRegularTrip.setHasRegularTripName(name.toString());
    	if (departureTime!=null)
    		toRegularTrip.setHasRegularTripDepartureTime(departureTime.asLiteral().getLong());
    	if (departureTimeSD!=null)
    		toRegularTrip.setHasRegularTripDepartureTimeSD(departureTimeSD.asLiteral().getLong());
    	if (travelTime!=null)
    		toRegularTrip.setHasRegularTripTravelTime(travelTime.asLiteral().getLong());
    	if (travelTimeSD!=null)
    		toRegularTrip.setHasRegularTripTravelTimeSD(travelTimeSD.asLiteral().getLong());
    	//if (fastestTravelTime!=null)
    	//	toRegularTrip.setHasRegularTripFastestTravelTime(fastestTravelTime.asLiteral().getLong());
    	if (lastChanged!=null)
    		toRegularTrip.setHasRegularTripLastChanged(lastChanged.asLiteral().getLong());
    	if (totalDistance!=null)
    		toRegularTrip.setHasRegularTripTotalDistance(totalDistance.asLiteral().getDouble());
    	if (totalCount!=null)
    		toRegularTrip.setHasRegularTripTotalCount(totalCount.asLiteral().getLong());
    	if (modalityType!=null)
    		toRegularTrip.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(modalityType.toString()));
    	if (weekdayPattern!=null)
    		toRegularTrip.setHasRegularTripWeekdayPattern(weekdayPattern.toString());
    	if (dayhourPattern!=null)
    		toRegularTrip.setHasRegularTripDayhourPattern(dayhourPattern.toString());
    	//if (timePattern!=null)
    	//    	toRegularTrip.setHasRegularTripTravelTimePattern(timePattern.toString());
    	if (weatherPattern!=null)
    		toRegularTrip.setHasRegularTripWeatherPattern(weatherPattern.toString());
    	
    	Set <eu.threecixty.profile.oldmodels.PersonalPlace> toPersonalPlaces = new HashSet <eu.threecixty.profile.oldmodels.PersonalPlace>();
		
    	if (regularTripURI!=null)
    		loadPersonalPlaceFromKBToRegularTrips(virtGraph, regularTripURI.asResource().getURI(),toPersonalPlaces);
    	
    	toRegularTrip.setHasPersonalPlacesNew(toPersonalPlaces);
	}

	/**
	 * Loads personal place from the KB.
	 * @param uid
	 * @param personalPlace
	 * @param toPersonalPlace
	 * @throws InterruptedException 
	 */
	private void loadPersonalPlaceFromKBToRegularTrips(VirtGraph virtGraph, String regularTripURI,
			Set <eu.threecixty.profile.oldmodels.PersonalPlace> toPersonalPlaces) throws InterruptedException {

		QueryReturnClass qRC = VirtuosoManager.getInstance().query(
				GetSetQueryStrings.getPersonalPlacesForRegularTrips(uid,regularTripURI), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				//?pplace ?ID ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage 
				//?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				eu.threecixty.profile.oldmodels.PersonalPlace toPersonalPlace = new eu.threecixty.profile.oldmodels.PersonalPlace();
				
				RDFNode externalIDs = qs.get("externalIDs");
				RDFNode latitude = qs.get("latitude");
				RDFNode longitude = qs.get("longitude");
				RDFNode stayDuration = qs.get("stayDuration");
				RDFNode accuracy = qs.get("accuracy");
				RDFNode stayPercentage = qs.get("stayPercentage");
				RDFNode pcode = qs.get("pcode");
				RDFNode weekDayPattern = qs.get("weekDayPattern");
				RDFNode dayHourPattern = qs.get("dayHourPattern");
				RDFNode placeType = qs.get("placeType");
				RDFNode placeName = qs.get("placeName");
			    
			    
			    if (externalIDs!=null)
			    	toPersonalPlace.setHasPersonalPlaceexternalIds(externalIDs.toString());
		    	if (latitude!=null)
		    		toPersonalPlace.setLatitude(latitude.asLiteral().getDouble());
				if (longitude!=null)
					toPersonalPlace.setLongitude(longitude.asLiteral().getDouble());
				if (stayDuration!=null)
					 toPersonalPlace.setHasPersonalPlaceStayDuration(stayDuration.asLiteral().getLong());
				if (accuracy!=null)
					 toPersonalPlace.setHasPersonalPlaceAccuracy(accuracy.asLiteral().getDouble());
				if (stayPercentage!=null)
					toPersonalPlace.setHasPersonalPlaceStayPercentage(stayPercentage.asLiteral().getDouble());
				if (pcode!=null)
					toPersonalPlace.setPostalcode(pcode.toString());
				if (weekDayPattern!=null)
					toPersonalPlace.setHasPersonalPlaceWeekdayPattern(weekDayPattern.toString());
				if (dayHourPattern!=null)
					toPersonalPlace.setHasPersonalPlaceDayhourPattern(dayHourPattern.toString());
				if (placeType!=null)
					toPersonalPlace.setHasPersonalPlaceType(placeType.toString());
				if (placeName!=null)
					toPersonalPlace.setHasPersonalPlaceName(placeName.toString());
			    							
				toPersonalPlaces.add(toPersonalPlace);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
			
		}
		qRC.closeConnection();
		return;
	}

	/**
	 * Loads Accompanying from the KB.
	 * @param accompanying
	 * @param toAccompanying
	 */
	private void loadAccompanyingFromKB(QuerySolution qs, String uid,
			eu.threecixty.profile.oldmodels.Accompanying toAccompanying) {
		RDFNode accompanyid = qs.get("accompany");
	    RDFNode uid2 = qs.get("uid2");
	    RDFNode score = qs.get("score");
	    RDFNode validity = qs.get("validity");
	    RDFNode acctime = qs.get("acctime");
	    
	    if (accompanyid!=null)
	    	toAccompanying.setHasAccompanyURI(accompanyid.asResource().getURI());
	    if (uid2!=null)
	    	toAccompanying.setHasAccompanyUserid2ST(uid2.toString());
	    	toAccompanying.setHasAccompanyUserid1ST(uid.toString());
    	if (score!=null)
		   	toAccompanying.setHasAccompanyScore(score.asLiteral().getDouble());
    	if (validity!=null)
		   	toAccompanying.setHasAccompanyValidity(validity.asLiteral().getLong());
    	if (acctime!=null)
		   	toAccompanying.setHasAccompanyTime(acctime.asLiteral().getLong());
	}

	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 * @throws InterruptedException 
	 */
	private void loadTripPreferencesFromKBToPreferences(VirtGraph virtGraph, String uid, eu.threecixty.profile.oldmodels.Preference to) throws InterruptedException {
		
		Set <eu.threecixty.profile.oldmodels.TripPreference> tripPreferences = new HashSet <eu.threecixty.profile.oldmodels.TripPreference>();
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getTripPreferences(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				//?tripPreference ?preferredMaxTotalDistance ?preferredTripDuration ?preferredTripTime 
				//?preferredCity ?preferredCountry ?preferredWeatherCondition 
				//?preferredMinTimeOfAccompany ?modality 
				eu.threecixty.profile.oldmodels.TripPreference tripPreference = new eu.threecixty.profile.oldmodels.TripPreference();
				RDFNode tripPreferenceURI = qs.get("tripPreference");
				RDFNode preferredMaxTotalDistance = qs.get("preferredMaxTotalDistance");
				RDFNode preferredTripDuration = qs.get("preferredTripDuration");
				RDFNode preferredTripTime = qs.get("preferredTripTime");
				RDFNode preferredCity = qs.get("preferredCity");
				RDFNode preferredCountry = qs.get("preferredCountry");
				RDFNode preferredWeatherCondition = qs.get("preferredWeatherCondition");
				RDFNode preferredMinTimeOfAccompany = qs.get("preferredMinTimeOfAccompany");
				RDFNode modality = qs.get("modality");
			    
			    if (tripPreferenceURI!=null)
			    	tripPreference.setHasTripPreferenceURI(tripPreferenceURI.asResource().getURI());
			    if (preferredMaxTotalDistance!=null)
			    	tripPreference.setHasPreferredMaxTotalDistance(preferredMaxTotalDistance.asLiteral().getDouble());
			    if (preferredTripDuration!=null)
			    	tripPreference.setHasPreferredTripDuration(preferredTripDuration.asLiteral().getLong());
			    if (preferredTripTime!=null)
			    	tripPreference.setHasPreferredTripTime(preferredTripTime.asLiteral().getLong());
			    if (preferredCity!=null)
			    	tripPreference.setHasPreferredCity(preferredCity.toString());
			    if (preferredCountry!=null)
			    	tripPreference.setHasPreferredCountry(preferredCountry.toString());
			    if (preferredWeatherCondition!=null)
			    	tripPreference.setHasPreferredWeatherCondition(preferredWeatherCondition.toString());
			    if (preferredMinTimeOfAccompany!=null)
			    	tripPreference.setHasPreferredMinTimeOfAccompany(preferredMinTimeOfAccompany.asLiteral().getLong());
			    if (modality!=null)
			    	tripPreference.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(modality.toString()));
			    
			    
			    tripPreferences.add(tripPreference);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		to.setHasTripPreference(tripPreferences);
		qRC.closeConnection();
		return;
	}
	
	/**
	 * load place preference from KB 
	 * @param uid
	 * @param to
	 * @throws InterruptedException 
	 */
	private void loadPlacePreferencesFromKBToPreferences(VirtGraph virtGraph, String uid, eu.threecixty.profile.oldmodels.Preference to) throws InterruptedException {
		
		Set <eu.threecixty.profile.oldmodels.PlacePreference> placePreferences = new HashSet <eu.threecixty.profile.oldmodels.PlacePreference>();
		QueryReturnClass qRC = VirtuosoManager.getInstance().query(GetSetQueryStrings.getPlacePreferences(uid), virtGraph);

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				//?placePreference ?placeDetailPreference 
				eu.threecixty.profile.oldmodels.PlacePreference placePreference = new eu.threecixty.profile.oldmodels.PlacePreference();
				RDFNode placePreferenceURI = qs.get("placePreference");
				RDFNode natureOfPlace = qs.get("natureOfPlace");
			    
			    if (placePreferenceURI!=null)
			    	placePreference.setHasPlacePreferenceURI(placePreferenceURI.asResource().getURI());
			    if (natureOfPlace!=null){
			    	loadPlaceDetailPreferenceFromKBToPlacePreference(natureOfPlace.asLiteral().getString(),placePreference);
			    }
			    placePreferences.add(placePreference);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		to.setHasPlacePreference(placePreferences);
		qRC.closeConnection();
		return;
	}

	/**
	 * load Place detail preferences from kb
	 * @param placeDetailPreferenceURI
	 * @param placePreference
	 */
	private void loadPlaceDetailPreferenceFromKBToPlacePreference(
			String natureOfPlace,
			eu.threecixty.profile.oldmodels.PlacePreference placePreference) {
		eu.threecixty.profile.oldmodels.PlaceDetailPreference placeDetailPreference= new eu.threecixty.profile.oldmodels.PlaceDetailPreference();

	 	if (natureOfPlace!=null)
	    	placeDetailPreference.setHasNatureOfPlace(eu.threecixty.profile.oldmodels.NatureOfPlace.valueOf(natureOfPlace));
		placePreference.setHasPlaceDetailPreference(placeDetailPreference);
	}

	/**
	 * Saves a given profileImage link into Virtuoso.
	 * @param uid
	 * @param profileImageLink
	 */
	private void saveProfileImage(String uid, String profileImageLink,
			List <String> queriesToRemoveData, List <String> queriesToInsertData) {
		if (profileImageLink == null || profileImageLink.equals("")) return;
		String queryToDeleteOldValue = GetSetQueryStrings.createQueryToDeleteProfileImage(uid);
		queriesToRemoveData.add(queryToDeleteOldValue);

		String queryToInsertValue = GetSetQueryStrings.createQueryToInsertProfileImage(uid, profileImageLink);
		queriesToInsertData.add(queryToInsertValue);
	}
	
	/**
	 * Checks whether or not a given UID exists in the UserProfile.
	 * @param uid
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean existUID() throws InterruptedException {
		if (uid == null) return false;
		StringBuilder qStr = new StringBuilder(Configuration.PROFILE_PREFIX);
	    qStr.append("SELECT  DISTINCT  ?uid\n");
	    qStr.append("FROM <" + VirtuosoManager.getInstance().getGraph(uid) + "> \n");
	    qStr.append("WHERE {\n\n");
	    qStr.append("?root profile:userID ?uid .\n");
	    qStr.append("FILTER (STR(?uid) = \"" + uid + "\") . \n\n");
	    qStr.append("}");
	    VirtGraph virtGraph = VirtuosoManager.getInstance().getVirtGraph();
	    QueryReturnClass qRC = VirtuosoManager.getInstance().query(qStr.toString(), virtGraph);
		ResultSet results = qRC.getReturnedResultSet();
		boolean found = false;
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			RDFNode tmpuid = qs.get("uid");
			if (tmpuid != null && !tmpuid.asLiteral().getString().equals("")) {
				found = true;
				break;
			}
		}
		qRC.closeConnection();
		VirtuosoManager.getInstance().releaseVirtGraph(virtGraph);
		if (DEBUG_MOD) {
			if (found) LOGGER.info("Found UID = " + uid + " in Virtuoso");
			else LOGGER.info("Not found UID = " + uid + " in Virtuoso. Here is the sparql query: " + qStr.toString());	
		}
		return found;
	}
	
	private static synchronized void cleanUserProfiles() {
		List <String> uidsToBeRemoved = new LinkedList <String>();
		for (String uid: loadedStorages.keySet()) {
			Long createdTime = lastUsedTimes.get(uid);
			if (createdTime == null) lastUsedTimes.put(uid, System.currentTimeMillis());
			else if (createdTime - System.currentTimeMillis() >= EXPIRATION) uidsToBeRemoved.add(uid);
		}
		for (String uid: uidsToBeRemoved) {
			lastUsedTimes.remove(uid);
			loadedStorages.remove(uid);
		}
		uidsToBeRemoved.clear();
	}
	
	private VirtuosoUserProfileStorage() {
	}
	
	public int hashCode() {
		if (this.uid == null) return -1;
		return uid.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof UserProfile)) return false;
		VirtuosoUserProfileStorage virtuosoUserProfileStorage = (VirtuosoUserProfileStorage) obj;
		if (uid == null) {
			if (virtuosoUserProfileStorage.uid == null) return true;
		} else if (uid.equals(virtuosoUserProfileStorage.uid)) return true;
		return false;
	}
	
	/**
	 * Cleans instances.
	 */
	public static void scheduleCleaner() {
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				cleanUserProfiles();
			}
		};
		Timer timer = new Timer();
		timer.schedule(tt, System.currentTimeMillis(), EXPIRATION);
	}
}

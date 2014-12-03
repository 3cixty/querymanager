package eu.threecixty.profile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.threecixty.Configuration;
import eu.threecixty.profile.oldmodels.UserInteractionMode;

public class VirtuosoUserProfileStorage {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 VirtuosoUserProfileStorage.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	
	private static final String PROFILE_URI = "http://data.linkedevents.org/person/";

	/**
	 * Loads profile information from the KB.
	 * @param uid
	 * @return
	 */	
	public static eu.threecixty.profile.UserProfile loadProfile(String uid) {
		if (uid == null || uid.equals("")) return null;		
		try {
			eu.threecixty.profile.UserProfile toUserProfile = new eu.threecixty.profile.UserProfile();
			toUserProfile.setHasUID(uid);
			
			loadGenderFromKBToUserProfile(uid,toUserProfile);
			
			loadNameFromKBToUserProfile(uid, toUserProfile);
			
			loadProfileImageToUserProfile(uid, toUserProfile);
			
			loadAddressInfoFromKBToUserProfile(uid, toUserProfile);
			
			loadLastCrawlTimeFromKBToUserProfile(uid,toUserProfile);
			
			loadProfileIdentitiesFromUserProfile(uid, toUserProfile);
			
			loadKnowsFromKBToUserProfile(uid, toUserProfile);
			
			loadPreferencesFromKBToUserProfile(uid, toUserProfile);
			
			return toUserProfile;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Saves profile information to the KB.
	 * @param profile
	 * @return
	 */
	public synchronized static boolean saveProfile(eu.threecixty.profile.UserProfile profile) {
		if (profile == null) return false;
		try {
			if (DEBUG_MOD) LOGGER.info("begin saving user profile");
			saveUIDInfoTOKB(profile.getHasUID());
			
			saveGenderToKB(profile.getHasUID(),profile.getHasGender());
					
			saveNameInfoToKB(profile.getHasUID(),profile.getHasName());
			
			saveProfileImage(profile.getHasUID(), profile.getProfileImage());
			
			saveAddressInfoToKB(profile.getHasUID(),profile.getHasAddress());
			
			saveLastCrawlTimeToKB(profile.getHasUID(), profile.getHasLastCrawlTime());
			
			saveProfileIdentitiesToKB(profile.getHasUID(), profile.getHasProfileIdenties());
			
			saveKnowsToKB(profile.getHasUID(), profile.getKnows());
			
			savePreferenceToKB(profile.getHasUID(), profile.getPreferences());
			if (profile.getPreferences()==null)
				saveTransportToKB(profile.getHasUID(), null);
			else
				saveTransportToKB(profile.getHasUID(), profile.getPreferences().getHasTransport());
			
			if (DEBUG_MOD) LOGGER.info("end saving user profile");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	private static void saveUIDInfoTOKB(String uid) {

		try {
			
			if (!VirtuosoManager.getInstance().existsAccount(uid)) {
				VirtuosoManager.getInstance().createAccount(uid);
				String insertQuery = GetSetQueryStrings.setUser(uid);
				VirtuosoConnection.insertDeleteQuery(insertQuery);
			}

		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
		
	}

	/**
	 * Adds knows information to KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveKnowsToKB(String uid, Set <String> knows) {

		try {
			
			String str = GetSetQueryStrings.removeAllKnows(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			//add new knows profiles
			if (knows!=null){
				if (knows!=null&&!knows.isEmpty()){
					Iterator <String> iterators = knows.iterator();
					for ( ; iterators.hasNext(); ){
						String know=iterators.next();
					
						QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getUserURI(know));
	
						ResultSet results = qRC.getReturnedResultSet();
						if (!results.hasNext()){
						//for ( ; results.hasNext(); ) {
							//QuerySolution qs = results.next();
							try {
								
							    //RDFNode uri = qs.get("uri");
							    
							    //if (uri==null){
							    	str=GetSetQueryStrings.setUser(know);
									VirtuosoConnection.insertDeleteQuery(str);
									eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentities=new eu.threecixty.profile.oldmodels.ProfileIdentities();
									profileIdentities.setHasSource("https://plus.google.com");
									profileIdentities.setHasSourceCarrier("Google");
									profileIdentities.setHasProfileIdentitiesURI(PROFILE_URI+know+"/Account/"+profileIdentities.getHasSourceCarrier());
									profileIdentities.setHasUserAccountID(know);
									profileIdentities.setHasUserInteractionMode(UserInteractionMode.Active);
									str=GetSetQueryStrings.setProfileIdentities(know, profileIdentities);
									VirtuosoConnection.insertDeleteQuery(str);
							    //}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}
				}
			str = GetSetQueryStrings.setMultipleKnows(uid,knows);
			VirtuosoConnection.insertDeleteQuery(str);
			}
			
		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * Adds profile identities found in a given instance of userprofile into the KB.
	 * @param fromUserProfile
	 * @param mf
	 * @param kbUserProfile
	 */
	private static void saveProfileIdentitiesToKB(String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities) {
		
		try {
			
			String str = GetSetQueryStrings.removeAllProfileIdentitiesOfUser(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			if (profileIdentities!=null&&!profileIdentities.isEmpty()){
				str = GetSetQueryStrings.setMultipleProfileIdentities(uid, profileIdentities);
				VirtuosoConnection.insertDeleteQuery(str);
			}
			return;


		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * add gender
	 * @param uid
	 * @param time
	 */
	private static void saveGenderToKB(String uid, String gender) {
		try {

			String str = GetSetQueryStrings.removeGender(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			if (gender!=null){
				str = GetSetQueryStrings.setGender(uid, gender);
				VirtuosoConnection.insertDeleteQuery(str);
			}
		}catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}
	
	/**
	 * add last crawl time
	 * @param uid
	 * @param time
	 */
	private static void saveLastCrawlTimeToKB(String uid, String time) {
		
		try {
		
			String str = GetSetQueryStrings.removeLastCrawlTime(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			str = GetSetQueryStrings.setLastCrawlTime(uid, time);
			VirtuosoConnection.insertDeleteQuery(str);
		}catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}
	
	/**
	 * add likes to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param likes
	 */
	private static void saveLikesToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference ){//Set<eu.threecixty.profile.oldmodels.Likes> likes) {
		
		try {

			String str = GetSetQueryStrings.removeAllLikesOfUser(uid);
			VirtuosoConnection.insertDeleteQuery(str);
		
			if (preference!=null){//likes!=null&&!likes.isEmpty()){
				Set<eu.threecixty.profile.oldmodels.Likes> likes=preference.getHasLikes();
				str = GetSetQueryStrings.setMultipleLikes(uid, likes);
				VirtuosoConnection.insertDeleteQuery(str);
			}
			return;


		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
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
	private static void savePreferenceToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference) {
			//if (preference!=null){
				
				saveLikesToKB(uid,  preference);//.getHasLikes());
				
				saveTripPreferenceToKB(uid, preference);//.getHasTripPreference());
				 
				savePlacePreferenceToKB(uid, preference);//.getHasPlacePreference());
			//}
	}

	/**
	 * add place preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param placePreferences
	 */
	private static void savePlacePreferenceToKB(String uid, eu.threecixty.profile.oldmodels.Preference preference){//Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences) {
		
		try {
				String str = GetSetQueryStrings.removePlacePreferences(uid);
				VirtuosoConnection.insertDeleteQuery(str);
				if(preference!=null){
					Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences=preference.getHasPlacePreference();
					if (placePreferences!=null&&!placePreferences.isEmpty()){
						Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators=placePreferences.iterator();
						for ( ; iterators.hasNext(); ){ 
							eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
							str = GetSetQueryStrings.setPlacePreferences(uid, placePreference);
							VirtuosoConnection.insertDeleteQuery(str);
						}
					}						
				}
				return;
		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * add trip preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param tripPreferences
	 */
	private static void saveTripPreferenceToKB(String uid,eu.threecixty.profile.oldmodels.Preference preference){// Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences) {
		
		try {
			String str = GetSetQueryStrings.removeMultipleTripPreferences(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			if (preference!=null){
			
				Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences=preference.getHasTripPreference();
				if (tripPreferences!=null&&!tripPreferences.isEmpty()){
					str = GetSetQueryStrings.setMultipleTripPreferences(uid, tripPreferences);
					VirtuosoConnection.insertDeleteQuery(str);
				}
			}
			return;
		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}
	
	/**
	 * add transport in the KB.
	 * @param uid
	 * @param transport
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveTransportToKB(String uid, Set<eu.threecixty.profile.oldmodels.Transport> transports) {
		
		try {
			
			QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getTransport(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode transport = qs.get("transport");

					if (transport==null) break;
					
					QueryReturnClass qRCRegularTrips=VirtuosoConnection.query(GetSetQueryStrings.getRegularTripsURIForTransport(transport.asResource().getURI()));
					ResultSet resultsRegularTrips = qRCRegularTrips.getReturnedResultSet();
					
					for ( ; resultsRegularTrips.hasNext(); ) {
						QuerySolution qsRegularTrips = resultsRegularTrips.next();
						try {
							RDFNode regularTripURI = qsRegularTrips.get("regularTrip");
							
							if (regularTripURI!=null){
								String str = GetSetQueryStrings.removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(uid, regularTripURI.asResource().getURI());
								VirtuosoConnection.insertDeleteQuery(str);
							}
						} catch (Exception e) {
							e.printStackTrace();
							LOGGER.error(e.getMessage());
						}
					}
					String str = GetSetQueryStrings.removeMultipleRegularTripsAssociatedToSpecificTransport(uid, transport.asResource().getURI());
					VirtuosoConnection.insertDeleteQuery(str);
					
					str = GetSetQueryStrings.removeMultipleAccompanyingAssociatedToSpecificTransport(uid, transport.asResource().getURI());
					VirtuosoConnection.insertDeleteQuery(str);
				
				}catch (Exception e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
			}
			String str = GetSetQueryStrings.removeTransport(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			if (transports!=null&&!transports.isEmpty()){
				Iterator<eu.threecixty.profile.oldmodels.Transport> iterators=transports.iterator();
				for ( ; iterators.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.Transport transport=iterators.next();
					if (transport.getHasTransportURI()==null||transport.getHasTransportURI().isEmpty()) {
						transport.setHasTransportURI(PROFILE_URI+uid+"/Mobility/"+UUID.randomUUID().toString());
					}
					
					if (transport.getHasAccompanyings()!=null&&!transport.getHasAccompanyings().isEmpty()){
						str = GetSetQueryStrings.setMultipleAccompanyingAssociatedToSpecificTransport(uid, transport.getHasTransportURI(), transport.getHasAccompanyings());
						VirtuosoConnection.insertDeleteQuery(str);
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
							VirtuosoConnection.insertDeleteQuery(str);
						}
					}
					if ( transport.getHasRegularTrip()!=null&&!transport.getHasRegularTrip().isEmpty()){
						str = GetSetQueryStrings.setMultipleRegularTripsAssociatedToSpecificTransport(uid, transport.getHasTransportURI(), transport.getHasRegularTrip());
						VirtuosoConnection.insertDeleteQuery(str);
					}
					
					str = GetSetQueryStrings.setTransport(uid, transport.getHasTransportURI());
					VirtuosoConnection.insertDeleteQuery(str);
				}		
			}
			return;


		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * Saves name information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveNameInfoToKB(String uid, eu.threecixty.profile.oldmodels.Name name) {
		
		try {
			
			String str = GetSetQueryStrings.removeName(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			if (name!=null){
				//name.setHasNameURI(PROFILE_URI+uid+"/Name");
				str = GetSetQueryStrings.setName(uid, name);
				VirtuosoConnection.insertDeleteQuery(str);
			}

		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(String uid,	eu.threecixty.profile.oldmodels.Address address) {
	
		try {
		
			String str = GetSetQueryStrings.removeAddress(uid);
			VirtuosoConnection.insertDeleteQuery(str);
			
			if (address!=null){
				address.setHasAddressURI(PROFILE_URI+uid+"/Address");
				str = GetSetQueryStrings.setAddress(uid, address);
				VirtuosoConnection.insertDeleteQuery(str);
			}

		} catch ( IOException  ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * Loads likes from the KB to a preference instance.
	 * @param from
	 * @param to
	 */
	private static void loadLikesFromKBToPreference(String uid, eu.threecixty.profile.oldmodels.Preference to) {
		Set <eu.threecixty.profile.oldmodels.Likes> toLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();

		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getLikes(uid));

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

		return;
	}
	
	/**
	 * load gender from the kb
	 * @param uid
	 * @param to
	 */
	private static void loadGenderFromKBToUserProfile(String uid,
			UserProfile to) {
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getGender(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				RDFNode gender = qs.get("gender");
			   
			    if (gender!=null)
			    	to.setHasGender(gender.toString());	
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		
		return;
	}
	
	/**
	 * load last crawl time from the kb
	 * @param uid
	 * @param to
	 */
	private static void loadLastCrawlTimeFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getLastCrawlTime(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				RDFNode lastCrawlTime = qs.get("lastCrawlTime");
			   
			    if (lastCrawlTime!=null)
			    	to.setHasLastCrawlTime(lastCrawlTime.toString());	
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		
		return;
	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		
		eu.threecixty.profile.oldmodels.Name toName = new eu.threecixty.profile.oldmodels.Name();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getName(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				//RDFNode nameuri = qs.get("name");
				RDFNode gn = qs.get("givenname");
				RDFNode fn = qs.get("familyname");
			    
			    //if (nameuri!=null)
			    //	toName.setHasNameURI(nameuri.asResource().getURI());
			    if (fn!=null)
			    	toName.setFamilyName(fn.toString());
			    if (gn!=null)
			    	toName.setGivenName(gn.toString());	
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		to.setHasName(toName);
		
		return;
	}

	/**
	 * Loads profile image from Virtuoso.
	 * @param uid
	 * @param to
	 */
	private static void loadProfileImageToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		String query = GetSetQueryStrings.createQueryToGetProfileImage(uid);
		QueryReturnClass qRC=VirtuosoConnection.query(query);

		ResultSet results = qRC.getReturnedResultSet();
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			to.setProfileImage(qs.get("profileImage").toString());
			break;
		}
	}
	
	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		
		eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
				
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getAddress(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {//?homeLocation ?geoLocation 
				RDFNode addressuri = qs.get("address");
				RDFNode cname = qs.get("countryname");
			    RDFNode tname = qs.get("townname");
			    RDFNode homeLocationURI = qs.get("homeLocation");
			    RDFNode geoLocationURI = qs.get("geoLocation");
			    RDFNode lon = qs.get("longitude");
			    RDFNode lat = qs.get("lat");
			    if (addressuri!=null)
			    	toAddress.setHasAddressURI(addressuri.asResource().getURI());
			    if (cname!=null)
			    	toAddress.setCountryName(cname.toString());
			    if (tname!=null)
			    	toAddress.setTownName(tname.toString());
			    if (homeLocationURI!=null)
			    	toAddress.setHasHomeLocationURI(homeLocationURI.asResource().getURI());
			    if (geoLocationURI!=null)
			    	toAddress.setHasGeoCoordinatesURI(geoLocationURI.asResource().getURI());
			    if (lon!=null)
			    	toAddress.setLongitute(Double.parseDouble(lon.asLiteral().getString()));
			    if (lat!=null)
			    	toAddress.setLatitude(Double.parseDouble(lat.asLiteral().getString()));	
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		to.setHasAddress(toAddress);
					
		return;
	}
	
	/**
	 * Loads profile identities from a given user profile to a given settings instance.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadProfileIdentitiesFromUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		
		Set <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = new HashSet <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getProfileIdentities(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				
				eu.threecixty.profile.oldmodels.ProfileIdentities tmpProfile = new eu.threecixty.profile.oldmodels.ProfileIdentities();
				RDFNode uri = qs.get("pi");
				RDFNode source = qs.get("source");
				RDFNode piID = qs.get("piID");
				RDFNode uIM = qs.get("uIM");
			    
			    if (uri!=null)
			    	tmpProfile.setHasProfileIdentitiesURI(uri.asResource().getURI());
			    if (source!=null)
			    	tmpProfile.setHasSource(source.asResource().getURI());
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
					
		return;
	}

	/**
	 * Loads knows information in the KB to a given instance of user profile.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadKnowsFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		
		Set <String> knows = new HashSet <String>();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getKnows(uid));

		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				
			    RDFNode uidknows = qs.get("uidknows");
			    
			    if (uidknows!=null)
			    	knows.add(uidknows.toString());
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
			
		}
		toUserProfile.setKnows(knows);
					
		return;
	}

	private static void loadPreferencesFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		eu.threecixty.profile.oldmodels.Preference toPrefs = new eu.threecixty.profile.oldmodels.Preference();

		loadLikesFromKBToPreference(uid,toPrefs);
		loadTripPreferencesFromKBToPreferences(uid, toPrefs);
		loadPlacePreferencesFromKBToPreferences(uid, toPrefs);
		loadTransportFromKBToPreferences(uid,toPrefs);
						
		toUserProfile.setPreferences(toPrefs);
						
		}
	/**
	 * Loads transport from the KB.
	 * @param userProfile
	 * @param toPrefs
	 */
	private static void loadTransportFromKBToPreferences(String uid, eu.threecixty.profile.oldmodels.Preference toPrefs) {
		
		Set <eu.threecixty.profile.oldmodels.Transport> toTransports = new HashSet <eu.threecixty.profile.oldmodels.Transport>();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getTransport(uid));
		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			try {
				RDFNode transport = qs.get("transport");

				if (transport==null) return;
				
				eu.threecixty.profile.oldmodels.Transport toTransport = new eu.threecixty.profile.oldmodels.Transport();
				
				toTransport.setHasTransportURI(transport.asResource().getURI());
				
				QueryReturnClass qRCRegularTrips=VirtuosoConnection.query(GetSetQueryStrings.getRegularTripsForTransport(transport.asResource().getURI()));
				ResultSet resultsRegularTrips = qRCRegularTrips.getReturnedResultSet();
				
				Set <eu.threecixty.profile.oldmodels.RegularTrip> toRegularTrips = new HashSet <eu.threecixty.profile.oldmodels.RegularTrip>();
				
				for ( ; resultsRegularTrips.hasNext(); ) {
					QuerySolution qsRegularTrips = resultsRegularTrips.next();
					try {
						eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip = new eu.threecixty.profile.oldmodels.RegularTrip();
						loadRegularTripFromKB(qsRegularTrips, toRegularTrip);
				    	toRegularTrips.add(toRegularTrip);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.getMessage());
					}
				}
				
				QueryReturnClass qRCAccompanying=VirtuosoConnection.query(GetSetQueryStrings.getAccompanyingForTransport(transport.asResource().getURI()));
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
			
				toTransport.setHasAccompanyings(toAccompanyings);
				toTransport.setHasRegularTrip(toRegularTrips);
				toTransports.add(toTransport);
				
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		
		toPrefs.setHasTransport(toTransports);
					
		return;
	}

	/**
	 * Loads regular trip from the KB.
	 * @param regularTrip
	 * @param toRegularTrip
	 */
	private static void loadRegularTripFromKB(QuerySolution qs,
			eu.threecixty.profile.oldmodels.RegularTrip toRegularTrip) {
		
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
    		loadPersonalPlaceFromKBToRegularTrips(regularTripURI.asResource().getURI(),toPersonalPlaces);
    	
    	toRegularTrip.setHasPersonalPlacesNew(toPersonalPlaces);
	}

	/**
	 * Loads personal place from the KB.
	 * @param personalPlace
	 * @param toPersonalPlace
	 */
	private static void loadPersonalPlaceFromKBToRegularTrips(String regularTripURI,
			Set <eu.threecixty.profile.oldmodels.PersonalPlace> toPersonalPlaces) {

		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getPersonalPlacesForRegularTrips(regularTripURI));

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
		
		return;
	}

	/**
	 * Loads Accompanying from the KB.
	 * @param accompanying
	 * @param toAccompanying
	 */
	private static void loadAccompanyingFromKB(QuerySolution qs, String uid,
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
	 */
	private static void loadTripPreferencesFromKBToPreferences(String uid, eu.threecixty.profile.oldmodels.Preference to) {
		
		Set <eu.threecixty.profile.oldmodels.TripPreference> tripPreferences = new HashSet <eu.threecixty.profile.oldmodels.TripPreference>();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getTripPreferences(uid));

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

		return;
	}
	
	/**
	 * load place preference from KB 
	 * @param uid
	 * @param to
	 */
	private static void loadPlacePreferencesFromKBToPreferences(String uid, eu.threecixty.profile.oldmodels.Preference to) {
		
		Set <eu.threecixty.profile.oldmodels.PlacePreference> placePreferences = new HashSet <eu.threecixty.profile.oldmodels.PlacePreference>();
		QueryReturnClass qRC=VirtuosoConnection.query(GetSetQueryStrings.getPlacePreferences(uid));

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

		return;
	}

	/**
	 * load Place detail preferences from kb
	 * @param placeDetailPreferenceURI
	 * @param placePreference
	 */
	private static void loadPlaceDetailPreferenceFromKBToPlacePreference(
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
	private static void saveProfileImage(String uid, String profileImageLink) {
		if (profileImageLink == null || profileImageLink.equals("")) return;
		String queryToDeleteOldValue = GetSetQueryStrings.createQueryToDeleteProfileImage(uid);
		try {
			VirtuosoConnection.insertDeleteQuery(queryToDeleteOldValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String queryToInsertValue = GetSetQueryStrings.createQueryToInsertProfileImage(uid, profileImageLink);
		try {
			VirtuosoConnection.insertDeleteQuery(queryToInsertValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks whether or not a given UID exists in the UserProfile.
	 * @param uid
	 * @return
	 */
	public static boolean existUID(String uid) {
		if (uid == null) return false;
		StringBuilder qStr = new StringBuilder(Configuration.PROFILE_PREFIX);
	    qStr.append("SELECT  DISTINCT  ?uid\n");
	    qStr.append("WHERE {\n\n");
	    qStr.append("?root profile:userID ?uid .\n");
	    qStr.append("FILTER (STR(?uid) = \"" + uid + "\") . \n\n");
	    qStr.append("}");
	    
	    QueryReturnClass qRC=VirtuosoConnection.query(qStr.toString());
		ResultSet results = qRC.getReturnedResultSet();
		
		for ( ; results.hasNext(); ) {
			QuerySolution qs = results.next();
			RDFNode tmpuid = qs.get("uid");
			if (tmpuid != null && !tmpuid.asLiteral().getString().equals("")) {
				if (DEBUG_MOD) LOGGER.info("Found UID = " + uid + " in Virtuoso");
				return true;
			}
		}
		if (DEBUG_MOD) LOGGER.info("Not found UID = " + uid + " in Virtuoso. Here is the sparql query: " + qStr.toString());	
		return false;
	}
	
	private VirtuosoUserProfileStorage() {
	}
}

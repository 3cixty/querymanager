package eu.threecixty.profile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.threecixty.profile.oldmodels.UserInteractionMode;

public class VirtuosoUserProfileStorage {
	
	private static final String PROFILE_URI = "http://www.eu.3cixty.org/profile#";
	
	private static final Object _sync = new Object();
	
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
			
			loadNameFromKBToUserProfile(uid, toUserProfile);
			
			loadAddressInfoFromKBToUserProfile(uid, toUserProfile);
			
			loadLastCrawlTimeFromKBToUserProfile(uid,toUserProfile);
			
			loadProfileIdentitiesFromUserProfile(uid, toUserProfile);
			
			loadKnowsFromKBToUserProfile(uid, toUserProfile);
			
			loadPreferencesFromKBToUserProfile(uid, toUserProfile);
			
			return toUserProfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Saves profile information to the KB.
	 * @param profile
	 * @return
	 */
	public synchronized static boolean saveProfile(eu.threecixty.profile.UserProfile profile, String type) {
		if (profile == null) return false;
		
		try {
			if (!type.equals("delete"))
				saveUIDInfoTOKB(profile.getHasUID(), type);
			
			saveNameInfoToKB(profile.getHasUID(),profile.getHasName());
			
			saveAddressInfoToKB(profile.getHasUID(),profile.getHasAddress());
			
			saveLastCrawlTimeToKB(profile.getHasUID(), profile.getHasLastCrawlTime());
			
			saveProfileIdentitiesToKB(profile.getHasUID(), profile.getHasProfileIdenties());
			
			saveKnowsToKB(profile.getHasUID(), profile.getKnows());
			
			savePreferenceToKB(profile.getHasUID(), profile.getPreferences());
			if (profile.getPreferences()==null)
				saveTransportToKB(profile.getHasUID(), null);
			else
				saveTransportToKB(profile.getHasUID(), profile.getPreferences().getHasTransport());
			
			if (type.equals("Delete"))
				saveUIDInfoTOKB(profile.getHasUID(), type);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void saveUIDInfoTOKB(String uid, String type) {

		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			if (type.equals("delete")){
				String str = GetSetQueryStrings.removeUser(uid);
				virtuosoConnection.insertDeleteQuery(str);
			}
			else{
				String str = GetSetQueryStrings.setUser(uid);
				virtuosoConnection.insertDeleteQuery(str);
			}

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
		
	}

	/**
	 * Adds knows information to KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveKnowsToKB(String uid, Set <String> knows) {
		
		Set <String> knowsTemp = new HashSet <String>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getKnows(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					
				    RDFNode uidknows = qs.get("uidknows");
				    
				    if (uidknows!=null)
				    	knowsTemp.add(uidknows.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			//add new knows profiles
			if (knows!=null){
				Set<String> knowsNotInKB=knows;
				knowsNotInKB.removeAll(knowsTemp);
			
			
				String str="";
				
				if (knowsNotInKB!=null&&!knowsNotInKB.isEmpty()){
					
					Iterator <String> iterators = knowsNotInKB.iterator();
					for ( ; iterators.hasNext(); ){
						String know=iterators.next();
						str=GetSetQueryStrings.setUser(know);
						virtuosoConnection.insertDeleteQuery(str);
						eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentities=new eu.threecixty.profile.oldmodels.ProfileIdentities();
						profileIdentities.setHasSource("https://plus.google.com");
						profileIdentities.setHasSourceCarrier("Google");
						profileIdentities.setHasProfileIdentitiesURI(PROFILE_URI+know+"/ProfileItentities/"+profileIdentities.getHasSourceCarrier());
						profileIdentities.setHasUserAccountID(know);
						profileIdentities.setHasUserInteractionMode(UserInteractionMode.Active);
						str=GetSetQueryStrings.setProfileIdentities(know, profileIdentities);
						virtuosoConnection.insertDeleteQuery(str);
					}
					str = GetSetQueryStrings.setMultipleKnows(uid,knowsNotInKB);
					virtuosoConnection.insertDeleteQuery(str);
				}
			}
			//delete know from profileKB
			//only delete links profile remains
			Set<String> knowsToDeleteKB=knowsTemp;
			if (knows!=null)
				knowsToDeleteKB.removeAll(knows);
			
			if (knowsToDeleteKB!=null&&!knowsToDeleteKB.isEmpty()){
				String str = GetSetQueryStrings.removeMultipleKnows(uid,knowsToDeleteKB);
				virtuosoConnection.insertDeleteQuery(str);
			}

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
	}

	/**
	 * Adds profile identities found in a given instance of userprofile into the KB.
	 * @param fromUserProfile
	 * @param mf
	 * @param kbUserProfile
	 */
	private static void saveProfileIdentitiesToKB(String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities) {
		
		Set <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = new HashSet <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getProfileIdentities(uid));

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
				    	tmpProfile.setHasSource(source.toString());
				    if (piID!=null)
				    	tmpProfile.setHasUserAccountID(piID.toString());	
				    if (uIM!=null)
				    	tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uIM.toString()));	
				    
				    oldProfiles.add(tmpProfile); 
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			String str = GetSetQueryStrings.removeMultipleProfileIdentities(uid, oldProfiles);
			virtuosoConnection.insertDeleteQuery(str);
			
			if (profileIdentities!=null&&!profileIdentities.isEmpty()){
				str = GetSetQueryStrings.setMultipleProfileIdentities(uid, profileIdentities);
				virtuosoConnection.insertDeleteQuery(str);
			}
			return;


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
	}

	/**
	 * add last crawl time
	 * @param uid
	 * @param time
	 */
	private static void saveLastCrawlTimeToKB(String uid, String time) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getLastCrawlTime(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			String oldtime="";
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					//?lastCrawlTime
					RDFNode lastCrawlTime = qs.get("lastCrawlTime");
					
				    if (lastCrawlTime!=null)
				    	oldtime=lastCrawlTime.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeLastCrawlTime(uid, oldtime);
			virtuosoConnection.insertDeleteQuery(str);
			
			str = GetSetQueryStrings.setLastCrawlTime(uid, time);
			virtuosoConnection.insertDeleteQuery(str);
		}catch ( IOException  ex) {
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
	}
	
	/**
	 * add likes to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param likes
	 */
	private static void saveLikesToKB(String uid, String preferenceURI, Set<eu.threecixty.profile.oldmodels.Likes> likes) {
		Set <eu.threecixty.profile.oldmodels.Likes> oldLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();
				
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getLikes(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					//?likes ?likeName ?liketype
					eu.threecixty.profile.oldmodels.Likes oldLike = new eu.threecixty.profile.oldmodels.Likes();
					RDFNode likeURI = qs.get("likes");
					RDFNode likeName = qs.get("likeName");
					RDFNode liketype = qs.get("liketype");
				    if (likeURI!=null)
				    	oldLike.setHasLikesURI(likeURI.asResource().getURI());
				    if (likeName!=null)
				    	oldLike.setHasLikeName(likeName.toString());
				    if (liketype!=null)
				    	oldLike.setHasLikeType(eu.threecixty.profile.oldmodels.LikeType.valueOf(liketype.toString()));	
				   oldLikes.add(oldLike);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeMultipleLikes(preferenceURI, oldLikes);
			virtuosoConnection.insertDeleteQuery(str);
		
			if (likes!=null&&!likes.isEmpty()){
				str = GetSetQueryStrings.setMultipleLikes(preferenceURI, likes);
				virtuosoConnection.insertDeleteQuery(str);
			}
			return;


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
		
		eu.threecixty.profile.oldmodels.Preference oldPrefs = new eu.threecixty.profile.oldmodels.Preference();

		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPreferences(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					
				    RDFNode prefURI = qs.get("pref");
				    
				    if (prefURI!=null)
				    	oldPrefs.setHasPreferenceURI(prefURI.asResource().getURI());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
			saveLikesToKB(uid, oldPrefs.getHasPreferenceURI(), null);
			
			saveTripPreferenceToKB(uid, oldPrefs.getHasPreferenceURI(), null);
			
			savePlacePreferenceToKB(uid, oldPrefs.getHasPreferenceURI(), null);
			
			String str = GetSetQueryStrings.removePreferences(uid,oldPrefs.getHasPreferenceURI());
			virtuosoConnection.insertDeleteQuery(str);
			
			if (preference!=null){
				if (preference.getHasPreferenceURI()==null && preference.getHasPreferenceURI().isEmpty())
					preference.setHasPreferenceURI(PROFILE_URI+uid+"/Preference");
				
				str = GetSetQueryStrings.setPreferences(uid,preference.getHasPreferenceURI());
				virtuosoConnection.insertDeleteQuery(str);
				
				saveLikesToKB(uid, preference.getHasPreferenceURI(), preference.getHasLikes());
				
				saveTripPreferenceToKB(uid, preference.getHasPreferenceURI(), preference.getHasTripPreference());
				
				savePlacePreferenceToKB(uid, preference.getHasPreferenceURI(), preference.getHasPlacePreference());
			}
			return;


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
	}

	/**
	 * add place preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param placePreferences
	 */
	private static void savePlacePreferenceToKB(String uid, String preferenceURI, Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences) {
		Set<eu.threecixty.profile.oldmodels.PlacePreference> oldPlacePreferences = new HashSet<eu.threecixty.profile.oldmodels.PlacePreference>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPlacePreferences(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					eu.threecixty.profile.oldmodels.PlacePreference placePreference = new eu.threecixty.profile.oldmodels.PlacePreference();
					
					RDFNode placePreferenceURI = qs.get("placePreference");
					RDFNode placeDetailPreferenceURI = qs.get("placeDetailPreference");
					
					if (placePreferenceURI!=null)
				    	placePreference.setHasPlacePreferenceURI(placePreferenceURI.asResource().getURI());
					if (placeDetailPreferenceURI!=null){
				       	loadPlaceDetailPreferenceFromKBToPlacePreference(placeDetailPreferenceURI.asResource().getURI(),placePreference);
				    }
				    oldPlacePreferences.add(placePreference);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators=oldPlacePreferences.iterator();
			for ( ; iterators.hasNext(); ){ 
				eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
				String str = GetSetQueryStrings.removePlaceDetailPreference(placePreference.getHasPlacePreferenceURI(), placePreference.getHasPlaceDetailPreference());
				virtuosoConnection.insertDeleteQuery(str);
				str = GetSetQueryStrings.removePlacePreferences(preferenceURI, placePreference.getHasPlacePreferenceURI());
				virtuosoConnection.insertDeleteQuery(str);
			}	
			
			if (placePreferences!=null&&!placePreferences.isEmpty()){
				iterators=placePreferences.iterator();
				for ( ; iterators.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
					String str = GetSetQueryStrings.setPlaceDetailPreference(placePreference.getHasPlacePreferenceURI(), placePreference.getHasPlaceDetailPreference());
					virtuosoConnection.insertDeleteQuery(str);
					str = GetSetQueryStrings.setPlacePreferences(preferenceURI, placePreference.getHasPlacePreferenceURI());
					virtuosoConnection.insertDeleteQuery(str);
				}
			}
			return;


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
	}

	/**
	 * add trip preferences to the kb
	 * @param uid
	 * @param preferenceURI
	 * @param tripPreferences
	 */
	private static void saveTripPreferenceToKB(String uid, String preferenceURI, Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences) {
		Set<eu.threecixty.profile.oldmodels.TripPreference> oldTripPreferences = new HashSet<eu.threecixty.profile.oldmodels.TripPreference>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getTripPreferences(uid));

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
				    
				    
				    oldTripPreferences.add(tripPreference);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeMultipleTripPreferences(preferenceURI, oldTripPreferences);
			virtuosoConnection.insertDeleteQuery(str);
			if (tripPreferences!=null&&!tripPreferences.isEmpty()){
				str = GetSetQueryStrings.setMultipleTripPreferences(preferenceURI, tripPreferences);
				virtuosoConnection.insertDeleteQuery(str);
			}
			return;


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
	}
	
	/**
	 * add transport in the KB.
	 * @param uid
	 * @param transport
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveTransportToKB(String uid, Set<eu.threecixty.profile.oldmodels.Transport> transports) {
		
		Set<eu.threecixty.profile.oldmodels.Transport> oldTransports = new HashSet<eu.threecixty.profile.oldmodels.Transport>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getTransport(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode transport = qs.get("transport");

					if (transport==null) break;
					
					eu.threecixty.profile.oldmodels.Transport oldtransport = new eu.threecixty.profile.oldmodels.Transport();
					
					oldtransport.setHasTransportURI(transport.asResource().getURI());
					
					queryReturnClass qRCRegularTrips=virtuosoConnection.query(GetSetQueryStrings.getRegularTripsForTransport(transport.asResource().getURI()));
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
						}
					}
					
					queryReturnClass qRCAccompanying=virtuosoConnection.query(GetSetQueryStrings.getAccompanyingForTransport(transport.asResource().getURI()));
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
						}
						
					}
				
					oldtransport.setHasAccompanyings(toAccompanyings);
					oldtransport.setHasRegularTrip(toRegularTrips);
					oldTransports.add(oldtransport);
				
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			String str ="";
			Iterator <eu.threecixty.profile.oldmodels.Transport> iterators=oldTransports.iterator();
			for ( ; iterators.hasNext(); ){ 
				eu.threecixty.profile.oldmodels.Transport transport=iterators.next();
				str = GetSetQueryStrings.removeMultipleAccompanyingAssociatedToSpecificTransport(transport.getHasTransportURI(), transport.getHasAccompanyings());
				virtuosoConnection.insertDeleteQuery(str);
				Set <eu.threecixty.profile.oldmodels.RegularTrip> setRegTrip=transport.getHasRegularTrip();
				Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iteratorRegTrip=setRegTrip.iterator();
				for ( ; iteratorRegTrip.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.RegularTrip regularTrip=iteratorRegTrip.next();
					str = GetSetQueryStrings.removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(regularTrip.getHasRegularTripURI(), regularTrip.getHasPersonalPlacesNew());
					virtuosoConnection.insertDeleteQuery(str);
				}
				str = GetSetQueryStrings.removeMultipleRegularTripsAssociatedToSpecificTransport(transport.getHasTransportURI(), transport.getHasRegularTrip());
				virtuosoConnection.insertDeleteQuery(str);
				
				str = GetSetQueryStrings.removeTransport(uid, transport.getHasTransportURI());
				virtuosoConnection.insertDeleteQuery(str);
			}
			
			if (transports!=null&&!transports.isEmpty()){
				iterators=transports.iterator();
				for ( ; iterators.hasNext(); ){ 
					eu.threecixty.profile.oldmodels.Transport transport=iterators.next();
					if (transport.getHasTransportURI()==null&&transport.getHasTransportURI().isEmpty()) {
						transport.setHasTransportURI(PROFILE_URI+uid+"/Transport/"+UUID.randomUUID().toString());
					}
					
					if (transport.getHasAccompanyings()!=null&&!transport.getHasAccompanyings().isEmpty()){
						str = GetSetQueryStrings.setMultipleAccompanyingAssociatedToSpecificTransport(transport.getHasTransportURI(), transport.getHasAccompanyings());
						virtuosoConnection.insertDeleteQuery(str);
					}
					
					Set <eu.threecixty.profile.oldmodels.RegularTrip> setRegTrip=transport.getHasRegularTrip();
					
					Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iteratorsRegularTrip=setRegTrip.iterator();
					for ( ; iteratorsRegularTrip.hasNext(); ){ 
						eu.threecixty.profile.oldmodels.RegularTrip regularTrip=iteratorsRegularTrip.next();
						if (regularTrip.getHasRegularTripURI()==null&&regularTrip.getHasRegularTripURI().isEmpty()) {
							regularTrip.setHasRegularTripURI(transport.getHasTransportURI()+"/RegularTrip/"+UUID.randomUUID().toString());
						}
						if (regularTrip.getHasPersonalPlacesNew()!=null&&!regularTrip.getHasPersonalPlacesNew().isEmpty()){
							str = GetSetQueryStrings.setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(regularTrip.getHasRegularTripURI(), regularTrip.getHasPersonalPlacesNew());
							virtuosoConnection.insertDeleteQuery(str);
						}
					}
					if ( transport.getHasRegularTrip()!=null&&!transport.getHasRegularTrip().isEmpty()){
						str = GetSetQueryStrings.setMultipleRegularTripsAssociatedToSpecificTransport(transport.getHasTransportURI(), transport.getHasRegularTrip());
						virtuosoConnection.insertDeleteQuery(str);
					}
					
					str = GetSetQueryStrings.setTransport(uid, transport.getHasTransportURI());
					virtuosoConnection.insertDeleteQuery(str);
				}		
			}
			return;


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
	}

	/**
	 * Saves name information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 * @param mf
	 */
	private static void saveNameInfoToKB(String uid, eu.threecixty.profile.oldmodels.Name name) {
		
		eu.threecixty.profile.oldmodels.Name toNameTemp = new eu.threecixty.profile.oldmodels.Name();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getName(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode nameuri = qs.get("name");
					RDFNode gn = qs.get("givenname");
					RDFNode fn = qs.get("familyname");
				    
				    if (nameuri!=null)
				    	toNameTemp.setHasNameURI(nameuri.asResource().getURI());
				    if (fn!=null)
				    	toNameTemp.setFamilyName(fn.toString());
				    if (gn!=null)
				    	toNameTemp.setGivenName(gn.toString());
				    
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeName(uid, toNameTemp);
			virtuosoConnection.insertDeleteQuery(str);
			
			if (name!=null){
				name.setHasNameURI(PROFILE_URI+uid+"/Name");
				str = GetSetQueryStrings.setName(uid, name);
				virtuosoConnection.insertDeleteQuery(str);
			}

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
	}

	/**
	 * Saves address information into the KB.
	 * @param profile
	 * @param kbUserProfile
	 */
	private static void saveAddressInfoToKB(String uid,	eu.threecixty.profile.oldmodels.Address address) {
		eu.threecixty.profile.oldmodels.Address oldddress =  new eu.threecixty.profile.oldmodels.Address();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getAddress(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode addressuri = qs.get("address");
					RDFNode cname = qs.get("countryname");
				    RDFNode tname = qs.get("townname");
				    RDFNode lon = qs.get("longitude");
				    RDFNode lat = qs.get("lat");
				    if (addressuri!=null)
				    	oldddress.setHasAddressURI(addressuri.asResource().getURI());
				    if (cname!=null)
				    	oldddress.setCountryName(cname.toString());
				    if (tname!=null)
				    	oldddress.setTownName(tname.toString());	
				    if (lon!=null)
				    	oldddress.setLongitute(lon.asLiteral().getDouble());
				    if (lat!=null)
				    	oldddress.setLatitude(lat.asLiteral().getDouble());	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeAddress(uid, oldddress);
			virtuosoConnection.insertDeleteQuery(str);
			
			if (address!=null){
				address.setHasAddressURI(PROFILE_URI+uid+"/Address");
				str = GetSetQueryStrings.setAddress(uid, address);
				virtuosoConnection.insertDeleteQuery(str);
			}

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
	}


	/**
	 * Loads likes from the KB to a preference instance.
	 * @param from
	 * @param to
	 */
	private static void loadLikesFromKBToPreference(String uid, eu.threecixty.profile.oldmodels.Preference to) {
		Set <eu.threecixty.profile.oldmodels.Likes> toLikes = new HashSet <eu.threecixty.profile.oldmodels.Likes>();
				
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getLikes(uid));

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
				}
			}
			to.setHasLikes(toLikes);

			return;


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
	}
	
	/**
	 * load last crawl time from the kb
	 * @param uid
	 * @param to
	 */
	private static void loadLastCrawlTimeFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();
			
			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getLastCrawlTime(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode lastCrawlTime = qs.get("lastCrawlTime");
				   
				    if (lastCrawlTime!=null)
				    	to.setHasLastCrawlTime(lastCrawlTime.toString());	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return;


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
	}
	
	/**
	 * Loads first name and last name from KB to profile information.
	 * @param from
	 * @param to
	 */
	private static void loadNameFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		
		eu.threecixty.profile.oldmodels.Name toName = new eu.threecixty.profile.oldmodels.Name();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getName(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode nameuri = qs.get("name");
					RDFNode gn = qs.get("givenname");
					RDFNode fn = qs.get("familyname");
				    
				    if (nameuri!=null)
				    	toName.setHasNameURI(nameuri.asResource().getURI());
				    if (fn!=null)
				    	toName.setFamilyName(fn.toString());
				    if (gn!=null)
				    	toName.setGivenName(gn.toString());	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			to.setHasName(toName);
			
			return;


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
	}

	/**
	 * Loads address information from KB (user profile).
	 * @param from
	 * @param to
	 */
	private static void loadAddressInfoFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile to) {
		
		eu.threecixty.profile.oldmodels.Address toAddress = new eu.threecixty.profile.oldmodels.Address();
				
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getAddress(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode addressuri = qs.get("address");
					RDFNode cname = qs.get("countryname");
				    RDFNode tname = qs.get("townname");
				    RDFNode lon = qs.get("longitude");
				    RDFNode lat = qs.get("lat");
				    if (addressuri!=null)
				    	toAddress.setHasAddressURI(addressuri.asResource().getURI());
				    if (cname!=null)
				    	toAddress.setCountryName(cname.toString());
				    if (tname!=null)
				    	toAddress.setTownName(tname.toString());	
				    if (lon!=null)
				    	toAddress.setLongitute(lon.asLiteral().getDouble());
				    if (lat!=null)
				    	toAddress.setLatitude(lat.asLiteral().getDouble());	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			to.setHasAddress(toAddress);
						
			return;


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
	}
	
	/**
	 * Loads profile identities from a given user profile to a given settings instance.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadProfileIdentitiesFromUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		
		Set <eu.threecixty.profile.oldmodels.ProfileIdentities> oldProfiles = new HashSet <eu.threecixty.profile.oldmodels.ProfileIdentities>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getProfileIdentities(uid));

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
				    	tmpProfile.setHasSource(source.toString());
				    if (piID!=null)
				    	tmpProfile.setHasUserAccountID(piID.toString());	
				    if (uIM!=null)
				    	tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uIM.toString()));	
				    
				    oldProfiles.add(tmpProfile); 
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			toUserProfile.setHasProfileIdenties(oldProfiles);
						
			return;


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
	}

	/**
	 * Loads knows information in the KB to a given instance of user profile.
	 * @param fromUserProfile
	 * @param toUserProfile
	 */
	private static void loadKnowsFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		
		Set <String> knows = new HashSet <String>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getKnows(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					
				    RDFNode uidknows = qs.get("uidknows");
				    
				    if (uidknows!=null)
				    	knows.add(uidknows.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			toUserProfile.setKnows(knows);
						
			return;


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
	}

	private static void loadPreferencesFromKBToUserProfile(String uid, eu.threecixty.profile.UserProfile toUserProfile) {
		eu.threecixty.profile.oldmodels.Preference toPrefs = new eu.threecixty.profile.oldmodels.Preference();

		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPreferences(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					
					RDFNode prefURI = qs.get("pref");
				    
				    if (prefURI!=null)
				    	toPrefs.setHasPreferenceURI(prefURI.asResource().getURI());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			loadLikesFromKBToPreference(uid,toPrefs);
			loadTripPreferencesFromKBToPreferences(uid, toPrefs);
			loadPlacePreferencesFromKBToPreferences(uid, toPrefs);
			loadTransportFromKBToPreferences(uid,toPrefs);
						
			toUserProfile.setPreferences(toPrefs);
						
			return;


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
	}
	/**
	 * Loads transport from the KB.
	 * @param userProfile
	 * @param toPrefs
	 */
	private static void loadTransportFromKBToPreferences(String uid, eu.threecixty.profile.oldmodels.Preference toPrefs) {
		
		Set <eu.threecixty.profile.oldmodels.Transport> toTransports = new HashSet <eu.threecixty.profile.oldmodels.Transport>();
				
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getTransport(uid));
			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					RDFNode transport = qs.get("transport");

					if (transport==null) return;
					
					eu.threecixty.profile.oldmodels.Transport toTransport = new eu.threecixty.profile.oldmodels.Transport();
					
					toTransport.setHasTransportURI(transport.asResource().getURI());
					
					queryReturnClass qRCRegularTrips=virtuosoConnection.query(GetSetQueryStrings.getRegularTripsForTransport(transport.asResource().getURI()));
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
						}
					}
					
					queryReturnClass qRCAccompanying=virtuosoConnection.query(GetSetQueryStrings.getAccompanyingForTransport(transport.asResource().getURI()));
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
						}
						
					}
				
					toTransport.setHasAccompanyings(toAccompanyings);
					toTransport.setHasRegularTrip(toRegularTrips);
					toTransports.add(toTransport);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			toPrefs.setHasTransport(toTransports);
						
			return;


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
		RDFNode fastestTravelTime = qs.get("fastestTravelTime");
		RDFNode lastChanged = qs.get("lastChanged");
		RDFNode totalDistance = qs.get("totalDistance");
		RDFNode totalCount = qs.get("totalCount");
		RDFNode modalityType = qs.get("modalityType");
		RDFNode weekdayPattern = qs.get("weekdayPattern");
		RDFNode dayhourPattern = qs.get("dayhourPattern");
		RDFNode timePattern = qs.get("timePattern");
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
    	if (fastestTravelTime!=null)
    		toRegularTrip.setHasRegularTripFastestTravelTime(fastestTravelTime.asLiteral().getLong());
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
    	if (timePattern!=null)
    		toRegularTrip.setHasRegularTripTravelTimePattern(timePattern.toString());
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
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPersonalPlacesForRegularTrips(regularTripURI));

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
				}
				
			}
			
			return;


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
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getTripPreferences(uid));

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
				}
			}
			to.setHasTripPreference(tripPreferences);

			return;


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
	}
	
	/**
	 * load place preference from KB 
	 * @param uid
	 * @param to
	 */
	private static void loadPlacePreferencesFromKBToPreferences(String uid, eu.threecixty.profile.oldmodels.Preference to) {
		
		Set <eu.threecixty.profile.oldmodels.PlacePreference> placePreferences = new HashSet <eu.threecixty.profile.oldmodels.PlacePreference>();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPlacePreferences(uid));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					//?placePreference ?placeDetailPreference 
					eu.threecixty.profile.oldmodels.PlacePreference placePreference = new eu.threecixty.profile.oldmodels.PlacePreference();
					RDFNode placePreferenceURI = qs.get("placePreference");
					RDFNode placeDetailPreferenceURI = qs.get("placeDetailPreference");
				    
				    if (placePreferenceURI!=null)
				    	placePreference.setHasPlacePreferenceURI(placePreferenceURI.asResource().getURI());
				    if (placeDetailPreferenceURI!=null){
				    	loadPlaceDetailPreferenceFromKBToPlacePreference(placeDetailPreferenceURI.asResource().getURI(),placePreference);
				    }
				    placePreferences.add(placePreference);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			to.setHasPlacePreference(placePreferences);

			return;


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
	}

	/**
	 * load Place detail preferences from kb
	 * @param placeDetailPreferenceURI
	 * @param placePreference
	 */
	private static void loadPlaceDetailPreferenceFromKBToPlacePreference(
			String placeDetailPreferenceURI,
			eu.threecixty.profile.oldmodels.PlacePreference placePreference) {
		eu.threecixty.profile.oldmodels.PlaceDetailPreference placeDetailPreference= new eu.threecixty.profile.oldmodels.PlaceDetailPreference();
		Connection conn = null;
		Statement stmt = null;

		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(GetSetQueryStrings.getPlaceDetailPreferenceFromURI(placeDetailPreferenceURI));

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				try {
					//?natureOfPlace 
					String natureOfPlace = qs.getLiteral("natureOfPlace").toString();
					
				    if (natureOfPlace!=null)
				    	placeDetailPreference.setHasNatureOfPlace(eu.threecixty.profile.oldmodels.NatureOfPlace.valueOf(natureOfPlace));
					placePreference.setHasPlaceDetailPreference(placeDetailPreference);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
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
	}

	/**
	 * Checks whether or not a given input string contains something.
	 * @param input
	 * @return
	 */
	private static boolean isNullOrEmpty(String input) {
		if (input == null || input.equals("")) return true;
		return false;
	}

	/**
	 * Converts a given date to string.
	 * @param date
	 * @return
	 */
	private static String convert(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(date);
	}
	
	/**
	 * convert string to date
	 * @param dateStr
	 * @return
	 */
	private static Date convert(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		int index = dateStr.indexOf("\"", 5);
		if (index < 0) return null;
		try {
			return sdf.parse(dateStr.substring(1, index));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Checks whether or not a given UID exists in the UserProfile.
	 * @param uid
	 * @return
	 */
	public static boolean existUID(String uid) {
		if (uid == null) return false;
		String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	    qStr += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
	    qStr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	    qStr += "PREFIX profile: <http://www.eu.3cixty.org/profile#>\n\n";
	    qStr += "SELECT  DISTINCT  ?uid\n";
	    qStr += "WHERE {\n\n";
	    qStr += "?root a owl:NamedIndividual .\n";
	    qStr += "?root profile:hasUID ?uid .\n";
	    qStr += "FILTER (STR(?uid) = \"" + uid + "\") . \n\n";
	    qStr += "}";
	    
	    Connection conn = null;
		Statement stmt = null;
	   		
	    try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return false;
			
			stmt = conn.createStatement();
			
			queryReturnClass qRC=virtuosoConnection.query(qStr);

			ResultSet results = qRC.getReturnedResultSet();
			
			for ( ; results.hasNext(); ) {
				QuerySolution qs = results.next();
				RDFNode tmpuid = qs.get("uid");
				if (tmpuid != null && !tmpuid.asLiteral().getString().equals("")) {
					return true;
				}
			}
						
			return false;


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
		return false;
	}
	
	private VirtuosoUserProfileStorage() {
	}
}
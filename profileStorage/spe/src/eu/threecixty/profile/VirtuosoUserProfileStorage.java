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

import eu.threecixty.profile.oldmodels.UserInteractionMode;

public class VirtuosoUserProfileStorage {
	
	private static final Object _sync = new Object();
	//private static MyFactory myFactory;
	
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
	public synchronized static boolean saveProfile(eu.threecixty.profile.UserProfile profile) {
		if (profile == null) return false;
		
		try {
			
			saveNameInfoToKB(profile.getHasUID(),profile.getHasName());
			
			saveAddressInfoToKB(profile.getHasUID(), profile.getHasAddress());
			
			saveLastCrawlTimeToKB(profile.getHasUID(), profile.getHasLastCrawlTime());
			
			saveProfileIdentitiesToKB(profile.getHasUID(), profile.getHasProfileIdenties());
			
			saveKnowsToKB(profile.getHasUID(), profile.getKnows());
			
			savePreferenceToKB(profile.getHasUID(), profile.getPreferences());
			
			saveTransportToKB(profile.getHasUID(), profile.getPreferences().getHasTransport());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
					
				    String uidknows = qs.getLiteral("uidknows").toString();
				    
				    if (uidknows!=null)
				    	knowsTemp.add(uidknows);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			//add new knows profiles
			Set<String> knowsNotInKB=knows;
			knowsNotInKB.removeAll(knowsTemp);
			
			String str="";
			
			if (knowsNotInKB!=null){
				
				Iterator <String> iterators = knowsNotInKB.iterator();
				for ( ; iterators.hasNext(); ){
				
					str=GetSetQueryStrings.setUser(iterators.next());
					virtuosoConnection.insertDeleteQuery(str);
					eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentities=new eu.threecixty.profile.oldmodels.ProfileIdentities();
					profileIdentities.setHasSource("https://plus.google.com");
					profileIdentities.setHasProfileIdentitiesURI(" profile:"+iterators.next()+"/ProfileItentities/"+profileIdentities.getHasSource());
					profileIdentities.setHasUserAccountID(iterators.next());
					profileIdentities.setHasUserInteractionMode(UserInteractionMode.Active);
					str=GetSetQueryStrings.setProfileIdentities(iterators.next(), profileIdentities);
					virtuosoConnection.insertDeleteQuery(str);
				}
				str = GetSetQueryStrings.setMultipleKnows(uid,knowsNotInKB);
				virtuosoConnection.insertDeleteQuery(str);
			}
			
			//delete know from profileKB
			//only delete links profile remains
			Set<String> knowsToDeleteKB=knowsTemp;
			knowsToDeleteKB.removeAll(knows);
			
			if (knowsToDeleteKB!=null){
				str = GetSetQueryStrings.removeMultipleKnows(uid,knowsToDeleteKB);
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
					String uri = qs.getLiteral("pi").toString();
				    String source = qs.getLiteral("source").toString();
				    String piID = qs.getLiteral("piID").toString();
				    String uIM = qs.getLiteral("uIM").toString();
				    
				    if (uri!=null)
				    	tmpProfile.setHasProfileIdentitiesURI(uri);
				    if (source!=null)
				    	tmpProfile.setHasSource(source);
				    if (piID!=null)
				    	tmpProfile.setHasUserAccountID(piID);	
				    if (uIM!=null)
				    	tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uIM));	
				    
				    oldProfiles.add(tmpProfile); 
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			String str = GetSetQueryStrings.removeMultipleProfileIdentities(uid, oldProfiles);
			virtuosoConnection.insertDeleteQuery(str);
			
			str = GetSetQueryStrings.setMultipleProfileIdentities(uid, profileIdentities);
			virtuosoConnection.insertDeleteQuery(str);
						
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

	private static void saveLastCrawlTimeToKB(String uid, String time) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=virtuosoConnection.processConfigFile();

			if (conn == null) return;
			
			stmt = conn.createStatement();
			
			String str = GetSetQueryStrings.removeLastCrawlTime(uid, time);
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
					String likeURI = qs.getLiteral("likes").toString();
					String likeName = qs.getLiteral("likeName").toString();
				    String liketype = qs.getLiteral("liketype").toString();
				    if (likeURI!=null)
				    	oldLike.setHasLikesURI(likeURI);
				    if (likeName!=null)
				    	oldLike.setHasLikeName(likeName);
				    if (liketype!=null)
				    	oldLike.setHasLikeType(eu.threecixty.profile.oldmodels.LikeType.valueOf(liketype));	
				   oldLikes.add(oldLike);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeMultipleLikes(preferenceURI, oldLikes);
			virtuosoConnection.insertDeleteQuery(str);
		

			str = GetSetQueryStrings.setMultipleLikes(preferenceURI, likes);
			virtuosoConnection.insertDeleteQuery(str);
			
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
					
				    String prefURI = qs.getLiteral("pref").toString();
				    
				    if (prefURI!=null)
				    	oldPrefs.setHasPreferenceURI(prefURI);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			String str = GetSetQueryStrings.removePreferences(uid,oldPrefs.getHasPreferenceURI());
			virtuosoConnection.insertDeleteQuery(str);
			
			
			if (preference.getHasPreferenceURI()==null || preference.getHasPreferenceURI()=="")
				preference.setHasPreferenceURI("profile:"+uid+"/Preference");
			
			str = GetSetQueryStrings.setPreferences(uid,preference.getHasPreferenceURI());
			virtuosoConnection.insertDeleteQuery(str);
			
			saveLikesToKB(uid, preference.getHasPreferenceURI(), preference.getHasLikes());
			
			saveTripPreferenceToKB(uid, preference.getHasPreferenceURI(), preference.getHasTripPreference());
			
			savePlacePreferenceToKB(uid, preference.getHasPreferenceURI(), preference.getHasPlacePreference());
						
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
					
					String placePreferenceURI = qs.getLiteral("placePreference").toString();
					String placeDetailPreferenceURI = qs.getLiteral("placeDetailPreference").toString();
					
					if (placePreferenceURI!=null)
				    	placePreference.setHasPlacePreferenceURI(placePreferenceURI);
					if (placeDetailPreferenceURI!=null){
				       	loadPlaceDetailPreferenceFromKBToPlacePreference(placeDetailPreferenceURI,placePreference);
				    }
				    oldPlacePreferences.add(placePreference);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators=oldPlacePreferences.iterator();
			for ( ; iterators.hasNext(); ){ 
				String str = GetSetQueryStrings.removePlaceDetailPreference(iterators.next().getHasPlacePreferenceURI(), iterators.next().getHasPlaceDetailPreference());
				virtuosoConnection.insertDeleteQuery(str);
				str = GetSetQueryStrings.removePlacePreferences(preferenceURI, iterators.next().getHasPlacePreferenceURI());
				virtuosoConnection.insertDeleteQuery(str);
			}	
			
			iterators=placePreferences.iterator();
			for ( ; iterators.hasNext(); ){ 
				String str = GetSetQueryStrings.setPlaceDetailPreference(iterators.next().getHasPlacePreferenceURI(), iterators.next().getHasPlaceDetailPreference());
				virtuosoConnection.insertDeleteQuery(str);
				str = GetSetQueryStrings.setPlacePreferences(preferenceURI, iterators.next().getHasPlacePreferenceURI());
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
					String tripPreferenceURI = qs.getLiteral("tripPreference").toString();
					String preferredMaxTotalDistance = qs.getLiteral("preferredMaxTotalDistance").toString();
				    String preferredTripDuration = qs.getLiteral("preferredTripDuration").toString();
				    String preferredTripTime = qs.getLiteral("preferredTripTime").toString();
					String preferredCity = qs.getLiteral("preferredCity").toString();
				    String preferredCountry = qs.getLiteral("preferredCountry").toString();
				    String preferredWeatherCondition = qs.getLiteral("preferredWeatherCondition").toString();
					String preferredMinTimeOfAccompany = qs.getLiteral("preferredMinTimeOfAccompany").toString();
				    String modality = qs.getLiteral("modality").toString();
				    
				    if (tripPreferenceURI!=null)
				    	tripPreference.setHasTripPreferenceURI(tripPreferenceURI);
				    if (preferredMaxTotalDistance!=null)
				    	tripPreference.setHasPreferredMaxTotalDistance(Double.parseDouble(preferredMaxTotalDistance));
				    if (preferredTripDuration!=null)
				    	tripPreference.setHasPreferredTripDuration(Long.parseLong(preferredTripDuration));
				    if (preferredTripTime!=null)
				    	tripPreference.setHasTripPreferenceURI(preferredTripTime);
				    if (preferredCity!=null)
				    	tripPreference.setHasPreferredCity(preferredCity);
				    if (preferredCountry!=null)
				    	tripPreference.setHasPreferredCountry(preferredCountry);
				    if (preferredWeatherCondition!=null)
				    	tripPreference.setHasPreferredWeatherCondition(preferredWeatherCondition);
				    if (preferredMinTimeOfAccompany!=null)
				    	tripPreference.setHasPreferredMinTimeOfAccompany(Long.parseLong(preferredMinTimeOfAccompany));
				    if (modality!=null)
				    	tripPreference.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(modality));
				    
				    
				    oldTripPreferences.add(tripPreference);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeMultipleTripPreferences(preferenceURI, oldTripPreferences);
			virtuosoConnection.insertDeleteQuery(str);
			str = GetSetQueryStrings.setMultipleTripPreferences(preferenceURI, tripPreferences);
			virtuosoConnection.insertDeleteQuery(str);

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
	 * Creates transport object store in the KB.
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
					String transport = qs.getLiteral("transport").toString();

					if (transport==null) break;
					
					eu.threecixty.profile.oldmodels.Transport oldtransport = new eu.threecixty.profile.oldmodels.Transport();
					
					oldtransport.setHasTransportURI(transport);
					
					queryReturnClass qRCRegularTrips=virtuosoConnection.query(GetSetQueryStrings.getRegularTripsForTransport(transport));
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
					
					queryReturnClass qRCAccompanying=virtuosoConnection.query(GetSetQueryStrings.getAccompanyingForTransport(transport));
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
				
				str = GetSetQueryStrings.removeMultipleAccompanyingAssociatedToSpecificTransport(iterators.next().getHasTransportURI(), iterators.next().getHasAccompanyings());
				virtuosoConnection.insertDeleteQuery(str);
				Set <eu.threecixty.profile.oldmodels.RegularTrip> setRegTrip=iterators.next().getHasRegularTrip();
				Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iteratorRegTrip=setRegTrip.iterator();
				for ( ; iteratorRegTrip.hasNext(); ){ 
					str = GetSetQueryStrings.removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(iteratorRegTrip.next().getHasRegularTripURI(), iteratorRegTrip.next().getHasPersonalPlacesNew());
					virtuosoConnection.insertDeleteQuery(str);
				}
				str = GetSetQueryStrings.removeMultipleRegularTripsAssociatedToSpecificTransport(iterators.next().getHasTransportURI(), iterators.next().getHasRegularTrip());
				virtuosoConnection.insertDeleteQuery(str);
				
				str = GetSetQueryStrings.removeTransport(uid, iterators.next().getHasTransportURI());
				virtuosoConnection.insertDeleteQuery(str);
			}
			
			iterators=transports.iterator();
			for ( ; iterators.hasNext(); ){ 
				if (iterators.next().getHasTransportURI()==null||iterators.next().getHasTransportURI()=="") {
					iterators.next().setHasTransportURI("profile:"+uid+"/Transport/"+UUID.randomUUID().toString());
				}
				
				str = GetSetQueryStrings.setMultipleAccompanyingAssociatedToSpecificTransport(iterators.next().getHasTransportURI(), iterators.next().getHasAccompanyings());
				virtuosoConnection.insertDeleteQuery(str);
				
				Set <eu.threecixty.profile.oldmodels.RegularTrip> setRegTrip=iterators.next().getHasRegularTrip();
				
				Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iteratorsRegularTrip=setRegTrip.iterator();
				for ( ; iteratorsRegularTrip.hasNext(); ){ 
					if (iteratorsRegularTrip.next().getHasRegularTripURI()==null||iteratorsRegularTrip.next().getHasRegularTripURI()=="") {
						iteratorsRegularTrip.next().setHasRegularTripURI(iterators.next().getHasTransportURI()+"/RegularTrip/"+UUID.randomUUID().toString());
					}
					str = GetSetQueryStrings.setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(iteratorsRegularTrip.next().getHasRegularTripURI(), iteratorsRegularTrip.next().getHasPersonalPlacesNew());
					virtuosoConnection.insertDeleteQuery(str);
				}
				str = GetSetQueryStrings.setMultipleRegularTripsAssociatedToSpecificTransport(iterators.next().getHasTransportURI(), iterators.next().getHasRegularTrip());
				virtuosoConnection.insertDeleteQuery(str);
				
				str = GetSetQueryStrings.removeTransport(uid, iterators.next().getHasTransportURI());
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
					String nameuri = qs.getLiteral("name").toString();
					String gn = qs.getLiteral("givenname").toString();
				    String fn = qs.getLiteral("familyname").toString();
				    
				    if (nameuri!=null)
				    	toNameTemp.setHasNameURI(nameuri);
				    if (fn!=null)
				    	toNameTemp.setFamilyName(fn);
				    if (gn!=null)
				    	toNameTemp.setGivenName(gn);
				    
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeName(uid, toNameTemp);
			virtuosoConnection.insertDeleteQuery(str);
			
			name.setHasNameURI("profile:"+uid+"/Name");
			str = GetSetQueryStrings.setName(uid, name);
			virtuosoConnection.insertDeleteQuery(str);
			

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
					String addressuri = qs.getLiteral("address").toString();
				    String cname = qs.getLiteral("countryname").toString();
				    String tname = qs.getLiteral("townname").toString();
				    String lon = qs.getLiteral("longitude").toString();
				    String lat = qs.getLiteral("lat").toString();
				    if (addressuri!=null)
				    	oldddress.setHasAddressURI(addressuri);
				    if (cname!=null)
				    	oldddress.setCountryName(cname);
				    if (tname!=null)
				    	oldddress.setTownName(tname);	
				    if (lon!=null)
				    	oldddress.setLongitute(Double.parseDouble(lon));
				    if (lat!=null)
				    	oldddress.setLatitude(Double.parseDouble(lat));	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String str = GetSetQueryStrings.removeAddress(uid, oldddress);
			virtuosoConnection.insertDeleteQuery(str);
			
			address.setHasAddressURI("profile:"+uid+"/Address");
			str = GetSetQueryStrings.setAddress(uid, address);
			virtuosoConnection.insertDeleteQuery(str);
			

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
					String likeURI = qs.getLiteral("likes").toString();
					String likeName = qs.getLiteral("likeName").toString();
				    String liketype = qs.getLiteral("liketype").toString();
				    if (likeURI!=null)
				    	oldLikes.setHasLikesURI(likeURI);
				    if (likeName!=null)
				    	oldLikes.setHasLikeName(likeName);
				    if (liketype!=null)
				    	oldLikes.setHasLikeType(eu.threecixty.profile.oldmodels.LikeType.valueOf(liketype));	
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
	
	//Todo: ADD
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
				    String lastCrawlTime = qs.getLiteral("lastCrawlTime").toString();
				   
				    if (lastCrawlTime!=null)
				    	to.setHasLastCrawlTime(lastCrawlTime);	
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
					String nameuri = qs.getLiteral("name").toString();
				    String gn = qs.getLiteral("givenname").toString();
				    String fn = qs.getLiteral("familyname").toString();
				    if (nameuri!=null)
				    	toName.setHasNameURI(nameuri);
				    if (fn!=null)
				    	toName.setFamilyName(fn);
				    if (gn!=null)
				    	toName.setGivenName(gn);	
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
					String addressuri = qs.getLiteral("address").toString();
				    String cname = qs.getLiteral("countryname").toString();
				    String tname = qs.getLiteral("townname").toString();
				    String lon = qs.getLiteral("longitude").toString();
				    String lat = qs.getLiteral("lat").toString();
				    if (addressuri!=null)
				    	toAddress.setHasAddressURI(addressuri);
				    if (cname!=null)
				    	toAddress.setCountryName(cname);
				    if (tname!=null)
				    	toAddress.setTownName(tname);	
				    if (lon!=null)
				    	toAddress.setLongitute(Double.parseDouble(lon));
				    if (lat!=null)
				    	toAddress.setLatitude(Double.parseDouble(lat));	
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
					String profileIdentitiesURI = qs.getLiteral("?pi").toString();
				    String source = qs.getLiteral("source").toString();
				    String piID = qs.getLiteral("piID").toString();
				    String uIM = qs.getLiteral("uIM").toString();
				    
				    if (profileIdentitiesURI!=null)
				    	tmpProfile.setHasProfileIdentitiesURI(profileIdentitiesURI);
				    if (source!=null)
				    	tmpProfile.setHasSource(source);
				    if (piID!=null)
				    	tmpProfile.setHasUserAccountID(piID);	
				    if (uIM!=null)
				    	tmpProfile.setHasUserInteractionMode(eu.threecixty.profile.oldmodels.UserInteractionMode.valueOf(uIM));	
				    
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
					
				    String uidknows = qs.getLiteral("uidknows").toString();
				    
				    if (uidknows!=null)
				    	knows.add(uidknows);
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
					
				    String prefURI = qs.getLiteral("pref").toString();
				    
				    if (prefURI!=null)
				    	toPrefs.setHasPreferenceURI(prefURI);
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
					String transport = qs.getLiteral("transport").toString();

					if (transport==null) return;
					
					eu.threecixty.profile.oldmodels.Transport toTransport = new eu.threecixty.profile.oldmodels.Transport();
					
					toTransport.setHasTransportURI(transport);
					
					queryReturnClass qRCRegularTrips=virtuosoConnection.query(GetSetQueryStrings.getRegularTripsForTransport(transport));
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
					
					queryReturnClass qRCAccompanying=virtuosoConnection.query(GetSetQueryStrings.getAccompanyingForTransport(transport));
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
		
		String regularTripURI = qs.getLiteral("regularTrip").toString();
		String tripID = qs.getLiteral("tripID").toString();
	    String name = qs.getLiteral("name").toString();
	    String departureTime = qs.getLiteral("departureTime").toString();
	    String departureTimeSD = qs.getLiteral("departureTimeSD").toString();
	    String travelTime = qs.getLiteral("travelTime").toString();
	    String travelTimeSD = qs.getLiteral("travelTimeSD").toString();
	    String fastestTravelTime = qs.getLiteral("fastestTravelTime").toString();
	    String lastChanged = qs.getLiteral("lastChanged").toString();
	    String totalDistance = qs.getLiteral("totalDistance").toString();
	    String totalCount = qs.getLiteral("totalCount").toString();
	    String modalityType = qs.getLiteral("modalityType").toString();
	    String weekdayPattern = qs.getLiteral("weekdayPattern").toString();
	    String dayhourPattern = qs.getLiteral("dayhourPattern").toString();
	    String timePattern = qs.getLiteral("timePattern").toString();
	    String weatherPattern = qs.getLiteral("weatherPattern").toString();
	    
	    if (regularTripURI!=null)
    		toRegularTrip.setHasRegularTripURI(regularTripURI);
	    if (tripID!=null)
	    	toRegularTrip.setHasRegularTripId(Long.parseLong(tripID));
    	if (name!=null)
    		toRegularTrip.setHasRegularTripName(name);
    	if (departureTime!=null)
    		toRegularTrip.setHasRegularTripDepartureTime(Long.parseLong(departureTime));
    	if (departureTimeSD!=null)
    		toRegularTrip.setHasRegularTripDepartureTimeSD(Long.parseLong(departureTimeSD));
    	if (travelTime!=null)
    		toRegularTrip.setHasRegularTripTravelTime(Long.parseLong(travelTime));
    	if (travelTimeSD!=null)
    		toRegularTrip.setHasRegularTripTravelTimeSD(Long.parseLong(travelTimeSD));
    	if (fastestTravelTime!=null)
    		toRegularTrip.setHasRegularTripFastestTravelTime(Long.parseLong(fastestTravelTime));
    	if (lastChanged!=null)
    		toRegularTrip.setHasRegularTripLastChanged(Long.parseLong(lastChanged));
    	if (totalDistance!=null)
    		toRegularTrip.setHasRegularTripTotalDistance(Double.parseDouble(totalDistance));
    	if (totalCount!=null)
    		toRegularTrip.setHasRegularTripTotalCount(Long.parseLong(totalCount));
    	if (modalityType!=null)
    		toRegularTrip.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(modalityType));
    	if (weekdayPattern!=null)
    		toRegularTrip.setHasRegularTripWeekdayPattern(weekdayPattern);
    	if (dayhourPattern!=null)
    		toRegularTrip.setHasRegularTripDayhourPattern(dayhourPattern);
    	if (timePattern!=null)
    		toRegularTrip.setHasRegularTripTravelTimePattern(timePattern);
    	if (weatherPattern!=null)
    		toRegularTrip.setHasRegularTripWeatherPattern(weatherPattern);
    	
    	Set <eu.threecixty.profile.oldmodels.PersonalPlace> toPersonalPlaces = new HashSet <eu.threecixty.profile.oldmodels.PersonalPlace>();
		
    	if (regularTripURI!=null)
    		loadPersonalPlaceFromKBToRegularTrips(regularTripURI,toPersonalPlaces);
    	
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
					
					String externalIDs = qs.getLiteral("externalIDs").toString();
				    String latitude = qs.getLiteral("latitude").toString();
				    String longitude = qs.getLiteral("longitude").toString();
				    String stayDuration = qs.getLiteral("stayDuration").toString();
				    String accuracy = qs.getLiteral("accuracy").toString();
				    String stayPercentage = qs.getLiteral("stayPercentage").toString();
				    String pcode = qs.getLiteral("pcode").toString();
				    String weekDayPattern = qs.getLiteral("weekDayPattern").toString();
				    String dayHourPattern = qs.getLiteral("dayHourPattern").toString();
				    String placeType = qs.getLiteral("placeType").toString();
				    String placeName = qs.getLiteral("placeName").toString();
				    
				    
				    if (externalIDs!=null)
				    	toPersonalPlace.setHasPersonalPlaceexternalIds(externalIDs);
			    	if (latitude!=null)
			    		toPersonalPlace.setLatitude(Double.parseDouble(latitude));
		    		if (longitude!=null)
		    			toPersonalPlace.setLongitude(Double.parseDouble(longitude));
         			if (stayDuration!=null)
         				 toPersonalPlace.setHasPersonalPlaceStayDuration(Long.parseLong(stayDuration));
    				if (accuracy!=null)
    					 toPersonalPlace.setHasPersonalPlaceAccuracy(Double.parseDouble(accuracy));
					if (stayPercentage!=null)
						toPersonalPlace.setHasPersonalPlaceStayPercentage(Double.parseDouble(stayPercentage));
					if (pcode!=null)
						toPersonalPlace.setPostalcode(pcode);
					if (weekDayPattern!=null)
						toPersonalPlace.setHasPersonalPlaceWeekdayPattern(weekDayPattern);
					if (dayHourPattern!=null)
						toPersonalPlace.setHasPersonalPlaceDayhourPattern(dayHourPattern);
					if (placeType!=null)
						toPersonalPlace.setHasPersonalPlaceType(placeType);
					if (placeName!=null)
						toPersonalPlace.setHasPersonalPlaceName(placeName);
				    							
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
		String accompanyid = qs.getLiteral("accompany").toString();
	    String uid2 = qs.getLiteral("uid2").toString();
	    String score = qs.getLiteral("score").toString();
	    String validity = qs.getLiteral("validity").toString();
	    String acctime = qs.getLiteral("acctime").toString();
	    
	    if (accompanyid!=null)
	    	toAccompanying.setHasAccompanyURI(accompanyid);
	    if (uid2!=null)
	    	toAccompanying.setHasAccompanyUserid2ST(uid2);
	    	toAccompanying.setHasAccompanyUserid1ST(uid);
    	if (score!=null)
		   	toAccompanying.setHasAccompanyScore(Double.parseDouble(score));
    	if (validity!=null)
		   	toAccompanying.setHasAccompanyValidity(Long.parseLong(validity));
    	if (acctime!=null)
		   	toAccompanying.setHasAccompanyTime(Long.parseLong(acctime));
	}

	//Todo: Add
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
					String tripPreferenceURI = qs.getLiteral("tripPreference").toString();
					String preferredMaxTotalDistance = qs.getLiteral("preferredMaxTotalDistance").toString();
				    String preferredTripDuration = qs.getLiteral("preferredTripDuration").toString();
				    String preferredTripTime = qs.getLiteral("preferredTripTime").toString();
					String preferredCity = qs.getLiteral("preferredCity").toString();
				    String preferredCountry = qs.getLiteral("preferredCountry").toString();
				    String preferredWeatherCondition = qs.getLiteral("preferredWeatherCondition").toString();
					String preferredMinTimeOfAccompany = qs.getLiteral("preferredMinTimeOfAccompany").toString();
				    String modality = qs.getLiteral("modality").toString();
				    
				    if (tripPreferenceURI!=null)
				    	tripPreference.setHasTripPreferenceURI(tripPreferenceURI);
				    if (preferredMaxTotalDistance!=null)
				    	tripPreference.setHasPreferredMaxTotalDistance(Double.parseDouble(preferredMaxTotalDistance));
				    if (preferredTripDuration!=null)
				    	tripPreference.setHasPreferredTripDuration(Long.parseLong(preferredTripDuration));
				    if (preferredTripTime!=null)
				    	tripPreference.setHasTripPreferenceURI(preferredTripTime);
				    if (preferredCity!=null)
				    	tripPreference.setHasPreferredCity(preferredCity);
				    if (preferredCountry!=null)
				    	tripPreference.setHasPreferredCountry(preferredCountry);
				    if (preferredWeatherCondition!=null)
				    	tripPreference.setHasPreferredWeatherCondition(preferredWeatherCondition);
				    if (preferredMinTimeOfAccompany!=null)
				    	tripPreference.setHasPreferredMinTimeOfAccompany(Long.parseLong(preferredMinTimeOfAccompany));
				    if (modality!=null)
				    	tripPreference.setHasModalityType(eu.threecixty.profile.oldmodels.ModalityType.valueOf(modality));
				    
				    
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
					String placePreferenceURI = qs.getLiteral("placePreference").toString();
					String placeDetailPreferenceURI = qs.getLiteral("placeDetailPreference").toString();
				    
				    if (placePreferenceURI!=null)
				    	placePreference.setHasPlacePreferenceURI(placePreferenceURI);
				    if (placeDetailPreferenceURI!=null){
				    	loadPlaceDetailPreferenceFromKBToPlacePreference(placeDetailPreferenceURI,placePreference);
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
				String tmpuid = qs.getLiteral("uid").getString();
				if (tmpuid != null && !tmpuid.equals("")) {
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

package eu.threecixty.MobilityCrawlerCron;

import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.oldmodels.PlaceDetailPreference;
import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.ModalityType;
import eu.threecixty.profile.oldmodels.NatureOfPlace;
import eu.threecixty.profile.oldmodels.PersonalPlace;
import eu.threecixty.profile.oldmodels.PlacePreference;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.RegularTrip;
import eu.threecixty.profile.oldmodels.Transport;
import eu.threecixty.profile.oldmodels.TripPreference;

/**
 * Features reads movesmarter data.
 * 
 * @author Rachit@inria
 * 
 */

public class MobilityCrawlerCron {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 MobilityCrawlerCron.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	/**
	 * check network
	 * 
	 * @return boolean
	 */
	public Boolean CheckNetwork() {
		try {
			URL url = new URL("https://www.movesmarter.nl");
			InputStream input = url.openStream();
			if (input != null)
				input.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Calls Movesmarter APIs to get Accompanying and sets Accompanying object.
	 * Also sets setHasAccompanyings in transport object and
	 * setHasPreferredMinTimeOfAccompany in tripPreference.
	 * 
	 * @param: IDMapping map,
	 * @param: Set<IDMapping> idMapping,
	 * @param: String MobidotBaseurl,
	 * @param: String APIKey,
	 * @param: Long mobidotID,
	 * @param: Transport transport,
	 * @param: TripPreference tripPreference,
	 * 
	 */
	private void extractAccompanying(IDMapping map, Set<IDMapping> idMapping,
			String MobidotBaseurl, String APIKey, String mobidotID,
			Transport transport, TripPreference tripPreference) {
		if (DEBUG_MOD) LOGGER.info("Start extracting Accompanying");
		int length;
		String urlStr;
		urlStr = MobidotBaseurl + "measurement/Accompanies/" + mobidotID
				+ "/modifiedSince/" + "0" + "?key=" + APIKey;
		JSONArray resultAccompany = getTravelInfoforMobiditID(urlStr);

		length = resultAccompany.length();

		Set<Accompanying> accompanyings = new HashSet<Accompanying>();
		Long minTime = 31556926L * 2;// 2 years
		for (int i = 0; i < length; i++) {

			JSONObject jsonobj = resultAccompany.getJSONObject(i);
			
			if (DEBUG_MOD) LOGGER.info("Accompanying received from Mobidot: " + jsonobj);
			
			Accompanying hasAccompany = storeAccompanyingDetailsInKB(
					map.getThreeCixtyID(), jsonobj, idMapping);

			if (hasAccompany != null) {
				accompanyings.add(hasAccompany);
				if (minTime > hasAccompany.getHasAccompanyValidity()) {
					minTime = hasAccompany.getHasAccompanyValidity();
				}
			}
		}
		transport.setHasAccompanyings(accompanyings);
		tripPreference.setHasPreferredMinTimeOfAccompany(minTime);
		if (DEBUG_MOD) LOGGER.info("Finish extracting Accompanying");
	}
	
	private void extractAccompanying(IDMapping map, Set<IDMapping> idMapping,
			String MobidotBaseurl, String APIKey, String mobidotID,
			UserProfile profile) {
		if (DEBUG_MOD) LOGGER.info("Start extracting Accompanying");
		int length;
		String urlStr;
		urlStr = MobidotBaseurl + "measurement/Accompanies/" + mobidotID.trim()
				+ "/modifiedSince/" + "0" + "?key=" + APIKey;
		if (DEBUG_MOD) LOGGER.info("URL to get accompanying: " + urlStr);
		JSONArray resultAccompany = getTravelInfoforMobiditID(urlStr);

		if (resultAccompany == null) {
			if (DEBUG_MOD) LOGGER.info("Error while accessing to Mobidot server");
			return;
		}
		length = resultAccompany.length();

		Set<Accompanying> accompanyings = profile.getAccompanyings();
		if (accompanyings == null) {
			accompanyings = new HashSet <Accompanying>();
			profile.setAccompanyings(accompanyings);
		} else accompanyings.clear();
		Long minTime = 31556926L * 2;// 2 years
		for (int i = 0; i < length; i++) {

			JSONObject jsonobj = resultAccompany.getJSONObject(i);
			
			if (DEBUG_MOD) LOGGER.info("Accompanying received from Mobidot: " + jsonobj);
			
			Accompanying hasAccompany = storeAccompanyingDetailsInKB(
					map.getThreeCixtyID(), jsonobj, idMapping);

			if (hasAccompany != null) {
				if (DEBUG_MOD) LOGGER.info("Accompany extracted: AccompanyId = " + hasAccompany.getHasAccompanyId()
						+ ", AccompanyUserid2ST = " + hasAccompany.getHasAccompanyUserid2ST()
						+ ", AccompanyUserid1ST = " + hasAccompany.getHasAccompanyUserid1ST()
						+ ", AccompanyScore = " + hasAccompany.getHasAccompanyScore()
						+ ", AccompanyTime = " + hasAccompany.getHasAccompanyTime()
						+ ", AccompanyValidity = " + hasAccompany.getHasAccompanyValidity());
				accompanyings.add(hasAccompany);
				if (minTime > hasAccompany.getHasAccompanyValidity()) {
					minTime = hasAccompany.getHasAccompanyValidity();
				}
			}
		}
		if (DEBUG_MOD) LOGGER.info("Finish extracting Accompanying");
	}

	/**
	 * Calls Movesmarter APIs to get Personal Places. from the list of places
	 * gets most highly visited place and sets setHasPlaceDetailPreference in
	 * placePreference object and placePreferences in preference object
	 * 
	 * @param: String MobidotBaseurl,
	 * @param: String APIKey,
	 * @param: Preference pref,
	 * @param: Long mobidotID,
	 * @param: Long fromTime
	 * 
	 */
	private void extractPersonalPlaces(String MobidotBaseurl, String APIKey,
			Preference pref, String mobidotID, Long fromTime) {
		
		if (DEBUG_MOD) LOGGER.info("Start extracting PersonalPlaces");
		
		int length;
		String urlStr;

		urlStr = MobidotBaseurl + "measurement/Places/" + mobidotID
				+ "/modifiedSince/" + fromTime.toString()
				+ "/excluding/None?key=" + APIKey;
		JSONArray resultPersonalPlace = getTravelInfoforMobiditID(urlStr);

		Set<PlacePreference> placePreferences = new HashSet<PlacePreference>();
		PlacePreference placePreference = new PlacePreference();
		length = resultPersonalPlace.length();
		PlaceDetailPreference placeDetailPreference = new PlaceDetailPreference();
		if (length > 0) {
			placeDetailPreference
					.setHasNatureOfPlace(mapNatureOfPlace(resultPersonalPlace
							.getJSONObject(0).getString("type")));
			placePreference.setHasPlaceDetailPreference(placeDetailPreference);
			placePreferences.add(placePreference);
			pref.setHasPlacePreference(placePreferences);
			
			//if (DEBUG_MOD) LOGGER.info("Nature of Place: " + placeDetailPreference.getHasNatureOfPlace());
		}
		if (DEBUG_MOD) LOGGER.info("Finish extracting PersonalPlaces");
	}

	/**
	 * Calls Movesmarter APIs to get RegularTrip.
	 * 
	 * @param: IDMapping map,
	 * @param: UserProfile user,
	 * @param: String MobidotBaseurl,
	 * @param: String APIKey,
	 * @param: String mobidotID,
	 * @param: Transport transport,
	 * @return: regulartrip maxRegularTrip,
	 * 
	 */
	private RegularTrip extractRegularTrips(IDMapping map, UserProfile user,
			String MobidotBaseurl, String APIKey, String mobidotID,
			Transport transport) {
		if (DEBUG_MOD) LOGGER.info("Start extracting RegularTrips");
		int length;
		String urlStr = MobidotBaseurl + "personalmobility/RegularTrips/"
				+ mobidotID + "?key=" + APIKey;
		JSONArray resultRegularTrip = getTravelInfoforMobiditID(urlStr);

		length = resultRegularTrip.length();
		Set<RegularTrip> regularTrips = new HashSet<RegularTrip>();
		int maxTimeRegularTripMade = 0;
		RegularTrip maxRegularTrip = null;
		for (int i = 0; i < length; i++) {
			JSONObject jsonobj = resultRegularTrip.getJSONObject(i);
			
			if (DEBUG_MOD) LOGGER.info("RegularTrip received from Mobidot: " + jsonobj);

			RegularTrip regularTrip = storeRegularTripsInKB(
					map.getThreeCixtyID(), jsonobj, user);
			
			int count = (int) (long) regularTrip.getHasRegularTripTotalCount();
			if (maxTimeRegularTripMade < count) {
				maxTimeRegularTripMade = count;
				maxRegularTrip = regularTrip;
			}
			regularTrips.add(regularTrip);
		}
		if (DEBUG_MOD) LOGGER.info("Finish extracting RegularTrips");
		transport.setHasRegularTrip(regularTrips);
		return maxRegularTrip;
	}

	/**
	 * Get seconds since 1970 in String
	 * 
	 * @return: String
	 */
	private Long getDateTime() {
		return GregorianCalendar.getInstance().getTimeInMillis() / 1000;
	}
	
	/**
	 * Calls Movesmarter APIs to get data
	 * 
	 * @param: IDMapping map,
	 * @param: UserProfile user,
	 * @param: Set<IDMapping> idMapping,
	 * @param: String MobidotBaseurl,
	 * @param: String Domain,
	 * @param: String APIKey
	 * 
	 */
	public void getmobility(IDMapping map, UserProfile user,
			Set<IDMapping> idMapping, String MobidotBaseurl, String Domain,
			String APIKey) {
		
		String mobidotID=map.getMobidotID();
		
		if (mobidotID!=null) {
			Long currentTime = getDateTime();
			//map.setMobidotID(mobidotID);
			/*
			Preference pref = new Preference();
			
			Transport transport = new Transport();
			
			// extract and set Regular trips
			RegularTrip maxRegularTrip = extractRegularTrips(map, user,
					MobidotBaseurl, APIKey, mobidotID, transport);
	
			Long fromTime = currentTime - 604800L;
	
			// Extract and set Radius of gyration
			String urlStr = MobidotBaseurl + "measurement/Radius/" + mobidotID
					+ "/from/" + fromTime.toString() + "/to/"
					+ currentTime.toString() + "?key=" + APIKey
					+ "&nBins=3&cog=dynamic";
			String[] radius = getRadiusforMobiditID(urlStr);
			Double distance = Double.parseDouble(radius[1]);
	
			//set MobilityRelatedPreferences
			TripPreference tripPreference = new TripPreference();
			MobilityInferences mobilityInferences = new MobilityInferences();
			mobilityInferences.setTripPreferences(maxRegularTrip, tripPreference,
					distance / 1000, user.getHasAddress());
			Set<TripPreference> tripPreferences = new HashSet<TripPreference>();
			tripPreferences.add(tripPreference);
			pref.setHasTripPreference(tripPreferences);
	
			// Extract and set Accompanying details
			extractAccompanying(map, idMapping, MobidotBaseurl, APIKey, mobidotID,
					transport, tripPreference);
	
			// Extract and set Personal Places
			extractPersonalPlaces(MobidotBaseurl, APIKey, pref, mobidotID, fromTime);
	
			Set<Transport> transports = new HashSet<Transport>();
			transports.add(transport);
	
			pref.setHasTransport(transports);
			
			user.setPreferences(pref);
			*/
			
			extractAccompanying(map, idMapping, MobidotBaseurl, APIKey, mobidotID, user);
			user.setHasLastCrawlTime(currentTime.toString());
		}
	}

	/**
	 * get Radius Info for specified user. The urlStr is the call for specific
	 * movesmarter facility.
	 * 
	 * @param String urlStr,
	 * @return String []
	 */
	public String[] getRadiusforMobiditID(String urlStr) {
		if (CheckNetwork()) {
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(urlStr);
				InputStream input = url.openStream();
				byte[] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					sb.append(new String(b, 0, readBytes));
				}
				input.close();
				String[] ret = sb.toString().replace("[", "").replace("]", "")
						.split(",");
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * get Travel Info for specified user. The urlStr is the call for specific
	 * mobidot facility.
	 * 
	 * @param String
	 *            urlStr
	 * @return JSONArray
	 */
	public JSONArray getTravelInfoforMobiditID(String urlStr) {
		if (CheckNetwork()) {
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(urlStr);
				InputStream input = url.openStream();
				byte[] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					sb.append(new String(b, 0, readBytes));
				}
				JSONArray jsonob = new JSONArray(sb.toString());
				input.close();
				return jsonob;
			} catch (Exception e) {
				if (DEBUG_MOD) LOGGER.info(e.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * Map modality values to strings
	 * 
	 * @param modalityID
	 * @return String modality
	 */
	private String mapModality(int modalityID) {
		String modality = "Unknown";
		switch (modalityID) {
		case 0:
			modality = "Unknown";
		case 1:
			modality = "Foot";
		case 2:
			modality = "Bike";
		case 4:
			modality = "Car";
		case 8:
			modality = "Bus";
		case 16:
			modality = "Tram";
		case 32:
			modality = "Metro";
		case 64:
			modality = "Lightrail";
		case 128:
			modality = "Ferry";
		case 256:
			modality = "Train";
		case 512:
			modality = "Taxi";
		case 1024:
			modality = "Plane";
		case 2048:
			modality = "Boat";
		case 4096:
			modality = "Motor";
		case 8192:
			modality = "Moped";
		case 16384:
			modality = "IceSkates";
		case 32768:
			modality = "InlineSkates";
		case 65536:
			modality = "Animal";
		case 131072:
			modality = "WheelChair";
		case 262144:
			modality = "ElectricBike";
		case 524288:
			modality = "ElectricCar";
		case 1048576:
			modality = "FuelPowered";
		case 2097152:
			modality = "ElectricPowered";
		case 4194304:
			modality = "HumanPowered";
		case 8388608:
			modality = "AnimalPowered";
		case 16777216:
			modality = "PublicTransport";
		case 33554432:
			modality = "PrivateTransport";
		case 67108864:
			modality = "Other";
		case 134217728:
			modality = "Horse";
		case 268435456:
			modality = "Carriage";
		case 536870912:
			modality = "ElectricScooter";
		default:
			modality = "Unknown";
		}
		return modality;
	}

	/**
	 * Map nature Of Place to NatureOfPlace enum. The code has been down-graded
	 * to use java 1.5. to use switch case with strings java 1.7 is to be used.
	 * 
	 * @param String natureOfPlace
	 * @return NatureOfPlace value
	 */
	private NatureOfPlace mapNatureOfPlace(String natureOfPlace) {
		String toTest = natureOfPlace.toLowerCase();
		if (toTest == "busstation")
			return NatureOfPlace.BusStation;
		else if (toTest == "country")
			return NatureOfPlace.Country;
		else if (toTest == "city")
			return NatureOfPlace.City;
		else if (toTest == "college")
			return NatureOfPlace.College;
		else if (toTest == "museum")
			return NatureOfPlace.Museum;
		else if (toTest == "cemetery")
			return NatureOfPlace.Cemetery;
		else if (toTest == "church")
			return NatureOfPlace.Church;
		else if (toTest == "shop")
			return NatureOfPlace.Shop;
		else if (toTest == "restaurant")
			return NatureOfPlace.Restaurant;
		else if (toTest == "bar")
			return NatureOfPlace.Bar;
		else if (toTest == "hostel")
			return NatureOfPlace.Hostel;
		else if (toTest == "home")
			return NatureOfPlace.Home;
		else if (toTest == "hotel")
			return NatureOfPlace.Hotel;
		else if (toTest == "station")
			return NatureOfPlace.Station;
		else if (toTest == "metro")
			return NatureOfPlace.Metro;
		else if (toTest == "office")
			return NatureOfPlace.Office;
		else if (toTest == "university")
			return NatureOfPlace.University;
		else if (toTest == "school")
			return NatureOfPlace.School;
		else if (toTest == "mall")
			return NatureOfPlace.Mall;
		else if (toTest == "place")
			return NatureOfPlace.Place;
		else if (toTest == "theater")
			return NatureOfPlace.Theater;
		else if (toTest == "gare")
			return NatureOfPlace.Gare;
		else if (toTest == "mosque")
			return NatureOfPlace.Mosque;
		else if (toTest == "temple")
			return NatureOfPlace.Temple;
		else if (toTest == "placeofworship")
			return NatureOfPlace.PlaceOfWorship;
		else if (toTest == "establishment")
			return NatureOfPlace.Establishment;
		else if (toTest == "residence")
			return NatureOfPlace.Residence;
		else if (toTest == "others")
			return NatureOfPlace.Others;
		else
			return NatureOfPlace.None;
	}

	/**
	 * reverse map to 3cixty ID. i.e., get 3cixtyID from mobidotID
	 * 
	 * @param Long mobidotID,
	 * @param Set<IDMapping> idMapping,
	 * @return String 3cixtyID
	 */
    public String reverseMap(Long mobidotID, Set<IDMapping> idMapping) {
        Iterator<IDMapping> iteratorMapping = idMapping.iterator();
        while (iteratorMapping.hasNext()) {
            IDMapping map = iteratorMapping.next();
            if (DEBUG_MOD) LOGGER.info("map.ThreeCixty ID = " + map.getThreeCixtyID() +", map.MobidotID = " + map.getMobidotID());
            if (map.getMobidotID()!=null){
                if (map.getMobidotID().equals(mobidotID.toString())) {
                    return map.getThreeCixtyID();
                }
            }
        }
        return null;
    }

	/**
	 * create the accompany object to store in KB
	 * 
	 * @param uID
	 * @param jsonobj
	 * @param idMapping
	 * @return accompany object
	 */
    public Accompanying storeAccompanyingDetailsInKB(String uID,
                                                     JSONObject jsonobj, Set<IDMapping> idMapping) {
        Long jsonUserid2 = jsonobj.getLong("userid2");
        Long jsonUserid1 = jsonobj.getLong("userid1");
    	String IDUser2 = reverseMap(jsonUserid2, idMapping);
        String IDUser1 = reverseMap(jsonUserid1, idMapping);
        if (DEBUG_MOD) LOGGER.info("JSON userid1 = " + jsonUserid1 + ", userid2 = " + jsonUserid2);
        if (DEBUG_MOD) LOGGER.info("IDUser1 = " + IDUser1 + ", IDUser2 = " + IDUser2);
        if (IDUser1 != null && IDUser1.equals(uID)){
            Accompanying accompany = new Accompanying();
            accompany.setHasAccompanyId(jsonobj.getLong("id"));
            accompany.setHasAccompanyUserid2ST(IDUser2);
            accompany.setHasAccompanyUserid1ST(IDUser1);
            accompany.setHasAccompanyScore(jsonobj.getDouble("score"));
            // start time of the accompany
            accompany.setHasAccompanyTime(jsonobj.getLong("time"));
            // duration of the accompany
            accompany.setHasAccompanyValidity(jsonobj.getLong("validity"));
            return accompany;
        }
        if (IDUser2 != null && IDUser2.equals(uID)){
            Accompanying accompany = new Accompanying();
            accompany.setHasAccompanyId(jsonobj.getLong("id"));
            accompany.setHasAccompanyUserid2ST(IDUser1);
            accompany.setHasAccompanyUserid1ST(IDUser2);
            accompany.setHasAccompanyScore(jsonobj.getDouble("score"));
            // start time of the accompany
            accompany.setHasAccompanyTime(jsonobj.getLong("time"));
            // duration of the accompany
            accompany.setHasAccompanyValidity(jsonobj.getLong("validity"));
            return accompany;
        }
        return null;
    }

	/**
	 * create the regular trip object to store in KB
	 * 
	 * @param uID
	 * @param jsonobj
	 * @param user
	 * @return regularTrip object
	 */
	public RegularTrip storeRegularTripsInKB(String uID, JSONObject jsonobj,
			UserProfile user) {
		RegularTrip regularTrip = new RegularTrip();
		regularTrip.setHasUID(jsonobj.getLong("id"));
		regularTrip.setHasRegularTripName(jsonobj.getString("tripName"));
		regularTrip.setHasRegularTripDepartureTime(jsonobj
				.getLong("departureTime"));
		regularTrip.setHasRegularTripDepartureTimeSD(jsonobj
				.getLong("departureTimeSD"));
		regularTrip.setHasRegularTripTravelTime(jsonobj.getLong("travelTime"));
		regularTrip.setHasRegularTripTravelTimeSD(jsonobj
				.getLong("travelTimeSD"));
		regularTrip.setHasRegularTripFastestTravelTime(jsonobj
				.getLong("fastestTravelTime"));
		regularTrip.setHasRegularTripTotalDistance((double) jsonobj
				.getLong("totalDistance"));
		regularTrip.setHasRegularTripTotalCount(jsonobj.getLong("totalCount"));
		regularTrip.setHasModalityType(ModalityType.valueOf(mapModality(jsonobj
				.getInt("tripModality"))));
		regularTrip.setHasRegularTripWeekdayPattern(jsonobj
				.getString("weekdayPattern"));
		regularTrip.setHasRegularTripDayhourPattern(jsonobj
				.getString("dayhourPattern"));
		regularTrip
				.setHasRegularTripLastChanged(jsonobj.getLong("lastChanged"));
		if (jsonobj.has("travelTimePattern") == true) {
			regularTrip.setHasRegularTripTravelTimePattern(jsonobj
					.getString("travelTimePattern"));
		}
		if (jsonobj.has("weatherPattern") == true) {
		regularTrip.setHasRegularTripWeatherPattern(jsonobj
					.getString("weatherPattern"));
		}
		Set<PersonalPlace> personalPlaces = new HashSet<PersonalPlace>();
		JSONArray arr = jsonobj.getJSONArray("tripPlaces");
		for (int length = 0; length < arr.length(); length++) {
			JSONObject jsonarrobj = arr.getJSONObject(length);
			PersonalPlace personalPlace = new PersonalPlace();
			if (jsonarrobj.has("externalIds") == true) {
				personalPlace.setHasPersonalPlaceexternalIds(jsonarrobj
						.getString("externalIds"));
			}
			personalPlace.setPostalcode(jsonarrobj.getString("postalcode"));
			if (jsonarrobj.has("weekdayPattern") == true) {
				personalPlace.setHasPersonalPlaceWeekdayPattern(jsonarrobj
						.getString("weekdayPattern"));
			}
			personalPlace.setHasPersonalPlaceStayPercentage(jsonarrobj
					.getDouble("stayPercentage"));
			personalPlace.setHasPersonalPlaceType(jsonarrobj.getString("type"));
			personalPlace.setHasUID(jsonarrobj.getLong("id"));
			if (jsonarrobj.has("dayhourPattern") == true) {
				personalPlace.setHasPersonalPlaceDayhourPattern(jsonarrobj
						.getString("dayhourPattern"));
			}
			personalPlace.setHasPersonalPlaceName(jsonarrobj.getString("name"));
			personalPlace.setLatitude(jsonarrobj.getDouble("latitude"));
			personalPlace.setLongitude(jsonarrobj.getDouble("longitude"));
			personalPlace.setHasPersonalPlaceStayDuration(jsonarrobj
					.getLong("stayDuration"));
			personalPlace.setHasPersonalPlaceAccuracy(jsonarrobj
					.getDouble("accuracy"));

			personalPlaces.add(personalPlace);
		}
		regularTrip.setHasPersonalPlacesNew(personalPlaces);
		return regularTrip;
	}
}

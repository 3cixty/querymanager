package eu.threecixty.CrawlerCron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;




import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.ModalityType;
import eu.threecixty.profile.oldmodels.PersonalPlace;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.RegularTrip;
import eu.threecixty.profile.oldmodels.Transport;

/**Features
 * Continuous run ever 3am
 * Pulling Movesmarter data (Trips, Regular trips, Accompany details) provided by Mobidot and writing it in to the profile KB.
 * No Direct API access to the component. The component connects to other components in the 3cixty Platform via KB.
 * if not connectivity then re-check 
 * History of when the job was last executed successfully.
 * The job checks for the network availability before execution of the new job.
 * accompany uses 3cixty IDs to related people 
 * inconsistent data field in json format output is handled

 * @author inria.fr
 *
 */

public class MobilityCrawlerCron {

	private final static long fONCE_PER_DAY = 1000*60*60*24;

	private final static int fONE_DAY = 1;
	private final static int fTHREE_AM = 3;
	private final static int fZERO_MINUTES = 0;

	private final static String MOBIDOT_BASEURL="https://www.movesmarter.nl/external/";
//	private final static String MOBIDOT_URL="https://www.movesmarter.nl/portal";
	
	// TODO: change this value to your key
	private final static String MOBIDOT_API_KEY = "SRjHX5yHgqqpZyiYaHSXVqhlFWzIEoxUBmbFcSxiZn58Go02rqB9gKwFqsGx5dks";
	// TODO: change this value to your domain at Mobidot
	private final static String DOMAIN = "3cixty";
	
	/**
	 * get the last successful runtime for the cronMobility  
	 * @return time milli-sec after epoch in String format
	 */
	private static String getLastRuntime() {
		BufferedReader br = null;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream("lastTime");
			br = new BufferedReader(new InputStreamReader(fis));
			String lastRuntime=br.readLine();
			br.close();
			fis.close();
			return lastRuntime;
		}catch(Exception e)
		{
			e.printStackTrace();
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}
	}
	/**
	 * set the last successful runtime for the cronMobility
	 * @param long lastRunTime: last successful run time in milli-sec after epoch
	 */
	private void setLastRuntime(Long lastRuntime) {
		try{
			BufferedWriter wr= new BufferedWriter(new FileWriter("lastTime"));
			wr.write(Long.toString(lastRuntime));
			wr.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	private static long getFoncePerDay() {
		return fONCE_PER_DAY;
	}

	private static int getFoneDay() {
		return fONE_DAY;
	}

	private static int getFthreeAm() {
		return fTHREE_AM;
	}

	private static int getFzeroMinutes() {
		return fZERO_MINUTES;
	}

	private static String getMobidotBaseurl() {
		return MOBIDOT_BASEURL;
	}

	private static String getMobidotApiKey() {
		return MOBIDOT_API_KEY;
	}

	private static String getDomain() {
		return DOMAIN;
	}
	
	/**
	 * Returns Date for next day 3am
	 * @return Date 
	 */
	private static Date getTomorrowMorning3am() {
		Calendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DATE, getFoneDay());
		Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR),
				tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE), getFthreeAm(), getFzeroMinutes());
		return result.getTime();
	}
	
//	/** 
//	 * Construct and use a TimerTask and Timer. 
//	 */
//	public static void main (String... arguments ) {
//		TimerTask mobilityCrawlerCron = new MobilityCrawlerCron();
//	    Timer timer = new Timer();
//		//timer.scheduleAtFixedRate(userPerformedAction, getTime(), 1000*10);
//		timer.scheduleAtFixedRate(mobilityCrawlerCron, getTomorrowMorning3am(), getFoncePerDay());
//	}
	
	/**
	 * Crawls Mobidot info.
	 */
	public void run() {
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				try {
				    crawl();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, getTomorrowMorning3am(), getFoncePerDay());
	}
	
	/**
	* Main entry to crawl Mobidot info.
	*/
	private void crawl(){
        //get all 3cixtyIDs and mobidotUserNames
        Set<IDMapping> idMapping = ProfileManagerImpl.getInstance().getIDMappings();
        long currentTime = getDateTime();
        try{
        	Iterator<IDMapping> iteratorMapping = idMapping.iterator();
        	
        	while (iteratorMapping.hasNext()){
        		IDMapping map = iteratorMapping.next();
        		int length=0;
        		UserProfile user = new UserProfile();
        		user.setHasUID(map.getThreeCixtyID());
        		Long mobidotID = getMobidotIDforUsername(map.getMobidotUserName());
        		map.setMobidotID(mobidotID);
        		Transport transport = new Transport();
        		if (mobidotID == null) continue;
        		
        		String urlStr=getMobidotBaseurl() +"personalmobility/RegularTrips/"+ mobidotID + "?key=" + getMobidotApiKey();
    			JSONArray resultRegularTrip = getTravelInfoforMobiditID(urlStr);
    			
    			urlStr=getMobidotBaseurl() + "measurement/Accompanies/" + mobidotID + "/modifiedSince/" + getLastRuntime() + "?key=" + getMobidotApiKey();
    			JSONArray resultAccompany = getTravelInfoforMobiditID(urlStr);
        		
    			length=resultRegularTrip.length();
        		Set <RegularTrip> regularTrips = new HashSet <RegularTrip>();	    	
		    	for (int i = 0; i < length; i++) {
		    		JSONObject jsonobj = resultRegularTrip.getJSONObject(i);
		    		
		    		regularTrips.add(storeRegularTripsInKB(map.getThreeCixtyID(), jsonobj, user));
		    	}
		    	transport.setHasRegularTrip(regularTrips);

		    	length=resultAccompany.length();
		    	
		    	Set <Accompanying> accompanyings = new HashSet <Accompanying>();
		    	for (int i=0;i<length;i++){
		    	
		    		JSONObject jsonobj = resultAccompany.getJSONObject(i);
		    		Accompanying hasAccompany = storeAccompanyingDetailsInKB(map.getThreeCixtyID(), jsonobj, idMapping);
		    		if (hasAccompany!=null){
		    			accompanyings.add(hasAccompany);
		    		}
		    	}
		    	
		    	transport.setHasAccompanyings(accompanyings);
		    	
		    	Preference pref = user.getPreferences();
		    	if (pref == null) {
		    		pref = new Preference();
		    		user.setPreferences(pref);
		    	}
		    	Set <Transport> transports = pref.getHasTransport();
		    	if (transports == null) {
		    		transports = new HashSet <Transport>();
		    		pref.setHasTransport(transports);
		    	}
		    	transports.add(transport);
		    	
		    	ProfileManagerImpl.getInstance().saveProfile(user);
        	}
	        setLastRuntime(currentTime);	
    	}catch(Exception e){
    		e.printStackTrace();
    		crawl();
    	}
	}

	/**
	 * create the accompany object to store in KB
	 * @param uID
	 * @param jsonobj
	 * @param idMapping
	 * @return accompany object
	 */
	private Accompanying storeAccompanyingDetailsInKB(String uID, JSONObject jsonobj, Set<IDMapping> idMapping) {
		String ID=reverseMap(jsonobj.getLong("userid2"),idMapping);
		if (ID!=null) {
			Accompanying accompany = new Accompanying();
			accompany.setHasAccompanyId(jsonobj.getLong("id"));
			accompany.setHasAccompanyUserid2(Long.valueOf(ID));
			accompany.setHasAccompanyUserid1(Long.valueOf(uID));
			accompany.setHasAccompanyScore(jsonobj.getDouble("score"));
			//start time of the accompany
			accompany.setHasAccompanyTime(jsonobj.getLong("time"));
			//duration of the accompany
			accompany.setHasAccompanyValidity(jsonobj.getLong("validity"));
			return accompany;
		}
		return null;
	}

	/**
	 * reverse map to 3cixty ID
	 * @param mobidotID
	 * @param idMapping
	 * @return 3cixty id in string
	 */
	private String reverseMap(Long mobidotID, Set<IDMapping> idMapping) {
		Iterator<IDMapping> iteratorMapping = idMapping.iterator();
    	while (iteratorMapping.hasNext()){
    		IDMapping map=iteratorMapping.next();
    		if (map.getMobidotID().equals(mobidotID)){
    			return map.getThreeCixtyID();
    		}
		}
    	return null;
	}

	/**
	 * create the regular trip object to store in KB
	 * @param uID
	 * @param jsonobj
	 * @param user
	 * @return regularTrip object
	 */
	private RegularTrip storeRegularTripsInKB(String uID, JSONObject jsonobj, UserProfile user) {
		RegularTrip regularTrip = new RegularTrip();
		regularTrip.setHasUID(jsonobj.getLong("id"));
		regularTrip.setHasRegularTripName(jsonobj.getString("tripName"));
		regularTrip.setHasRegularTripDepartureTime(jsonobj.getLong("departureTime"));
		regularTrip.setHasRegularTripDepartureTimeSD(jsonobj.getLong("departureTimeSD"));
		regularTrip.setHasRegularTripTravelTime(jsonobj.getLong("travelTime"));
		regularTrip.setHasRegularTripTravelTimeSD(jsonobj.getLong("travelTimeSD"));
		regularTrip.setHasRegularTripFastestTravelTime(jsonobj.getLong("fastestTravelTime"));
		regularTrip.setHasRegularTripTotalDistance((double) jsonobj.getLong("totalDistance"));
		regularTrip.setHasRegularTripTotalCount(jsonobj.getLong("totalCount"));
		regularTrip.setHasModalityType(ModalityType.valueOf(mapModality(jsonobj.getInt("tripModality"))));
		regularTrip.setHasRegularTripWeekdayPattern(jsonobj.getString("weekdayPattern"));
		regularTrip.setHasRegularTripDayhourPattern(jsonobj.getString("dayhourPattern"));
		regularTrip.setHasRegularTripLastChanged(jsonobj.getLong("lastChanged"));
		if (jsonobj.has("travelTimePattern")==true){
			regularTrip.setHasRegularTripTravelTimePattern(jsonobj.getString("travelTimePattern"));
		}
		List <PersonalPlace> personalPlaces = new ArrayList<PersonalPlace>(); 
		JSONArray arr=jsonobj.getJSONArray("tripPlaces");
		for (int length=0;length<arr.length();length++)
		{
			JSONObject jsonarrobj = arr.getJSONObject(length);
			PersonalPlace personalPlace = new PersonalPlace();
			if (jsonarrobj.has("externalIds")==true){
				personalPlace.setHasPersonalPlaceexternalIds(jsonarrobj.getString("externalIds"));
			}
			personalPlace.setPostalcode(jsonarrobj.getString("postalcode"));
			if (jsonarrobj.has("weekdayPattern")==true){
				personalPlace.setHasPersonalPlaceWeekdayPattern(jsonarrobj.getString("weekdayPattern"));
			}
			personalPlace.setHasPersonalPlaceStayPercentage(jsonarrobj.getDouble("stayPercentage"));
			personalPlace.setHasPersonalPlaceType(jsonarrobj.getString("type"));
			personalPlace.setHasUID(jsonarrobj.getLong("id"));
			if (jsonarrobj.has("dayhourPattern")==true){
				personalPlace.setHasPersonalPlaceDayhourPattern(jsonarrobj.getString("dayhourPattern"));
			}
			personalPlace.setHasPersonalPlaceName(jsonarrobj.getString("name"));
			personalPlace.setLatitude(jsonarrobj.getDouble("latitude"));
			personalPlace.setLongitude(jsonarrobj.getDouble("longitude"));
			personalPlace.setHasPersonalPlaceStayDuration(jsonarrobj.getLong("stayDuration"));
			personalPlace.setHasPersonalPlaceAccuracy(jsonarrobj.getDouble("accuracy"));
			
			personalPlaces.add(personalPlace);
		}
		regularTrip.setHasPersonalPlaces(personalPlaces.toArray(new PersonalPlace[personalPlaces.size()]));
		return regularTrip;
	}

	/**
	 * Map modality values to strings
	 * @param modalityID
	 * @return
	 */
	private String mapModality(int modalityID) {
		String modality="Unknown";
		switch(modalityID){
		case 0: modality="Unknown";
		case 1: modality="Foot";
		case 2: modality="Bike";
		case 4: modality="Car";
		case 8: modality="Bus";
		case 16: modality="Tram";
		case 32: modality="Metro";
		case 64: modality="Lightrail";
		case 128: modality="Ferry";
		case 256: modality="Train";
		case 512: modality="Taxi";
		case 1024: modality="Plane";
		case 2048: modality="Boat";
		case 4096: modality="Motor";
		case 8192: modality="Moped";
		case 16384: modality="IceSkates";
		case 32768: modality="InlineSkates";
		case 65536: modality="Animal";
		case 131072: modality="WheelChair";
		case 262144: modality="ElectricBike";
		case 524288: modality="ElectricCar";
		case 1048576: modality="FuelPowered";
		case 2097152: modality="ElectricPowered";
		case 4194304: modality="HumanPowered";
		case 8388608: modality="AnimalPowered";
		case 16777216: modality="PublicTransport";
		case 33554432: modality="PrivateTransport";
		case 67108864: modality="Other";
		case 134217728: modality="Horse";
		case 268435456: modality="Carriage";
		case 536870912: modality="ElectricScooter";
		default: modality="Unknown";
		}
		return modality;
	}
	
	/**
	 * check network
	 * @return boolean
	 */
	private Boolean CheckNetwork() {
		try{
		URL url = new URL("https://www.movesmarter.nl");
		InputStream input = url.openStream();
		if (input != null) input.close();
		return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	* Get seconds since 1970 in String
	* @return: String
	*/
	private Long getDateTime() {
		return GregorianCalendar.getInstance().getTimeInMillis()/1000;
	}
	
	/**
	 * get mobidotID from the mobidotUserName 
	 * @param mobidotusername
	 * @return mobidotID
	 */
	private Long getMobidotIDforUsername(String mobidotusername) {
		if (CheckNetwork()){
			String urlStr=getMobidotBaseurl()+"identitymanager/userIdForUser/"+getDomain()+"/"+mobidotusername+"?key="+getMobidotApiKey();
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(urlStr);
				InputStream input = url.openStream();
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					sb.append(new String(b, 0, readBytes));
				}
				input.close();
				return Long.parseLong(sb.toString());
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * get Travel Info for specified user. The urlStr is the call for specific mobidot facility.
	 * @param urlStr
	 * @return jsonArray
	 */
	private JSONArray getTravelInfoforMobiditID(String urlStr) {
		if (CheckNetwork()){
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(urlStr);
				InputStream input = url.openStream();
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = input.read(b)) >= 0) {
					sb.append(new String(b, 0, readBytes));
				}
				JSONArray jsonob=new JSONArray(sb.toString());
				input.close();
				return jsonob;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}

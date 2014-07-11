package eu.threecixty.CrawlerCron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.threecixty.models.Accompanying;
import eu.threecixty.models.MyFactory;
import eu.threecixty.models.PersonalPlace;
import eu.threecixty.models.RegularTrip;
import eu.threecixty.models.Transport;
import eu.threecixty.models.UserProfile;
import eu.threecixty.profile.RdfFileManager;

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
	private final static String MOBIDOT_URL="https://www.movesmarter.nl/portal";
	
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

	private static String getMobidotUrl() {
		return MOBIDOT_URL;
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
		// Kinh: should set path in which we run main method or initial Servlet
		//URL resourceUrl = MobilityCrawlerCron.class.getResource("/UserProfileKBmodelWithIndividuals.rdf");
		//RdfFileManager.getInstance().setPathToRdfFile(resourceUrl.getPath());
		Long currentTime = getDateTime();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(RdfFileManager.getInstance().getPathToRdfFile());
        IRI iri= IRI.create(file);

        OWLOntology ontology=null;
        MyFactory mf = null;
        try{
        	ontology= manager.loadOntologyFromOntologyDocument(iri);
        	mf = new MyFactory(ontology);
        }catch(Exception e){
        	System.out.println("bad code");
        	//run();
        }
        
        //get all 3cixtyIDs, mobidotUserName and mobidotIDs
        
        String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
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
    			+ "Filter(STR(?source) =\"" + getMobidotUrl() + "\"). \n"
    			+ "\n"
    			+ "}";
        
        Set<IDMapping> idMapping=getMobidotIDsForUsers(qStr,"uid","mobidotID");
        
        try{
        	Iterator<IDMapping> iteratorMapping = idMapping.iterator();
        	
        	while (iteratorMapping.hasNext()){
        		IDMapping map=iteratorMapping.next();
        		
        		int length=0;
        		UserProfile user= mf.getUserProfile("http://www.eu.3cixty.org/profile#"+map.getThreeCixtyID());
        		Transport transport = mf.createTransport("http://www.eu.3cixty.org/profile#"+map.getThreeCixtyID()+"Transport"+Long.toString(currentTime));
        		if (map.getMobidotID() == null) continue;
        		String mID=Long.toString(map.getMobidotID());
        		
        		String urlStr=getMobidotBaseurl() +"personalmobility/RegularTrips/"+ mID+ "?key="+getMobidotApiKey();
    			JSONArray resultRegularTrip=getTravelInfoforMobiditID(urlStr);
    			
    			urlStr=getMobidotBaseurl() +"measurement/Accompanies/"+mID + "/modifiedSince/" + getLastRuntime()+"?key="+getMobidotApiKey();
    			JSONArray resultAccompany=getTravelInfoforMobiditID(urlStr);
        		
    			length=resultRegularTrip.length();
        				    	
		    	for (int i=0;i<length;i++){
		    		JSONObject jsonobj = resultRegularTrip.getJSONObject(i);
		    		
		    		transport.addHasRegularTrip(storeRegularTripsInKB(map.getThreeCixtyID(),jsonobj,mf,user,currentTime,i));
		    	}
		    	length=resultAccompany.length();
		    	
		    	for (int i=0;i<length;i++){
		    	
		    		JSONObject jsonobj = resultAccompany.getJSONObject(i);
		    		Accompanying hasAccompany=storeAccompanyingDetailsInKB(map.getThreeCixtyID(),jsonobj,mf,user,currentTime,i,idMapping);
		    		if (hasAccompany!=null){
		    			transport.addHasAccompany(hasAccompany);
		    		}
		    	}
		    	
				user.addHasTransport(transport);
				try{		
					mf.saveOwlOntology();
				} catch(OWLOntologyStorageException e){
					e.printStackTrace();
				}
        	}
	        setLastRuntime(currentTime);	
    	}catch(Exception e){
    		e.printStackTrace();
    		crawl();
    	}
	}

	/**
	 * get MobidotIDs For the 3cixty Users
	 * @param queryString
	 * @param RDFresource
	 * @param extractLiteralUID
	 * @param extractLiteralMobidotUserName
	 * @return
	 */
	private Set<IDMapping> getMobidotIDsForUsers(String queryString, String extractLiteralUID, String extractLiteralMobidotUserName) {
		Set<IDMapping> idMapping=new HashSet<IDMapping>();
		Query query = QueryFactory.create(queryString);

		InputStream input = null;
		try {
			input = new FileInputStream(new File(RdfFileManager.getInstance().getPathToRdfFile()));

			Model rdfModel=null;
			if (input != null) {
				rdfModel = ModelFactory.createDefaultModel().read(input, "UTF-8");
			}
			QueryExecution qe = QueryExecutionFactory.create(query, rdfModel);
			ResultSet rs = qe.execSelect();
			for ( ; rs.hasNext(); ) {
				QuerySolution qs = rs.next();
				String UID = qs.getLiteral(extractLiteralUID).getString();
				String mobidotUserName = qs.getLiteral(extractLiteralMobidotUserName).getString();
				Long mobidotID= getMobidotIDforUsername(mobidotUserName);
				IDMapping mapper=new IDMapping();
				mapper.setThreeCixtyID(UID);
				mapper.setMobidotUserName(mobidotUserName);
				mapper.setMobidotID(mobidotID);
				idMapping.add(mapper);
			}
			input.close();
			return idMapping;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return idMapping;
	}

	/**
	 * create the accompany object to store in KB
	 * @param uID
	 * @param jsonobj
	 * @param mf
	 * @param user
	 * @param currentTime
	 * @param index
	 * @param idMapping
	 * @return accompany object
	 */
	private Accompanying storeAccompanyingDetailsInKB(String uID, JSONObject jsonobj, MyFactory mf, UserProfile user, Long currentTime, int index, Set<IDMapping> idMapping) {
		String ID=reverseMap(jsonobj.getLong("userid2"),idMapping);
		if (ID!=null) {
			Accompanying accompany=mf.createAccompanying("http://www.eu.3cixty.org/profile#"+uID+"AccompanyingDetails"+Long.toString(currentTime)+"_"+Integer.toString(index));
			accompany.addHasAccompanyID(jsonobj.getLong("id"));
			accompany.addHasAccompanyUserID2(ID);
			accompany.addHasAccompanyUserID1(uID);
			accompany.addHasAccompanyScore((float)jsonobj.getDouble("score"));
			//start time of the accompany
			accompany.addHasAccompanyTime(jsonobj.getLong("time"));
			//duration of the accompany
			accompany.addHasAccompanyValidity(jsonobj.getLong("validity"));
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
	private String reverseMap(Long mobidotID,Set<IDMapping> idMapping) {
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
	 * @param mf
	 * @param user
	 * @param currentTime
	 * @param index
	 * @return regularTrip object
	 */
	private RegularTrip storeRegularTripsInKB(String uID, JSONObject jsonobj, MyFactory mf, UserProfile user,Long currentTime, int index) {
		RegularTrip regularTrip=mf.createRegularTrip("http://www.eu.3cixty.org/profile#"+uID+"RegularTrip"+Long.toString(currentTime)+"_"+Integer.toString(index));
		regularTrip.addHasUID(Long.toString(jsonobj.getLong("id")));
		regularTrip.addHasRegularTripName(jsonobj.getString("tripName"));
		regularTrip.addHasRegularTripDepartureTime(jsonobj.getLong("departureTime"));
		regularTrip.addHasRegularTripDepartureTimeSD(jsonobj.getLong("departureTimeSD"));
		regularTrip.addHasRegularTripTravelTime(jsonobj.getLong("travelTime"));
		regularTrip.addHasRegularTripTravelTimeSD(jsonobj.getLong("travelTimeSD"));
		regularTrip.addHasRegularTripFastestTravelTime(jsonobj.getLong("fastestTravelTime"));
		regularTrip.addHasRegularTripTotalDistance(jsonobj.getLong("totalDistance"));
		regularTrip.addHasRegularTripTotalCount(jsonobj.getLong("totalCount"));
		regularTrip.addHasModalityType(mapModality(jsonobj.getInt("tripModality")));
		regularTrip.addHasRegularTripWeekdayPattern(jsonobj.getString("weekdayPattern"));
		regularTrip.addHasRegularTripDayHourPattern(jsonobj.getString("dayhourPattern"));
		regularTrip.addHasRegularTripLastChanged(jsonobj.getLong("lastChanged"));
		if (jsonobj.has("travelTimePattern")==true){
			regularTrip.addHasRegularTripTravelTimePattern(jsonobj.getString("travelTimePattern"));
		}
		
		JSONArray arr=jsonobj.getJSONArray("tripPlaces");
		for (int length=0;length<arr.length();length++)
		{
			JSONObject jsonarrobj = arr.getJSONObject(length);
			PersonalPlace personalPlace=mf.createPersonalPlace("http://www.eu.3cixty.org/profile#"+uID+"RegularTrip"+Long.toString(currentTime)+"_"+Integer.toString(index)+"PersonalPlace"+Integer.toString(length));
			if (jsonarrobj.has("externalIds")==true){
				personalPlace.addHasPersonalPlaceExternalIds(jsonarrobj.getString("externalIds"));
			}
			personalPlace.addPostal_code(jsonarrobj.getString("postalcode"));
			if (jsonarrobj.has("weekdayPattern")==true){
				personalPlace.addHasPersonalPlaceWeekDayPattern(jsonarrobj.getString("weekdayPattern"));
			}
			personalPlace.addHasPersonalPlaceStayPercentage((float)jsonarrobj.getDouble("stayPercentage"));
			personalPlace.addHasPersonalPlaceType(jsonarrobj.getString("type"));
			personalPlace.addHasUID(Long.toString(jsonarrobj.getLong("id")));
			if (jsonarrobj.has("dayhourPattern")==true){
				personalPlace.addHasPersonalPlaceDayHourPattern(jsonarrobj.getString("dayhourPattern"));
			}
			personalPlace.addHasPersonalPlaceName(jsonarrobj.getString("name"));
			personalPlace.addLatitude((float)jsonarrobj.getDouble("latitude"));
			personalPlace.addLongitude((float)jsonarrobj.getDouble("longitude"));
			personalPlace.addHasPersonalPlaceStayDuration(jsonarrobj.getLong("stayDuration"));
			personalPlace.addHasPersonalPlaceAccuracy((float)jsonarrobj.getDouble("accuracy"));
			
			regularTrip.addHasPersonalPlace(personalPlace);
		}
		
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

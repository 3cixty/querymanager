package eu.threecixty.profile.partners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.partners.PartnerAccount;

/**
 * 
 * Utility class to create, retrieve Mobidot users.
 *
 */
public class MobidotUserUtils {
	private static final Logger LOGGER = Logger.getLogger(
			MobidotUserUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	 
	 private static String MOBIDOT_BASEURL="https://www.movesmarter.nl";
	 private static String MOBIDOT_API_KEY = "SRjHX5yHgqqpZyiYaHSXVqhlFWzIEoxUBmbFcSxiZn58Go02rqB9gKwFqsGx5dks";
	 private static String DOMAIN="3cixty";
	 private static String GROUP="ExpoVisitor";
	 
	 public static int getMobidotId(String uid) {
		 if (uid == null || "".equals(uid)) return -1;
		 PartnerAccount account = PartnerAccountUtils.retrieveMobidotUser(uid); // check in 3cixty database
		 if (account == null) return -1;
		 return Integer.parseInt(account.getUser_id().trim());
	 }
	  
	 /**
	  * To Create users on MoveSmarter server using HttpURLConnection use this method. 
	  * @param uid
	  * @param name
	  * @param password
	  * @return
	  * @throws Exception
	  */
	 public static String createMobidotUser(String uid, String displayName, String password) throws Exception{
			if (DEBUG_MOD) LOGGER.info("Start creating Mobidot user");
			
			JSONObject userObj = new JSONObject();
			userObj.put("userName",uid);
			userObj.put("displayName", displayName);
			userObj.put("email", uid+"@3cixty.com");
			userObj.put("profile", new JSONObject());
			
			String urlStr = MOBIDOT_BASEURL + "/external/identitymanager/userindomain/"+DOMAIN+"/andgroup/"+GROUP+"/andpassword/"+password
					+ "?key=" + MOBIDOT_API_KEY;
			
			URL url = new  URL(urlStr);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.setRequestProperty("content-type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			
			OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());

			wr.write(userObj.toString());

			wr.flush();
			wr.close();
				
			if (DEBUG_MOD) LOGGER.info("Finished creating mobidot user");
			 
		    int response = conn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK){
				return getContent(conn.getInputStream());
			}
			if (response == HttpURLConnection.HTTP_BAD_REQUEST)	return getMobidotID(uid);
			else return null;
		}
	 
	 /**
	  * Get Mobidot User ID from Mobidot user name.
	  * @param username
	  * @return
	  * @throws Exception
	  */
	 public static String getMobidotID(String username) throws Exception{
		 if (DEBUG_MOD) LOGGER.info("Start check Mobidot user");
			
			String urlStr = MOBIDOT_BASEURL + "/external/identitymanager/userIdForUser/"+DOMAIN+"/"+username
					+ "?key=" + MOBIDOT_API_KEY;
			try {
				URL url = new URL(urlStr);
				return getContent(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	 }
	 
	 /**
	  * To get moveSmarterID that has max distance traveled. 
	  * @param movesmarterID1
	  * @param movesmarterID2
	  * @param movesmarterID3
	  * @return
	  * @throws Exception
	  */
	 public static int getMaxMobidotID(int movesmarterID1, int movesmarterID2, int movesmarterID3) throws Exception{
		 long endtime=GregorianCalendar.getInstance().getTimeInMillis() / 1000;
		 float distance1=getStatistics(movesmarterID1,endtime);
		 float distance2=getStatistics(movesmarterID2,endtime);
		 float distance3=getStatistics(movesmarterID3,endtime);
		 
		 if (distance1>distance2 && distance1>distance3){
			 return movesmarterID1;
		 }
		 else if (distance2>distance1 && distance2>distance3){
			 return movesmarterID2;
		 } 
		 else
			 return movesmarterID3;
	 }
	 /**
	  * To get moveSmarterID that has max distance traveled. 
	  * @param movesmarterID1
	  * @param movesmarterID2
	  * @return
	  * @throws Exception
	  */
	 public static int getMaxMobidotID(int movesmarterID1, int movesmarterID2) throws Exception{
		 long endtime=GregorianCalendar.getInstance().getTimeInMillis() / 1000;
		 float distance1=getStatistics(movesmarterID1,endtime);
		 float distance2=getStatistics(movesmarterID2,endtime);
		 return distance1>distance2 ? movesmarterID1:movesmarterID2;
		 
	 }
	 /**
	  * To Create users on MoveSmarter server using HttpURLConnection use this method. 
	  * @param movesmarterID
	  * @param endtime
	  * @return
	  * @throws Exception
	  */
	 private static float getStatistics(int movesmarterID, long endTime) throws Exception{
	
			if (DEBUG_MOD) LOGGER.info("getStats for the movesmarter user = "+movesmarterID);
	
			String urlStr = MOBIDOT_BASEURL + "/external/personalmobility/PeriodStatistics/"
					+movesmarterID+"/Month/false/"+"0"+"/"+endTime
					+ "?key=" + MOBIDOT_API_KEY;
			
			JSONArray resultStats = getStatsForMobidotID(urlStr);
			System.out.println(urlStr);
			if (DEBUG_MOD) LOGGER.info("Finished downloading the stats from mobidot for the user = "+movesmarterID);
			
			int length = resultStats.length();
			float sumDistance=0;
			for (int i = 0; i < length; i++) {
				
				JSONObject jsonobj = resultStats.getJSONObject(i);
				System.out.println(jsonobj);
				float distance = jsonobj.getLong("totalDistance");
				sumDistance+=distance;

			}
		    
			if (DEBUG_MOD) LOGGER.info("Finished aggregating the stats for the user = " +movesmarterID);
			return sumDistance;
		}
	 /**
		 * get Travel Info for specified user. The urlStr is the call for specific
		 * mobidot facility.
		 * 
		 * @param String
		 *            urlStr
		 * @return JSONArray
		 */
		private static JSONArray getStatsForMobidotID(String urlStr) {
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
	 
	 private static String getContent(InputStream input) {
		 StringBuilder sb = new StringBuilder();
		 byte [] b = new byte[1024];
		 int readBytes = 0;
		 try {
			while ((readBytes = input.read(b)) >= 0) {
				 sb.append(new String(b, 0, readBytes, "UTF-8"));
			 }
			input.close();
			return sb.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
			if (input != null)
				try {
					input.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		 return null;
	 }
	 
	 private MobidotUserUtils() {
	 }
}
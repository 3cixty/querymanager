package eu.threecixty.profile.partners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class MobidotUserUtils {
	private static final Logger LOGGER = Logger.getLogger(
			MobidotUserUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	 
	 private static String MOBIDOT_BASEURL="https://www.movesmarter.nl";
	 private static String MOBIDOT_API_KEY = "SRjHX5yHgqqpZyiYaHSXVqhlFWzIEoxUBmbFcSxiZn58Go02rqB9gKwFqsGx5dks";
	 private static String DOMAIN="3cixty";
	 private static String GROUP="ExpoVisitor";
	  
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
	 
	 private static String getContent(InputStream input) {
		 StringBuilder sb = new StringBuilder();
		 byte [] b = new byte[1024];
		 int readBytes = 0;
		 try {
			while ((readBytes = input.read(b)) >= 0) {
				 sb.append(new String(b, 0, readBytes, "UTF-8"));
			 }
			input.close();
			return sb.toString();
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
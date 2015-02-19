package eu.threecixty.profile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	 public static String createMobidotUser(String uid,eu.threecixty.profile.oldmodels.Name name, String password) throws Exception{
			if (DEBUG_MOD) LOGGER.info("Start creating Mobidot user");
			
			JSONObject userObj = new JSONObject();
			userObj.put("userName",uid);
			userObj.put("displayName", name.getGivenName()+" "+name.getFamilyName());
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
		    StringBuilder sb = new StringBuilder();
			if (response == HttpURLConnection.HTTP_OK){
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
				
				String line = null;  

			    while ((line = br.readLine()) != null) {  
			    	sb.append(line + "\n");  
			    }  

			    br.close();
			    return sb.toString();
			}
			if (response == HttpURLConnection.HTTP_BAD_REQUEST)	return getMobidotID(uid);
			else return null;
		}
	 
	 public static String getMobidotID(String username) throws Exception{
		 if (DEBUG_MOD) LOGGER.info("Start check Mobidot user");
			
			String urlStr = MOBIDOT_BASEURL + "/external/identitymanager/userIdForUser/"+DOMAIN+"/"+username
					+ "?key=" + MOBIDOT_API_KEY;
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
				if (sb.toString().isEmpty()) return null;
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	 }
	 
	 private MobidotUserUtils() {
	 }
}
package eu.threecixty.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Name;

/**
 * Utility class to update account info.
 *
 * @author Rachit Agarwal
 *
 */
public class GoogleAccountUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 GoogleAccountUtils.class.getName());
	
//	/**
//	 * Validates a given access token, 
//   * then extract Google info (name, knows) to update UserProfile if there is
//	 * no information about this user.
//	 * @param accessToken
//	 * @return public user ID if the given access token is valid. Otherwise, the method returns an empty string.
//	 */


	public static String getUID(String accessToken) {
		if (accessToken == null) return "";
		String user_id = null;
		try {
			
			if (!accessToken.equals("")) { // TODO: remove after testing
				String uid = String.valueOf(System.nanoTime()); // random uid
				UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid);
				profile.setHasUID(uid);
				String picture = "https://www.google.fr/images/srpr/logo11w.png";
				profile.setProfileImage(picture);
				Name name = new Name();
				profile.setHasName(name);
				String givenName = "GN" + RandomStringUtils.random(20);
				String familyName = RandomStringUtils.random(20) + "FN";
				name.setGivenName(givenName);
				name.setFamilyName(familyName);
				
				Random random = new Random();
				int val = random.nextInt(2);
				if (val == 0) {
					profile.setHasGender("Female");
				} else {
					profile.setHasGender("Male");
				}
				
				Set<String> knows = new HashSet<String>();
				
				knows.add("103411760688868522737"); // this would be useful to test augmentation query
				
				ProfileManagerImpl.getInstance().saveProfile(profile);
				
				return uid;
			}
			
			// due to error asked by Christian
//			String reqMsg = readUrl(
//					"https://www.googleapis.com/plus/v1/people/me?access_token=" + accessToken);
			String reqMsg = readUrl(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			user_id = json.getString("id");
//			JSONObject nameObj = json.getJSONObject("name");
			String givenName = json.getString("given_name");
			String familyName = json.getString("family_name");
			
			// XXX: always save UserProfile to update with GoogleProfile
			//if (ProfileManagerImpl.getInstance().existUID(user_id)) return user_id; // no need to update info as it exists
			
			String picture = json.getString("picture");
			
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(user_id);
			profile.setHasUID(user_id);
			profile.setProfileImage(picture);
			Name name = new Name();
			profile.setHasName(name);
			name.setGivenName(givenName);
			name.setFamilyName(familyName);

			if (json.has("gender")) {
				profile.setHasGender(json.getString("gender"));
			}
			
			// do this when login to 3cixty authorization say edit settings,
			// select circles you want the app to get info form.
			// then select only me.
			// finaly say authourize.
			// XXX: quick fix as TI could not get ProfileImage. Need to deal with Android OAuth Client to be able to get knows from Android Google access token
			try {
				reqMsg = readUrl("https://www.googleapis.com/plus/v1/people/me/people/visible?access_token="
						+ accessToken);
				json = new JSONObject(reqMsg);

				String nextPageToken = null;
				Set<String> knows = new HashSet<String>();


				if (json.has("nextPageToken")){		
					while(json.has("nextPageToken")){
						nextPageToken = json.getString("nextPageToken");
						JSONArray jsonArray = json.getJSONArray("items");
						int length=jsonArray.length();
						for (int i = 0; i < length; i++) {
							JSONObject jObject = jsonArray.getJSONObject(i);
							knows.add(jObject.getString("id"));
						}
						reqMsg = readUrl("https://www.googleapis.com/plus/v1/people/me/people/visible?access_token="
								+ accessToken+"&pageToken="+nextPageToken);
						json = new JSONObject(reqMsg);
					}
				}
				else{
					JSONArray jsonArray = json.getJSONArray("items");
					int length=jsonArray.length();
					for (int i = 0; i < length; i++) {
						JSONObject jObject = jsonArray.getJSONObject(i);
						knows.add(jObject.getString("id"));
					}
				}			
				
				// hack for Tony
				if (user_id.equals("117895882057702509461")) {
					if (!knows.contains("103411760688868522737")) { // does not know Animesh
						knows.add("103411760688868522737");
					}
				}
				
				profile.setKnows(knows);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
			}

			ProfileManagerImpl.getInstance().saveProfile(profile);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		if (user_id == null) return "";
		return user_id;
	}
	
	public static int getValidationTime(String accessToken) {
		try {
			String reqMsg = readUrl(
					"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			int time = json.getInt("expires_in");
			return time;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * Gets content from a given URL string.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	private GoogleAccountUtils() {
	}
}

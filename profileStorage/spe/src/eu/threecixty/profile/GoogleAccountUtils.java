package eu.threecixty.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

/**
 * Utility class to update account info.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class GoogleAccountUtils {
	
//	/**
//	 * Validates a given access token, then extract Google info to update UserProfile if there is
//	 * no information about this user.
//	 * @param accessToken
//	 * @return public user ID if the given access token is valid. Otherwise, the method returns an empty string.
//	 */


	public synchronized static String getUID(String accessToken) {
		if (accessToken == null) return "";
		String user_id = null;
		try {
			String reqMsg = readUrl(
					"https://www.googleapis.com/plus/v1/people/me?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			user_id = json.getString("id");
			JSONObject nameObj = json.getJSONObject("name");
			String givenName = nameObj.getString("givenName");
			String familyName = nameObj.getString("familyName");
			
			if (ProfileInformationStorage.existUID(user_id)) return user_id; // no need to update info as it exists
			
			ProfileInformation profile = new ProfileInformation();
			profile.setUid(user_id);
			profile.setFirstName(givenName);
			profile.setLastName(familyName);

			ProfileInformationStorage.saveProfile(profile);
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (user_id == null) return "";
		return user_id;
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

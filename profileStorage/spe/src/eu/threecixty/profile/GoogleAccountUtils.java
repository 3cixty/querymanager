package eu.threecixty.profile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * Utility class to update account info.
 *
 * @author Rachit Agarwal
 *
 */
public class GoogleAccountUtils {
	
	public static final String GOOGLE_SOURCE = "Google";
	
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
		String _3cixtyUID = null;
		try {
			
			if (!accessToken.equals("")) { // TODO: remove after testing
				String uid = String.valueOf(System.nanoTime()); // random uid
				Map <String, Boolean> attrs = Utils.getAttributesToLoadProfileFromGoogleFB();
				UserProfile profile = new UserProfile();
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
				String animeshGoogleUID = Utils.gen3cixtyUID("103411760688868522737", Utils.UidSource.GOOGLE);
				knows.add(animeshGoogleUID); // this would be useful to test augmentation query
				
				ProfileManagerImpl.getInstance().saveProfile(profile, attrs);
				
				return uid;
			}
			
			// due to error asked by Christian
//			String reqMsg = readUrl(
//					"https://www.googleapis.com/plus/v1/people/me?access_token=" + accessToken);
			String reqMsg = Utils.readUrl(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			user_id = json.getString("id");
//			JSONObject nameObj = json.getJSONObject("name");
			String givenName = json.getString("given_name");
			String familyName = json.getString("family_name");
						
			String picture = json.getString("picture");
			
			UserProfile profile = ProfileManagerImpl.getInstance().findUserProfile(
					user_id, GOOGLE_SOURCE, picture);
			if (profile == null) {
				_3cixtyUID = Utils.gen3cixtyUID(user_id, UidSource.GOOGLE);
				profile = new UserProfile();
				profile.setHasUID(_3cixtyUID);
			} else {
				_3cixtyUID = profile.getHasUID();
			}
						
			profile.setProfileImage(picture);
			Name name = new Name();
			profile.setHasName(name);
			name.setGivenName(givenName);
			name.setFamilyName(familyName);

			if (json.has("gender")) {
				profile.setHasGender(json.getString("gender"));
			}
			
			try {
				
				List <String> googleUidsOfFriends = getGoogleUidsOfFriends(accessToken);

				Set<String> knows = Utils.getOrCreate3cixtyUIDsForKnows(googleUidsOfFriends, GOOGLE_SOURCE);
				
				// hack for Tony
				if (user_id.contains("117895882057702509461")) {
					String animesh3cixtyUID = ProfileManagerImpl.getInstance().find3cixtyUID(
							"103411760688868522737", GOOGLE_SOURCE, null);
					if (!knows.contains(animesh3cixtyUID)) { // does not know Animesh
						knows.add(animesh3cixtyUID);
					}
				}
				
				profile.setKnows(knows);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
				return null;
			}

			Set <ProfileIdentities> profileIdentities = null;
			if (profile.getHasProfileIdenties() == null) {
				profileIdentities = new HashSet <ProfileIdentities>();
				profile.setHasProfileIdenties(profileIdentities);
			} else profileIdentities = profile.getHasProfileIdenties();
			
			Utils.setProfileIdentities(_3cixtyUID, user_id, GOOGLE_SOURCE, profileIdentities);
			
			Map <String, Boolean> attrs = Utils.getAttributesToStoreForCrawlingSocialProfile();
			
			ProfileManagerImpl.getInstance().saveProfile(profile, attrs);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		if (_3cixtyUID == null) return "";
		return _3cixtyUID;
	}
	
	private static List <String> getGoogleUidsOfFriends(String accessToken) throws Exception {
		String nextPageToken = null;
		List <String> googleUidsOfFriends = new LinkedList <String>();

		String reqMsg = Utils.readUrl("https://www.googleapis.com/plus/v1/people/me/people/visible?access_token="
				+ accessToken);
		JSONObject json = new JSONObject(reqMsg);


		if (json.has("nextPageToken")){		
			while(json.has("nextPageToken")){
				nextPageToken = json.getString("nextPageToken");
				JSONArray jsonArray = json.getJSONArray("items");
				int length=jsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject jObject = jsonArray.getJSONObject(i);
					googleUidsOfFriends.add(jObject.getString("id"));
				}
				reqMsg = Utils.readUrl("https://www.googleapis.com/plus/v1/people/me/people/visible?access_token="
						+ accessToken+"&pageToken="+nextPageToken);
				json = new JSONObject(reqMsg);
			}
		}
		else{
			JSONArray jsonArray = json.getJSONArray("items");
			int length=jsonArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject jObject = jsonArray.getJSONObject(i);
				googleUidsOfFriends.add(jObject.getString("id"));
			}
		}
		return googleUidsOfFriends;
	}
	
	public static int getValidationTime(String accessToken) {
		try {
			String reqMsg = Utils.readUrl(
					"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			int time = json.getInt("expires_in");
			return time;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}


	
	private GoogleAccountUtils() {
	}
}

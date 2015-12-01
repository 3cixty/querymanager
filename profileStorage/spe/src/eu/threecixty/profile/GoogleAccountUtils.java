package eu.threecixty.profile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * Utility class to extract information via Google access token.
 *
 * @author Rachit Agarwal
 *
 */
public class GoogleAccountUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 GoogleAccountUtils.class.getName());
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	/**
	/**
	 * Input: 		a Google access token.
	 * Output: 		3cixty UID.
	 * 
	 * <br>
	 * <br>
	 * The method does the following things:
	 * <pre>
	 * 1. Extract Google UID, firstName, lastName, and profile image.
	 * 2. Extract friends list if the user gives permission to 3cixty to crawl it.
	 * 3. Check whether or not there is a profile corresponding with this Google UID. If yes, go to step 4; otherwise, go to step 5.
	 * 4. Update user profile into MySQL.
	 * 5. Create a new user profile and persist in MySQL.
	 * </pre>
	 * Each Google UID is always considered to contain a ProfileIdentities which is used to distinguish
	 * from which source the user profile is. Besides, the method also checks whether or not there is any
	 * friends in the forgotten list to be ignored by crawling job.
	 *
	 * @param accessToken
	 * @return
	 */
	public static String getUID(String accessToken) {
		if (accessToken == null) return "";
		String user_id = null;
		String _3cixtyUID = null;
		try {
			long time1 = System.currentTimeMillis();
			String reqMsg = Utils.readUrl(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			user_id = json.getString("id");
			String givenName = json.getString("given_name");
			String familyName = json.getString("family_name");
						
			String picture = json.getString("picture");
			
			UserProfile profile = ProfileManagerImpl.getInstance().findUserProfile(
					user_id, SPEConstants.GOOGLE_SOURCE);
			if (profile == null) {
				_3cixtyUID = Utils.gen3cixtyUID(user_id, UidSource.GOOGLE);
				profile = new UserProfile();
				profile.setHasUID(_3cixtyUID);
			} else {
				_3cixtyUID = profile.getHasUID();
			}
			ProfileManagerImpl.getInstance().getForgottenUserManager().remove(_3cixtyUID); // remove from forgotten user table
			boolean generalInfoModified = Utils.checkNameAndProfileImageModified(profile, givenName, familyName, picture);
			if (generalInfoModified) {
				profile.setProfileImage(picture);
				Name name = new Name();
				profile.setHasName(name);
				name.setGivenName(givenName);
				name.setFamilyName(familyName);
			}

			if (DEBUG_MOD) LOGGER.info("user_id = " + user_id + ", 3cixty UID = " + _3cixtyUID + ", givenName = " + givenName + ", familyName = " + familyName);

			Set <ProfileIdentities> profileIdentities = null;
			if (profile.getHasProfileIdenties() == null) {
				profileIdentities = new HashSet <ProfileIdentities>();
				profile.setHasProfileIdenties(profileIdentities);
			} else profileIdentities = profile.getHasProfileIdenties();
			
			boolean profileIdentitiesModified = Utils.checkProfileIdentitiesModified(profileIdentities, user_id, SPEConstants.GOOGLE_SOURCE);
			if (profileIdentitiesModified) Utils.setProfileIdentities(_3cixtyUID, user_id, SPEConstants.GOOGLE_SOURCE, profileIdentities);

			try {
				if (generalInfoModified || profileIdentitiesModified) {
					boolean successful = ProfileManagerImpl.getInstance().saveProfile(profile);
					if (successful) {
						updateKnows(accessToken, user_id, profile);
					}
				} else updateKnows(accessToken, user_id, profile);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			long time3 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to process info (relevant to UserProfile model) at backend for one log-in process: " + (time3 - time1) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		if (_3cixtyUID == null) return "";
		return _3cixtyUID;
	}
	
	/**
	 * Checks whether or not the is a user profile corresponding with a given Google access token.
	 * @param accessToken
	 * @return
	 */
	public static boolean existUserProfile(String accessToken) {
		try {
			String reqMsg = Utils.readUrl(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			String user_id = json.getString("id");
			UserProfile profile = ProfileManagerImpl.getInstance().findUserProfile(user_id, SPEConstants.GOOGLE_SOURCE);
			if (profile != null) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Updates knows from different threads.
	 * @param accessToken
	 * @param user_id
	 * @param profile
	 */
	private static void updateKnows(String accessToken, String user_id,
			UserProfile profile) {
		KnowsPersistence persistence = new KnowsPersistence(accessToken, SPEConstants.GOOGLE_SOURCE, user_id, profile);
		PersistenceWorkerManager.getInstance().add(persistence); // create a task to run on background
	}

	/**
	 * Get the list of friends UID from Google Plus.
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	protected static List <String> getGoogleUidsOfFriends(String accessToken) throws Exception {
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

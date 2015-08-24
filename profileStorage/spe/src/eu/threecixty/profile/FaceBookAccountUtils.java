package eu.threecixty.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class is to get information from FB user access token.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class FaceBookAccountUtils {
	
	private static final String FACE_BOOK_ACCESS_TOKEN_VALIDATION = "https://graph.facebook.com/me?access_token=";
	private static final String FACEBOOK_PROFILE_IMAGE_PREFIX = "https://graph.facebook.com/v2.2/me/picture?access_token=";
	protected static final String FACEBOOK_FRIENDS_PREFIX = "https://graph.facebook.com/v2.2/me/friends?fields=id&format=json&method=get&pretty=0&suppress_http_code=1&access_token=";
	
	private static final Logger LOGGER = Logger.getLogger(
			 FaceBookAccountUtils.class.getName());
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	public static String getUID(String accessToken, int width, int height) {
		if (accessToken == null) return "";
		long time1 = System.currentTimeMillis();
		try {
			String content = Utils.readUrl(FACE_BOOK_ACCESS_TOKEN_VALIDATION + accessToken);
			if (content == null) return null;
			JSONObject json = new JSONObject(content);
			if (!json.has("id")) return null;
			String uid = json.getString("id");
			String firstName = json.getString("first_name");
			String lastName = json.getString("last_name");

			String picture = getProfileImage(accessToken, width, height);
			
			// TODO: 1. need to check ProfileIdentities to know whether or not there is a profile corresponding with this uid
			//       2. need to check email to know whether or not there is a profile corresponding with this uid
			// The following code is supposed that FaceBook info is independent with Google info.
			
			String _3cixtyUID = null;
			
			UserProfile profile = ProfileManagerImpl.getInstance().findUserProfile(uid, SPEConstants.FACEBOOK_SOURCE);
			if (profile == null) {
				_3cixtyUID = Utils.gen3cixtyUID(uid, UidSource.FACEBOOK);
				profile = new UserProfile();
				profile.setHasUID(_3cixtyUID);
			} else {
				_3cixtyUID = profile.getHasUID();
			}
			ProfileManagerImpl.getInstance().getForgottenUserManager().remove(_3cixtyUID); // remove from forgotten user table
			boolean generalInfoModified = Utils.checkNameAndProfileImageModified(profile, firstName, lastName, picture);
			if (generalInfoModified) {
				if (picture != null) profile.setProfileImage(picture);
				Name name = new Name();
				profile.setHasName(name);
				name.setGivenName(firstName);
				name.setFamilyName(lastName);
			}


			
			Set <ProfileIdentities> profileIdentities = null;
			if (profile.getHasProfileIdenties() == null) {
				profileIdentities = new HashSet <ProfileIdentities>();
				profile.setHasProfileIdenties(profileIdentities);
			} else profileIdentities = profile.getHasProfileIdenties();
			boolean profileIdentitiesModified = Utils.checkProfileIdentitiesModified(profileIdentities, uid, SPEConstants.FACEBOOK_SOURCE);
			if (profileIdentitiesModified) Utils.setProfileIdentities(_3cixtyUID, uid, SPEConstants.FACEBOOK_SOURCE, profileIdentities);
			
			Map <String, Boolean> attrs = Utils.getAttributesToStoreForCrawlingSocialProfile();
			
			if (generalInfoModified || profileIdentitiesModified) {
				boolean successful = ProfileManagerImpl.getInstance().saveProfile(profile, attrs);
				if (successful) {
					updateKnows(accessToken, uid, profile);
				}
			} else updateKnows(accessToken, uid, profile);
			long time3 = System.currentTimeMillis();
			if (DEBUG_MOD) LOGGER.info("Time to process info (relevant to UserProfile model) at backend for one log-in process: " + (time3 - time1) + " ms");
			return _3cixtyUID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	


	private static void updateKnows(String accessToken, String user_id,
			UserProfile profile) {
		KnowsPersistence persistence = new KnowsPersistence(accessToken, SPEConstants.FACEBOOK_SOURCE, user_id, profile);
		PersistenceWorkerManager.getInstance().add(persistence);
	}



	protected static void findFacebookUidsFromFriends(String url, String accessToken,
			List <String> facebookUidsFromFriends) {
		// XXX: Note: we can only get friends' UID if your friends and you are using 3cixty.
		// We can consider using 'taggable_friends' or 'invitable_friends' to get friends' UID,
		// but to be considered later when we have time
		try {
			String content = Utils.readUrl(url + accessToken);
			JSONObject json = new JSONObject(content);
			JSONArray arr = json.getJSONArray("data");
			int len = arr.length();
			if (len == 0) return;
			for (int i = 0; i < len; i++) {
				JSONObject tmpJson = arr.getJSONObject(i);
				String fUID = tmpJson.getString("id");
				if (facebookUidsFromFriends.contains(fUID)) continue;
				facebookUidsFromFriends.add(fUID);
			}
			String nextURL = json.getJSONObject("paging").getString("next");
			findFacebookUidsFromFriends(nextURL + "&access_token=", accessToken, facebookUidsFromFriends);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getProfileImage(String accessToken, int width, int height) {
		try {
			String content = Utils.readUrl(FACEBOOK_PROFILE_IMAGE_PREFIX + accessToken
					+ "&format=json&method=get&pretty=0&redirect=false&suppress_http_code=1&width="
					+ width + "&height=" + height);
			JSONObject json = new JSONObject(content);
			return json.getJSONObject("data").getString("url");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private FaceBookAccountUtils() {
	}
}

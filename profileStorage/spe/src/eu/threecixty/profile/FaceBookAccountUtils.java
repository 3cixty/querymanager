package eu.threecixty.profile;

import java.util.HashSet;
import java.util.Set;

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
	private static final String FACEBOOK_FRIENDS_PREFIX = "https://graph.facebook.com/v2.2/me/friends?fields=id&format=json&method=get&pretty=0&suppress_http_code=1&access_token=";


	public static String getUID(String accessToken) {
		if (accessToken == null) return "";
		try {
			String content = Utils.readUrl(FACE_BOOK_ACCESS_TOKEN_VALIDATION + accessToken);
			if (content == null) return null;
			JSONObject json = new JSONObject(content);
			if (!json.has("id")) return null;
			String uid = json.getString("id");
			String firstName = json.getString("first_name");
			String lastName = json.getString("last_name");
			String gender = json.has("gender") ? json.getString("gender") : null;
			String picture = getProfileImage(accessToken);
			
			// TODO: 1. need to check ProfileIdentities to know whether or not there is a profile corresponding with this uid
			//       2. need to check email to know whether or not there is a profile corresponding with this uid
			// The following code is supposed that FaceBook info is independent with Google info.
			
			String _3cixtyUID = Utils.gen3cixtyUID(uid, UidSource.FACEBOOK);
			
			
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(_3cixtyUID);
			profile.setHasUID(_3cixtyUID);
			if (picture != null) profile.setProfileImage(picture);
			Name name = new Name();
			profile.setHasName(name);
			name.setGivenName(firstName);
			name.setFamilyName(lastName);

			if (gender != null) {
				profile.setHasGender(json.getString("gender"));
			}
			Set <ProfileIdentities> profileIdentities = null;
			if (profile.getHasProfileIdenties() == null) {
				profileIdentities = new HashSet <ProfileIdentities>();
				profile.setHasProfileIdenties(profileIdentities);
			} else profileIdentities = profile.getHasProfileIdenties();
			
			Utils.setProfileIdentities(_3cixtyUID, uid, "Facebook", profileIdentities);
			
			Set <String> knows = new HashSet <String>(); // always refresh 'knows'
			
			findKnows(FACEBOOK_FRIENDS_PREFIX, accessToken, knows);

			profile.setKnows(knows);
			
			PartnerAccountUtils.addMobidotID(_3cixtyUID, uid, profile.getHasName(), profileIdentities);
			
			PartnerAccountUtils.addGoflowID(_3cixtyUID, uid, profileIdentities);
			
			ProfileManagerImpl.getInstance().saveProfile(profile);
			
			return _3cixtyUID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	


	private static void findKnows(String url, String accessToken, Set <String> knowsResult) {
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
				String know = Utils.gen3cixtyUID(tmpJson.getString("id"), Utils.UidSource.FACEBOOK);
				if (knowsResult.contains(know)) continue;
				knowsResult.add(know);
			}
			String nextURL = json.getJSONObject("paging").getString("next");
			findKnows(nextURL + "&access_token=", accessToken, knowsResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getProfileImage(String accessToken) {
		try {
			String content = Utils.readUrl(FACEBOOK_PROFILE_IMAGE_PREFIX + accessToken + "&format=json&method=get&pretty=0&redirect=false&suppress_http_code=1");
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

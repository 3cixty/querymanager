package eu.threecixty.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

import eu.threecixty.Configuration;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.oldmodels.UserInteractionMode;

/**
 * This is a utility class.
 * @author Cong-Kinh Nguyen
 *
 */
public class Utils {
	
	/**A prefix of two characters is really enough for future: 99 social networks*/
	protected static final String GOOGLE_PREFIX = "10";
	private static final String FACEBOOK_PREFIX = "11";
	private static final String NO_SOCIAL_NETWORK_PREFIX = "99";
	
	private static final String PROFILE_URI = Configuration.PROFILE_URI;
	
	public static String gen3cixtyUID(String originalUID, UidSource source) {
		if (source == null || originalUID == null) return null;
		if (source == UidSource.GOOGLE) {
			return GOOGLE_PREFIX + originalUID;
		} else if (source == UidSource.FACEBOOK) {
			return FACEBOOK_PREFIX + originalUID;
		}
		return NO_SOCIAL_NETWORK_PREFIX + originalUID;
	}

	protected static void setProfileIdentities(String _3cixtyUID, String uid, String source,
			Set<ProfileIdentities> profileIdentities) {
		boolean found = false;
		for (ProfileIdentities pi: profileIdentities) {
			if (uid.equals(pi.getHasUserAccountID())) {
				found = true;
				break;
			}
		}
		if (found) return; // already existed
		addProfileIdentities(_3cixtyUID, uid, source, profileIdentities);
		/*ProfileIdentities pi = new ProfileIdentities();
		pi.setHasSourceCarrier(source);
		pi.setHasUserAccountID(uid);
		
		pi.setHasUserInteractionMode(UserInteractionMode.Active);
		pi.setHasProfileIdentitiesURI(PROFILE_URI+ _3cixtyUID + "/Account/" + pi.getHasSourceCarrier());
		
		profileIdentities.add(pi);*/
	}
	
	protected static void addProfileIdentities(String _3cixtyUID, String uid, String source,
			Set<ProfileIdentities> profileIdentities) {
		
		ProfileIdentities pi = new ProfileIdentities();
		pi.setHasSourceCarrier(source);
		pi.setHasUserAccountID(uid);
		pi.setHasUserInteractionMode(UserInteractionMode.Active);
		pi.setHasProfileIdentitiesURI(PROFILE_URI+ _3cixtyUID + "/Account/" + pi.getHasSourceCarrier());
		
		profileIdentities.add(pi);
	}
	
	/**
	 * Gets content from a given URL string.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	protected static String readUrl(String urlString) throws Exception {
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
	
	private Utils() {
	}
	
	protected enum UidSource {
		GOOGLE, FACEBOOK
	}
}

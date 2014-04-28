package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


import org.json.JSONObject;

/**
 * This class is to check an access token.
 * 
 * @author Cong-Kinh NGUYEN
 *
 */
public class TokenVerifier {

	private static final Object _sync = new Object();
	
	private static final String OAUTH2_VALIDATION_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";
	
	private static TokenVerifier singleton;
	
	public static TokenVerifier getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new TokenVerifier();
			}
		}
		return singleton;
	}

	/**
	 * Gets public user_id from a given access token.
	 *
	 * @param accessToken
	 * 				An access token
	 * @return Public user_id if an access token is correct or empty in other cases. 
	 */
	public String getUserId(String accessToken) {
		try {
		    JSONObject json = new JSONObject(readUrl(OAUTH2_VALIDATION_URL + accessToken));

		    String user_id = (String) json.get("user_id");
		    return user_id;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return "";
	}

	/**
	 * Gets content from a given URL string.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	private String readUrl(String urlString) throws Exception {
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
	
	private TokenVerifier() {
	}
}

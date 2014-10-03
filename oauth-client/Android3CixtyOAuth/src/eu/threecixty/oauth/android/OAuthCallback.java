package eu.threecixty.oauth.android;

/**
 * Callback to notify about responses received from 3Cixty OAuth server.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public interface OAuthCallback {

	/**
	 * This method is to notify about 3Cixty access token.
	 * @param tokenInfo
	 */
	void notify3CixtyAccessToken(TokenInfo tokenInfo);

	void notifyTokenRevoked(String token, boolean tokenRevoked);
}

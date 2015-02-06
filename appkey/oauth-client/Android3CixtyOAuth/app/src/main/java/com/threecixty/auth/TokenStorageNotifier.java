package com.threecixty.auth;

import android.content.Context;

/**
 * This class is used to notify OAuthManager about a new token info received.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class TokenStorageNotifier {

	private Context context;

	protected TokenStorageNotifier(Context context) {
		this.context = context;
	}

	protected void notifyToken(TokenInfo tokenInfo, String appid) {
		OAuthManager.getInstance().saveTokenInfo(context, tokenInfo, appid);
	}

	protected void deleteToken(String appid) {
		OAuthManager.getInstance().deleteTokenInfo(context, appid);
	}
}

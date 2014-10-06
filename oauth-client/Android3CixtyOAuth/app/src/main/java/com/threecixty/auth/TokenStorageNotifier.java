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

	protected void notifyToken(TokenInfo tokenInfo) {
		OAuthManager.getInstance().saveTokenInfo(context, tokenInfo);
	}

	protected void deleteToken() {
		OAuthManager.getInstance().deleteTokenInfo(context);
	}
}

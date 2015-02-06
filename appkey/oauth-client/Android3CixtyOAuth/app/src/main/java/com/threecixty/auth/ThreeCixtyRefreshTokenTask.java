package com.threecixty.auth;

import java.io.InputStream;
import java.net.URLConnection;

import android.content.Context;

public class ThreeCixtyRefreshTokenTask extends ThreeCixtyAbstractTask {

    private String appid;
	private TokenInfo lastTokenInfo;
	private OAuthCallback callback;
	private Context context;
	
	private TokenInfo tokenInfo;
	
	protected ThreeCixtyRefreshTokenTask(Context context, String appid, TokenInfo lastTokenInfo, OAuthCallback callback) {
		this.lastTokenInfo = lastTokenInfo;
		this.callback = callback;
		this.context = context;
        this.appid = appid;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (lastTokenInfo == null) return null;
		try {
			long creationTime = System.currentTimeMillis();
			URLConnection conn = getUrlConnection(REFRESH_OAUTH_SERVICE, context);
			conn.setRequestProperty("refresh_token", lastTokenInfo.getRefresh_token());
			InputStream input = conn.getInputStream();
			tokenInfo = parse(input);
			if (tokenInfo != null) tokenInfo.setCreationTime(creationTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (tokenInfo != null && context != null) {
			new TokenStorageNotifier(context).notifyToken(tokenInfo, appid);
		}
		if (callback != null && tokenInfo != null) {
			callback.notify3CixtyAccessToken(tokenInfo);
		}
	}
}

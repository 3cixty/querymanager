package com.threecixty.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLConnection;

import org.json.JSONException;

import android.app.Activity;

public class ThreeCixtyAccessTokenTask extends ThreeCixtyAbstractTask {

	private String appkey;
	private String googleToken;
	private String scope;
	private OAuthCallback callback;
	private Activity activity;
	
	private TokenInfo tokenInfo;

	protected ThreeCixtyAccessTokenTask(Activity activity, String appkey, String googleToken, String scope, OAuthCallback callback) {
		this.appkey = appkey;
		this.googleToken = googleToken;
		this.scope = scope;
		this.callback = callback;
		this.activity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			long creationTime = System.currentTimeMillis();
			URLConnection conn = getUrlConnection(OAUTH_ANDROID_SERVICE);
			
			conn.setRequestProperty("google_access_token", encode(googleToken));
			conn.setRequestProperty("key", encode(appkey));
			conn.setRequestProperty("scope", encode(scope));
			
			InputStream input = conn.getInputStream();
			tokenInfo = parse(input);
			if (tokenInfo != null) tokenInfo.setCreationTime(creationTime);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (tokenInfo != null) {
			new TokenStorageNotifier(activity).notifyToken(tokenInfo);
		}
		if (tokenInfo != null && callback != null) {
			callback.notify3CixtyAccessToken(tokenInfo);
		}
		if (activity != null) {
			activity.setResult(MainActivity.RESULT_OK);
			activity.finish();
		}
	}
}

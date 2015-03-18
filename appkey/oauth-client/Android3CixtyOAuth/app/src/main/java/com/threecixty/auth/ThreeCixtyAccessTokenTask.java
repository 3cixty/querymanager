package com.threecixty.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLConnection;

import org.json.JSONException;

import android.app.Activity;

public class ThreeCixtyAccessTokenTask extends ThreeCixtyAbstractTask {

    protected static final String GOOGLE_SOURCE = "Google";
    protected static final String FACEBOOK_SOURCE = "Facebook";


	private String appkey;
    private String appid;
	private String token;
	private String scope;
	private OAuthCallback callback;
	private Activity activity;
    private String tokenSource;
	
	private TokenInfo tokenInfo;

	protected ThreeCixtyAccessTokenTask(Activity activity, String appkey, String appid, String token,
                                        String tokenSource, String scope, OAuthCallback callback) {
		this.appkey = appkey;
        this.appid = appid;
		this.token = token;
        this.tokenSource = tokenSource;
		this.scope = scope;
		this.callback = callback;
		this.activity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			long creationTime = System.currentTimeMillis();
			URLConnection conn = null;
			if (GOOGLE_SOURCE.equals(tokenSource)) {
                conn = getUrlConnection(OAUTH_ANDROID_SERVICE, activity);
                conn.setRequestProperty("google_access_token", encode(token));
            } else if (FACEBOOK_SOURCE.equals(tokenSource)) {
                conn = getUrlConnection(OAUTH_ANDROID_SERVICE_FB + "?width=100&height=100", activity);
                conn.setRequestProperty("fb_access_token", encode(token));
            }
            if (conn != null) {
                conn.setRequestProperty("key", encode(appkey));
                conn.setRequestProperty("scope", scope);

                InputStream input = conn.getInputStream();
                tokenInfo = parse(input);
                if (tokenInfo != null) tokenInfo.setCreationTime(creationTime);
            }
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
			new TokenStorageNotifier(activity).notifyToken(tokenInfo, appid);
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

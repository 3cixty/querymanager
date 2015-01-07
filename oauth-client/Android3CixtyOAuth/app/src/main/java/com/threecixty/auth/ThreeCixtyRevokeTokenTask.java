package com.threecixty.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ThreeCixtyRevokeTokenTask extends ThreeCixtyAbstractTask {

	private static final String REVOKE_OAUTH_SERVICE = SERVER + "/v2/revoke";
	
	private Context context;
    private String appid;
	private String token;
	private OAuthCallback callback;
	
	private boolean successful = false;
	
	protected ThreeCixtyRevokeTokenTask(Context context, String appid, String token, OAuthCallback callback) {
		this.context = context;
        this.appid = appid;
		this.token = token;
		this.callback = callback;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (token == null) return null;
		try {
		    URLConnection conn = getUrlConnection(REVOKE_OAUTH_SERVICE, context);
		    if (REVOKE_OAUTH_SERVICE.startsWith("https")) {
		    	((HttpsURLConnection) conn).setRequestMethod("POST");
		    } else ((HttpURLConnection) conn).setRequestMethod("POST");
			conn.setRequestProperty("access_token", encode(token));
			InputStream input = conn.getInputStream();
			String content = getContent(input);
			input.close();
			JSONObject jsonObj = new JSONObject(content);
			if (jsonObj.has("response")) {
				String response = jsonObj.getString("response");
				successful = "successful".equalsIgnoreCase(response);
			}
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
		if (callback == null) return;
		if (isCancelled()) return;
		callback.notifyTokenRevoked(token, successful);
		if (successful) {
		    new TokenStorageNotifier(context).deleteToken(appid);
		}
	}
}

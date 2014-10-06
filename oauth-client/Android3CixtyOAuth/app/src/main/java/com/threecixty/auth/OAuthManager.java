package com.threecixty.auth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;

public class OAuthManager {

	private static final Object _sync = new Object();
	
	private static final String OAUTH_FILE_NAME = "oauth.txt";
	
	private static final String NULL_CALLBACK = "Callback to notify about 3Cixty OAuth is null";
	
	private static OAuthManager singleton;

	public static OAuthManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new OAuthManager();
			}
		}
		return singleton;
	}
	
	protected boolean existsToken(Context context) {
		TokenInfo tokenInfo = loadTokenInfo(context);
		return tokenInfo != null;
	}

	protected boolean hasValidToken(TokenInfo tokenInfo) {
		if (tokenInfo == null) return false;
		if (System.currentTimeMillis() >= tokenInfo.getCreationTime() + tokenInfo.getExpires_inInMillis())
			return false;
		return true;
	}
	
	protected TokenInfo getToken(Context context) {
		return loadTokenInfo(context);
	}

	protected synchronized boolean saveTokenInfo(Context context, TokenInfo tokenInfo) {
		// delete file if there exists
		if (exists(context.fileList())) {
			if (!context.deleteFile(OAUTH_FILE_NAME)) return false;
		}
		try {
			OutputStream output = context.openFileOutput(OAUTH_FILE_NAME, Context.MODE_PRIVATE);
			output.write(tokenInfo.toJson().getBytes());
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void auth3CixtyServer(Activity context, String appkey, String appName,
			OAuthCallback callback, String accountName) {
		if (callback == null) {
			throw new RuntimeException(NULL_CALLBACK);
		}
		if (isNullOrEmpty(appkey) || isNullOrEmpty(appName)) {
			throw new RuntimeException("Please check your application key or scope");
		}
		GoogleAuthTask googleTask = new GoogleAuthTask(context, accountName, appkey, appName, callback);
		googleTask.execute();
	}
	
	protected void revokeToken(Context context, String token, OAuthCallback callback) {
		new ThreeCixtyRevokeTokenTask(context, token, callback).execute();
	}
	
	protected synchronized void deleteTokenInfo(Context context) {
		if (exists(context.fileList())) {
			context.deleteFile(OAUTH_FILE_NAME);
		}
	}

	protected void refreshToken(Context context, TokenInfo lastTokenInfo, OAuthCallback callback) {
	    ThreeCixtyRefreshTokenTask task = new ThreeCixtyRefreshTokenTask(context, lastTokenInfo, callback);
	    task.execute();
    }

	private synchronized TokenInfo loadTokenInfo(Context context) {
		if (!exists(context.fileList())) return null;
		try {
			InputStream input = context.openFileInput(OAUTH_FILE_NAME);
			if (input == null) return null;
			StringBuilder builder = new StringBuilder();
			byte[] bytes = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(bytes)) >= 0) {
				builder.append(new String(bytes, 0, readBytes));
			}
			input.close();
			return TokenInfo.parse(builder.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean exists(String [] fileList) {
		if (fileList == null) return false;
		for (String filename: fileList) {
			if (filename.equals(OAUTH_FILE_NAME)) return true;
		}
		return false;
	}
	
	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private OAuthManager() {
	}
}

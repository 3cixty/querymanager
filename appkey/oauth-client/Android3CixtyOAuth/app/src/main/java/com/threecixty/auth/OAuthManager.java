/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

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
	
	protected boolean existsToken(Context context, String appid) {
		TokenInfo tokenInfo = loadTokenInfo(context, appid);
		return tokenInfo != null;
	}

	protected boolean hasValidToken(TokenInfo tokenInfo) {
		if (tokenInfo == null) return false;
		if (System.currentTimeMillis() >= tokenInfo.getCreationTime() + tokenInfo.getExpires_inInMillis())
			return false;
		return true;
	}
	
	protected TokenInfo getToken(Context context, String appid) {
		return loadTokenInfo(context, appid);
	}

	protected synchronized boolean saveTokenInfo(Context context, TokenInfo tokenInfo, String appid) {
		// delete file if there exists
        String filename = getFileName(appid);
		if (exists(context.fileList(), appid)) {
			if (!context.deleteFile(filename)) return false;
		}
		try {
			OutputStream output = context.openFileOutput(filename, Context.MODE_PRIVATE);
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

	protected void auth3CixtyServer(Activity context, String appkey, String appId, String appName,
			OAuthCallback callback, String accountName) {
		if (callback == null) {
			throw new RuntimeException(NULL_CALLBACK);
		}
		if (isNullOrEmpty(appkey) || isNullOrEmpty(appName)) {
			throw new RuntimeException("Please check your application key or scope");
		}
		GoogleAuthTask googleTask = new GoogleAuthTask(context, accountName, appkey, appId, appName, callback);
		googleTask.execute();
	}
	
	protected void revokeToken(Context context, String appid, String token, OAuthCallback callback) {
		new ThreeCixtyRevokeTokenTask(context, appid, token, callback).execute();
	}
	
	protected synchronized void deleteTokenInfo(Context context, String appid) {
		if (exists(context.fileList(), appid)) {
			context.deleteFile(getFileName(appid));
		}
	}

	protected void refreshToken(Context context, String appid, TokenInfo lastTokenInfo, OAuthCallback callback) {
	    ThreeCixtyRefreshTokenTask task = new ThreeCixtyRefreshTokenTask(context, appid, lastTokenInfo, callback);
	    task.execute();
    }

	private synchronized TokenInfo loadTokenInfo(Context context, String appid) {
		if (!exists(context.fileList(), appid)) return null;
		try {
			InputStream input = context.openFileInput(getFileName(appid));
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
	
	private boolean exists(String [] fileList, String appid) {
		if (fileList == null) return false;
        String filename = getFileName(appid);
		for (String tmpName: fileList) {
			if (tmpName.equals(filename)) return true;
		}
		return false;
	}

    private String getFileName(String appid) {
        return appid.hashCode() + OAUTH_FILE_NAME;
    }
	
	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private OAuthManager() {
	}
}

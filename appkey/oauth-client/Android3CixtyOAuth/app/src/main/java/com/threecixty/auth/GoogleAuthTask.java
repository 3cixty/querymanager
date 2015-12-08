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

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;

public class GoogleAuthTask extends AsyncTask<Void, Void, Void> {

	private Activity context;
	private String accountName;
	private String token;
	
	private OAuthCallback callback;
	private String appkey;
	private String appName;
    private String appId;
    private boolean shown = true;
	
	protected GoogleAuthTask(Activity context, String accountName, String appkey, String appId, String appName, OAuthCallback callback) {
		this.context = context;
		this.accountName = accountName;
		this.appkey = appkey;
        this.appId = appId;
		this.appName = appName;
		this.callback = callback;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		if (accountName != null) {
			try {
				token = GoogleAuthUtil.getToken(context, accountName, "oauth2:" + Scopes.PLUS_LOGIN
                        + " https://www.googleapis.com/auth/userinfo.email");
			} catch (UserRecoverableAuthException e) {
				//e.printStackTrace();
                shown = false;
				context.startActivityForResult(e.getIntent(), MainActivity.GOOGLE_PERMISSION_REQUEST);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (token != null && !token.equals("")) {
			ScopesActivity.setAppkey(appkey);
			ScopesActivity.setAppName(appName);
			ScopesActivity.setCallback(callback);
			ScopesActivity.setToken(token);
            ScopesActivity.setAppid(appId);
            ScopesActivity.setTokenSource(ThreeCixtyAccessTokenTask.GOOGLE_SOURCE);
			Intent intent = new Intent(context, ScopesActivity.class);
			context.startActivityForResult(intent, MainActivity.THREE_CIXTY_PERMISSION_REQUEST);
		} else {
            if (shown) {
                Toast.makeText(context, "Cannot authenticate Google account", Toast.LENGTH_SHORT).show();
            }
		}
	}
}

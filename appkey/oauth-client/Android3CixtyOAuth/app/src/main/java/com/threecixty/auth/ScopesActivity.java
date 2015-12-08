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


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ScopesActivity extends Activity {

	private static String appkey;
	private static String appName;
    private static String appid;
	private static String token;
	private static OAuthCallback callback;
    private static String tokenSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scopes);
		
		TextView titleView = (TextView) findViewById(R.id.scopeTitle);
		titleView.setText(appName + " wants to retrieve data from 3cixty platform");

		TextView decView = (TextView) findViewById(R.id.scopeDeclaration);
		decView.setText("This data will be shared");
	    
		final CheckBox wishlistChk = (CheckBox) findViewById(R.id.scopeWishlistChk);
        final CheckBox profileChk = (CheckBox) findViewById(R.id.scopeProfileChk);
		
		final Button grantCmd = (Button) findViewById(R.id.grantPermission);
		grantCmd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String scope;
                if (wishlistChk.isChecked() && profileChk.isChecked()) scope = "WishList,Profile";
                else if (wishlistChk.isChecked()) scope = "WishList";
                else if (profileChk.isChecked()) scope = "Profile";
                else scope = "";
				new ThreeCixtyAccessTokenTask(ScopesActivity.this, appkey, appid, token, tokenSource, scope, callback).execute();
				grantCmd.setEnabled(false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scopes, menu);
		return true;
	}

	protected static void setAppkey(String appkey) {
		ScopesActivity.appkey = appkey;
	}

	protected static void setAppName(String appName) {
		ScopesActivity.appName = appName;
	}

	protected static void setCallback(OAuthCallback callback) {
		ScopesActivity.callback = callback;
	}

	protected static void setToken(String token) {
		ScopesActivity.token = token;
	}

    protected static void setAppid(String appid) {
        ScopesActivity.appid = appid;
    }

    protected static void setTokenSource(String tokenSource) {
        ScopesActivity.tokenSource = tokenSource;
    }
}

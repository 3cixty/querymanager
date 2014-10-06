package com.threecixty.auth;

import org.json.JSONException;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import eu.threecixty.oauth.android.R;

public class MainActivity extends Activity {

	public static final int THREE_CIXTY_PERMISSION_REQUEST = 200;
	public static final int GOOGLE_PERMISSION_REQUEST = 1000;
	public static final String OAUTH_TOKEN_KEY = "3CixtyOAuth";
	private static final int PICK_ACCOUNT_REQUEST = 100;
	
	private static final String EXTRA_APP_KEY = "app_key";
	private static final String EXTRA_APP_NAME = "app_name";
	private static final String EXTRA_TOKEN_KEY = "access_token";
	
	private String appkey;
	private String appName;
	
	private String accessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent callerIntent = getIntent();
		if (callerIntent.hasExtra(EXTRA_APP_KEY)) {
			appkey = callerIntent.getStringExtra(EXTRA_APP_KEY);
		}
		if (callerIntent.hasExtra(EXTRA_APP_NAME)) appName = callerIntent.getStringExtra(EXTRA_APP_NAME);
		
		if (callerIntent.hasExtra(EXTRA_TOKEN_KEY)) accessToken = callerIntent.getStringExtra(EXTRA_TOKEN_KEY);
		
		Button cmdLogin = (Button) findViewById(R.id.login);
		if (appkey != null && appName != null) { // get 3Cixty token
			if (!OAuthManager.getInstance().existsToken(this)) {
				cmdLogin.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						goWithAskingForPermission();
					}
				});
			} else {
				cmdLogin.setEnabled(false);
				TokenInfo tokenInfo = OAuthManager.getInstance().getToken(this);
				if (OAuthManager.getInstance().hasValidToken(tokenInfo)) {
					quitOAuthActivity(tokenInfo);
				} else {
					OAuthManager.getInstance().refreshToken(this, tokenInfo, new CallbackImpl());
				}
			}
		} else if (accessToken != null) { // revoke 3Cixty token
			OAuthManager.getInstance().revokeToken(this, accessToken, new CallbackImpl());
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (appkey == null && appName == null && accessToken == null) {
			Toast.makeText(this, "Please check app_key and app_name for getting 3Cixty token or access_token for revoking it", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode == RESULT_OK && requestCode == PICK_ACCOUNT_REQUEST) {
	    	if (data.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
	            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	            OAuthManager.getInstance().auth3CixtyServer(this, appkey, appName, new CallbackImpl(), accountName);
	    	}
	      } else if (resultCode == RESULT_OK && requestCode == THREE_CIXTY_PERMISSION_REQUEST) {
	      }

	}

	private void goWithAskingForPermission() {
		Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
				new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, true, null, null, null, null);
		startActivityForResult(googlePicker, PICK_ACCOUNT_REQUEST);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void quitOAuthActivity(TokenInfo tokenInfo) {
		Intent intent = new Intent();
		try {
			intent.putExtra(OAUTH_TOKEN_KEY, tokenInfo.toJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.setResult(RESULT_OK, intent);
		finish();
	}

	private class CallbackImpl implements OAuthCallback {

		@Override
		public void notify3CixtyAccessToken(TokenInfo tokenInfo) {
			quitOAuthActivity(tokenInfo);
		}

		@Override
		public void notifyTokenRevoked(String token, boolean tokenRevoked) {
			Intent intent = new Intent();
			intent.putExtra("3CixtyRevokeResponse", tokenRevoked);

			setResult(RESULT_OK, intent);
			finish();
		}
		
	}
}

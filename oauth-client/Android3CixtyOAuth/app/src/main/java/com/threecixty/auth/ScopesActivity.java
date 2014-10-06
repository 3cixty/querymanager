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
	private static String googleToken;
	private static OAuthCallback callback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scopes);
		
		TextView titleView = (TextView) findViewById(R.id.scopeTitle);
		titleView.setText(appName + " wants to retrieve data from 3Cixty platform");

		TextView decView = (TextView) findViewById(R.id.scopeDeclaration);
		decView.setText("This data will be shared");
	    
		final CheckBox chk = (CheckBox) findViewById(R.id.scopeCheckbox);
		
		final Button grantCmd = (Button) findViewById(R.id.grantPermission);
		grantCmd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String scope = chk.isChecked() ? "Wishlist" : "";
				new ThreeCixtyAccessTokenTask(ScopesActivity.this, appkey, googleToken, scope, callback).execute();
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

	protected static void setGoogleToken(String googleToken) {
		ScopesActivity.googleToken = googleToken;
	}
}

package com.threecixty.oauthsample;


import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int OAUTH_REQUEST_ID = 101; // any number you want
	private static final String OAUTH_ACTION = "com.threecixty.oauth.OAUTH";
	
	private String token;
	
	private Button revokeToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button showToken = (Button) findViewById(R.id.showToken);
		
		showToken.setOnClickListener(new View.OnClickListener() { // show 3Cixty token
			
			@Override
			public void onClick(View v) {
				Intent oauthIntent = new Intent(OAUTH_ACTION);
				oauthIntent.setType("*/*");
				oauthIntent.putExtra("app_key", "a6e90e87-f29b-4d6a-aed9-456d956b9532");
				oauthIntent.putExtra("app_name", "Test appp");
				
				Intent chooser = Intent.createChooser(oauthIntent, "Authenticate with 3Cixty server");
			
				startActivityForResult(chooser, OAUTH_REQUEST_ID);
			}
		});
		
		revokeToken = (Button) findViewById(R.id.revokeToken);
		revokeToken.setOnClickListener(new View.OnClickListener() { // revoke 3Cixty token
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (token == null) {
					Toast.makeText(MainActivity.this, "3Cixty token is null", Toast.LENGTH_SHORT).show();
				} else {
					Intent oauthIntent = new Intent(OAUTH_ACTION);
					oauthIntent.setType("*/*");
					oauthIntent.putExtra("access_token", token);
					
					Intent chooser = Intent.createChooser(oauthIntent, "Authenticate with 3Cixty server");
				
					startActivityForResult(chooser, OAUTH_REQUEST_ID);
				}
			}
		});
		revokeToken.setEnabled(false);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OAUTH_REQUEST_ID && resultCode == RESULT_OK) {
			if (data.hasExtra("3CixtyOAuth")) { // result for getting 3Cixty token
				// print your 3Cixty token
				System.out.println(data.getStringExtra("3CixtyOAuth"));
				Toast.makeText(this, data.getStringExtra("3CixtyOAuth"), Toast.LENGTH_LONG).show();
				
				// enable 'revokeToken' button
				try {
					JSONObject jsonObj = new JSONObject(data.getStringExtra("3CixtyOAuth"));
					token = jsonObj.getString("access_token");
					revokeToken.setEnabled(true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (data.hasExtra("3CixtyRevokeResponse")) { // result for revoking 3Cixty token
				boolean successful = data.getBooleanExtra("3CixtyRevokeResponse", false);
				Toast.makeText(this, successful ? "successful" : "failed", Toast.LENGTH_LONG).show();
				
				// disable 'revokeToken' button
				if (successful) revokeToken.setEnabled(false);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}

package com.threecixty.auth;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class MainActivity extends Activity {

	public static final int THREE_CIXTY_PERMISSION_REQUEST = 200;
	public static final int GOOGLE_PERMISSION_REQUEST = 1000;
	public static final String OAUTH_TOKEN_KEY = "3CixtyOAuth";
	private static final int PICK_ACCOUNT_REQUEST = 100;
	
	private static final String EXTRA_APP_KEY = "app_key";
	private static final String EXTRA_TOKEN_KEY = "access_token";
	
	private String appkey;
    private String appid;
	private String appName;
	
	private String accessToken;
    private String accountName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent callerIntent = getIntent();
		if (callerIntent.hasExtra(EXTRA_APP_KEY)) {
			appkey = callerIntent.getStringExtra(EXTRA_APP_KEY);
		}
		
		if (callerIntent.hasExtra(EXTRA_TOKEN_KEY)) accessToken = callerIntent.getStringExtra(EXTRA_TOKEN_KEY);
		
		final Button cmdLogin = (Button) findViewById(R.id.login);
		if (appkey != null) { // get 3Cixty token

            AsyncTask <Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    URLConnection conn = ThreeCixtyAbstractTask.getUrlConnection(ThreeCixtyAbstractTask.GET_KEY_INFO_SERVICE_FROM_APPKEY, MainActivity.this);
                    conn.setRequestProperty("key", appkey);
                    try {
                        String content = getContent(conn);
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("appid") && jsonObject.has("appname")) {
                            appid = jsonObject.getString("appid");
                            appName = jsonObject.getString("appname");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (appName != null && appid != null) {
                        if (!OAuthManager.getInstance().existsToken(MainActivity.this, appid)) {
                            cmdLogin.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    goWithAskingForPermission();
                                }
                            });
                        } else {
                            cmdLogin.setEnabled(false);
                            TokenInfo tokenInfo = OAuthManager.getInstance().getToken(MainActivity.this, appid);
                            if (OAuthManager.getInstance().hasValidToken(tokenInfo)) {
                                quitOAuthActivity(tokenInfo);
                            } else {
                                OAuthManager.getInstance().refreshToken(MainActivity.this, appid, tokenInfo, new CallbackImpl());
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Your appkey is invalid or there is no Internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            };
            task.execute();
		} else if (accessToken != null) { // revoke 3Cixty token

            AsyncTask <Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    URLConnection conn = ThreeCixtyAbstractTask.getUrlConnection(ThreeCixtyAbstractTask.GET_KEY_INFO_SERVICE_FROM_ACCESS_TOKEN, MainActivity.this);
                    conn.setRequestProperty("access_token", accessToken);
                    try {
                        String content = getContent(conn);
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("appid")) {
                            appid = jsonObject.getString("appid");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (appid != null) {
                        OAuthManager.getInstance().revokeToken(MainActivity.this, appid, accessToken, new CallbackImpl());
                    } else {
                        Toast.makeText(MainActivity.this, "Your access token is invalid or there is no Internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            };
            task.execute();

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
	            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	            OAuthManager.getInstance().auth3CixtyServer(this, appkey, appid, appName, new CallbackImpl(), accountName);
	    	}
	    } else if (resultCode == RESULT_OK && requestCode == THREE_CIXTY_PERMISSION_REQUEST) {
	    } else if (resultCode == RESULT_OK && requestCode == GOOGLE_PERMISSION_REQUEST) {
            if (accountName != null) {
                OAuthManager.getInstance().auth3CixtyServer(this, appkey, appid, appName, new CallbackImpl(), accountName);
            }
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

    protected String getContent(URLConnection conn) throws IOException {
        byte[] bytes = new byte[1024];
        int readBytes = 0;
        InputStream input = conn.getInputStream();
        StringBuilder builder = new StringBuilder();
        while ((readBytes = input.read(bytes)) >= 0) {
            builder.append(new String(bytes, 0, readBytes));
        }
        input.close();
        return builder.toString();
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

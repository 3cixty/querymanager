package eu.threecixty.querymanager.robustness.oauth;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.threecixty.querymanager.HTTPCall;

public class FakeTokenTests extends HTTPCall {

	@BeforeClass
	public static void setup() {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
		
		try {
			initSSL();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testGetAccessTokenWithFakeGoogleToken10() {
		testGetAccessTokenWithFakeGoogleToken(THREADS_10);
	}
	
	//@Test
	public void testGetAccessTokenWithFakeGoogleToken100() {
		testGetAccessTokenWithFakeGoogleToken(THREADS_100);
	}
	
	//@Test
	public void testGetAccessTokenWithFakeGoogleToken200() {
		testGetAccessTokenWithFakeGoogleToken(THREADS_200);
	}
	

	@Test
	public void testGetAccessTokenWithFakeGoogleToken500() {
		testGetAccessTokenWithFakeGoogleToken(1000);
	}
	
	public void testRevokeAccessTokenWithFakeToken() {
		final int numberOfThreads = 100;
		final int loops = 100;
		
		Runnable runnable = new Runnable() {

			public void run() {
				for (int i = 0; i < loops; i++) {
					String fakeGoogleAccessToken = RandomStringUtils.randomAscii(20);
					String strUrl = SERVER + "getAccessToken";
					HttpURLConnection conn;
					try {
						conn = createConnection(strUrl, "GET",
								new String[]{"google_access_token", "key", "scope"}, new String[] {fakeGoogleAccessToken, KEY, "Profile,WishList"});
						int responseCode = conn.getResponseCode();
						//Assert.assertEquals(responseCode, HttpURLConnection.HTTP_BAD_REQUEST);
						Assert.assertEquals(responseCode, HttpURLConnection.HTTP_OK);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		
		for (int i = 0; i< numberOfThreads; i++) {
			new Thread(runnable).start();
		}
	}
	
	
	private void testGetAccessTokenWithFakeGoogleToken(int numberOfThreads) {
		final int loops = 1;
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);
		Runnable runnable = new Runnable() {

			public void run() {
				for (int i = 0; i < loops; i++) {
					String fakeGoogleAccessToken = RandomStringUtils.randomAscii(20);
					String strUrl = SERVER + "getAccessToken";
					HttpURLConnection conn = null;
					try {
						conn = createConnection(strUrl, "GET",
								new String[]{"google_access_token", "key", "scope"}, new String[] {fakeGoogleAccessToken, KEY, "Profile,WishList"});
						int responseCode = conn.getResponseCode();
						if (responseCode != 200) {
							String error = getContent(conn.getErrorStream());
							System.out.println(error);
						}
						//Assert.assertEquals(responseCode, HttpURLConnection.HTTP_BAD_REQUEST);
						Assert.assertEquals(responseCode, HttpURLConnection.HTTP_OK);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (conn != null) conn.disconnect();
						latch.countDown();
					}
				}
			}
		};
		
		for (int i = 0; i< numberOfThreads; i++) {
			new Thread(runnable).start();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

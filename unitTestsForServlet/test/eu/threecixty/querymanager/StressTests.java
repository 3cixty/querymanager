package eu.threecixty.querymanager;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import eu.threecixty.profile.ProfileInformation;

/**
 * This class is to make sure whether or not our APIs are robust.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class StressTests extends HTTPCall {

	private String googleAccessToken = "ya29.AAG9kcKZw6AWhdXTwPEuxPsQs_jWAuJESxS04teOYd5p6V0IJkFylYAhWC6kOJvT6Fpj03oYDTMu9g";
	
	@BeforeClass
	public static void setup() {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
	}
	
	/**
	 * This method is to make sure 5 different connections to a same user can get the same data for his profile (first & last name).
	 * Since every time, our API removes and updates user profile. It is critical to make sure data for a user is consistent.
	 */
	//@Test
	public void testConsistentUserProfile() throws Exception {
		int numberOfThread = 5;
		final CountDownLatch latch = new CountDownLatch(numberOfThread);
		Runnable runnable = new Runnable() {
			
			public void run() {
				try {
					connect(googleAccessToken);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			}
		};
		
		for (int i = 0; i < numberOfThread; i++) {
			new Thread(runnable).start();
		}
		
		latch.await();
	}
	
	@Test
	public void testLoadUserProfile() throws Exception {
		final int numberOfThread = 50;
		final CountDownLatch latch = new CountDownLatch(numberOfThread);
		final String accessToken = getAccessToken(googleAccessToken);
		Runnable runnable = new Runnable() {
			
			public void run() {
				try {
					String strUrl = SERVER + "getProfile";
					HttpURLConnection conn = createConnection(strUrl, "GET",
							new String[]{"access_token"}, new String[] {accessToken});
					int responseCode = conn.getResponseCode();
					
					if (responseCode != 200) Assert.fail();

					String content = getContent(conn);
					
					if (content == null || content.equals("")) Assert.fail();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("number of Thread still remains" + latch.getCount());
					latch.countDown();
				}
			}
		};
		
		for (int i = 0; i < numberOfThread; i++) {
			new Thread(runnable).start();
		}
		
		latch.await();
	}
	

	private void connect(String googleAccessToken) throws Exception {
		String accessToken = getAccessToken(googleAccessToken);
		
		String strUrl = SERVER + "getProfile";
		HttpURLConnection conn = createConnection(strUrl, "GET",
				new String[]{"access_token"}, new String[] {accessToken});
		int responseCode = conn.getResponseCode();
		
		if (responseCode != 200) Assert.fail();

		String content = getContent(conn);
		
		if (content == null || content.equals("")) Assert.fail();
		
		Gson gson = new Gson();
		
		ProfileInformation profileInfo = gson.fromJson(content, ProfileInformation.class);
		
		if (profileInfo == null) Assert.fail();
		
		String firstName = "ThreeCixty";
		String lastName = "ThreeCixty";
		
		if (!firstName.equals(profileInfo.getFirstName())) Assert.fail();
		if (!lastName.equals(profileInfo.getLastName())) Assert.fail();
		System.out.println("UID = " + profileInfo.getUid());
	}
}

package eu.threecixty.querymanager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
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

	private String googleAccessToken = "ya29.AgGyefhW6cnF9ugRiPnJHW9gaoeZ-E1YZhn4zSV-luck49OCOhBI1T6nOYireaVsXpumdb78KFn5kQ";
	
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
		final int numberOfThreads = 200;
		final int numberOfLoops = 200;
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);
		final String accessToken = getAccessToken(googleAccessToken);
		Runnable runnable = new Runnable() {
			
			public void run() {
				try {
					for (int i = 0; i < numberOfLoops; i++) {
						System.out.println("Before connecting to the server");
						String strUrl = SERVER + "getProfile";
						System.out.println(strUrl);
						HttpURLConnection conn = createConnection(strUrl, "GET",
								new String[]{"access_token"}, new String[] {accessToken});
						System.out.println("already created connection");
						int responseCode = conn.getResponseCode();

						if (responseCode != 200) Assert.fail();

						String content = getContent(conn);

						if (content == null || content.equals("")) Assert.fail();


						Gson gson = new Gson();
						ProfileInformation profileInfo = gson.fromJson(content, ProfileInformation.class);
						if (!profileInfo.getFirstName().equals("ThreeCixty")) Assert.fail();
						if (!profileInfo.getLastName().equals("ThreeCixty")) Assert.fail();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("number of Threads still remains" + latch.getCount());
					latch.countDown();
				}
			}
		};
		
		for (int i = 0; i < numberOfThreads; i++) {
			new Thread(runnable).start();
		}
		
		latch.await();
	}
	
	//@Test
	public void testQueryWithoutAugmentation() throws Exception {
		final int numberOfThreads = 100;
		final int numberOfLoops = 100;
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);
		Runnable runnable = new Runnable() {
			
			public void run() {
				try {
					for (int i = 0; i < numberOfLoops; i++) {
						String query = "SELECT * WHERE { ?s ?p ?o . } LIMIT 25";
						String strUrl = SERVER + "executeQuery?format=json&query=" + URLEncoder.encode(query, "UTF-8");
						System.out.println("create connection i = " + i);
						HttpURLConnection conn = createConnection(strUrl, "GET",
								new String[]{"key"}, new String[] {KEY});
						int responseCode = conn.getResponseCode();
						if (responseCode != 200) Assert.fail();

						String content = getContent(conn);

						JSONObject jsonObj = new JSONObject(content);

						if (!jsonObj.has("results")) Assert.fail();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("number of Threads still remains" + latch.getCount());
					latch.countDown();
				}
			}
		};
		
		for (int i = 0; i < numberOfThreads; i++) {
			new Thread(runnable).start();
		}
		
		latch.await();
	}
	
	//@Test
	public void testSayHello() throws Exception {
		final int numberOfThreads = 50;
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);

		Runnable runnable = new Runnable() {
			
			public void run() {
				try {
					String hello = "Hello World";
					HttpURLConnection conn =  (HttpURLConnection)(
							new URL("https://dev.3cixty.com/v2-test-1/sayHello?input=" + URLEncoder.encode(hello, "UTF-8"))).openConnection();
					String content = getContent(conn);
					if (!hello.equals(content)) Assert.fail();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("number of Threads still remains" + latch.getCount());
					latch.countDown();
				}
			}
		};
		
		for (int i = 0; i < numberOfThreads; i++) {
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

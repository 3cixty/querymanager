package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import eu.threecixty.profile.Tray;
import eu.threecixty.profile.Tray.ItemType;

public class TrayServiceTests {

	private static final String TRAY_SERVICE = "http://localhost:8080/querymanagerServlet-1.0/services/tray/";
	private static final String USER_AGENT = "Mozilla/5.0";
	
	@Test
	public void testAdd() {
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "UID" + currentTime;
			Tray tray = new Tray();
			tray.setItemId("ItemID_001");
			tray.setItemType(ItemType.event);
			tray.setSource("TestApp");

			sendPost(TRAY_SERVICE + "add", createParams(uid, tray));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdate() {
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "UID" + currentTime;

			Tray tray = new Tray();
			tray.setItemId("ItemID_002");
			tray.setItemType(ItemType.event);
			tray.setSource("TestApp");

			sendPost(TRAY_SERVICE + "add", createParams(uid, tray));

			
			tray.setAttended(true);
			tray.setRating(5);
						
			sendPost(TRAY_SERVICE + "update", createParams(uid, tray));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelete() {
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "UID" + currentTime;
			Tray tray = new Tray();
			tray.setItemId("ItemID_003");
			tray.setItemType(ItemType.event);
			tray.setSource("TestApp");

			sendPost(TRAY_SERVICE + "add", createParams(uid, tray));
			
			sendPost(TRAY_SERVICE + "delete", createParams(uid, tray));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmpty() {
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "UID" + currentTime;
			Tray tray = new Tray();
			tray.setItemId("ItemID_004");
			tray.setItemType(ItemType.event);
			tray.setSource("TestApp");

			sendPost(TRAY_SERVICE + "add", createParams(uid, tray));			
			
			sendPost(TRAY_SERVICE + "empty", createParams(uid));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testList() {
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "UID" + currentTime;
			Tray tray1 = new Tray();
			tray1.setItemId("ItemID_005");
			tray1.setItemType(ItemType.event);
			tray1.setSource("TestApp");

			sendPost(TRAY_SERVICE + "add", createParams(uid, tray1));
			
			Tray tray2 = new Tray();
			tray2.setItemId("ItemID_006");
			tray2.setItemType(ItemType.event);
			tray2.setSource("TestApp");
			
			sendPost(TRAY_SERVICE + "add", createParams(uid, tray2));
			
			StringBuffer response = new StringBuffer();
			
			sendPost(TRAY_SERVICE + "list", createParams(uid), response);
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String createParams(String uid) {
		return "accessToken=" + uid;
	}
	
	private String createParams(String uid, Tray tray) throws Exception {
		StringBuffer params = new StringBuffer();
		params.append("accessToken=").append(uid);
		params.append("&tray=");
		Gson gson = new Gson();
		params.append(gson.toJson(tray));
		return params.toString();
	}
	
	private void sendPost(String url, String params) throws Exception {
		sendPost(url, params, null);
	}
	
	private void sendPost(String url, String params, StringBuffer response) throws Exception {
		 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();

		Assert.assertEquals(responseCode, 200);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
	}

}

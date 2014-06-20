package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

public class TrayTests {

	private static final String TRAY_SERVLET = "http://localhost:8080/querymanagerServlet-1.0/trayServlet";
	private static final String USER_AGENT = "Mozilla/5.0";
	
	@Test
	public void testAdd() {
		try {
			long currentTime = System.currentTimeMillis();
			sendPost(TRAY_SERVLET,"action=add_tray_element&element_id=http://data.linkedevents.org/event/000ad3b7-98ce-4865-b2f8-efbec380a06d " + currentTime + "&element_type=event&token=ya29.LgATIqu78NH5Fx8AAACwO8g1qmGzzupZ4ms0OifXu4oIV79YZl0dNkPVon5pXA&source=ExlorationApp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testList() {
		StringBuffer response = new StringBuffer();
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "TestUID" + currentTime;
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=001&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=002&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=003&element_type=event&token=" + uid + "&source=ExlorationApp");
			
			
			sendPost(TRAY_SERVLET, "action=get_tray_elements&google_token=" + uid, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("itemId").equals("001"));
			Assert.assertTrue(arr.getJSONObject(1).getString("itemId").equals("002"));
			Assert.assertTrue(arr.getJSONObject(2).getString("itemId").equals("003"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdate() {
		StringBuffer response = new StringBuffer();
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "TestUID" + currentTime;
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=004&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=005&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=006&element_type=event&token=" + uid + "&source=ExlorationApp");
			
			
			sendPost(TRAY_SERVLET, "action=update_tray_element&token=" + uid + "&element_id=004&element_type=event&source=ExlorationApp&attend=true");


			sendPost(TRAY_SERVLET, "action=get_tray_elements&google_token=" + uid, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("itemId").equals("005"));
			Assert.assertTrue(arr.getJSONObject(1).getString("itemId").equals("006"));
			Assert.assertTrue(arr.getJSONObject(2).getString("itemId").equals("004")); // changed because of updating time
			
			sendPost(TRAY_SERVLET, "action=update_tray_element&token=" + uid + "&element_id=004&element_type=event&attend=true&source=ExlorationApp&delete=true");
			
			response = new StringBuffer();
			
			sendPost(TRAY_SERVLET, "action=get_tray_elements&google_token=" + uid, response);
			arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmpty() {
		StringBuffer response = new StringBuffer();
		try {
			long currentTime = System.currentTimeMillis();
			String uid = "TestUID" + currentTime;
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=007&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=008&element_type=event&token=" + uid + "&source=ExlorationApp");
			sendPost(TRAY_SERVLET, "action=add_tray_element&element_id=009&element_type=event&token=" + uid + "&source=ExlorationApp");
			
			
			sendPost(TRAY_SERVLET, "action=get_tray_elements&google_token=" + uid, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			sendPost(TRAY_SERVLET, "action=empty_tray&token=" + uid);
			
			response = new StringBuffer();
			
			sendPost(TRAY_SERVLET, "action=get_tray_elements&google_token=" + uid, response);

			arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

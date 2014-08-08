package eu.threecixty.querymanager;


import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

public class TrayServiceTests extends HTTPCall {

//	private static final String TRAY_SERVLET = "http://3cixty.com:8080/querymanagerServlet-1.0/trayServlet";
//	private static final String APP_KEY="MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDMxMTM5Nzg3MikoZXMJaWxfd3BxZAVs";
	private static final String TRAY_SERVLET = "http://localhost:8080/querymanagerServlet-1.0/services/tray";
	private static final String APP_KEY="Y29uZy1raW5oLm5ndXllbkBpbnJpYS5mcjE0MDQ5ODQ2NTcwMTMAZW11XWEDKGdj";
	
	
	@Test
	public void testAdd() {
		try {
			long currentTime = System.currentTimeMillis();
			String params = createTray("add_tray_element", "http://data.linkedevents.org/event/000ad3b7-98ce-4865-b2f8-efbec380a06d " + currentTime, "event", "ExlorationApp", "ya29.NgBcSL5m75lSmiEAAAAKurNghpKPkcriWULzU-EC5JhQAye9t5AAy-BfPW4-usgHu90gjv4nF7BKHhkSvbY");
			sendPost(TRAY_SERVLET, params);
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
			
			String params1 = createTray("add_tray_element", "001", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params1);
			
			String params2 = createTray("add_tray_element", "002", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params2);
			
			String params3 = createTray("add_tray_element", "003", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params3);
						
			String params4 = createTray("get_tray_elements", null, null, null, uid);
			
			sendPost(TRAY_SERVLET, params4, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("element_id").equals("001"));
			Assert.assertTrue(arr.getJSONObject(1).getString("element_id").equals("002"));
			Assert.assertTrue(arr.getJSONObject(2).getString("element_id").equals("003"));
			
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
			
			String params4 = createTray("add_tray_element", "004", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params4);
			
			String params5 = createTray("add_tray_element", "005", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params5);
			
			String params6 = createTray("add_tray_element", "006", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params6);
			
			params4 = createTray("update_tray_element", "004", "event", "ExlorationApp", uid, true);
			sendPost(TRAY_SERVLET, params4);
			

			String paramsList = createTray("get_tray_elements", null, null, null, uid);
			
			sendPost(TRAY_SERVLET, paramsList, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			Assert.assertTrue(arr.getJSONObject(0).getString("element_id").equals("005"));
			Assert.assertTrue(arr.getJSONObject(1).getString("element_id").equals("006"));
			Assert.assertTrue(arr.getJSONObject(2).getString("element_id").equals("004")); // changed because of updating time
			
			// delete a tray
			params4 = createTray("update_tray_element", "004", "event", "ExlorationApp", uid, true, true);
			sendPost(TRAY_SERVLET, params4);
			
			response = new StringBuffer();
			
			sendPost(TRAY_SERVLET, paramsList, response);
			
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
			
			String params4 = createTray("add_tray_element", "004", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params4);
			
			String params5 = createTray("add_tray_element", "005", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params5);
			
			String params6 = createTray("add_tray_element", "006", "event", "ExlorationApp", uid);
			sendPost(TRAY_SERVLET, params6);			
			
			String paramsList = createTray("get_tray_elements", null, null, null, uid);
			
			sendPost(TRAY_SERVLET, paramsList, response);
			
			JSONArray arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 3);
			
			
			String paramsEmpty = createTray("empty_tray", null, null, null, uid);
			sendPost(TRAY_SERVLET, paramsEmpty);
			
			response = new StringBuffer();
			
			sendPost(TRAY_SERVLET, paramsList, response);

			arr = new JSONArray(response.toString());
			Assert.assertTrue(arr.length() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String createTray(String action, String trayItemId, String trayType, String source, String token, boolean attended, boolean deleted) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		if (action != null) buffer.append("\"action\":").append("\"" + action + "\", ");
		if (trayItemId != null) buffer.append("\"element_id\":").append("\"" + trayItemId + "\", ");
		if (trayType != null) buffer.append("\"element_type\":").append("\"" + trayType + "\", ");
		if (source != null) buffer.append("\"source\":").append("\"" + source + "\", ");
		if (token != null) buffer.append("\"token\":").append("\"" + token + "\", ");
		buffer.append("\"attend\":").append("\"" + attended + "\", ");
		buffer.append("\"delete\":").append("\"" + deleted + "\", ");
		buffer.append("\"key\":").append("\"" + APP_KEY + "\"");
		
		buffer.append("}");
		return buffer.toString();		
	}
	
	private String createTray(String action, String trayItemId, String trayType, String source, String token, boolean attended) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		if (action != null) buffer.append("\"action\":").append("\"" + action + "\", ");
		if (trayItemId != null) buffer.append("\"element_id\":").append("\"" + trayItemId + "\", ");
		if (trayType != null) buffer.append("\"element_type\":").append("\"" + trayType + "\", ");
		if (source != null) buffer.append("\"source\":").append("\"" + source + "\", ");
		if (token != null) buffer.append("\"token\":").append("\"" + token + "\", ");
		buffer.append("\"attend\":").append("\"" + attended + "\", ");
		buffer.append("\"key\":").append("\"" + APP_KEY + "\"");
		
		buffer.append("}");
		return buffer.toString();		
	}
	
	private String createTray(String action, String trayItemId, String trayType, String source, String token) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		if (action != null) buffer.append("\"action\":").append("\"" + action + "\", ");
		if (trayItemId != null) buffer.append("\"element_id\":").append("\"" + trayItemId + "\", ");
		if (trayType != null) buffer.append("\"element_type\":").append("\"" + trayType + "\", ");
		if (source != null) buffer.append("\"source\":").append("\"" + source + "\", ");
		if (token != null) buffer.append("\"token\":").append("\"" + token + "\", ");
		buffer.append("\"key\":").append("\"" + APP_KEY + "\"");
		buffer.append("}");
		return buffer.toString();
	}
}

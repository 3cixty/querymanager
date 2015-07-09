package eu.threecixty.cache;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.querymanager.HTTPCall;

/**
 * 
 * This class is used to test Tray items is in consistent by adding them in one server,
 * then getting them from another server.
 *
 */
public class TrayTests extends HTTPCall {
	
	private static final String APP_KEY = "687eedc0-17a4-4835-bee6-43ac9394cd04";
	private static final String SERVER1 = "http://91.250.81.194:8080/v2/tray";
	private static final String SERVER2 = "http://5.35.252.103:8080/v2/tray";

	@Test
	public void testAddGet() throws Exception {
		String token = System.currentTimeMillis() + "";
		String trayContent1 = createTray("add_tray_element", "001", "Event", "Explr", token);
		String trayContent2 = createTray("add_tray_element", "002", "Poi", "Explr", token);
		String trayContent3 = createTray("add_tray_element", "003", "Poi", "Explr", token);
		String trayContent4 = createTray("add_tray_element", "004", "Poi", "Explr", token);
		
		StringBuffer response = new StringBuffer();
		sendPost(SERVER1, trayContent1, response);
		
		response.setLength(0);
		String params4 = createTray("get_tray_elements", null, null, null, token);
		sendPost(SERVER2, params4, response); // this should cache trays list on server 2
		JSONArray arr1 = new JSONArray(response.toString());
		Assert.assertTrue(arr1.length() == 1);
		
		response.setLength(0);
		sendPost(SERVER1, trayContent2, response);
		response.setLength(0);
		sendPost(SERVER1, trayContent3, response);
		response.setLength(0);
		sendPost(SERVER1, trayContent4, response);
		
		response.setLength(0);
		sendPost(SERVER2, params4, response);
		JSONArray arr2 = new JSONArray(response.toString());
		Assert.assertTrue(arr2.length() == 4); // make sure that trays list on server 2 is in sync
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

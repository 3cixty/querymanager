package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;

import eu.threecixty.keys.KeyManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.Tray.OrderType;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.profile.Tray.ItemType;

public class TrayServlet extends HttpServlet {

	private static final String ITEM_ID_PARAM = "element_id";
	private static final String ITEM_TYPE_PARAM = "element_type";
	private static final String TOKEN_PARAM = "token";
	private static final String SOURCE_PARAM = "source";
	
	private static final String ADD_EXCEPTION_MSG = "Invalid parameters or duplicated tray items";
	
	private static final String LIST_EXCEPTION_MSG = "Invalid parameters";
	
	private static final int OK_CODE = 200;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2917965698509557521L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String inputContent = getRequestContent(req);
		if (inputContent == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		JSONObject jsonObj = new JSONObject(inputContent);
		
		PrintWriter out = resp.getWriter();
		String key = jsonObj.getString("key");
		if (KeyManager.getInstance().checkAppKey(key)) {
			String action = jsonObj.getString("action");

			if (action != null) {
				if (action.equals("add_tray_element")) {
					if (!addTrayElement(jsonObj)) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						out.write(ADD_EXCEPTION_MSG);
					} else {
						resp.setContentType("application/json");
						resp.setStatus(OK_CODE);
						out.write("{\"response\": \"OK\"}");
					}
				} else if (action.equals("get_tray_elements")) {
					List <Tray> trays = getTrayElements(jsonObj);
					if (trays == null) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						out.write(LIST_EXCEPTION_MSG);
					} else {
						resp.setContentType("application/json");
						resp.setStatus(OK_CODE);
						Gson gson = new Gson();
						String content = gson.toJson(trays);
						out.write(content);
					}
				} else if (action.equals("login_tray")) {
					List <Tray> trays = loginTray(jsonObj);
					if (trays == null) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					} else {
						resp.setContentType("application/json");
						resp.setStatus(OK_CODE);
						Gson gson = new Gson();
						String content = gson.toJson(trays);
						out.write(content);
					}
				} else if (action.equals("empty_tray")) {
					if (!cleanTrays(jsonObj)) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					} else {
						resp.setContentType("application/json");
						resp.setStatus(OK_CODE);
						out.write("{\"response\": \"OK\"}");
					}
				} else if (action.equals("update_tray_element")) {
					if (!updateTray(jsonObj)) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					} else {
						resp.setContentType("application/json");
						resp.setStatus(OK_CODE);
						out.write("{\"response\": \"OK\"}");					}
				}

			}
			out.close();
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your AppKey '" + key + "' is invalid. Please get a new key");		
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private boolean updateTray(JSONObject req) {
		String itemId = getKey(req, "element_id");
		if (itemId == null || itemId.equals("")) return false;
		String itemTypeStr = getKey(req, "element_type");
		if (itemTypeStr == null || itemTypeStr.equals("")) return false;
		ItemType itemType = ItemType.valueOf(itemTypeStr.toLowerCase());
		String token = req.getString("token");
		String uid = GoogleAccountUtils.getUID(token);
		String source = getKey(req, "source");
		String element_title = getKey(req, "element_title");
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setElement_title(element_title);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setUid((uid == null || uid.equals("")) ? token : uid);
		
		String deleteStr = getKey(req, "delete");
		if ("true".equals(deleteStr)) {
			return TrayStorage.deleteTray(tray);
		}
		
		String attendedStr = getKey(req, "attend");
		boolean attended = "true".equals(attendedStr);
		
		String datetimeAttendedStr = getKey(req, "attend_datetime");
		if (datetimeAttendedStr != null && !datetimeAttendedStr.equals("")) {
			boolean okDatetime = false;
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				Date d = format.parse(datetimeAttendedStr);
				if (d != null) okDatetime = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!okDatetime) return false;
			tray.setDateTimeAttended(datetimeAttendedStr);
		}
		
		tray.setAttended(attended);
		
		String ratingStr = getKey(req, "rating");
		if (ratingStr != null && !ratingStr.equals("")) {
			tray.setRating(Integer.parseInt(ratingStr));
		}
		
		return TrayStorage.update(tray);
	}

	private boolean cleanTrays(JSONObject req) {
		String token = getKey(req, "token");
		if (token == null || token.equals("")) return false;
		String uid = GoogleAccountUtils.getUID(token);
		if (uid == null || uid.equals("")) {
			return TrayStorage.cleanTrays(token);
		}
		return TrayStorage.cleanTrays(uid);
	}

	private List<Tray> loginTray(JSONObject req) {
		String junkToken = getKey(req, "junk_token");
		if (junkToken == null || junkToken.equals("")) return null;
		String googleToken = getKey(req, "google_token");
		String uid = GoogleAccountUtils.getUID(googleToken);
		if (uid == null || uid.equals("")) return null;
		if (!TrayStorage.replaceUID(junkToken, uid)) return null;
		return TrayStorage.getTrays(uid, 0, 100, OrderType.Desc, true);
	}

	private List<Tray> getTrayElements(JSONObject req) {
		String accessToken = getKey(req, "token");
		String uid = GoogleAccountUtils.getUID(accessToken);

		
		int offset = (!req.has("offset")) ? 0 : Integer.parseInt(req.getString("offset"));
		int limit = (!req.has("limit")) ? 100 : Integer.parseInt(req.getString("limit"));
		String orderStr = null;
		if (req.has("order_type")) orderStr = req.getString("order_type");
		OrderType orderType = (orderStr == null) ? OrderType.Desc
				: orderStr.equalsIgnoreCase("Desc") ? OrderType.Desc : OrderType.Asc;
		String showPastEventsStr = null;
		if (req.has("show_past_events")) showPastEventsStr = req.getString("show_past_events");
		boolean showPastEvents = (showPastEventsStr == null) ? true : "true".equalsIgnoreCase(showPastEventsStr);
		
		return TrayStorage.getTrays((uid == null || uid.equals("")) ? accessToken : uid,
				offset, limit, orderType, showPastEvents);
	}

	private boolean addTrayElement(JSONObject req) {
		String itemId = getKey(req, ITEM_ID_PARAM);
		if (itemId == null) return false;
		String itemTypeStr = getKey(req, ITEM_TYPE_PARAM);
		if (itemTypeStr == null) return false;
		ItemType itemType = null;
		try {
			itemType = ItemType.valueOf(itemTypeStr.toLowerCase());
			if (itemType == null) return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String token = getKey(req, TOKEN_PARAM);
		if (token == null) return false;
		
		String source = getKey(req, SOURCE_PARAM);
		if (source == null) return false;
		
		String element_title = getKey(req, "element_title");
		
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setElement_title(element_title);
		
		String uid = GoogleAccountUtils.getUID(token);
		if (uid == null || uid.equals("")) {
			tray.setUid(token);
		} else {
			tray.setUid(uid);
		}
		return TrayStorage.addTray(tray);
	}
	
	private String getRequestContent(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		byte[] b = new byte[1024];
		int readBytes = 0;
		try {
			InputStream input = req.getInputStream();
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes));
			}
			input.close();
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getKey(JSONObject json, String key) {
		if (json.has(key)) return json.getString(key);
		return null;
	}
}

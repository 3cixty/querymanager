package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import eu.threecixty.profile.Tray;
import eu.threecixty.profile.Tray.OrderType;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.profile.Tray.ItemType;

public class TrayServlet extends HttpServlet {

	private static final String ITEM_ID_PARAM = "element_id";
	private static final String ITEM_TYPE_PARAM = "element_type";
	private static final String TOKEN_PARAM = "token";
	private static final String SOURCE_PARAM = "source";
	
	private static final int ADD_ERROR = 401;
	private static final int LIST_ERROR = 402;
	private static final int LOGIN_ERROR = 403;
	private static final int CLEAN_ERROR = 404;
	private static final int UPDATE_ERROR = 405;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2917965698509557521L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String action = req.getParameter("action");
		
		if (action != null) {
			if (action.equals("add_tray_element")) {
				if (!addTrayElement(req)) {
					resp.setStatus(ADD_ERROR);
				}
			} else if (action.equals("get_tray_elements")) {
				List <Tray> trays = getTrayElements(req);
				if (trays == null) {
					resp.setStatus(LIST_ERROR);
				} else {
					Gson gson = new Gson();
					String content = gson.toJson(trays);
					out.write(content);
				}
			} else if (action.equals("login_tray")) {
				List <Tray> trays = loginTray(req);
				if (trays == null) {
					resp.setStatus(LOGIN_ERROR);
				} else {
					Gson gson = new Gson();
					String content = gson.toJson(trays);
					out.write(content);
				}
			} else if (action.equals("empty_tray")) {
				if (!cleanTrays(req)) {
					resp.setStatus(CLEAN_ERROR);
				}
			} else if (action.equals("update_tray_element")) {
				if (!updateTray(req)) {
					resp.setStatus(UPDATE_ERROR);
				}
			}
			
		}
		out.close();
	}
	
	private boolean updateTray(HttpServletRequest req) {
		String itemId = req.getParameter("element_id");
		if (itemId == null || itemId.equals("")) return false;
		String itemTypeStr = req.getParameter("element_type");
		if (itemTypeStr == null || itemTypeStr.equals("")) return false;
		ItemType itemType = ItemType.valueOf(itemTypeStr);
		String token = req.getParameter("token");
		String uid = GoogleAccountUtils.updateInfo(token);
		String source = req.getParameter("source");
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setUid((uid == null || uid.equals("")) ? token : uid);
		
		String deleteStr = req.getParameter("delete");
		if ("true".equals(deleteStr)) {
			return TrayStorage.deleteTray(tray);
		}
		
		String attendedStr = req.getParameter("attend");
		boolean attended = "true".equals(attendedStr);
		
		String datetimeAttendedStr = req.getParameter("attend_datetime");
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
		
		String ratingStr = req.getParameter("rating");
		if (ratingStr != null && !ratingStr.equals("")) {
			tray.setRating(Integer.parseInt(ratingStr));
		}
		
		return TrayStorage.update(tray);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doPost(req, resp);
	}

	private boolean cleanTrays(HttpServletRequest req) {
		String token = req.getParameter("token");
		if (token == null || token.equals("")) return false;
		String uid = GoogleAccountUtils.updateInfo(token);
		if (uid == null || uid.equals("")) {
			return TrayStorage.cleanTrays(token);
		}
		return TrayStorage.cleanTrays(uid);
	}

	private List<Tray> loginTray(HttpServletRequest req) {
		String junkToken = req.getParameter("junk_token");
		if (junkToken == null || junkToken.equals("")) return null;
		String googleToken = req.getParameter("google_token");
		String uid = GoogleAccountUtils.updateInfo(googleToken);
		if (uid == null || uid.equals("")) return null;
		if (!TrayStorage.replaceUID(junkToken, uid)) return null;
		return TrayStorage.getTrays(uid, 0, 100, OrderType.Desc, true);
	}

	private List<Tray> getTrayElements(HttpServletRequest req) {
		String accessToken = req.getParameter("google_token");
		String uid = GoogleAccountUtils.updateInfo(accessToken);

		String offsetStr = req.getParameter("offset");
		int offset = (offsetStr == null) ? 0 : Integer.parseInt(offsetStr);
		String limitStr = req.getParameter("limit");
		int limit = (limitStr == null) ? 100 : Integer.parseInt(limitStr);
		String orderStr = req.getParameter("order_type");
		OrderType orderType = (orderStr == null) ? OrderType.Desc
				: orderStr.equals("desc") ? OrderType.Desc : OrderType.Asc;
		String showPastEventsStr = req.getParameter("show_past_events");
		boolean showPastEvents = (showPastEventsStr == null) ? true : "true".equalsIgnoreCase(showPastEventsStr);
		
		return TrayStorage.getTrays((uid == null || uid.equals("")) ? accessToken : uid,
				offset, limit, orderType, showPastEvents);
	}

	private boolean addTrayElement(HttpServletRequest req) {
		String itemId = req.getParameter(ITEM_ID_PARAM);
		if (itemId == null) return false;
		String itemTypeStr = req.getParameter(ITEM_TYPE_PARAM);
		if (itemTypeStr == null) return false;
		ItemType itemType = null;
		try {
			itemType = ItemType.valueOf(itemTypeStr);
			if (itemType == null) return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String token = req.getParameter(TOKEN_PARAM);
		if (token == null) return false;
		
		String source = req.getParameter(SOURCE_PARAM);
		if (source == null) return false;
		
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		
		String uid = GoogleAccountUtils.updateInfo(token);
		if (uid == null || uid.equals("")) {
			tray.setUid(token);
		} else {
			tray.setUid(uid);
		}
		return TrayStorage.addTray(tray);
	}
}

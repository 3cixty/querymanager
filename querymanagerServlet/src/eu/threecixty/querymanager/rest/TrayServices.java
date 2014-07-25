package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;




import com.google.gson.Gson;

import eu.threecixty.keys.KeyManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.RestTrayObject;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.profile.Tray.ItemType;
import eu.threecixty.profile.Tray.OrderType;

/**
 * This class is an end point to expose Rest TrayAPIs to other components.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/tray")
public class TrayServices {
	private static final String ADD_ACTION = "add_tray_element";
	private static final String GET_ACTION = "get_tray_elements";
	private static final String LOGIN_ACTION = "login_tray";
	private static final String EMPTY_ACTION = "empty_tray";
	private static final String UPDATE_ACTION = "update_tray_element";

	
	private static final String ADD_EXCEPTION_MSG = "Invalid parameters or duplicated tray items";
	private static final String INVALID_PARAMS_EXCEPTION_MSG = "Invalid parameters";
	
	
    @POST
    @Path("/")
    public Response createProductInJSON(InputStream input) {
    	RestTrayObject restTray = getRestTrayObject(input);
    	if (input == null || restTray == null) {
			return createResponseException("Failed to understand your tray request");
    	} else {
    		if (!KeyManager.getInstance().checkAppKey(restTray.getKey())) {
    			return createResponseException("The key is invalid '" + restTray.getKey() + "'");
    		} else {
    			String action = restTray.getAction();
    			if (ADD_ACTION.equalsIgnoreCase(action)) {
    				if (!addTrayElement(restTray)) {
    	    			return createResponseException(ADD_EXCEPTION_MSG);
    				}
    			} else if (GET_ACTION.equalsIgnoreCase(action)) {
					List <Tray> trays = getTrayElements(restTray);
					if (trays == null) {
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
					} else {
						Gson gson = new Gson();
						String content = gson.toJson(trays);
						return Response.status(Response.Status.OK)
								.entity(content)
								.type(MediaType.APPLICATION_JSON_TYPE)
								.build();
					}
    			} else if (LOGIN_ACTION.equalsIgnoreCase(action)) {
    				List <Tray> trays = loginTray(restTray);
					if (trays == null) {
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
					} else {
						Gson gson = new Gson();
						String content = gson.toJson(trays);
						return Response.status(Response.Status.OK)
								.entity(content)
								.type(MediaType.APPLICATION_JSON_TYPE)
								.build();
					}
    			} else if (EMPTY_ACTION.equalsIgnoreCase(action)) {
    				if (!cleanTrays(restTray)) {
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    				}
    			} else if (UPDATE_ACTION.equalsIgnoreCase(action)) {
    				if (!updateTray(restTray)) {
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    				}
    			} else {
    				return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    			}
    		}
    	}
	    return Response.status(Response.Status.OK).build();

    }
	
    private RestTrayObject getRestTrayObject(InputStream input) {
    	if (input == null) return null;
    	StringBuffer buffer = new StringBuffer();
    	byte[] b = new byte[1024];
    	int readBytes = 0;
    	try {
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes));
			}
			Gson gson = new Gson();
			return gson.fromJson(buffer.toString(), RestTrayObject.class);
		} catch (IOException e) {
		}
		return null;
	}

	/**
     * Add tray into the KB.
     * @param restTray
     * @return
     */
	private boolean addTrayElement(RestTrayObject restTray) {
		String itemId = restTray.getElement_id();
		if (itemId == null) return false;
		String itemTypeStr = restTray.getElement_type();
		if (itemTypeStr == null) return false;
		ItemType itemType = null;
		try {
			itemType = ItemType.valueOf(itemTypeStr.toLowerCase());
			if (itemType == null) return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String token = restTray.getToken();
		if (token == null) return false;
		
		String source = restTray.getSource();
		if (source == null) return false;
		
		String element_title = restTray.getElement_title();
		
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
	
	/**
	 * Lists tray elements.
	 * @param restTray
	 * @return
	 */
	private List<Tray> getTrayElements(RestTrayObject restTray) {
		String accessToken = restTray.getToken();
		String uid = GoogleAccountUtils.getUID(accessToken);

		
		int offset = (restTray.getOffset() == null ? 0 : restTray.getOffset());
		int limit = (restTray.getLimit() == null ? 100 : restTray.getLimit());
		String orderStr = restTray.getOrderType();
		OrderType orderType = (orderStr == null) ? OrderType.Desc
				: orderStr.equalsIgnoreCase("Desc") ? OrderType.Desc : OrderType.Asc;
		boolean showPastEvents = (restTray.getShow_past_events() == null) ? true : restTray.getShow_past_events();
		
		return TrayStorage.getTrays((uid == null || uid.equals("")) ? accessToken : uid,
				offset, limit, orderType, showPastEvents);
	}
	
	/**
	 * Login
	 * @param restTray
	 * @return List of trays associated with a given junk token
	 */
	private List<Tray> loginTray(RestTrayObject restTray) {
		String junkToken = restTray.getJunk_token();
		if (junkToken == null || junkToken.equals("")) return null;
		String googleToken = restTray.getGoogle_token();
		String uid = GoogleAccountUtils.getUID(googleToken);
		if (uid == null || uid.equals("")) return null;
		if (!TrayStorage.replaceUID(junkToken, uid)) return null;
		return TrayStorage.getTrays(uid, 0, -1, OrderType.Desc, true);
	}

	/**
	 * Empties tray list.
	 * @param restTray
	 * @return
	 */
	private boolean cleanTrays(RestTrayObject restTray) {
		String token = restTray.getToken();
		if (token == null || token.equals("")) return false;
		String uid = GoogleAccountUtils.getUID(token);
		if (uid == null || uid.equals("")) {
			return TrayStorage.cleanTrays(token);
		}
		return TrayStorage.cleanTrays(uid);
	}

	/**
	 * Updates tray item;
	 * @param restTray
	 * @return
	 */
	private boolean updateTray(RestTrayObject restTray) {
		String itemId = restTray.getElement_id();
		if (itemId == null || itemId.equals("")) return false;
		String itemTypeStr = restTray.getElement_type();
		if (itemTypeStr == null || itemTypeStr.equals("")) return false;
		ItemType itemType = ItemType.valueOf(itemTypeStr.toLowerCase());
		String token = restTray.getToken();
		String uid = GoogleAccountUtils.getUID(token);
		String source = restTray.getSource();
		String element_title = restTray.getElement_title();
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setElement_title(element_title);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setUid((uid == null || uid.equals("")) ? token : uid);
		
		if (restTray.getDelete() != null && restTray.getDelete().booleanValue()) {
			return TrayStorage.deleteTray(tray);
		}
		
		boolean attended = (restTray.getAttend() == Boolean.TRUE);
		
		String datetimeAttendedStr = restTray.getAttend_datetime();
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
		
		if (restTray.getRating() > 0) {
			tray.setRating(restTray.getRating());
		}
		
		return TrayStorage.update(tray);
	}

	private Response createResponseException(String msg) {
		return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity(msg)
		        .type(MediaType.TEXT_PLAIN)
		        .build();
	}
}

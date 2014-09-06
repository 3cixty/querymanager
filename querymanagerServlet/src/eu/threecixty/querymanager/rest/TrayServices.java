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

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.OAuthWrappers;
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
@Path("/" + Constants.PREFIX_NAME + "/tray")
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
    public Response invokeTrayServices(InputStream input) {
    	long starttime = System.currentTimeMillis();
    	String restTrayStr = getRestTrayString(input);
		Gson gson = new Gson();
		RestTrayObject restTray = null;
		if (restTrayStr != null) {
			try {
				restTray = gson.fromJson(restTrayStr, RestTrayObject.class);
			} catch (Exception e) {}
		}
    	if (input == null || restTray == null) {
    		CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_PARAMS + restTrayStr);
			return createResponseException("Failed to understand your tray request");
    	} else {
    		if (!OAuthWrappers.validateAppKey(restTray.getKey())) {
    			CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_APP_KEY + restTray.getKey());
    			return createResponseException("The key is invalid '" + restTray.getKey() + "'");
    		} else {
    			String action = restTray.getAction();
    			if (ADD_ACTION.equalsIgnoreCase(action)) {
    				if (!addTrayElement(restTray)) {
    					CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_ADD_SERVICE, CallLoggingConstants.FAILED);
    	    			return createResponseException(ADD_EXCEPTION_MSG);
    				}
    				CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_ADD_SERVICE, CallLoggingConstants.SUCCESSFUL);
    			} else if (GET_ACTION.equalsIgnoreCase(action)) {
					List <Tray> trays = getTrayElements(restTray);
					if (trays == null) {
						CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.FAILED);
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
					} else {
						String content = gson.toJson(trays);
						CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
						return Response.status(Response.Status.OK)
								.entity(content)
								.type(MediaType.APPLICATION_JSON_TYPE)
								.build();
					}
    			} else if (LOGIN_ACTION.equalsIgnoreCase(action)) {
    				List <Tray> trays = loginTray(restTray);
					if (trays == null) {
						CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_LOGIN_SERVICE, CallLoggingConstants.FAILED);
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
					} else {
						String content = gson.toJson(trays);
						CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_LOGIN_SERVICE, CallLoggingConstants.SUCCESSFUL);
						return Response.status(Response.Status.OK)
								.entity(content)
								.type(MediaType.APPLICATION_JSON_TYPE)
								.build();
					}
    			} else if (EMPTY_ACTION.equalsIgnoreCase(action)) {
    				if (!cleanTrays(restTray)) {
    					CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_EMPTY_SERVICE, CallLoggingConstants.FAILED);
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    				}
    				CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_EMPTY_SERVICE, CallLoggingConstants.SUCCESSFUL);
    			} else if (UPDATE_ACTION.equalsIgnoreCase(action)) {
    				if (!updateTray(restTray)) {
    					CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.FAILED);
    	    			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    				}
    				CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.SUCCESSFUL);
    			} else {
    				CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_PARAMS + restTrayStr);
    				return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    			}
    		}
    	}
	    return Response.status(Response.Status.OK).entity("{\"response\": \"OK\" }").type(MediaType.APPLICATION_JSON_TYPE).build();

    }
	
    private String getRestTrayString(InputStream input) {
    	if (input == null) return null;
    	StringBuffer buffer = new StringBuffer();
    	byte[] b = new byte[1024];
    	int readBytes = 0;
    	try {
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes));
			}
			return buffer.toString();
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
		
		String image_url = restTray.getImage_url();
		
		String element_title = restTray.getElement_title();
		
		Tray tray = new Tray();
		tray.setItemId(itemId);
		tray.setItemType(itemType);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setElement_title(element_title);
		tray.setImage_url(image_url);
		
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

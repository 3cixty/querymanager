package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.ElementDetails;
import eu.threecixty.profile.ElementDetailsUtils;
import eu.threecixty.profile.InvalidTrayElement;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.RestTrayObject;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.Tray.OrderType;
import eu.threecixty.profile.VirtuosoManager;

/**
 * This class is an end point to expose Rest TrayAPIs to other components.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class TrayServices {
	private static final String WISH_LIST_SCOPE_NAME = Constants.WISH_LIST_SCOPE_NAME;
	
	private static final String ADD_ACTION = "add_tray_element";
	private static final String GET_ACTION = "get_tray_elements";
	private static final String GET_ACTION_IN_DETAILS = "get_tray_elements_in_details";
	private static final String LOGIN_ACTION = "login_tray";
	private static final String EMPTY_ACTION = "empty_tray";
	private static final String UPDATE_ACTION = "update_tray_element";
	
	private static final String EVENT_TYPE = "Event";
	private static final String POI_TYPE = "Poi";

	
	private static final String ADD_EXCEPTION_MSG = "Invalid parameters or duplicated tray items";
	private static final String INVALID_PARAMS_EXCEPTION_MSG = "Invalid parameters";
	
	 private static final Logger LOGGER = Logger.getLogger(
			 TrayServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
    @POST
    @Path("/tray")
    public Response invokeTrayServices(InputStream input, @Context Request req) {
    	long starttime = System.currentTimeMillis();
    	String restTrayStr = getRestTrayString(input);
		RestTrayObject restTray = null;
		if (restTrayStr != null) {
			try {
				Gson gson = new Gson();
				restTray = gson.fromJson(restTrayStr, RestTrayObject.class);
			} catch (Exception e) {}
		}
    	if (input == null || restTray == null) {
    		CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_PARAMS + restTrayStr);
			return createResponseException("Failed to understand your tray request");
    	} else {
    		if (!OAuthWrappers.validateAppKey(restTray.getKey())) {
    			if (restTray.getKey() != null && !restTray.getKey().equals("")) CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_APP_KEY + restTray.getKey());
    			return createResponseException("The key is invalid, key = " + restTray.getKey());
    		} else {
    			try {
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
    						long newestTimestamp = getNewestTimestamp(trays);
    						EntityTag etag = new EntityTag(Long.valueOf(newestTimestamp).hashCode() + "");
    						Response.ResponseBuilder rb = null;
    				        //Verify if it matched with etag available in http request
    				        rb = req.evaluatePreconditions(etag);
					        //Create cache control header
					         CacheControl cc = new CacheControl();
					         //Set max age to one day
					         cc.setMaxAge(86400);
    						if (rb == null) { // changed
    							String content = JSONObject.wrap(trays).toString();
    							
    							if (DEBUG_MOD) LOGGER.info(content);
    							
    							CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
    							return Response.status(Response.Status.OK)
    									.entity(content)
    									.type(MediaType.APPLICATION_JSON_TYPE)
    									.cacheControl(cc)
    									.tag(etag)
    									.build();
    						} else {
    							return rb.cacheControl(cc).tag(etag).status(Status.NOT_MODIFIED).build();
    						}
    					}
    				} else if (LOGIN_ACTION.equalsIgnoreCase(action)) {
    					List <Tray> trays = loginTray(restTray);
    					if (trays == null) {
    						CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_LOGIN_SERVICE, CallLoggingConstants.FAILED);
    						return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    					} else {
    						String content = JSONObject.wrap(trays).toString();
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
    				} else if (GET_ACTION_IN_DETAILS.equalsIgnoreCase(action)) {
    					return get_tray_elements_details(restTray, req, starttime);
    				} else {
    					CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_SERVICE, CallLoggingConstants.INVALID_PARAMS + restTrayStr);
    					return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
    				}
    			} catch (ThreeCixtyPermissionException e) {
    				return Response.status(Response.Status.FORBIDDEN)
    						.entity(e.getMessage())
    						.type(MediaType.TEXT_PLAIN)
    						.build();
    			} catch (InvalidTrayElement e) {
    				return Response.status(Response.Status.BAD_REQUEST)
    						.entity(e.getMessage())
    						.type(MediaType.TEXT_PLAIN_TYPE)
    						.build();
    			} catch (TooManyConnections e) {
    				return Response.status(Response.Status.SERVICE_UNAVAILABLE)
    						.entity(e.getMessage())
    						.type(MediaType.TEXT_PLAIN_TYPE)
    						.build();
				}
    		}
    	}
	    return Response.status(Response.Status.OK).entity("{\"response\": \"OK\" }").type(MediaType.APPLICATION_JSON_TYPE).build();

    }
	
//	@POST
//	@Path("/allTrays")
//	public Response showAllTrays(@FormParam("username") String username, @FormParam("password") String password) {
//		try {
//			AdminValidator admin = new AdminValidator();
//			if (admin.validate(username, password, CallLogServices.realPath)) {
//				List <Tray> allProfiles = ProfileManagerImpl.getInstance().getTrayManager().getAllTrays();
//				Gson gson = new Gson();
//				return Response.ok(gson.toJson(allProfiles), MediaType.APPLICATION_JSON_TYPE).build();
//			} else {
//				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_ERROR_PAGE + "errorLogin.jsp")).build();
//			}
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		return Response.serverError().build();
//	}
    
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
	private boolean addTrayElement(RestTrayObject restTray) throws ThreeCixtyPermissionException,
	        InvalidTrayElement, TooManyConnections {
		String itemId = restTray.getElement_id();
		if (itemId == null || itemId.equals("")) return false;
		String itemTypeStr = restTray.getElement_type();
		if (itemTypeStr == null) return false;

		String token = restTray.getToken();
		if (token == null || token.equals("")) return false;
		
		String source = restTray.getSource();
		if (source == null) return false;
		
		String image_url = restTray.getImage_url();
		
		String element_title = restTray.getElement_title();
		
		Tray tray = new Tray();
		tray.setElement_id(itemId);
		tray.setElement_type(itemTypeStr);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setElement_title(element_title);
		tray.setImage_url(image_url);
		
		String uid = OAuthWrappers.findGoogleUIDFrom(token);
		if (uid == null || uid.equals("")) {
			tray.setToken(token);
		} else {
			if (uid != null && !"".equals(uid)) {
				// check user permission
				checkPermission(token);
			}
			tray.setToken(uid);
		}
		return ProfileManagerImpl.getInstance().getTrayManager().addTray(tray);
	}
	
	/**
	 * Lists tray elements.
	 * @param restTray
	 * @return
	 */
	private List<Tray> getTrayElements(RestTrayObject restTray) throws ThreeCixtyPermissionException,
	        InvalidTrayElement, TooManyConnections {
		String access_token = restTray.getToken();
		String uid = OAuthWrappers.findGoogleUIDFrom(access_token);

		// XXX: check user permission
		if (uid != null && !"".equals(uid)) {
			checkPermission(access_token);
		}
		
		int offset = (restTray.getOffset() == null ? 0 : restTray.getOffset());
		int limit = (restTray.getLimit() == null ? 100 : restTray.getLimit());
		String orderStr = restTray.getOrderType();
		OrderType orderType = (orderStr == null) ? OrderType.Desc
				: orderStr.equalsIgnoreCase("Desc") ? OrderType.Desc : OrderType.Asc;
		boolean showPastEvents = (restTray.getShow_past_events() == null) ? true : restTray.getShow_past_events();
		
		return ProfileManagerImpl.getInstance().getTrayManager().getTrays((uid == null || uid.equals("")) ? access_token : uid,
				offset, limit, orderType, showPastEvents);
	}
	
	/**
	 * Login
	 * @param restTray
	 * @return List of trays associated with a given junk token
	 */
	private List<Tray> loginTray(RestTrayObject restTray) throws ThreeCixtyPermissionException,
	        InvalidTrayElement, TooManyConnections {
		String junkToken = restTray.getJunk_token();
		if (junkToken == null || junkToken.equals("")) return null;
		String threeCixtyToken = restTray.getThree_cixty_token();
		String uid = OAuthWrappers.findGoogleUIDFrom(threeCixtyToken);
		if (uid == null || uid.equals("")) return null;
		if (!ProfileManagerImpl.getInstance().getTrayManager().replaceUID(junkToken, uid)) return null;
		checkPermission(threeCixtyToken);
		return ProfileManagerImpl.getInstance().getTrayManager().getTrays(uid, 0, -1, OrderType.Desc, true);
	}

	/**
	 * Empties tray list.
	 * @param restTray
	 * @return
	 */
	private boolean cleanTrays(RestTrayObject restTray) throws ThreeCixtyPermissionException,
	        InvalidTrayElement, TooManyConnections {
		String token = restTray.getToken();
		if (token == null || token.equals("")) return false;
		String uid = OAuthWrappers.findGoogleUIDFrom(token);
		if (uid == null || uid.equals("")) {
			return ProfileManagerImpl.getInstance().getTrayManager().cleanTrays(token);
		}
		checkPermission(token);
		return ProfileManagerImpl.getInstance().getTrayManager().cleanTrays(uid);
	}

	/**
	 * Updates tray item;
	 * @param restTray
	 * @return
	 */
	private boolean updateTray(RestTrayObject restTray) throws ThreeCixtyPermissionException,
	        InvalidTrayElement, TooManyConnections {
		String itemId = restTray.getElement_id();
		if (itemId == null || itemId.equals("")) return false;
		String token = restTray.getToken();

		String uid = OAuthWrappers.findGoogleUIDFrom(token);
		
		// check user permission
		if (uid != null && !uid.equals("")) {
		    checkPermission(token);
		}
		
		Tray tray = ProfileManagerImpl.getInstance().getTrayManager().getTray((uid == null || uid.equals("")) ? token : uid, itemId);
		if (tray == null) return false;
		tray.setElement_id(itemId);
		
		String itemTypeStr = restTray.getElement_type();
		if (itemTypeStr != null) tray.setElement_type(itemTypeStr);
		
		String source = restTray.getSource();
		if (source != null) tray.setSource(source);
		
		String element_title = restTray.getElement_title();
		if (element_title != null) tray.setElement_title(element_title);

		tray.setTimestamp(System.currentTimeMillis());
		tray.setToken((uid == null || uid.equals("")) ? token : uid);

		
		if (restTray.getDelete() != null && restTray.getDelete().booleanValue()) {
			return ProfileManagerImpl.getInstance().getTrayManager().deleteTray(tray);
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
			tray.setAttend_datetime(datetimeAttendedStr);
		}
		
		tray.setAttend(attended);
		
		if (restTray.getRating() > 0) {
			tray.setRating(restTray.getRating());
		}
		
		return ProfileManagerImpl.getInstance().getTrayManager().updateTray(tray);
	}
	
	private Response get_tray_elements_details(RestTrayObject restTray, Request req, long starttime)
			throws ThreeCixtyPermissionException, InvalidTrayElement, TooManyConnections {
		List <Tray> trays = getTrayElements(restTray);
		if (trays == null) {
			CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.FAILED);
			return createResponseException(INVALID_PARAMS_EXCEPTION_MSG);
		} else {
			long newestTimestamp = getNewestTimestamp(trays);
			EntityTag etag = new EntityTag(Long.valueOf(newestTimestamp).hashCode() + "");
			Response.ResponseBuilder rb = null;
	        //Verify if it matched with etag available in http request
	        rb = req.evaluatePreconditions(etag);
	        //Create cache control header
	         CacheControl cc = new CacheControl();
	         //Set max age to one day
	         cc.setMaxAge(86400);
			if (rb == null) { // changed
				List <ElementDetails> trayDetailsList = new ArrayList <ElementDetails>();
				try {
					findTrayDetails(trays, trayDetailsList);
				} catch (IOException e) {
					throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
				}
				String content = JSONObject.wrap(trayDetailsList).toString();
				
				if (DEBUG_MOD) LOGGER.info(content);
				
				CallLoggingManager.getInstance().save(restTray.getKey(), starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.status(Response.Status.OK)
						.entity(content)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.cacheControl(cc)
						.tag(etag)
						.build();
			} else {
				return rb.cacheControl(cc).tag(etag).status(Status.NOT_MODIFIED).build();
			}
		}
	}
	
	private void findTrayDetails(List<Tray> trays,
			List<ElementDetails> trayDetailsList) throws IOException {
		List <String> eventIds = new LinkedList <String>();
		List <String> poiIds = new LinkedList <String>();
		for (Tray tray: trays) {
			if (tray.getElement_type().equalsIgnoreCase(EVENT_TYPE)) eventIds.add(tray.getElement_id());
			else if (tray.getElement_type().equalsIgnoreCase(POI_TYPE)) poiIds.add(tray.getElement_id());
		}

		List <ElementDetails> elementEventsDetails = ElementDetailsUtils.createEventsDetails(eventIds);
		if (elementEventsDetails != null) {
			for (ElementDetails eventDetails: elementEventsDetails) {
				eventDetails.setType(EVENT_TYPE);
			}
			trayDetailsList.addAll(elementEventsDetails);
		}
		List <ElementDetails> elementPoIsDetails = ElementDetailsUtils.createPoIsDetails(poiIds);
		if (elementPoIsDetails != null) {
			for (ElementDetails poiDetails: elementPoIsDetails) {
				poiDetails.setType(POI_TYPE);
			}
			trayDetailsList.addAll(elementPoIsDetails);
		}
	}

	private long getNewestTimestamp(List <Tray> trays) {
		long ret = -1;
		if (trays == null ||trays.size() == 0) return ret;
		for (Tray tray: trays) {
			if (tray.getTimestamp() > ret) {
				ret = tray.getTimestamp();
			}
		}
		return ret;
	}
	
	private void checkPermission(String token) throws ThreeCixtyPermissionException {
		AccessToken accessToken = OAuthWrappers.findAccessTokenFromDB(token);
		if (accessToken == null || !accessToken.getScopeNames().contains(WISH_LIST_SCOPE_NAME)) {
		    throw new ThreeCixtyPermissionException("{\"error\": \"no permission\"}");
		}
	}

	private Response createResponseException(String msg) {
		return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity(msg)
		        .type(MediaType.TEXT_PLAIN)
		        .build();
	}
}

package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



import com.google.gson.Gson;

import eu.threecixty.keys.KeyManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.TrayStorage;

/**
 * This class is an end point to expose Rest TrayAPIs to other components.
 * @author Cong-Kinh NGUYEN
 *
 */
@Path("/tray")
public class TrayServices {
	
	/**
	 * The method does some actions: add, update, and delete a tray item.
	 * @param action
	 * @param key
	 * @param accessToken
	 * @param tray
	 * @return
	 */
	@POST
	@Path("/{action}")
	@Produces("application/json")
	public String invokeTrayService(@PathParam("action") String action, @FormParam("key") String key,
			@FormParam("accessToken") String accessToken, @FormParam("tray") String tray) {
		if (!isNotNullOrEmpty(action) || !isNotNullOrEmpty(accessToken) || !isNotNullOrEmpty(tray)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Please check your parameters: action = " + action + ", accessToken = " + accessToken + ", tray = " + tray)
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) uid = accessToken;
			Tray newTray = convertString2Tray(tray);
			newTray.setUid(uid);
			if (action.equalsIgnoreCase("add")) {
				return addTray(uid, newTray);
			} else if (action.equalsIgnoreCase("update")) {
				return updateTray(uid, newTray);
			} else if (action.equalsIgnoreCase("delete")) {
				return deleteTray(uid, newTray);
			} else {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("The path /" + action + " is not supported. There are three paths supported: /add, /update and /delete")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * This method is to empty tray items associated with a given token.
	 * @param accessToken
	 * @param key
	 * @return Returns the message <code>{"empty": "true"}</code> if a given access token and key are correct.
	 *         Otherwise, returns an error with HTTP status code = 400.
	 */
	@POST
	@Path("/empty")
	@Produces("application/json")
	public String empty(
			@FormParam("accessToken") String accessToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(accessToken)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Please check your parameter:  accessToken = " + accessToken)
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) uid = accessToken;
			emptyTrays(uid);
			return "{\"empty\":\"true\"}";
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * This method is to create a list of Tray items in JSON format. 
	 * @param accessToken
	 * @param key
	 * @return
	 */
	@POST
	@Path("/list")
	@Produces("application/json")
	public String list(
			@FormParam("accessToken") String accessToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(accessToken)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Please check your parameter:  accessToken = " + accessToken)
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) uid = accessToken;
		    return listTrays(uid);
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	/**
	 * This method is to change a junk token with a Google access token.
	 * @param junkToken
	 * @param googleToken
	 * @param key
	 * @return Returns the message <code>{"login": "true"}</code> if a given access token and key are correct.
	 *         Otherwise, returns an error with HTTP status code = 400.
	 */
	@POST
	@Path("/login")
	@Produces("application/json")
	public String login(
			@FormParam("junkToken") String junkToken, @FormParam("googleToken") String googleToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(junkToken) || !isNotNullOrEmpty(googleToken)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Please check your parameters:  junkToken = " + junkToken + ", googleToken = " + googleToken)
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(googleToken);
			if (uid == null || uid.equals("")) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("The googleToken is invalid '" + googleToken + "'")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}

			if (TrayStorage.replaceUID(junkToken, uid)) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Cannot replace the junkToken with googleToken")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
			return "{\"login\":\"true\"}";
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
		
	private String listTrays(String uid) {
		List <Tray> trays = TrayStorage.getTrays(uid);
		Gson gson = new Gson();
		return gson.toJson(trays);
	}

	/**
	 * 
	 * @param uid
	 * @param newTray
	 * @return Returns the message <code>{"action": "true"}</code> if a given access token and key are correct.
	 *         Otherwise, returns an error with HTTP status code = 400.
	 */
	private String addTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId()) || !isNotNullOrEmpty(newTray.getSource())
				|| newTray.getItemType() == null) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("element_id, element_type and source must be not empty. They are: element_id = " + newTray.getItemId() + ", element_type = " + newTray.getItemType() + ", source = " + newTray.getSource())
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (!TrayStorage.addTray(newTray)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The tray seems to be existed or there is a problem for storing the tray")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		return "{\"action\":\"true\"}";
	}

	/**
	 * 
	 * @param uid
	 * @param newTray
	 * @return Returns the message <code>{"action": "true"}</code> if a given access token and key are correct.
	 *         Otherwise, returns an error with HTTP status code = 400.
	 */
	private String updateTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId()) || !isNotNullOrEmpty(newTray.getSource())
				|| newTray.getItemType() == null) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("element_id, element_type and source must be not empty. They are: element_id = " + newTray.getItemId() + ", element_type = " + newTray.getItemType() + ", source = " + newTray.getSource())
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (!TrayStorage.update(newTray)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The tray seems not to be existed or there is a problem for storing the tray")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		return "{\"action\":\"true\"}";
	}
	
	/**
	 * 
	 * @param uid
	 * @param newTray
	 * @return Returns the message <code>{"action": "true"}</code> if a given access token and key are correct.
	 *         Otherwise, returns an error with HTTP status code = 400.
	 */
	private String deleteTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId())) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("element_id must be not empty: element_id = " + newTray.getItemId())
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		if (!TrayStorage.deleteTray(newTray)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The tray seems not to be existed or there is a problem for deleting the tray")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
		return "{\"action\":\"true\"}";
	}
	
	private void emptyTrays(String uid) {
		if (!TrayStorage.cleanTrays(uid)) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("there is a problem for read/write the trays")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	private Tray convertString2Tray(String trayStr) {
		Gson gson = new Gson();
		Tray tray = gson.fromJson(trayStr, Tray.class);
		return tray;
	}

	private boolean isNotNullOrEmpty(String str) {
		if (str == null || str.equals("")) return false;
		return true;
	}
}

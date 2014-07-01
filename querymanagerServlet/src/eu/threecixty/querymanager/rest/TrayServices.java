package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;



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
	public String invokeTrayService(@PathParam("action") String action, @FormParam("key") String key,
			@FormParam("accessToken") String accessToken, @FormParam("tray") String tray) {
		if (!isNotNullOrEmpty(action) || !isNotNullOrEmpty(accessToken) || !isNotNullOrEmpty(tray))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
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
				throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
			}
		} else {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
	}
	
	/**
	 * This method is to empty tray items associated with a given token.
	 * @param accessToken
	 * @param key
	 * @return
	 */
	@POST
	@Path("/empty")
	public String empty(
			@FormParam("accessToken") String accessToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(accessToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) uid = accessToken;
			emptyTrays(uid);
			return "";
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
	
	/**
	 * This method is to create a list of Tray items in JSON format. 
	 * @param accessToken
	 * @param key
	 * @return
	 */
	@POST
	@Path("/list")
	public String list(
			@FormParam("accessToken") String accessToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(accessToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) uid = accessToken;
		    return listTrays(uid);
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
	
	/**
	 * This method is to change a junk token with a Google access token.
	 * @param junkToken
	 * @param googleToken
	 * @param key
	 * @return
	 */
	@POST
	@Path("/login")
	public String login(
			@FormParam("junkToken") String junkToken, @FormParam("googleToken") String googleToken, @FormParam("key") String key) {
		if (!isNotNullOrEmpty(junkToken) || !isNotNullOrEmpty(googleToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		if (KeyManager.getInstance().checkAppKey(key)) {
			String uid = GoogleAccountUtils.getUID(googleToken);
			if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);

			if (TrayStorage.replaceUID(junkToken, uid)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
			return "";
		} else throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}
		
	private String listTrays(String uid) {
		List <Tray> trays = TrayStorage.getTrays(uid);
		Gson gson = new Gson();
		return gson.toJson(trays);
	}

	private String addTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId()) || !isNotNullOrEmpty(newTray.getSource())
				|| newTray.getItemType() == null) {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
		if (!TrayStorage.addTray(newTray)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		return "";
	}

	private String updateTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId()) || !isNotNullOrEmpty(newTray.getSource())
				|| newTray.getItemType() == null) {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
		if (!TrayStorage.update(newTray)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		return "";
	}
	
	private String deleteTray(String uid, Tray newTray) {
		if (!isNotNullOrEmpty(newTray.getItemId())
				|| newTray.getItemType() == null) {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
		if (!TrayStorage.deleteTray(newTray)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		return "";
	}
	
	private void emptyTrays(String uid) {
		if (!TrayStorage.cleanTrays(uid)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
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

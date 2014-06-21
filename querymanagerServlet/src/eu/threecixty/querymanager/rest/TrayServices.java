package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;



import com.google.gson.Gson;

import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.TrayStorage;


@Path("/tray")
public class TrayServices {
	
	@POST
	@Path("/{action}")
	public String invokeTrayService(@PathParam("action") String action,
			@FormParam("accessToken") String accessToken, @FormParam("tray") String tray) {
		if (!isNotNullOrEmpty(action) || !isNotNullOrEmpty(accessToken) || !isNotNullOrEmpty(tray))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
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
	}
	
	@POST
	@Path("/empty")
	public String empty(
			@FormParam("accessToken") String accessToken) {
		if (!isNotNullOrEmpty(accessToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) uid = accessToken;
		emptyTrays(uid);
		return "";
	}
	
	@POST
	@Path("/list")
	public String list(
			@FormParam("accessToken") String accessToken) {
		if (!isNotNullOrEmpty(accessToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) uid = accessToken;
		return listTrays(uid);
	}
	
	@POST
	@Path("/login")
	public String login(
			@FormParam("junkToken") String junkToken, @FormParam("googleToken") String googleToken) {
		if (!isNotNullOrEmpty(junkToken) || !isNotNullOrEmpty(googleToken))
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		String uid = GoogleAccountUtils.getUID(googleToken);
		if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		
		if (TrayStorage.replaceUID(junkToken, uid)) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		return "";
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

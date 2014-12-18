package eu.threecixty.profile;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import eu.threecixty.profile.Tray.OrderType;

/**
 * This class is to deal with managing tray items in the KB.
 * @author Cong-Kinh NGUYEN
 *
 */
public class TrayStorage implements TrayManager {

	private static final Object _sync = new Object();
	
	private static final String TRAY_FILENAME = "temporaryTrayFile.json";
	
	private static TrayStorage instance;
	
	private static String path;
	
	public static TrayManager getInstance() {
		if (instance == null) {
			synchronized (_sync) {
				if (instance == null) {
					instance = new TrayStorage();
				}
			}
		}
		return instance;
	}

	public synchronized boolean addTray(Tray tray) {
		if (tray == null) return false;
		List <Tray> allTrays = getAllTrays();
		if (checkTrayExisted(tray, allTrays)) {
			return false;
		}
		allTrays.add(tray);
		return save(allTrays);
	}
	
	public synchronized boolean deleteTray(Tray tray) {
		if (tray == null) return false;
		List <Tray> allTrays = getAllTrays();

		int index = -1;
		for (int i = 0; i < allTrays.size(); i++) {
			Tray tmpTray = allTrays.get(i);
			if (checkEquality(tray.getItemId(), tmpTray.getItemId())) {
				if (tray.getUid() != null && !tray.getUid().equals("")) {
					if (tray.getUid().equals(tmpTray.getUid())) {
						index = i;
						break;
					}
				}
			}
		}
		if (index == -1) return false;
		allTrays.remove(index);
		return save(allTrays);
	}

	public synchronized boolean replaceUID(String junkID, String uid) {
		if (junkID == null || uid == null) return false;
		List <Tray> allTrays = getAllTrays();
		boolean changed = false;
		for (Tray tray: allTrays) {
			if (junkID.equals(tray.getUid())) {
				changed = true;
				tray.setUid(uid);
			}
		}
		if (!changed) return true;
		return save(allTrays);
	}

	public synchronized boolean updateTray(Tray tray) {
		if (tray == null) return false;
		List <Tray> allTrays = getAllTrays();
		
		int index = -1;
		for (int i = 0; i < allTrays.size(); i++) {
			Tray tmpTray = allTrays.get(i);
			if (checkEquality(tray.getItemId(), tmpTray.getItemId())) {
				if (tray.getUid() != null && !tray.getUid().equals("")) {
					if (tray.getUid().equals(tmpTray.getUid())) {
						index = i;
						break;
					}
				}
			}
		}
		if (index == -1) return true;
		allTrays.set(index, tray);
		return save(allTrays);
	}
	
	public synchronized List <Tray> getTrays(String uid, int offset, int limit,
			OrderType orderType, boolean eventsPast) {
		List <Tray> trays = getTrays(uid);
		int firstIndex = (offset < 0) ? 0: offset;
		if (firstIndex >= trays.size()) {
			trays.clear();
			return trays;
		}
		if (limit <= -1) return getTraysWithOrderAndEventPast(trays, orderType, eventsPast);
		List <Tray> limitedTrays = new ArrayList <Tray>();
		int lastIndex = Math.min(firstIndex + limit, trays.size());
		for (int i = firstIndex; i < lastIndex; i++) {
			limitedTrays.add(trays.get(i));
		}
		return getTraysWithOrderAndEventPast(limitedTrays, orderType, eventsPast);
	}
	
	public synchronized boolean cleanTrays(String token) {
		List <Tray> allTrays = getAllTrays();
		List <Tray> trays = getTrays(allTrays, token);
		allTrays.removeAll(trays);
		return save(allTrays);
	}

	private static List<Tray> getTraysWithOrderAndEventPast(List<Tray> trays,
			OrderType orderType, boolean eventsPast) {
		// TODO: correct this method with eventsPast. Need to get event time to decide
		// which event was taken place
		if (orderType == OrderType.Desc) {
			Collections.sort(trays, new Comparator<Tray>() {

				@Override
				public int compare(Tray tray1, Tray tray2) {
					long distance = tray1.getTimestamp() - tray2.getTimestamp();
					if (distance > 0) return 1;
					else if (distance < 0) return -1;
					return 0;
				}
			});
		} else if (orderType == OrderType.Asc) {
			Collections.sort(trays, new Comparator<Tray>() {

				@Override
				public int compare(Tray tray1, Tray tray2) {
					long distance = tray1.getTimestamp() - tray2.getTimestamp();
					if (distance > 0) return -1;
					else if (distance < 0) return 1;
					return 0;
				}
			});
		}
		if (eventsPast) return trays;
		return trays;
	}

	public synchronized List <Tray> getTrays(String uid) {
		if (uid == null) return new ArrayList <Tray>();
		List <Tray> allTrays = getAllTrays();
		return getTrays(allTrays, uid);
	}

	public synchronized Tray getTray(String uid, String trayId) {
		if (uid == null || trayId == null) return null;
		List <Tray> trays = getTrays(uid);
		for (Tray tmp: trays) {
			if (trayId.equals(tmp.getItemId())) {
				return tmp;
			}
		}
		return null;
	}


	public synchronized List <Tray> getAllTrays() {
		List <Tray> trays = new ArrayList <Tray>();
		String content = getContent();
		try {
		JSONArray array = new JSONArray(content);
		for(int i = 0 ; i < array.length(); i++){
		    JSONObject jsonObj = (JSONObject)array.get(i);
		    Tray tray = new Tray();
		    tray.setItemId(jsonObj.getString("element_id"));
		    tray.setItemType(jsonObj.getString("element_type"));
		    tray.setTimestamp(jsonObj.getLong("timestamp"));
		    tray.setUid(jsonObj.getString("token"));
		    tray.setSource(jsonObj.getString("source"));
		    
		    if (jsonObj.has("attend")) tray.setAttended(jsonObj.getBoolean("attend"));
		    if (jsonObj.has("attend_datetime")) tray.setDateTimeAttended(jsonObj.getString("attend_datetime"));
		    if (jsonObj.has("rating")) tray.setRating(jsonObj.getInt("rating"));
		    if (jsonObj.has("element_title")) tray.setElement_title(jsonObj.getString("element_title"));
		    if (jsonObj.has("image_url")) tray.setImage_url(jsonObj.getString("image_url"));
		    trays.add(tray);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trays;
	}

	public synchronized static boolean save(List <Tray> trays) {
		if (path == null) return false;
		File file = new File(path + "/" + TRAY_FILENAME);
		if (file.exists()) file.delete();
		FileOutputStream fos = null;
		boolean ok = false;
		try {
			fos = new FileOutputStream(file);
			Gson gson = new Gson();
			String content = gson.toJson(trays);
			fos.write(content.getBytes());
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ok;
	}
		
	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		TrayStorage.path = path;
	}
	
	private static List <Tray> getTrays(List <Tray> allTrays, String token) {
		List <Tray> rets = new ArrayList <Tray>();
		if (token == null) return rets;
		for (Tray tray: allTrays) {
			if (token.equals(tray.getUid())) rets.add(tray);
		}
		return rets;
	}
	
	private static String getContent() {
		if (path == null) return "[]";
		StringBuffer buffer = new StringBuffer();
		File file = new File(path + "/" + TRAY_FILENAME);
		if (!file.exists()) return "[]";
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			byte [] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if (buffer.length() == 0) return "[]";
		return buffer.toString();
	}

	private static boolean checkTrayExisted(Tray tray, List<Tray> allTrays) {
		for (Tray tmpTray: allTrays) {
			if (checkEquality(tray.getItemId(), tmpTray.getItemId()) &&
					(tray.getItemType().equals(tmpTray.getItemType()))) {
				if (tray.getUid() != null && !tray.getUid().equals("")) {
					if (tray.getUid().equals(tmpTray.getUid())) return true;
				}
			}
		}
		return false;
	}
	
	private static boolean checkEquality(String str1, String str2) {
		if (str1 == null && str2 == null) return true;
		else if (str1 == null && str2 != null) return false;
		return str1.equals(str2);
	}
	
	private TrayStorage() {
	}
}

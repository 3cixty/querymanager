package eu.threecixty.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.threecixty.profile.Tray;

public class TrayCacheManager {

	private static final TrayCacheManager INSTANCE = new TrayCacheManager();
	
	private Map <String, List<Tray>> trayCaches;
	
	public static TrayCacheManager getInstance() {
		return INSTANCE;
	}
	
	public List <Tray> getTrays(String token) {
//		return trayCaches.get(token);
		return null;
	}
	
	public Tray getTray(String token, String elementId) {
//		List <Tray> list = trayCaches.get(token);
//		if (list == null) return null;
//		for (Tray tmp: list) {
//			if (tmp.getElement_id().equals(elementId)) return tmp;
//		}
		return null;
	}
	
	public void putTray(Tray tray) {
//		if (tray == null) return;
//		List <Tray> list = trayCaches.get(tray.getToken());
//		if (list == null) return;
//		Tray oldTray = null;
//		boolean found = false;
//		for (Tray tmpTray: list) {
//			if (tmpTray.getElement_id().equals(tray.getElement_id())) {
//				found = true;
//				oldTray = tmpTray;
//				break;
//			}
//		}
//		if (!found) list.add(tray);
//		else {
//			int index = list.indexOf(oldTray);
//			list.set(index, tray);
//		}
	}
	
	public void removeTray(Tray tray) {
//		List <Tray> list = trayCaches.get(tray == null ? null : tray.getToken());
//		if (list == null) return;
//		Tray trayRemoved = null;
//		boolean found = false;
//		for (Tray tmpTray: list) {
//			if (tmpTray.getElement_id().equals(tray.getElement_id())) {
//				found = true;
//				trayRemoved = tmpTray;
//				break;
//			}
//		}
//		if (found) {
//			list.remove(trayRemoved);
//		}
	}
	
	public void removeTrays(String token) {
//		trayCaches.remove(token);
	}
	
	public void addTrays(List <Tray> trays) {
//		if (trays == null || trays.size() == 0) return;
//		String token = trays.get(0).getToken();
//		if (!trayCaches.containsKey(token)) {
//			trayCaches.put(token, trays);
//		} else {
//			for (Tray tray: trays) {
//				putTray(tray);
//			}
//		}
	}
	
	private TrayCacheManager() {
		trayCaches = new ConcurrentHashMap<String, List<Tray>>();
	}
}

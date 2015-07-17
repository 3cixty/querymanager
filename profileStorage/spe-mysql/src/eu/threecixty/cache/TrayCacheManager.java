package eu.threecixty.cache;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;
import eu.threecixty.profile.Tray;

public class TrayCacheManager {
	
	private static final String TRAY_KEY = "tray";
	private static final int TIME_OUT_TO_GET_CACHE = 200; // in millisecond
	
	private List<MemcachedClient> memcachedClients;
	
	public static TrayCacheManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public List <Tray> getTrays(String token) {
		if (token == null) return null;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, TRAY_KEY + token);
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(TRAY_KEY + token);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) {
					
					@SuppressWarnings("unchecked")
					List <Tray> trays = (List <Tray>) myObj;
					return trays;
				}
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public Tray getTray(String token, String elementId) {
		List <Tray> list = getTrays(token);
		if (list == null) return null;
		for (Tray tmp: list) {
			if (tmp.getElement_id().equals(elementId)) return tmp;
		}
		return null;
	}
	
	public void putTray(Tray tray) {
		if (tray == null) return;
		List <Tray> list = getTrays(tray.getToken());
		if (list == null) return;
		Tray oldTray = null;
		boolean found = false;
		for (Tray tmpTray: list) {
			if (tmpTray.getElement_id().equals(tray.getElement_id())) {
				found = true;
				oldTray = tmpTray;
				break;
			}
		}
		if (!found) list.add(tray);
		else {
			int index = list.indexOf(oldTray);
			list.set(index, tray);
		}
		putData(tray.getToken(), list);
	}
	
	public void removeTray(Tray tray) {
		List <Tray> list = getTrays(tray == null ? null : tray.getToken());
		if (list == null) return;
		Tray trayRemoved = null;
		boolean found = false;
		for (Tray tmpTray: list) {
			if (tmpTray.getElement_id().equals(tray.getElement_id())) {
				found = true;
				trayRemoved = tmpTray;
				break;
			}
		}
		if (found) {
			list.remove(trayRemoved);
			putData(tray.getToken(), list);
		}
	}
	
	public void removeTrays(String token) {
		if (token == null) return;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, TRAY_KEY + token);
		if (memcachedClient == null) return;
		if (memcachedClient != null) {
			memcachedClient.delete(TRAY_KEY + token);
		}
	}
	
	public void addTrays(List <Tray> trays) {
		if (trays == null || trays.size() == 0) return;
		String token = trays.get(0).getToken();
		List <Tray> existingTrays = getTrays(token);
		if (existingTrays == null) {
			putData(token, trays);
		} else {
			for (Tray tray: trays) {
				boolean found = false;
				for (Tray tmpTray: existingTrays) {
					if (tmpTray.getElement_id().equals(tray.getElement_id())) {
						found = true;
						break;
					}
				}
				if (!found) existingTrays.add(tray);
			}
			putData(token, existingTrays);
		}
	}
	
	public void stop() {
		if (memcachedClients != null) {
			for (MemcachedClient client: memcachedClients) {
				client.shutdown();
			}
		}
	}
	
	private <T> void putData(String key, T data) {
		if (memcachedClients != null) {
			MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, TRAY_KEY + key);
			if (memcachedClient == null) return;
			memcachedClient.set(TRAY_KEY + key, 0, data);
			memcachedClient.flush();
		}
	}
	
	private TrayCacheManager() {		
		memcachedClients = MemcachedUtils.createClients();
	}
	
	private static class SingletonHolder {
		private static TrayCacheManager INSTANCE = new TrayCacheManager();
	}
}

package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import eu.threecixty.cache.TrayCacheManager;
import eu.threecixty.profile.Tray.OrderType;

public class MySQLTrayManager implements TrayManager {
	
	private static final TrayManager instance = new MySQLTrayManager();
	
	public static TrayManager getInstance() {
		return instance;
	}

	public boolean addTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		boolean successful = TrayUtils.addTray(tray);
		if (successful) {
			TrayCacheManager.getInstance().putTray(tray);
			TrexManager.getInstance().publish(tray.getElement_id(),
					tray.getElement_title(), tray.getImage_url());
		}
		return successful;
	}

	public boolean cleanTrays(String token) throws InvalidTrayElement,
			TooManyConnections {
		boolean successful = TrayUtils.cleanTrays(token);
		if (successful) TrayCacheManager.getInstance().removeTrays(token);
		return successful;
	}

	public boolean deleteTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		boolean successful = TrayUtils.deleteTray(tray);
		if (successful) {
			TrayCacheManager.getInstance().removeTray(tray);
		}
		return successful;
	}

	public List<Tray> getAllTrays() throws TooManyConnections {
		// TODO Auto-generated method stub
		return null;
	}

	public Tray getTray(String token, String elementId) throws InvalidTrayElement,
			TooManyConnections {
		Tray tray = TrayCacheManager.getInstance().getTray(token, elementId);
		if (tray != null) return tray;
		return TrayUtils.getTray(token, elementId);
	}

	public List<Tray> getTrays(String token) throws InvalidTrayElement,
			TooManyConnections {
		List <Tray> trays = TrayCacheManager.getInstance().getTrays(token);
		if (trays != null) return trays;
		trays = TrayUtils.getTrays(token);
		TrayCacheManager.getInstance().addTrays(trays);
		return trays;
	}

	public List<Tray> getTrays(String token, int offset, int limit, OrderType orderType,
			boolean pastEventsShown) throws InvalidTrayElement, TooManyConnections {
		List <Tray> trays = getTrays(token);
		int firstIndex = (offset < 0) ? 0: offset;
		if (firstIndex >= trays.size()) {
			trays.clear();
			return trays;
		}
		if (limit <= -1) return getTraysWithOrderAndEventPast(
				trays, orderType, pastEventsShown);
		List <Tray> limitedTrays = new ArrayList <Tray>();
		int lastIndex = Math.min(firstIndex + limit, trays.size());
		for (int i = firstIndex; i < lastIndex; i++) {
			limitedTrays.add(trays.get(i));
		}
		return getTraysWithOrderAndEventPast(
				limitedTrays, orderType, pastEventsShown);
	}

	public boolean replaceUID(String junkToken, String uid)
			throws InvalidTrayElement, TooManyConnections {
		if (junkToken == null || uid == null) return false;
		List <Tray> junkTrays = getTrays(junkToken);
		List <Tray> originalTrays = getTrays(uid);
		boolean successful = TrayUtils.deleteTrays(junkToken);
		if (successful) {
			List <Tray> tmpTrays = new LinkedList <Tray>();
			for (Tray junkTray: junkTrays) {
				if (!isFound(junkTray, originalTrays)) {
					tmpTrays.add(junkTray);
				}
			}
			TrayCacheManager.getInstance().removeTrays(junkToken);
			List <Tray> trays = TrayCacheManager.getInstance().getTrays(uid);
			if (tmpTrays != null && tmpTrays.size() > 0) {
				for (Tray junkTray: tmpTrays) {
					junkTray.setToken(uid);
				}
				TrayUtils.addTrays(tmpTrays);
			}
			if (trays != null) TrayCacheManager.getInstance().addTrays(tmpTrays);
		}
		return successful;
	}

	public boolean updateTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		boolean successful = TrayUtils.updateTray(tray);
		if (successful) TrayCacheManager.getInstance().putTray(tray);
		return successful;
	}
	
	private List<Tray> getTraysWithOrderAndEventPast(List<Tray> trays,
			OrderType orderType, boolean eventsPast) {
		if (orderType == OrderType.Desc) {
			Collections.sort(trays, new Comparator<Tray>() {


				public int compare(Tray tray1, Tray tray2) {
					long distance = tray1.getTimestamp() - tray2.getTimestamp();
					if (distance > 0) return 1;
					else if (distance < 0) return -1;
					return 0;
				}
			});
		} else if (orderType == OrderType.Asc) {
			Collections.sort(trays, new Comparator<Tray>() {

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

	private boolean isFound(Tray tray, List <Tray> list) {
		for (Tray tmp: list) {
			if (tmp.getElement_id().equals(tray.getElement_id())) return true;
		}
		return false;
	}
}

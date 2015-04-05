package eu.threecixty.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.threecixty.profile.Tray.OrderType;

public class MySQLTrayManager implements TrayManager {
	
	private static final TrayManager instance = new MySQLTrayManager();
	
	public static TrayManager getInstance() {
		return instance;
	}

	public boolean addTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.addTray(tray);
	}

	public boolean cleanTrays(String token) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.cleanTrays(token);
	}

	public boolean deleteTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.deleteTray(tray);
	}

	public List<Tray> getAllTrays() throws TooManyConnections {
		// TODO Auto-generated method stub
		return null;
	}

	public Tray getTray(String token, String elementId) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.getTray(token, elementId);
	}

	public List<Tray> getTrays(String token) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.getTrays(token);
	}

	public List<Tray> getTrays(String token, int offset, int limit, OrderType orderType,
			boolean pastEventsShown) throws InvalidTrayElement, TooManyConnections {
		// XXX: for sake of simplicity, I get all trays. The following code should be 
		// replaced by SQL order clause.
		List <Tray> trays = TrayUtils.getTrays(token);
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
		return TrayUtils.replaceUID(junkToken, uid);
	}

	public boolean updateTray(Tray tray) throws InvalidTrayElement,
			TooManyConnections {
		return TrayUtils.updateTray(tray);
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

}

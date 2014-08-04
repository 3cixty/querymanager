package eu.threecixty.stats;

public class StatsManager {

	private static final Object _sync = new Object();

	private static StatsManager singleton;

	private StatsStorage storage;
	

	public static StatsManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new StatsManager();
			}
		}
		return singleton;
	}

	public boolean save(Stats stats) {
		return storage.save(stats);
	}

	private StatsManager() {
		storage = new StatsStorageImpl();
	}
}

package eu.threecixty.logs;

import java.util.List;

import eu.threecixty.keys.KeyManager;

public class CallLoggingManager {

	private static final Object _sync = new Object();

	private static CallLoggingManager singleton;

	private CallLoggingStorage storage;
	

	public static CallLoggingManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new CallLoggingManager();
			}
		}
		return singleton;
	}

	public boolean save(CallLogging stats) {
		return storage.save(stats);
	}

	/**
	 * Saves information for a call to ThreeCixty services from Apps.
	 * @param appkey
	 * @param starttime
	 * 				start time in millisecond
	 * @param endtime
	 * 				end time in millisecond
	 * @param serviceName
	 * @return
	 */
	public boolean save(String appkey, long starttime, long endtime, String serviceName) {
		CallLogging logging = new CallLogging();
		logging.setAppKey(KeyManager.getInstance().getAppKeyFromKey(appkey));
		logging.setServiceName(serviceName);
		logging.setStartTime(starttime);
		int timeConsumed = (int) ((endtime - starttime) / 1000); // in second
		logging.setTimeConsumed(timeConsumed);
		return save(logging);
	}

	/**
	 * Saves information for a call to ThreeCixty services from Apps.
	 * @param appkey
	 * @param starttime
	 * 				start time in millisecond
	 * @param serviceName
	 * @param description
	 * @return
	 */
	public boolean save(String appkey, long starttime, String serviceName, String description) {
		long endtime = System.currentTimeMillis();
		CallLogging logging = new CallLogging();
		logging.setAppKey(KeyManager.getInstance().getAppKeyFromKey(appkey));
		logging.setServiceName(serviceName);
		logging.setStartTime(starttime);
		int timeConsumed = (int) ((endtime - starttime) / 1000); // in second
		logging.setTimeConsumed(timeConsumed);
		logging.setDescription(description);
		return save(logging);
	}

	/**
	 * Lists all stats related to a given appkey
	 * @param appkey
	 * @return
	 */
	public List<CallLogging> getStats(String appkey) {
		return getStats(appkey, 0, System.currentTimeMillis());
	}

	public List<CallLogging> getStats(String appkey, long from, long to) {
		return getStats(appkey, from, to, 0, Integer.MAX_VALUE);
	}

	public List<CallLogging> getStats(String appkey, long from, long to, int minTimeConsumed, int maxTimeConsumed) {
		return storage.getStats(appkey, from, to, minTimeConsumed, maxTimeConsumed);
	}

	

	private CallLoggingManager() {
		storage = new CallLoggingStorageImpl();
	}
}

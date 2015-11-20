package eu.threecixty.logs;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * This class to provide interfaces to persist callLog and retrieve them from database.
 *
 */
public class CallLoggingManager {

	private CallLoggingStorage storage;
	
	private Queue <CallLogging> callLoggings;
	

	public static CallLoggingManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Persists callLog into database.
	 * <br>
	 * This method persists callLog every 50 items. If the current queue has less than 50 items,
	 * the method just stores callLog information in memory.
	 *
	 * @param stats
	 * @return
	 */
	public boolean save(CallLogging stats) {
		callLoggings.add(stats);
		List <CallLogging> listToStore = null;
		if (callLoggings.size() >= 50) {
			synchronized (this) {
				if (callLoggings.size() >= 50) {
				    int count = 0;
					while (count < 50) {
						CallLogging callLogging = callLoggings.poll();
						if (callLogging != null) {
							if (count == 0) listToStore = new LinkedList<CallLogging>();
							listToStore.add(callLogging);
							count++;
						} else {
							break;
						}
					}
				}
			}
		}
		if (listToStore != null && listToStore.size() > 0) {
			return storage.save(listToStore);
		}
		return true;
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
		logging.setKey(appkey);
		logging.setServiceName(serviceName);
		logging.setStartTime(new Date(starttime));
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
		logging.setKey(appkey);
		logging.setServiceName(serviceName);
		logging.setStartTime(new Date(starttime));
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
	public List<CallLogging> getCalls(String appkey) {
		return getCalls(appkey, 0, System.currentTimeMillis());
	}

	public List<CallLogging> getCalls(String appkey, long from, long to) {
		return getCalls(appkey, from, to, 0, Integer.MAX_VALUE);
	}

	public List<CallLogging> getCalls(String appkey, long from, long to, int minTimeConsumed, int maxTimeConsumed) {
		return storage.getCalls(appkey, from, to, minTimeConsumed, maxTimeConsumed);
	}

	
	public List<CallLoggingDisplay> getCallsWithCount() {
		return getCallsWithCount( 0, System.currentTimeMillis());
	}

	public List<CallLoggingDisplay> getCallsWithCount(long from, long to) {
		return getCallsWithCount(from, to, 0, Integer.MAX_VALUE);
	}

	public List<CallLoggingDisplay> getCallsWithCount(long from, long to, int minTimeConsumed, int maxTimeConsumed) {
		return storage.getCallsWithCount(from, to, minTimeConsumed, maxTimeConsumed);
	}

	public List<RelativeNumberOfUsers> getRelativeNumberofUsers(){
		return storage.getRelativeNumberofUsers();
	}
	
	public List<CallLoggingDisplay> getCallsWithCountByMonth() {
		return storage.getCallsWithCountByMonth();
	}
	
	public List<CallLoggingDisplay> getCallsWithCountByDay() {
		return storage.getCallsWithCountByDay();
	}
	
	private CallLoggingManager() {
		storage = new CallLoggingStorageImpl();
		callLoggings = new ConcurrentLinkedQueue<CallLogging>();
	}
	
	/**Singleton holder for lazy initiation*/
	private static class SingletonHolder {
		private static final CallLoggingManager INSTANCE = new CallLoggingManager();
	}
}

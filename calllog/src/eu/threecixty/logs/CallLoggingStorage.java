package eu.threecixty.logs;

import java.util.List;

/**
 * This interface aims at easily changing class implementation to test.
 * @author Cong-Kinh Nguyen
 *
 */
public interface CallLoggingStorage {

	/**
	 * Saves statistic information.
	 * @param logging
	 * @return <code>true</code> if the method successfully saves statistics, and <code>false</code> otherwise.
	 */
	boolean save(CallLogging logging);

	/**
	 * Gets a list of log calls.
	 * @param appkey
	 * @param from
	 * @param to
	 * @param minTimeConsumed
	 * @param maxTimeConsumed
	 * @return
	 */
	List<CallLogging> getCalls(String appkey, long from, long to,
			int minTimeConsumed, int maxTimeConsumed);
	
	List<CallLoggingDisplay> getCallsWithCount(long from, long to,
			int minTimeConsumed, int maxTimeConsumed);
	
}

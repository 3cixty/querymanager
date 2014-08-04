package eu.threecixty.stats;

import eu.threecixty.keys.AppKey;

/**
 * This class is to represent information about 3cixty App statistics.
 * @author Cong-Kinh Nguyen
 *
 */
public class Stats {

	private AppKey appKey;

	private long startTime;
	
	private int timeConsumed;

	private String serviceName;

	public Stats() {
		startTime = System.currentTimeMillis();
	}
	
	public AppKey getAppKey() {
		return appKey;
	}

	public void setAppKey(AppKey appKey) {
		this.appKey = appKey;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getTimeConsumed() {
		return timeConsumed;
	}

	public void setTimeConsumed(int timeConsumed) {
		this.timeConsumed = timeConsumed;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}

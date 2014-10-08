package eu.threecixty.logs;


/**
 * This class is to represent information about 3cixty App statistics.
 * @author Cong-Kinh Nguyen
 *
 */
public class CallLogging {

	private String key;

	private long startTime;
	
	private int timeConsumed;

	private String serviceName;
	
	private String description;
	

	protected CallLogging() {
		startTime = System.currentTimeMillis();
	}

	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

package eu.threecixty.cache;

public class CacheElement {

	private static final int VALIDATION = 1000 * 60 * 60 * 24; // one day
	private long creationTime;
	private long lastValidTime;
	public String content;
	
	public CacheElement() {
		creationTime = System.currentTimeMillis();
		lastValidTime = creationTime + VALIDATION;
	}
	
	public boolean isValid() {
		long currentTime = System.currentTimeMillis();
		return (lastValidTime >= currentTime);
	}
}

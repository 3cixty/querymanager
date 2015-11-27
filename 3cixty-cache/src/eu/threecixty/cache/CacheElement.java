package eu.threecixty.cache;

import java.util.Calendar;

/**
 * 
 * @author kinh
 *
 */
public class CacheElement {
	private long lastValidTime;
	public String content;
	
	public CacheElement() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59);
		lastValidTime = calendar.getTimeInMillis();
	}
	
	public boolean isValid() {
		long currentTime = System.currentTimeMillis();
		return (lastValidTime >= currentTime);
	}
}

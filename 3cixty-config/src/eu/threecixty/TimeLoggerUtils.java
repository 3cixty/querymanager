package eu.threecixty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeLoggerUtils {

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	
	/**
	 * Gets time.
	 * <br>
	 * Note that this method returns time in string format which should be comprehensible by human as this information
	 * is logged with messages.
	 * @return
	 */
	public static String getCurrentTime() {
		Date date = Calendar.getInstance().getTime();
		return format.format(date);
	}
	
	private TimeLoggerUtils() {
	}
}

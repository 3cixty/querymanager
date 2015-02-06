package eu.threecixty.querymanager.jsonrpc;

public class AttendDateTimeFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2389113029926592734L;

	public AttendDateTimeFormatException() {
		super();
	}

	public AttendDateTimeFormatException(String msg) {
		super(msg);
	}

	public AttendDateTimeFormatException(Throwable thr) {
		super(thr);
	}

	public AttendDateTimeFormatException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

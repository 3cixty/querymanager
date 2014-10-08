package eu.threecixty.querymanager.rest;

public class ThreeCixtyPermissionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1898737284643191683L;

	public ThreeCixtyPermissionException() {
	}

	public ThreeCixtyPermissionException(String msg) {
		super(msg);
	}

	public ThreeCixtyPermissionException(Throwable thr) {
		super(thr);
	}

	public ThreeCixtyPermissionException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

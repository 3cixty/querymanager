package eu.threecixty.querymanager;

public class UnknownException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7350289072331172794L;

	public UnknownException() {
		super();
	}
	
	public UnknownException(String msg) {
		super(msg);
	}
	
	public UnknownException(Throwable thr) {
		super(thr);
	}
	
	public UnknownException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

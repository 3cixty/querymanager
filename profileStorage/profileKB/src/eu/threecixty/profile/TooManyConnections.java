package eu.threecixty.profile;

public class TooManyConnections extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1172518365854517543L;

	public TooManyConnections() {
		super();
	}
	
	public TooManyConnections(String msg) {
		super(msg);
	}
	
	public TooManyConnections(Throwable thr) {
		super(thr);
	}

	public TooManyConnections(String msg, Throwable thr) {
		super(msg, thr);
	}
}

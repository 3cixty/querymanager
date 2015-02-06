package eu.threecixty.querymanager.jsonrpc;

public class InvalidKeyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4749541238340319492L;

	public InvalidKeyException() {
		super();
	}

	public InvalidKeyException(String msg) {
		super(msg);
	}

	public InvalidKeyException(Throwable thr) {
		super(thr);
	}

	public InvalidKeyException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

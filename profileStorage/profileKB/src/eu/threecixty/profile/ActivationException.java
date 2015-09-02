package eu.threecixty.profile;

public class ActivationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8127329179575466369L;

	public ActivationException() {
	}
	
	public ActivationException(String msg) {
		super(msg);
	}

	public ActivationException(Throwable thr) {
		super(thr);
	}
	
	public ActivationException(String msg, Throwable thr) {
		super(msg, thr);
	}
}

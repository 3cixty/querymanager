package eu.threecixty.profile;

public class InvalidTrayElement extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3598109648241998188L;

	public InvalidTrayElement() {
		super();
	}
	
	public InvalidTrayElement(String msg) {
		super(msg);
	}
	
	public InvalidTrayElement(Throwable thr) {
		super(thr);
	}
	
	public InvalidTrayElement(String msg, Throwable thr) {
		super(msg, thr);
	}
}

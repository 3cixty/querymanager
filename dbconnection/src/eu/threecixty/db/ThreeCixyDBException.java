package eu.threecixty.db;

public class ThreeCixyDBException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6306130633177886068L;

	public ThreeCixyDBException() {
		super();
	}

	public ThreeCixyDBException(String msg) {
		super(msg);
	}

	public ThreeCixyDBException(Throwable throwable) {
		super(throwable);
	}

	public ThreeCixyDBException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}

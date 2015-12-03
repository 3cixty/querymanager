package eu.threecixty.profile;

public class AccountNotActivatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8524934993708245361L;

	public AccountNotActivatedException() {
		super("Sorry, your account has not activated yet.");
	}
}

package eu.threecixty.privacy.store;

/**
 * Logical representation of a user.
 * 
 * <p>
 * A user must have an identifier, unless anonymous, and should have an
 * authenticator. The authenticator is untyped, manipulated as bytes and may
 * have to be converted into a proper artifact for use.
 * </p>
 */
public interface User {

	/**
	 * @return a token identifying the user
	 */
	public abstract String getId();

	/**
	 * @return a token to authenticate the user or null if none is provided in
	 *         which case this user is unsafe to use. Anonymous user should have
	 *         a null authenticator.
	 */
	public abstract byte[] getAuthenticator();

}
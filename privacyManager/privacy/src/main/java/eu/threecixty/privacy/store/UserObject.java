package eu.threecixty.privacy.store;

/**
 * Mutable implementation of the {@link User} interface.
 */
public class UserObject implements User {

	private String id;
	private byte[] auth;

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getAuthenticator()
	 */
	public byte[] getAuthenticator() {
		return auth;
	}

	public void setAuthenticator(byte[] authenticator) {
		this.auth = authenticator;
	}

	@Override
	public String toString() {
		return "StoreUser [id=" + id + ", authenticator=" + auth + "]";
	}

}

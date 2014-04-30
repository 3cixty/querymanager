package eu.threecixty.privacy.store;

/**
 * Mutable implementation of the {@link User} interface.
 */
public class UserObject implements User {
	private Long userId;
	private String name;
	private byte[] auth;

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getUserId()
	 */
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "StoreUser [id=" + userId + ", name=" + name + ", authenticator="
				+ auth + "]";
	}

}

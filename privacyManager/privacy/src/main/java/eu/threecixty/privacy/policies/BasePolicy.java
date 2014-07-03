package eu.threecixty.privacy.policies;

import java.io.Serializable;

import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.storage.Policy;

public abstract class BasePolicy implements Policy, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 991926738054855761L;

	private final Identity identity;

	private boolean cipher;
	
	/**
	 * Create a policy for contents that is not ciphered. This setting can be
	 * changed by calling {@link #setCipher(boolean)}.
	 * 
	 * @param owner
	 *            Something that will uniquely identity the owner of the data
	 *            created with this policy. Must not be null.
	 */
	public BasePolicy(Serializable owner) {
		this.identity = new Identity(owner);
		this.cipher = false;
	}

	/**
	 * @param owner
	 *            Something that will uniquely identity the owner of the data
	 *            created with this policy. Must not be null.
	 * @param cipher
	 *            ciphering flag
	 */
	public BasePolicy(Serializable owner, boolean cipher) {
		this.identity = new Identity(owner);
		this.cipher = cipher;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cipher ? 1231 : 1237);
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasePolicy other = (BasePolicy) obj;
		if (cipher != other.cipher)
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		return true;
	}

	public final Credential getCredential() {
		return identity;
	}

	public boolean getCipher() {
		return cipher;
	}

	public void setCipher(boolean cipher) {
		this.cipher = cipher;
	}

}

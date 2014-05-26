package eu.threecixty.privacy.policies;

import java.io.Serializable;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.storage.Credential;

public final class PrivateData extends BasePolicy {

	/**
	 * @param owner
	 *            Something that will uniquely identity the owner of the data
	 *            created with this policy. Must not be null.
	 */
	public PrivateData(Serializable owner) {
		super(owner);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8438170808679634643L;

	public boolean isReadAuthorized(Resource<?> resource, Credential credential) {
		return isCompatibleCredential(credential);
	}

	private boolean isCompatibleCredential(Credential credential) {
		if (credential == null)
			return false;
		if (!(credential instanceof Identity))
			return false;
		if (getCredential().equals(credential))
			return true;
		return false;
	}

	public boolean isWriteAuthorized(Resource<?> resource, Credential credential) {
		return isCompatibleCredential(credential);
	}

}

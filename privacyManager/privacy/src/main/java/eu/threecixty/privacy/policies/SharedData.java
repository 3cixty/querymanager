package eu.threecixty.privacy.policies;

import java.io.Serializable;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.storage.Credential;

public final class SharedData extends BasePolicy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2044246502855688547L;
	
	private final AccessMode otherAccessMode;

	/**
	 * Policy for public data. The application that created the data has
	 * read-write access to the data. Other parties can write the data only if
	 * we granted write access. They all have read access.
	 * 
	 * @param owner
	 *            Something that will uniquely identity the owner of the data
	 *            created with this policy. Must not be null.
	 * @param otherAccess
	 *            defines the privileges for access by others than the owner. If
	 *            set to {@link AccessMode#NONE} then the class acts the same as
	 *            {@link PrivateData}.
	 */
	public SharedData(Serializable owner, AccessMode otherAccess) {
		super(owner);
		
		if (otherAccess == null) {
			throw new NullPointerException("Must specify the access mode for other party");
		}
		this.otherAccessMode = otherAccess;
	}
	
	public boolean isReadAuthorized(Resource<?> resource, Credential credential) {
		// The author can always read the data.
		// Other parties can also write the data but only if we granted write access.
		if (credential == null)
			return false;
		if (!(credential instanceof Identity))
			return false;
		if (getCredential().equals(credential) || otherAccessMode.canRead())
			return true;
		return false;
	}

	public boolean isWriteAuthorized(Resource<?> resource, Credential credential) {
		
		// The author can always write the data.
		// Other parties can also write the data but only if we granted write access.
		if (credential == null)
			return false;
		if (!(credential instanceof Identity))
			return false;
		if (getCredential().equals(credential) || otherAccessMode.canWrite())
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((otherAccessMode == null) ? 0 : otherAccessMode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedData other = (SharedData) obj;
		if (otherAccessMode != other.otherAccessMode)
			return false;
		return true;
	}

}

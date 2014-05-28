package eu.threecixty.privacy.security;

import java.io.IOException;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.storage.Policy;
import eu.threecixty.privacy.storage.Storage;

/**
 * This class provides security to a wrapped unsafe {@link Storage}.
 * 
 * <p>
 * SecureStorage guarantees secured reading and writing access to stored
 * resources. The policies are checked on every access and any anomaly or
 * security threat is reported by the mean of a SecurityException. The
 * wrapped object should be unsafe as there is no meaning in adding security
 * to an object that is already safe itself.
 * </p>
 * 
 * @param <T>
 */
public final class SecureStorage<T> implements Storage<T> {

	private Storage<T> storage;

	public SecureStorage(Storage<T> storage) {
		this.storage = storage;
	}

	public <V extends T> V getValue(Resource<V> resource, Credential credential)
			throws SecurityException, IOException {
		// Get the resource registered policy.
		Policy policy = storage.getPolicy(resource, credential);
		
		V data = null;
		
		// If there is none then it means there is no value thus null is returned.
		if (null != policy) {
			if (!policy.isReadAuthorized(resource, credential)) {
				throw new SecurityException("" + credential.getSubject() + " is denied read access to resource " + resource);
			}
			
			// Since we are granted reading, do read the stored value now.
			data = storage.getValue(resource, credential);
		}
		
		return data;
	}

	public <V extends T, Y> void setValue(Resource<V> resource, Y value,
			Credential credential) throws SecurityException, IOException {
		// Get the resource registered policy, if already written once.
		Policy storedPolicy = storage.getPolicy(resource, credential);
		
		if (storedPolicy != null) {
			if (!storedPolicy.isWriteAuthorized(resource, credential)) {
				throw new SecurityException("" + credential.getSubject() + " is denied write access to resource " + resource);
			}
		}
		
		// Here we are allowed write access to the resource.
		storage.setValue(resource, value, credential);
	}

	public Policy getPolicy(Resource<? extends T> resource,
			Credential credential) throws SecurityException, IOException {
		Policy currentPolicy = storage.getPolicy(resource, credential);
		if (currentPolicy != null && !currentPolicy.isReadAuthorized(resource, credential)) {
			throw new SecurityException("" + credential.getSubject() + " is denied read access to resource " + resource);
		}
		
		return currentPolicy;
	}

	public void setPolicy(Resource<? extends T> resource, Policy policy,
			Credential credential) throws SecurityException, IOException {
		Policy currentPolicy = storage.getPolicy(resource, credential);
		if (currentPolicy != null && !currentPolicy.isWriteAuthorized(resource, credential)) {
			throw new SecurityException("" + credential.getSubject() + " is denied write access to resource " + resource);
		}
		
		storage.setPolicy(resource, policy, credential);
	}

	public Credential newCredential(Scope scope, String user, String password)
			throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy newReadAccessPolicy(Scope scope) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy newWriteAccessPolicy(Scope scope) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

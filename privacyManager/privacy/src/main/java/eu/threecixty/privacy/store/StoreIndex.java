package eu.threecixty.privacy.store;

import java.io.IOException;

/**
 * Index of users and resources. This index allows a level of indirection
 * between the resource and the place where it is stored. It maps each logical
 * entity to its physical representation in the store.
 */
public interface StoreIndex {

	/**
	 * Open the index. Then the operations methods should be available and apply
	 * to the opened index.
	 */
	public abstract void open() throws IOException;

	/**
	 * Close the index if opened
	 */
	public abstract void close();

	/**
	 * Add a user into the index. The operation fails if the user is already in
	 * the index.
	 * 
	 * @param user
	 *            the user name. Must not be registered yet or the operation
	 *            will fail.
	 * @param authenticator
	 *            an optional information that can used to challenge self
	 *            proclaimed user later. May be null.
	 * @return the created StoreUser or null if it failed.
	 */
	public abstract User addUser(String user, String authenticator);

	/**
	 * Fetch a store user from its ID.
	 * 
	 * @param user
	 *            the user ID to search for
	 * @return the matching StoreUser or null if it failed or none is found.
	 */
	public abstract User findUser(String user);

	/**
	 * Add a resource into the index. The operation fails if a resource of same
	 * ontology already exist in the index.
	 * 
	 * <p>
	 * This method must be implemented if the storage and the index are
	 * different systems otherwise it's up to the implementation to decide if
	 * there is a need for a mapping between the resource and the place where
	 * its value is stored
	 * </p>
	 * 
	 * @param user
	 *            the ID of the user who owns this resource. This value is
	 *            obtained with {@link #findUser(String)} and must be already
	 *            registered in the index.
	 * @param ontology
	 *            the ontology URL string that identifies this resource. Must
	 *            not be null or an empty string. Must be unique in the index as
	 *            only one resource can be identified per ontology.
	 * @param resource
	 *            path to the resource. May be an URL formated as string. The
	 *            resource name should be meaningful to the provider. Cannot be
	 *            null or an empty string.
	 * @param provider
	 *            identifies a unique system, resolved by the caller, to access
	 *            the resource
	 * @return the created StoreResource or null if the operation failed.
	 */
	public abstract Value addResource(String user, String ontology,
			String resource, String provider);

	/**
	 * Find a resource by its ontology string.
	 * 
	 * <p>
	 * This method must be implemented if the storage and the index are
	 * different systems. If not, the value of the resource should be stored
	 * directly in the storage system without a need for mapping between the
	 * logical identifier of the resource and the location where its value is
	 * stored.
	 * </p>
	 * 
	 * @param ontology
	 *            the ontology string to search for, that was set when creating
	 *            the record with
	 *            {@link #addResource(long, String, String, String)}
	 * @return the matching StoreResource or null if it failed or none is found.
	 */
	public abstract Value getResourceByOntology(String ontology);

}
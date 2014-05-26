package eu.threecixty.privacy.store;

/**
 * Logical representation of a {@link eu.threecixty.privacy.storage.Storage}
 * managed value.
 */
public interface Value {

	/**
	 * @return the semantic annotation for this value which is its identifier too.
	 */
	public abstract String getOntology();

	/**
	 * @return the identifier of the user the value is related to.
	 */
	public abstract String getOwner();

	/**
	 * @return the resource the value is related to.
	 */
	public abstract String getResource();

	/**
	 * @return the provider of the concrete value
	 */
	public abstract String getProvider();

}
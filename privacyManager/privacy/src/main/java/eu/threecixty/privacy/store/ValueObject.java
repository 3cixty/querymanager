package eu.threecixty.privacy.store;

/**
 * Mutable implementation of the {@link Value} interface.
 */
public class ValueObject implements Value {
	private String ontology;
	
	/** References an id from the PRIVACY_USER table. */
	private String owner;
	
	private String resource;
	private String provider;

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getResource()
	 */
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getProvider()
	 */
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "StoreResource [ontology=" + ontology
				+ ", owner=" + owner
				+ ", resource=" + resource
				+ ", provider=" + provider + "]";
	}
}

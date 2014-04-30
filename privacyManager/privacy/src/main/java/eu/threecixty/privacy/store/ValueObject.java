package eu.threecixty.privacy.store;

/**
 * Mutable implementation of the {@link Value} interface.
 */
public class ValueObject implements Value {
	private Long id;
	private String ontology;
	
	/** References an id from the user table. */
	private Long userId;
	
	private String resource;
	private String provider;

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getId()
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getOntology()
	 */
	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	/* (non-Javadoc)
	 * @see eu.threecixty.privacy.store.IUser#getOnwerUid()
	 */
	public long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
		return "StoreResource [id=" + id + ", ontology=" + ontology
				+ ", creatorUid=" + userId + ", resource=" + resource
				+ ", provider=" + provider + "]";
	}
}

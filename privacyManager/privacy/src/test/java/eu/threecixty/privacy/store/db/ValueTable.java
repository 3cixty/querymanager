package eu.threecixty.privacy.store.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import eu.threecixty.privacy.store.Value;

/**
 * JPA implementation for interface {@link Value} used in tests.
 */
@Entity
@Table(name = "ENTITY")
public class ValueTable implements Serializable, Value {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5021574837721853489L;

	@Id
	@Column(name = "ontology")
	private String ontology;

	@Column(name = "owner")
	private String owner;

	@Column(name = "resource")
	private String resource;

	@Column(name = "provider")
	private String provider;
	
	public String getOwner() {
		return owner;
	}

	public String getOntology() {
		return ontology;
	}

	public String getResource() {
		return resource;
	}

	public String getProvider() {
		return provider;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}

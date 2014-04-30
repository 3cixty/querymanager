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
@Table(name = "entity")
public class ValueTable implements Serializable, Value {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5021574837721853489L;

	@Id
	@Column(name = "_id")
	private Long id;

	@Column(name = "ontology")
	private String ontology;

	@Id
	@Column(name = "owner")
	private Long userId;

	@Column(name = "resource")
	private String resource;

	@Column(name = "provider")
	private String provider;

	public Long getId() {
		return id;
	}

	public String getOntology() {
		return ontology;
	}

	public long getUserId() {
		return userId;
	}

	public String getResource() {
		return resource;
	}

	public String getProvider() {
		return provider;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}

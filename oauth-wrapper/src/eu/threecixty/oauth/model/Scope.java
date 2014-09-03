package eu.threecixty.oauth.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "3cixty_scope", uniqueConstraints = {
		@UniqueConstraint(columnNames = "scope_name"),
		@UniqueConstraint(columnNames = "scope_level")})
public class Scope implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5527982130550713799L;
	
	private Integer id;
	private String scopeName;
	private String description;
	private Integer scopeLevel;
	
	private Set<App> apps = new HashSet <App>();

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}
 
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "scope_name", unique = true, nullable = false, length = 255)
	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	@Column(name = "description", unique = false, nullable = true, length = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "scope_level", unique = true, nullable = false)
	public Integer getScopeLevel() {
		return scopeLevel;
	}

	public void setScopeLevel(Integer scopeLevel) {
		this.scopeLevel = scopeLevel;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scope")
	public Set<App> getApps() {
		return apps;
	}

	public void setApps(Set<App> apps) {
		this.apps = apps;
	}
}

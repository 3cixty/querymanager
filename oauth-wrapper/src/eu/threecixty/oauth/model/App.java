package eu.threecixty.oauth.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "3cixty_app", uniqueConstraints = {
		@UniqueConstraint(columnNames = "app_key"),
		@UniqueConstraint(columnNames = "name_space")})
public class App implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 52823901759062535L;
	
	private Integer id;
	private String key;
	private Set <UserAccessToken> userAccessTokens = new HashSet <UserAccessToken>();
	private Developer developer;
	private Scope scope;

	private String appNameSpace;
	private String description;
	private String category;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}
 
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "app")
	public Set<UserAccessToken> getUserAccessTokens() {
		return userAccessTokens;
	}

	public void setUserAccessTokens(Set<UserAccessToken> userAccessTokens) {
		this.userAccessTokens = userAccessTokens;
	}

	@Column(name = "app_key", unique = true, nullable = false, length = 64)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_developer_id", nullable = false)
	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	@Column(name = "description", unique = false, nullable = true, length = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "category", unique = false, nullable = false, length = 64)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "name_space", unique = true, nullable = false, length = 255)
	public String getAppNameSpace() {
		return appNameSpace;
	}

	public void setAppNameSpace(String appNameSpace) {
		this.appNameSpace = appNameSpace;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_scope_id", nullable = false)
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}

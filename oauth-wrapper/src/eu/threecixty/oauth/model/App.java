package eu.threecixty.oauth.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "3cixty_app", uniqueConstraints = {
		@UniqueConstraint(columnNames = "app_key"),
		@UniqueConstraint(columnNames = "name_space"), @UniqueConstraint(columnNames = "clientId")})
public class App implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 52823901759062535L;
	
	private Integer id;
	private String key;
	private Set <UserAccessToken> userAccessTokens = new HashSet <UserAccessToken>();
	private Developer developer;
	private Set <Scope> scopes = new HashSet<Scope>();
	//private Scope scope;

	// appid
	private String appNameSpace;
	private String description;
	private String appName;
	private String category;
	private String redirectUri;
	
	
	// also check clientId in the 'client' table
	private String clientId;

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

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "app_scope", joinColumns = { 
			@JoinColumn(name = "app_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "scope_id", 
					nullable = false, updatable = false) })
	public Set<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(Set<Scope> scopes) {
		this.scopes = scopes;
	}

	@Column(name = "redirect_uri", unique = false, nullable = true, length = 255)
	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	@Column(name = "clientId", unique = true, nullable = false, length = 255)
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Column(name = "app_name", unique = false, nullable = false, length = 255)
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}

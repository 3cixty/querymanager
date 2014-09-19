package eu.threecixty.oauth.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "3cixty_user_accessToken", uniqueConstraints = {
		@UniqueConstraint(columnNames = "access_token"),
		@UniqueConstraint(columnNames = "refresh_token")})
public class UserAccessToken implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3728934204797749232L;

	private Integer id;
	
	private String accessToken;
	private String refreshToken;
	private User user;
	private App app;
	
	private Set <Scope> scopes = new HashSet <Scope>();
	

	public UserAccessToken() {
	}

	public UserAccessToken(Integer id, String accessToken, User user, App app) {
		this.id = id;
		this.accessToken = accessToken;
		this.user = user;
		this.app = app;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}
 
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "access_token", unique = true, nullable = false, length = 64)
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@Column(name = "refresh_token", unique = true, nullable = false, length = 64)
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_user_id", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_app_id", nullable = false)
	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_accessToken_scope", joinColumns = { 
			@JoinColumn(name = "user_accessToken_id", nullable = false, updatable = true) }, 
			inverseJoinColumns = { @JoinColumn(name = "scope_id", 
					nullable = false, updatable = true) })
	public Set<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(Set<Scope> scopes) {
		this.scopes = scopes;
	}
}

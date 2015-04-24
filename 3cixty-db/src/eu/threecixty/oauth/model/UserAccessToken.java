package eu.threecixty.oauth.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
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
	//private App app;
	private Integer _3cixty_app_id;
	private Long creation;
	private Integer expiration;
	
	private String scope;

	public UserAccessToken() {
	}

	public UserAccessToken(Integer id, String accessToken, User user) {
		this.id = id;
		this.accessToken = accessToken;
		this.user = user;
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

	@Column(name = "scope", unique = false, nullable = true, length = 64)
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Column(name = "3cixty_app_id", nullable = false)
	public Integer get_3cixty_app_id() {
		return _3cixty_app_id;
	}

	public void set_3cixty_app_id(Integer _3cixty_app_id) {
		this._3cixty_app_id = _3cixty_app_id;
	}

	@Column(name = "creation", nullable = true)
	public Long getCreation() {
		return creation;
	}

	public void setCreation(Long creation) {
		this.creation = creation;
	}

	public Integer getExpiration() {
		return expiration;
	}

	@Column(name = "expiration", nullable = true)
	public void setExpiration(Integer expiration) {
		this.expiration = expiration;
	}
}

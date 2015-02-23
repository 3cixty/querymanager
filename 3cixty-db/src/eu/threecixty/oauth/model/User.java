package eu.threecixty.oauth.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("User")
@Table(name = "3cixty_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"uid", "DTYPE"})})
public class User implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7879877349825721983L;

	private Integer id;
	private String uid; // which associates with 3cixty user_id

	private Set <UserAccessToken> userAccessTokens = new HashSet <UserAccessToken>();

	public User() {
	}

	public User(Integer id, String uid) {
		this.id =id;
		this.uid = uid;
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

	@Column(name = "uid", unique = false, nullable = false, length = 30)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	public Set<UserAccessToken> getUserAccessTokens() {
		return userAccessTokens;
	}

	public void setUserAccessTokens(Set<UserAccessToken> userAccessTokens) {
		this.userAccessTokens = userAccessTokens;
	}
}

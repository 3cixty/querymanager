package eu.threecixty.privacy.store.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import eu.threecixty.privacy.store.User;

/**
 * JPA implementation for interface {@link User} used in tests.
 */
@Entity
@Table(name = "user")
public final class UserTable implements User, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877464239506748677L;

	@Id
	@Column(name = "_id")
	private Long userId;

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "auth")
	private byte[] auth;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getAuthenticator() {
		return auth;
	}

	public void setAuthenticator(byte[] auth) {
		this.auth = auth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserTable other = (UserTable) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}

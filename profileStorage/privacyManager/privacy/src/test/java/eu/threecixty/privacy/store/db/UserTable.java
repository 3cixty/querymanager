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
@Table(name = "PRIVACY_USER")
public final class UserTable implements User, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877464239506748677L;

	@Id
	@Column(name = "id")
	private String id;

	@Lob
	@Column(name = "auth")
	private byte[] auth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

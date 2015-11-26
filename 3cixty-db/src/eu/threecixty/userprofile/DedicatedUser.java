package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * This class represents information about 3cixty dedicated user.
 *
 */
@Entity
@Table(name = "3cixty_dedicated_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"email", "uid"})})
public class DedicatedUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 536080511154642911L;

	private Integer id;
	private String email;
	private String password;
	private boolean emailConfirmed;
	private String uid;
	private String appkey; // app key instead of appId since we might have to disable keys in the future due to the app key being abuse
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "email", unique = true, nullable = false, length = 100)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "password", unique = false, nullable = false, length = 100)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "emailConfirmed")
	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}
	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}
	
	@Column(name = "uid", unique = true, nullable = false, length = 100)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "app_key", nullable = false, length = 100)
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
}

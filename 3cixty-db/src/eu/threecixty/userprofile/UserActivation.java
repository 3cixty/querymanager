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
 * This class is to deal with activating a 3cixty dedicated account, resetting password.
 *
 */
@Entity
@Table(name = "3cixty_user_activation", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"code"})})
public class UserActivation implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3909126009057180100L;

	private Integer id;
	private String code;
	private ActivationType type;
	private boolean used;
	private long creation;
	private boolean sent;
	
	/**DO NOT reference the attribute with UserModel to avoid having to remove when deleting a user profile*/
	private Integer dedicatedUserId;
	private Integer appId;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "code", unique = true, nullable = false, length = 100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "type", unique = false, nullable = false, length = 100)
	public ActivationType getType() {
		return type;
	}

	public void setType(ActivationType type) {
		this.type = type;
	}

	@Column(name = "used")
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	@Column(name = "creation", nullable = false)
	public long getCreation() {
		return creation;
	}

	public void setCreation(long creation) {
		this.creation = creation;
	}

	@Column(name = "dedicated_user_id", nullable = false)
	public Integer getDedicatedUserId() {
		return dedicatedUserId;
	}

	public void setDedicatedUserId(Integer dedicatedUserId) {
		this.dedicatedUserId = dedicatedUserId;
	}
	
	@Column(name = "sent")
	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	@Column(name = "app_id", nullable = false)
	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}



	public enum ActivationType {
		CREATION, FORGOTTEN_PASSWORD
	}

}

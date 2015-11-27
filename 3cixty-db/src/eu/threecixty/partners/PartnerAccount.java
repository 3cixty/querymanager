package eu.threecixty.partners;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * This class is to represent Mobidot acccount.
 *
 */
@Entity
@Table(name = "3cixty_partner_account")
public class PartnerAccount implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1174387556869889178L;
	private String username; // not sure if username is unique
	private String password;
	
	private String user_id; // only use for Mobidot
	
	/**appId might be a dumb string since Mobidot doesn't care about which app this user belongs to*/
	private String appId; // 3cixty app id
	
	// for GoFlow
	private String role;
	private Integer id;
	private PartnerUser partnerUser;

	@Column(name = "user_name", nullable = false, length = 64)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	@Column(name = "password", nullable = false, length = 64)
	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "app_id", nullable = false, length = 64)
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appid) {
		this.appId = appid;
	}

	@Column(name = "role", nullable = false, length = 64)
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_user_id", nullable = false)
	public PartnerUser getPartnerUser() {
		return partnerUser;
	}

	public void setPartnerUser(PartnerUser partnerUser) {
		this.partnerUser = partnerUser;
	}

	@Column(name = "mobidot_user_id", nullable = true, length = 255)
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
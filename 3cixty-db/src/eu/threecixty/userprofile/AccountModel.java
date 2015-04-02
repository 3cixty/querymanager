package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "3cixty_account")
public class AccountModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974632259252458061L;

	private String source;
	private String accountId;
	private Integer id;
	private UserModel userModel;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "source", nullable = true, length = 255)
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@Column(name = "accountId", nullable = false, length = 255)
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_user_id", nullable = false)
	public UserModel getUserModel() {
		return userModel;
	}
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	
}

package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@Table(name = "3cixty_user_profile", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"uid", "username"})})
public class UserModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312079254371061310L;
	private Integer id;
	private String firstName;
	private String lastName;
	private String uid;
	private String profileImage;
	
	
	private Set <String> knows;
	
	private long lastCrawlTimeToKB;
	private Set <AccountModel> accounts;

	private AddressModel address;
	private Set <AccompanyingModel> accompanyings;
	
	/**The following attributes are used for creating 3cixty account*/
	private String password;
	private String email;
	private String username;
	private Boolean emailConfirmed;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "firstName", nullable = true, length = 255)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name = "lastName", nullable = true, length = 255)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name = "uid", unique = true, nullable = false, length = 100)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "profileImage", nullable = true, length = 255)
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
	@Column(name = "lastCrawlTimeToKB")
	public long getLastCrawlTimeToKB() {
		return lastCrawlTimeToKB;
	}
	public void setLastCrawlTimeToKB(long lastCrawlTimeToKB) {
		this.lastCrawlTimeToKB = lastCrawlTimeToKB;
	}

	@CollectionOfElements
	public Set<String> getKnows() {
		return knows;
	}
	public void setKnows(Set<String> knows) {
		this.knows = knows;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel")
	public Set<AccountModel> getAccounts() {
		return accounts;
	}
	public void setAccounts(Set<AccountModel> accounts) {
		this.accounts = accounts;
	}
	
	@OneToOne(mappedBy="userModel", cascade = CascadeType.ALL)
	public AddressModel getAddress() {
		return address;
	}
	public void setAddress(AddressModel address) {
		this.address = address;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel")
	public Set<AccompanyingModel> getAccompanyings() {
		return accompanyings;
	}
	public void setAccompanyings(Set<AccompanyingModel> accompanyings) {
		this.accompanyings = accompanyings;
	}
	
	@Column(name = "password", unique = false, nullable = true, length = 100)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "email", unique = true, nullable = true, length = 100)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "username", unique = true, nullable = true, length = 100)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name = "emailConfirmed", nullable = true)
	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}
	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}
}

package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "3cixty_user_profile", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"uid"})})
public class UserModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312079254371061310L;
	private Integer id;
	private String firstName;
	private String lastName;
	private String uid;
	private Boolean gender;
	private String profileImage;
	private Double latitude;
	private Double longitude;
	private String townName;
	private String countryName;
	private String streetAddress;
	private Set <Know> knows;
	private Set <Accompanying> accompanyings;
	private long lastCrawlTimeToKB;
	private Set <AccountModel> accounts;
	
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
	
	@Column(name = "gender", nullable = true)
	public Boolean getGender() {
		return gender;
	}
	public void setGender(Boolean gender) {
		this.gender = gender;
	}
	
	@Column(name = "profileImage", nullable = true, length = 255)
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
	@Column(name = "latitude", nullable = true)
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "longitude", nullable = true)
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "townName", nullable = true, length = 255)
	public String getTownName() {
		return townName;
	}
	public void setTownName(String townName) {
		this.townName = townName;
	}
	
	@Column(name = "countryName", nullable = true, length = 255)
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	@Column(name = "streetAddress", nullable = true, length = 255)
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	@Column(name = "lastCrawlTimeToKB")
	public long getLastCrawlTimeToKB() {
		return lastCrawlTimeToKB;
	}
	public void setLastCrawlTimeToKB(long lastCrawlTimeToKB) {
		this.lastCrawlTimeToKB = lastCrawlTimeToKB;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	public Set<Know> getKnows() {
		return knows;
	}
	public void setKnows(Set<Know> knows) {
		this.knows = knows;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	public Set<Accompanying> getAccompanyings() {
		return accompanyings;
	}
	public void setAccompanyings(Set<Accompanying> accompanyings) {
		this.accompanyings = accompanyings;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel")
	public Set<AccountModel> getAccounts() {
		return accounts;
	}
	public void setAccounts(Set<AccountModel> accounts) {
		this.accounts = accounts;
	}
}

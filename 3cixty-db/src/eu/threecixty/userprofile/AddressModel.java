package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * Address for user.
 * <br>
 * Currently, this class hasn't yet used.
 *
 */
@Entity
@Table(name = "3cixty_address")
public class AddressModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6173017498421165029L;
	private Double latitude;
	private Double longitude;
	private String townName;
	private String countryName;
	private String streetAddress;
	private String postalCode;
	private Integer id;
	
	private UserModel userModel;
	
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
	
	@Column(name = "postalCode", nullable = true, length = 25)
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "3cixty_user_id")
	public UserModel getUserModel() {
		return userModel;
	}
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
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
}

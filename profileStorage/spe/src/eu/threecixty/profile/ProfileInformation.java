package eu.threecixty.profile;

/**
 * This class is to represent information which is provided at SPE API.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ProfileInformation {

	private String uid;
	private String firstName;
	private String lastName;
	private String townName;
	private String countryName;
	private double latitude;
	private double longitude;

	private SpePreference preference;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public SpePreference getPreference() {
		return preference;
	}

	public void setPreference(SpePreference preference) {
		this.preference = preference;
	}
}

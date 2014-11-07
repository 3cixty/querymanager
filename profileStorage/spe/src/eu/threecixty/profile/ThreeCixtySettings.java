package eu.threecixty.profile;

import java.io.Serializable;
import java.util.List;

import eu.threecixty.profile.oldmodels.EventDetailPreference;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class contains information to store in KB from Settings page.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ThreeCixtySettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4204058915242741613L;

	private List <ProfileIdentities> identities;
	
	private String uid;

	private String firstName;
	private String lastName;
	private String townName;
	private String countryName;
	
	private double currentLatitude;
	private double currentLongitude;

	private EventDetailPreference eventDetailPreference;

	public List<ProfileIdentities> getIdentities() {
		return identities;
	}

	public void setIdentities(List<ProfileIdentities> identities) {
		this.identities = identities;
	}

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

	public double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public EventDetailPreference getEventDetailPreference() {
		return eventDetailPreference;
	}

	public void setEventDetailPreference(EventDetailPreference eventDetailPreference) {
		this.eventDetailPreference = eventDetailPreference;
	}

}

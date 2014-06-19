package eu.threecixty.profile;

import java.util.List;

import eu.threecixty.profile.oldmodels.EventDetailPreference;
import eu.threecixty.profile.oldmodels.ModalityType;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class contains information to store in KB from Settings page.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ThreeCixtySettings {

	private List <ProfileIdentities> identities;
	
	private String uid;

	private String townName;
	private String countryName;
	
	private double currentLatitude;
	private double currentLongitude;

	private EventDetailPreference eventDetailPreference;
	
	// TODO: need to persist this attribute
	private boolean queryHistoryStored = false;
	
	// TODO: need to persist this attribute
	private ModalityType preferredTripModality;

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

	public boolean isQueryHistoryStored() {
		return queryHistoryStored;
	}

	public void setQueryHistoryStored(boolean queryHistoryStored) {
		this.queryHistoryStored = queryHistoryStored;
	}

	public ModalityType getPreferredTripModality() {
		return preferredTripModality;
	}

	public void setPreferredTripModality(ModalityType preferredTripModality) {
		this.preferredTripModality = preferredTripModality;
	}
}

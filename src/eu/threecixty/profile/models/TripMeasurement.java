package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Trip Measurement. 
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.TripMeasurement")
 * @author Mobidot
 *
 */
public class TripMeasurement extends Measurement {
	@Description(hasText="Trip information")
	private Trip reading;
	@Description(hasText="ID")
    private String clientId="";
	public Trip getReading() {
		return reading;
	}
	public void setReading(Trip reading) {
		this.reading = reading;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}

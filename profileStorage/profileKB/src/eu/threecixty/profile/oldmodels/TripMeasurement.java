package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Trip Measurement. 
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.TripMeasurement")
 * @author Mobidot
 *
 */
public class TripMeasurement extends Measurement {
	@Description(hasText="Trip information")
	private Trip hasTrip;
	@Description(hasText="ID")
    private String clientId="";
	public Trip getHasTrip() {
		return hasTrip;
	}
	public void setHasTrip(Trip hasTrip) {
		this.hasTrip = hasTrip;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}

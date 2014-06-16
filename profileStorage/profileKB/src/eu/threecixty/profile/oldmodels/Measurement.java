package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Meta data for each top level context type. Allows to specify that user x is in context c in period p.
 * Measurements can also be used for predictions, indicated by a time period in the future."
 * @author Mobidot
 *
 */
public class Measurement {
	@Description(hasText = "In seconds since epoch, in UTC")
    private Long hasMeasurementTime; 
    @Description(hasText = "In seconds")
    private Long hasMeasurementValidity; //In seconds
    @Description(hasText = "In seconds, GMT+1 equals 3600")
    private Long hasMeasurementTimeZone; 
    @Description(hasText = "Unique ID of a measurement or derived value, "
    		+ "IDs are unique per reading type (location, vri, weather, ...)")
    private Long hasMeasurementId; 
    @Description(hasText = "Unique ID of the user (key for personal data)")
    private Long hasUID; 
    @Description(hasText = "Data quality, default is good")
    private DataQuality hasDataQuality; 
    //@Description(hasText = "Describes the client submitting the data")
    //ClientCharacteristic client; 
	public Long getHasMeasurementTime() {
		return hasMeasurementTime;
	}
	public void setHasMeasurementTime(Long hasMeasurementTime) {
		this.hasMeasurementTime = hasMeasurementTime;
	}
	public Long getHasMeasurementValidity() {
		return hasMeasurementValidity;
	}
	public void setHasMeasurementValidity(Long hasMeasurementValidity) {
		this.hasMeasurementValidity = hasMeasurementValidity;
	}
	public Long getHasMeasurementTimeZone() {
		return hasMeasurementTimeZone;
	}
	public void setHasMeasurementTimeZone(Long hasMeasurementTimeZone) {
		this.hasMeasurementTimeZone = hasMeasurementTimeZone;
	}
	public Long getHasMeasurementId() {
		return hasMeasurementId;
	}
	public void setHasMeasurementId(Long hasMeasurementId) {
		this.hasMeasurementId = hasMeasurementId;
	}
	public Long getHasUID() {
		return hasUID;
	}
	public void setHasUID(Long hasUID) {
		this.hasUID = hasUID;
	}
	public DataQuality getHasDataQuality() {
		return hasDataQuality;
	}
	public void setHasDataQuality(DataQuality hasDataQuality) {
		this.hasDataQuality = hasDataQuality;
	}
	
    
}

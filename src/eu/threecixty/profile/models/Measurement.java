package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Meta data for each top level context type. Allows to specify that user x is in context c in period p.
 * Measurements can also be used for predictions, indicated by a time period in the future."
 * @author Mobidot
 *
 */
public class Measurement {
	@Description(hasText = "In seconds since epoch, in UTC")
    private Long time; 
    @Description(hasText = "In seconds")
    private Long validity; //In seconds
    @Description(hasText = "In seconds, GMT+1 equals 3600")
    private Long timezone; 
    @Description(hasText = "Unique ID of a measurement or derived value, "
    		+ "IDs are unique per reading type (location, vri, weather, ...)")
    private Long id; 
    @Description(hasText = "Unique ID of the user (key for personal data)")
    private Long userid; 
    @Description(hasText = "Data quality, default is good")
    private DataQuality quality; 
    //@Description(hasText = "Describes the client submitting the data")
    //ClientCharacteristic client; 
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getValidity() {
		return validity;
	}
	public void setValidity(Long validity) {
		this.validity = validity;
	}
	public Long getTimezone() {
		return timezone;
	}
	public void setTimezone(Long timezone) {
		this.timezone = timezone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public DataQuality getQuality() {
		return quality;
	}
	public void setQuality(DataQuality quality) {
		this.quality = quality;
	}
    
}

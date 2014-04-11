package eu.threecixty.profile.models;


import eu.threecixty.profile.annotations.Description;
 
 
/**
 * Important personal place, often used static location, derived and defined on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.PersonalPlace
 * @author Mobidot
 */
public class PersonalPlace {
    @Description(hasText = "Place ID")
    Long id; 
    @Description(hasText = "User ID who visits this place frequently")
    Long userid; 
    @Description(hasText = "Place location latitude")
    Double latitude; 
    @Description(hasText = "Place location longitude")
    Double longitude; 
    @Description(hasText = "Place location accuracy, in meters")
    Double accuracy; 
    @Description(hasText = "Place name")
    String name; 
    @Description(hasText = "Place type")
    String type;
    @Description(hasText = "Place postal code")
    String postalcode; 
    @Description(hasText = "Total duration of stay of the user in this place, in seconds")
    Long stayDuration; 
    @Description(hasText = "Total duration of stay of the user in this place, relatively with respect to the observed time period for place detection")
    Double stayPercentage; 
    @Description(hasText = "Duration of stay per week day")
    String weekdayPattern; 
    @Description(hasText = "Duration of stay per day hour")
    String dayhourPattern; 
    @Description(hasText = "Set to manual if the user changed name and/or location")
    DecisionLevel level; 
    @Description(hasText = "External IDs of this place, as a + concatenated string, with e.g. FourSquare and FaceBook identities: 4sq:43432+fb:8348734")
    String externalIds;
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
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public Long getStayDuration() {
		return stayDuration;
	}
	public void setStayDuration(Long stayDuration) {
		this.stayDuration = stayDuration;
	}
	public Double getStayPercentage() {
		return stayPercentage;
	}
	public void setStayPercentage(Double stayPercentage) {
		this.stayPercentage = stayPercentage;
	}
	public String getWeekdayPattern() {
		return weekdayPattern;
	}
	public void setWeekdayPattern(String weekdayPattern) {
		this.weekdayPattern = weekdayPattern;
	}
	public String getDayhourPattern() {
		return dayhourPattern;
	}
	public void setDayhourPattern(String dayhourPattern) {
		this.dayhourPattern = dayhourPattern;
	}
	public DecisionLevel getLevel() {
		return level;
	}
	public void setLevel(DecisionLevel level) {
		this.level = level;
	}
	public String getExternalIds() {
		return externalIds;
	}
	public void setExternalIds(String externalIds) {
		this.externalIds = externalIds;
	} 
    
 }
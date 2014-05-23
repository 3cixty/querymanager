package eu.threecixty.profile.oldmodels;


import eu.threecixty.profile.annotations.Description;
 
 
/**
 * Important personal place, often used static location, derived and defined on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.PersonalPlace
 * @author Mobidot
 */
public class PersonalPlace {
    @Description(hasText = "Place ID")
    private Long hasPersonalPlaceId; 
    @Description(hasText = "User ID who visits this place frequently")
    private Long hasUID; 
    @Description(hasText = "Place location latitude")
    private Double latitude; 
    @Description(hasText = "Place location longitude")
    private Double longitude; 
    @Description(hasText = "Place location accuracy, in meters")
    private Double hasPersonalPlaceAccuracy; 
    @Description(hasText = "Place name")
    private String hasPersonalPlaceName; 
    @Description(hasText = "Place type")
    private String hasPersonalPlaceType;
    @Description(hasText = "Place postal code")
    private String postalcode; 
    @Description(hasText = "Total duration of stay of the user in this place, in seconds")
    private Long hasPersonalPlaceStayDuration; 
    @Description(hasText = "Total duration of stay of the user in this place, relatively with respect to the observed time period for place detection")
    private Double hasPersonalPlaceStayPercentage; 
    @Description(hasText = "Duration of stay per week day")
    private String hasPersonalPlaceWeekdayPattern; 
    @Description(hasText = "Duration of stay per day hour")
    private String hasPersonalPlaceDayhourPattern; 
    @Description(hasText = "Set to manual if the user changed name and/or location")
    private DecisionLevel hasDecisionlevel; 
    @Description(hasText = "External IDs of this place, as a + concatenated string, with e.g. FourSquare and FaceBook identities: 4sq:43432+fb:8348734")
    private String hasPersonalPlaceexternalIds;
	public Long getHasPersonalPlaceId() {
		return hasPersonalPlaceId;
	}
	public void setHasPersonalPlaceId(Long hasPersonalPlaceId) {
		this.hasPersonalPlaceId = hasPersonalPlaceId;
	}
	public Long getHasUID() {
		return hasUID;
	}
	public void setHasUID(Long hasUID) {
		this.hasUID = hasUID;
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
	public Double getHasPersonalPlaceAccuracy() {
		return hasPersonalPlaceAccuracy;
	}
	public void setHasPersonalPlaceAccuracy(Double hasPersonalPlaceAccuracy) {
		this.hasPersonalPlaceAccuracy = hasPersonalPlaceAccuracy;
	}
	public String getHasPersonalPlaceName() {
		return hasPersonalPlaceName;
	}
	public void setHasPersonalPlaceName(String hasPersonalPlaceName) {
		this.hasPersonalPlaceName = hasPersonalPlaceName;
	}
	public String getHasPersonalPlaceType() {
		return hasPersonalPlaceType;
	}
	public void setHasPersonalPlaceType(String hasPersonalPlaceType) {
		this.hasPersonalPlaceType = hasPersonalPlaceType;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public Long getHasPersonalPlaceStayDuration() {
		return hasPersonalPlaceStayDuration;
	}
	public void setHasPersonalPlaceStayDuration(Long hasPersonalPlaceStayDuration) {
		this.hasPersonalPlaceStayDuration = hasPersonalPlaceStayDuration;
	}
	public Double getHasPersonalPlaceStayPercentage() {
		return hasPersonalPlaceStayPercentage;
	}
	public void setHasPersonalPlaceStayPercentage(
			Double hasPersonalPlaceStayPercentage) {
		this.hasPersonalPlaceStayPercentage = hasPersonalPlaceStayPercentage;
	}
	public String getHasPersonalPlaceWeekdayPattern() {
		return hasPersonalPlaceWeekdayPattern;
	}
	public void setHasPersonalPlaceWeekdayPattern(
			String hasPersonalPlaceWeekdayPattern) {
		this.hasPersonalPlaceWeekdayPattern = hasPersonalPlaceWeekdayPattern;
	}
	public String getHasPersonalPlaceDayhourPattern() {
		return hasPersonalPlaceDayhourPattern;
	}
	public void setHasPersonalPlaceDayhourPattern(
			String hasPersonalPlaceDayhourPattern) {
		this.hasPersonalPlaceDayhourPattern = hasPersonalPlaceDayhourPattern;
	}
	public DecisionLevel getHasDecisionlevel() {
		return hasDecisionlevel;
	}
	public void setHasDecisionlevel(DecisionLevel hasDecisionlevel) {
		this.hasDecisionlevel = hasDecisionlevel;
	}
	public String getHasPersonalPlaceexternalIds() {
		return hasPersonalPlaceexternalIds;
	}
	public void setHasPersonalPlaceexternalIds(String hasPersonalPlaceexternalIds) {
		this.hasPersonalPlaceexternalIds = hasPersonalPlaceexternalIds;
	}
	
    
 }
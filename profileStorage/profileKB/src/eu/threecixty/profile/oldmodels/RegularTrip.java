package eu.threecixty.profile.oldmodels;


import java.util.Set;

import eu.threecixty.profile.annotations.Description;
 
/**
 * Description of a regular, weekly trip of a user between the point of departure and
 * the point of arrival, derived on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.RegularTrip
 * @author Mobidot
*/
public class RegularTrip {
	@Description(hasText="UniqueURI")
	private String hasRegularTripURI="";
	@Description(hasText = "Trip name, formatted as [start street - end street, city] or "
		+ "[start street, start city - end street, end city] or [place name 1 - place name 2], "
		+ "to distinguish between trips")
    private String hasRegularTripName=""; 
    @Description(hasText = "Average departure time, in seconds since 0:00")
    private Long hasRegularTripDepartureTime; 
    @Description(hasText = "Standard deviation in the departure time, in seconds")
    private Long hasRegularTripDepartureTimeSD; 
    @Description(hasText = "Average travel time, in seconds")
    private Long hasRegularTripTravelTime; 
    @Description(hasText = "Standard deviation in the travel time, in seconds")
    private Long hasRegularTripTravelTimeSD; 
    @Description(hasText = "Fastest travel time, in seconds")
    private Long hasRegularTripFastestTravelTime; 
    @Description(hasText = "Distance of the regular trip")
    private Double hasRegularTripTotalDistance=0.0; 
    @Description(hasText = "Number of times the user took this route for this particular trip "
		+ "(e.g. Staringlaan-Brouwerijstraat), within the totalTravelTime of the mobility profile")
    private Long hasRegularTripTotalCount; 
    @Description(hasText = "Important personal places along the trip route. In the simplest "
		+ "form this could be start and end place only, but later also a school drop-off.")
    private  PersonalPlace[] hasPersonalPlaces;
    //TODO: change to set
    private  Set <PersonalPlace> hasPersonalPlacesNew; 
    @Description(hasText = "Modality used during trip: none, foot, bike, car, bus, ...")
    private ModalityType hasModalityType; 
    @Description(hasText = "Unique trip ID")
    private Long hasRegularTripId; 
    @Description(hasText = "User ID")
    private Long hasUID; 
    @Description(hasText = "Usage pattern per week day")
    private String hasRegularTripWeekdayPattern=""; 
    @Description(hasText = "Departure time per day hour")
    private String hasRegularTripDayhourPattern=""; 
    @Description(hasText = "Concatenated string of street names")
    private String hasRegularTripRouteName="";
    @Description(hasText = "Names of the most important via roads of this regular trip")
    private String hasRegularTripViaName=""; 
    @Description(hasText = "Locations mapped on the OSM network")
    private MappedLocation[] hasMappedLocations; 
    @Description(hasText = "Timestamp of the last modification of this regular trip")
    private Long hasRegularTripLastChanged; 
    @Description(hasText = "Total travel time as function of the departure time")
    private String hasRegularTripTravelTimePattern="";
	@Description(hasText = "Weather condition histogram")
	private String hasRegularTripWeatherPattern;
	
    public String getHasRegularTripURI() {
		return hasRegularTripURI;
	}
	public void setHasRegularTripURI(String hasRegularTripURI) {
		this.hasRegularTripURI = hasRegularTripURI;
	}
	public String getHasRegularTripName() {
		return hasRegularTripName;
	}
	public void setHasRegularTripName(String hasRegularTripName) {
		this.hasRegularTripName = hasRegularTripName;
	}
	public Long getHasRegularTripDepartureTime() {
		return hasRegularTripDepartureTime;
	}
	public void setHasRegularTripDepartureTime(Long hasRegularTripDepartureTime) {
		this.hasRegularTripDepartureTime = hasRegularTripDepartureTime;
	}
	public Long getHasRegularTripDepartureTimeSD() {
		return hasRegularTripDepartureTimeSD;
	}
	public void setHasRegularTripDepartureTimeSD(Long hasRegularTripDepartureTimeSD) {
		this.hasRegularTripDepartureTimeSD = hasRegularTripDepartureTimeSD;
	}
	public Long getHasRegularTripTravelTime() {
		return hasRegularTripTravelTime;
	}
	public void setHasRegularTripTravelTime(Long hasRegularTripTravelTime) {
		this.hasRegularTripTravelTime = hasRegularTripTravelTime;
	}
	public Long getHasRegularTripTravelTimeSD() {
		return hasRegularTripTravelTimeSD;
	}
	public void setHasRegularTripTravelTimeSD(Long hasRegularTripTravelTimeSD) {
		this.hasRegularTripTravelTimeSD = hasRegularTripTravelTimeSD;
	}
	public Long getHasRegularTripFastestTravelTime() {
		return hasRegularTripFastestTravelTime;
	}
	public void setHasRegularTripFastestTravelTime(
			Long hasRegularTripFastestTravelTime) {
		this.hasRegularTripFastestTravelTime = hasRegularTripFastestTravelTime;
	}
	public Double getHasRegularTripTotalDistance() {
		return hasRegularTripTotalDistance;
	}
	public void setHasRegularTripTotalDistance(Double hasRegularTripTotalDistance) {
		this.hasRegularTripTotalDistance = hasRegularTripTotalDistance;
	}
	public Long getHasRegularTripTotalCount() {
		return hasRegularTripTotalCount;
	}
	public void setHasRegularTripTotalCount(Long hasRegularTripTotalCount) {
		this.hasRegularTripTotalCount = hasRegularTripTotalCount;
	}
	public  Set <PersonalPlace> getHasPersonalPlacesNew() {
		return hasPersonalPlacesNew;
	}
	public void setHasPersonalPlacesNew( Set <PersonalPlace> hasPersonalPlacesNew) {
		this.hasPersonalPlacesNew = hasPersonalPlacesNew;
	}
	public PersonalPlace[] getHasPersonalPlaces() {
		return hasPersonalPlaces;
	}
	public void setHasPersonalPlaces(PersonalPlace[] hasPersonalPlaces) {
		this.hasPersonalPlaces = hasPersonalPlaces;
	}
	public ModalityType getHasModalityType() {
		return hasModalityType;
	}
	public void setHasModalityType(ModalityType hasModalityType) {
		this.hasModalityType = hasModalityType;
	}
	public Long getHasRegularTripId() {
		return hasRegularTripId;
	}
	public void setHasRegularTripId(Long hasRegularTripId) {
		this.hasRegularTripId = hasRegularTripId;
	}
	public Long getHasUID() {
		return hasUID;
	}
	public void setHasUID(Long hasUID) {
		this.hasUID = hasUID;
	}
	public String getHasRegularTripWeekdayPattern() {
		return hasRegularTripWeekdayPattern;
	}
	public void setHasRegularTripWeekdayPattern(String hasRegularTripWeekdayPattern) {
		this.hasRegularTripWeekdayPattern = hasRegularTripWeekdayPattern;
	}
	public String getHasRegularTripDayhourPattern() {
		return hasRegularTripDayhourPattern;
	}
	public void setHasRegularTripDayhourPattern(String hasRegularTripDayhourPattern) {
		this.hasRegularTripDayhourPattern = hasRegularTripDayhourPattern;
	}
	public String getHasRegularTripRouteName() {
		return hasRegularTripRouteName;
	}
	public void setHasRegularTripRouteName(String hasRegularTripRouteName) {
		this.hasRegularTripRouteName = hasRegularTripRouteName;
	}
	public String getHasRegularTripViaName() {
		return hasRegularTripViaName;
	}
	public void setHasRegularTripViaName(String hasRegularTripViaName) {
		this.hasRegularTripViaName = hasRegularTripViaName;
	}
	public MappedLocation[] getHasMappedLocations() {
		return hasMappedLocations;
	}
	public void setHasMappedLocations(MappedLocation[] hasMappedLocations) {
		this.hasMappedLocations = hasMappedLocations;
	}
	public Long getHasRegularTripLastChanged() {
		return hasRegularTripLastChanged;
	}
	public void setHasRegularTripLastChanged(Long hasRegularTripLastChanged) {
		this.hasRegularTripLastChanged = hasRegularTripLastChanged;
	}
	public String getHasRegularTripTravelTimePattern() {
		return hasRegularTripTravelTimePattern;
	}
	public void setHasRegularTripTravelTimePattern(
			String hasRegularTripTravelTimePattern) {
		this.hasRegularTripTravelTimePattern = hasRegularTripTravelTimePattern;
	}
	public String getHasRegularTripWeatherPattern() {
		return hasRegularTripWeatherPattern;
	}
	public void setHasRegularTripWeatherPattern(String hasRegularTripWeatherPattern) {
		this.hasRegularTripWeatherPattern = hasRegularTripWeatherPattern;
	}
    
    
}


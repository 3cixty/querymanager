package eu.threecixty.profile.oldmodels;

import java.io.Serializable;

import eu.threecixty.profile.annotations.Description;

/**
 * Description of a trip of a user between the point of departure and the point of arrival, derived on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.Trip
 * @author Mobidot
 */
public class Trip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7779808220255530546L;
	@Description(hasText = "Average speed of a trip, in meter per second")
    private Double hasTripAverageSpeed=0.0; 
    @Description(hasText = "Total distance of a trip, in meter")
    private Double hasTripTotalDistance=0.0; 
    @Description(hasText = "Total distance of a trip, measured along map matched route")
    private Double hasTripTotalInfraSegmentDistance=0.0; 
    @Description(hasText = "Type of the trip, identifies a.o. whether or not the user is moving,"
    		+ " e.g. static or enroute")
    private TripType hasTripTripType; 
    @Description(hasText = "Modality used during trip (foot, bike, car, bus, ...) as automatically "
    		+ "derived or user corrected")
    private ModalityType hasModalityType; 
    @Description(hasText = "Modality selection level: none if the modality is not set, manual "
    		+ "if a user decided the modality,  automatic if modality was derived by an automated "
    		+ "recognizer, override if the user chose to override or correct the automatically derived modality")
    private DecisionLevel hasDecisionLevel; 
    @Description(hasText = "Modality used during trip (foot, bike, car, bus, ...) as originally automatically derived, "
    		+ "to be able to generate a confusion or correction matrix")
    private ModalityType hasTripModalityAutomatic; 
    @Description(hasText = "Weather at the start of the trip")
    private Weather hasTripWeather; 
    @Description(hasText = "Important personal places along the trip route. In the simplest form this "
    		+ "could be home and office as start and end point, but later also a friend's place to pick him up")
    private PersonalPlace[] hasPersonalPlaces; 
    @Description(hasText = "Role of the user on this specific trip, e.g. driver or passenger in the back seat")
    private ModalityRole hasModalityRole; 
    @Description(hasText = "The individual's travel objective with this trip. Indicate the main objective, "
    		+ "e.g. GoingToWork and EnjoyingTheView can be combined in one trip, but the first is the main objective.")
    private TravelObjectiveType hasTravelObjectiveType; 
    @Description(hasText = "Unique trip ID")
    private Long hasTripId; 
    @Description(hasText = "Unique trip group ID. All single-modality trips with the same "
    		+ "group ID constitute a door-to-door trip, in chronological trip order")
    private String hasTripGroupId=""; 
    @Description(hasText = "Costs of a trip, in Euros")
    private Double hasTripTotalCost=0.0; 
    @Description(hasText = "Number of passengers")
    private Integer hasTripNumberOfPassengers=0; 
    @Description(hasText = "Timestamp of the last automatic trip analysis")
    private Long hasTripLastAnalyzed; 
    //@Description(hasText = "Domain-specific properties of a trip")
    //private Tag[] tags; 
    @Description(hasText = "ID of the regular trip this trip matches with, 0 if no match")
    private Long hasRegularTripId; 
    @Description(hasText = "Locations mapped on the OSM network")
    private MappedLocation[] hasMappedLocations; 
    @Description(hasText = "Routes associated with this trip")
    private InfraRoute[] hasInfraRoutes; 
    @Description(hasText = "Total calories spent on this trip")
    private Double hasTripTotalCalories=0.0; //Total calories spent on this trip
	public Double getHasTripAverageSpeed() {
		return hasTripAverageSpeed;
	}
	public void setHasTripAverageSpeed(Double hasTripAverageSpeed) {
		this.hasTripAverageSpeed = hasTripAverageSpeed;
	}
	public Double getHasTripTotalDistance() {
		return hasTripTotalDistance;
	}
	public void setHasTripTotalDistance(Double hasTripTotalDistance) {
		this.hasTripTotalDistance = hasTripTotalDistance;
	}
	public Double getHasTripTotalInfraSegmentDistance() {
		return hasTripTotalInfraSegmentDistance;
	}
	public void setHasTripTotalInfraSegmentDistance(
			Double hasTripTotalInfraSegmentDistance) {
		this.hasTripTotalInfraSegmentDistance = hasTripTotalInfraSegmentDistance;
	}
	public TripType getHasTripTripType() {
		return hasTripTripType;
	}
	public void setHasTripTripType(TripType hasTripTripType) {
		this.hasTripTripType = hasTripTripType;
	}
	public ModalityType getHasModalityType() {
		return hasModalityType;
	}
	public void setHasModalityType(ModalityType hasModalityType) {
		this.hasModalityType = hasModalityType;
	}
	public DecisionLevel getHasDecisionLevel() {
		return hasDecisionLevel;
	}
	public void setHasDecisionLevel(DecisionLevel hasDecisionLevel) {
		this.hasDecisionLevel = hasDecisionLevel;
	}
	public ModalityType getHasTripModalityAutomatic() {
		return hasTripModalityAutomatic;
	}
	public void setHasTripModalityAutomatic(ModalityType hasTripModalityAutomatic) {
		this.hasTripModalityAutomatic = hasTripModalityAutomatic;
	}
	public Weather getHasTripWeather() {
		return hasTripWeather;
	}
	public void setHasTripWeather(Weather hasTripWeather) {
		this.hasTripWeather = hasTripWeather;
	}
	public PersonalPlace[] getHasPersonalPlaces() {
		return hasPersonalPlaces;
	}
	public void setHasPersonalPlaces(PersonalPlace[] hasPersonalPlaces) {
		this.hasPersonalPlaces = hasPersonalPlaces;
	}
	public ModalityRole getHasModalityRole() {
		return hasModalityRole;
	}
	public void setHasModalityRole(ModalityRole hasModalityRole) {
		this.hasModalityRole = hasModalityRole;
	}
	public TravelObjectiveType getHasTravelObjectiveType() {
		return hasTravelObjectiveType;
	}
	public void setHasTravelObjectiveType(TravelObjectiveType hasTravelObjectiveType) {
		this.hasTravelObjectiveType = hasTravelObjectiveType;
	}
	public Long getHasTripId() {
		return hasTripId;
	}
	public void setHasTripId(Long hasTripId) {
		this.hasTripId = hasTripId;
	}
	public String getHasTripGroupId() {
		return hasTripGroupId;
	}
	public void setHasTripGroupId(String hasTripGroupId) {
		this.hasTripGroupId = hasTripGroupId;
	}
	public Double getHasTripTotalCost() {
		return hasTripTotalCost;
	}
	public void setHasTripTotalCost(Double hasTripTotalCost) {
		this.hasTripTotalCost = hasTripTotalCost;
	}
	public Integer getHasTripNumberOfPassengers() {
		return hasTripNumberOfPassengers;
	}
	public void setHasTripNumberOfPassengers(Integer hasTripNumberOfPassengers) {
		this.hasTripNumberOfPassengers = hasTripNumberOfPassengers;
	}
	public Long getHasTripLastAnalyzed() {
		return hasTripLastAnalyzed;
	}
	public void setHasTripLastAnalyzed(Long hasTripLastAnalyzed) {
		this.hasTripLastAnalyzed = hasTripLastAnalyzed;
	}
	public Long getHasRegularTripId() {
		return hasRegularTripId;
	}
	public void setHasRegularTripId(Long hasRegularTripId) {
		this.hasRegularTripId = hasRegularTripId;
	}
	public MappedLocation[] getHasMappedLocations() {
		return hasMappedLocations;
	}
	public void setHasMappedLocations(MappedLocation[] hasMappedLocations) {
		this.hasMappedLocations = hasMappedLocations;
	}
	public InfraRoute[] getHasInfraRoutes() {
		return hasInfraRoutes;
	}
	public void setHasInfraRoutes(InfraRoute[] hasInfraRoutes) {
		this.hasInfraRoutes = hasInfraRoutes;
	}
	public Double getHasTripTotalCalories() {
		return hasTripTotalCalories;
	}
	public void setHasTripTotalCalories(Double hasTripTotalCalories) {
		this.hasTripTotalCalories = hasTripTotalCalories;
	}
	
    
}

package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Description of a trip of a user between the point of departure and the point of arrival, derived on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.Trip
 * @author Mobidot
 */
public class Trip {
	@Description(hasText = "Average speed of a trip, in meter per second")
    private Double averageSpeed=0.0; 
    @Description(hasText = "Total distance of a trip, in meter")
    private Double totalDistance=0.0; 
    @Description(hasText = "Total distance of a trip, measured along map matched route")
    private Double totalInfraSegmentDistance=0.0; 
    @Description(hasText = "Type of the trip, identifies a.o. whether or not the user is moving,"
    		+ " e.g. static or enroute")
    private TripType tripType; 
    @Description(hasText = "Modality used during trip (foot, bike, car, bus, ...) as automatically "
    		+ "derived or user corrected")
    private ModalityType tripModality; 
    @Description(hasText = "Modality selection level: none if the modality is not set, manual "
    		+ "if a user decided the modality,  automatic if modality was derived by an automated "
    		+ "recognizer, override if the user chose to override or correct the automatically derived modality")
    private DecisionLevel tripModalityLevel; 
    @Description(hasText = "Modality used during trip (foot, bike, car, bus, ...) as originally automatically derived, "
    		+ "to be able to generate a confusion or correction matrix")
    private ModalityType tripModalityAutomatic; 
    @Description(hasText = "Weather at the start of the trip")
    private Weather tripWeather; 
    @Description(hasText = "Important personal places along the trip route. In the simplest form this "
    		+ "could be home and office as start and end point, but later also a friend's place to pick him up")
    private PersonalPlace[] tripPlaces; 
    @Description(hasText = "Role of the user on this specific trip, e.g. driver or passenger in the back seat")
    private ModalityRole userRole; 
    @Description(hasText = "The individual's travel objective with this trip. Indicate the main objective, "
    		+ "e.g. GoingToWork and EnjoyingTheView can be combined in one trip, but the first is the main objective.")
    private TravelObjectiveType tripMainObjective; 
    @Description(hasText = "Unique trip ID")
    private Long id; 
    @Description(hasText = "Unique trip group ID. All single-modality trips with the same "
    		+ "group ID constitute a door-to-door trip, in chronological trip order")
    private String groupId=""; 
    @Description(hasText = "Costs of a trip, in Euros")
    private Double totalCost=0.0; 
    @Description(hasText = "Emissions of the trip, per emission type")
    private Emission[] totalEmission; 
    @Description(hasText = "Number of passengers")
    private Integer numberOfPassengers=0; 
    @Description(hasText = "Timestamp of the last automatic trip analysis")
    private Long lastAnalyzed; 
    //@Description(hasText = "Domain-specific properties of a trip")
    //private Tag[] tags; 
    @Description(hasText = "ID of the regular trip this trip matches with, 0 if no match")
    private Long regularTripId; 
    @Description(hasText = "Locations mapped on the OSM network")
    private MappedLocation[] mappedLocations; 
    @Description(hasText = "Routes associated with this trip")
    private InfraRoute[] associatedRoutes; 
    @Description(hasText = "Total calories spent on this trip")
    private Double totalCalories=0.0; //Total calories spent on this trip
	public Double getAverageSpeed() {
		return averageSpeed;
	}
	public void setAverageSpeed(Double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
	public Double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public Double getTotalInfraSegmentDistance() {
		return totalInfraSegmentDistance;
	}
	public void setTotalInfraSegmentDistance(Double totalInfraSegmentDistance) {
		this.totalInfraSegmentDistance = totalInfraSegmentDistance;
	}
	public TripType getTripType() {
		return tripType;
	}
	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}
	public ModalityType getTripModality() {
		return tripModality;
	}
	public void setTripModality(ModalityType tripModality) {
		this.tripModality = tripModality;
	}
	public DecisionLevel getTripModalityLevel() {
		return tripModalityLevel;
	}
	public void setTripModalityLevel(DecisionLevel tripModalityLevel) {
		this.tripModalityLevel = tripModalityLevel;
	}
	public ModalityType getTripModalityAutomatic() {
		return tripModalityAutomatic;
	}
	public void setTripModalityAutomatic(ModalityType tripModalityAutomatic) {
		this.tripModalityAutomatic = tripModalityAutomatic;
	}
	public Weather getTripWeather() {
		return tripWeather;
	}
	public void setTripWeather(Weather tripWeather) {
		this.tripWeather = tripWeather;
	}
	public PersonalPlace[] getTripPlaces() {
		return tripPlaces;
	}
	public void setTripPlaces(PersonalPlace[] tripPlaces) {
		this.tripPlaces = tripPlaces;
	}
	public ModalityRole getUserRole() {
		return userRole;
	}
	public void setUserRole(ModalityRole userRole) {
		this.userRole = userRole;
	}
	public TravelObjectiveType getTripMainObjective() {
		return tripMainObjective;
	}
	public void setTripMainObjective(TravelObjectiveType tripMainObjective) {
		this.tripMainObjective = tripMainObjective;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Emission[] getTotalEmission() {
		return totalEmission;
	}
	public void setTotalEmission(Emission[] totalEmission) {
		this.totalEmission = totalEmission;
	}
	public Integer getNumberOfPassengers() {
		return numberOfPassengers;
	}
	public void setNumberOfPassengers(Integer numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}
	public Long getLastAnalyzed() {
		return lastAnalyzed;
	}
	public void setLastAnalyzed(Long lastAnalyzed) {
		this.lastAnalyzed = lastAnalyzed;
	}
	public Long getRegularTripId() {
		return regularTripId;
	}
	public void setRegularTripId(Long regularTripId) {
		this.regularTripId = regularTripId;
	}
	public MappedLocation[] getMappedLocations() {
		return mappedLocations;
	}
	public void setMappedLocations(MappedLocation[] mappedLocations) {
		this.mappedLocations = mappedLocations;
	}
	public InfraRoute[] getAssociatedRoutes() {
		return associatedRoutes;
	}
	public void setAssociatedRoutes(InfraRoute[] associatedRoutes) {
		this.associatedRoutes = associatedRoutes;
	}
	public Double getTotalCalories() {
		return totalCalories;
	}
	public void setTotalCalories(Double totalCalories) {
		this.totalCalories = totalCalories;
	}
    
    
}

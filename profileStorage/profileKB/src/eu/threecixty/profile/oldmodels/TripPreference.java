package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Based on Thales Inputs
 * @author Thales
 *
 */
public class TripPreference {
	@Description(hasText="UniqueURI")
	private String hasTripPreferenceURI="";
	@Description(hasText = "The minimal preferred average speed for the current trip, in meter per second")
    private Double hasPreferredMinAverageSpeed=0.0;
    @Description(hasText = "The maximal total distance for the current trip, in meters")
    private Double hasPreferredMaxTotalDistance=0.0;
    @Description(hasText = "Modality preferred during the current trip (foot, bike, car, bus, ...) ")
    private ModalityType hasModalityType;
    @Description(hasText = "preferred Weather at the start of the trip")
    private String hasPreferredWeatherCondition;
    @Description(hasText = "Preferred Role of the user during the current trip, e.g. driver or passenger in the back seat")
    private ModalityRole hasModalityRole;
    @Description(hasText = "The individual's travel objective with this trip. Indicate the main objective, e.g. GoingToWork and EnjoyingTheView can be combined in one trip, but the first is the main objective.")
    private TravelObjectiveType hasPreferredTripObjective;
    @Description(hasText = "The maximal accepted Costs for the current trip, in Euros")
    private Double hasPreferredMaxTotalCost=0.0;
    @Description(hasText = "The maximal accepted Emissions of the current trip, per emission type")
    private Emission[] maxTotalEmission;
    @Description(hasText = "The maximal accepted Number of passengers in the current trip")
    private Integer maxNumberOfPassengers=0;
    @Description(hasText = "The minimal accepted Number of passengers in the current trip")
    private Integer minNumberOfPassengers=0;
    @Description(hasText = "ID of the regular trip this trip matches with, 0 if no match")
    private Long hasRegularTripID=0L;
    @Description(hasText = "preferred Trip time  (the start of the trip)")
    private Long hasPreferredTripTime=0L;
    @Description(hasText = "Preferred city")
    private String hasPreferredCity="";
    @Description(hasText = "Preferred country")
    private String hasPreferredCountry="";
    @Description(hasText = "preferred Trip Duration")
    private Long hasPreferredTripDuration=0L;
    @Description(hasText = "preferred min Trip time of Accompany (the start of the trip)")
    private Long hasPreferredMinTimeOfAccompany=0L;
    
    
    public String getHasTripPreferenceURI() {
		return hasTripPreferenceURI;
	}
	public void setHasTripPreferenceURI(String hasTripPreferenceURI) {
		this.hasTripPreferenceURI = hasTripPreferenceURI;
	}
	public Double getHasPreferredMinAverageSpeed() {
		return hasPreferredMinAverageSpeed;
	}
	public void setHasPreferredMinAverageSpeed(Double hasPreferredMinAverageSpeed) {
		this.hasPreferredMinAverageSpeed = hasPreferredMinAverageSpeed;
	}
	public Double getHasPreferredMaxTotalDistance() {
		return hasPreferredMaxTotalDistance;
	}
	public void setHasPreferredMaxTotalDistance(Double hasPreferredMaxTotalDistance) {
		this.hasPreferredMaxTotalDistance = hasPreferredMaxTotalDistance;
	}
	public ModalityType getHasModalityType() {
		return hasModalityType;
	}
	public void setHasModalityType(ModalityType hasModalityType) {
		this.hasModalityType = hasModalityType;
	}
	public ModalityRole getHasModalityRole() {
		return hasModalityRole;
	}
	public void setHasModalityRole(ModalityRole hasModalityRole) {
		this.hasModalityRole = hasModalityRole;
	}
	public TravelObjectiveType getHasPreferredTripObjective() {
		return hasPreferredTripObjective;
	}
	public void setHasPreferredTripObjective(
			TravelObjectiveType hasPreferredTripObjective) {
		this.hasPreferredTripObjective = hasPreferredTripObjective;
	}
	public Double getHasPreferredMaxTotalCost() {
		return hasPreferredMaxTotalCost;
	}
	public void setHasPreferredMaxTotalCost(Double hasPreferredMaxTotalCost) {
		this.hasPreferredMaxTotalCost = hasPreferredMaxTotalCost;
	}
	public Emission[] getMaxTotalEmission() {
		return maxTotalEmission;
	}
	public void setMaxTotalEmission(Emission[] maxTotalEmission) {
		this.maxTotalEmission = maxTotalEmission;
	}
	public Integer getMaxNumberOfPassengers() {
		return maxNumberOfPassengers;
	}
	public void setMaxNumberOfPassengers(Integer maxNumberOfPassengers) {
		this.maxNumberOfPassengers = maxNumberOfPassengers;
	}
	public Integer getMinNumberOfPassengers() {
		return minNumberOfPassengers;
	}
	public void setMinNumberOfPassengers(Integer minNumberOfPassengers) {
		this.minNumberOfPassengers = minNumberOfPassengers;
	}
	public Long getHasRegularTripID() {
		return hasRegularTripID;
	}
	public void setHasRegularTripID(Long hasRegularTripID) {
		this.hasRegularTripID = hasRegularTripID;
	}
	public String getHasPreferredWeatherCondition() {
		return hasPreferredWeatherCondition;
	}
	public void setHasPreferredWeatherCondition(String hasPreferredWeatherCondition) {
		this.hasPreferredWeatherCondition = hasPreferredWeatherCondition;
	}
	public Long getHasPreferredTripTime() {
		return hasPreferredTripTime;
	}
	public void setHasPreferredTripTime(Long hasPreferredTripTime) {
		this.hasPreferredTripTime = hasPreferredTripTime;
	}
	public String getHasPreferredCity() {
		return hasPreferredCity;
	}
	public void setHasPreferredCity(String hasPreferredCity) {
		this.hasPreferredCity = hasPreferredCity;
	}
	public String getHasPreferredCountry() {
		return hasPreferredCountry;
	}
	public void setHasPreferredCountry(String hasPreferredCountry) {
		this.hasPreferredCountry = hasPreferredCountry;
	}
	public Long getHasPreferredTripDuration() {
		return hasPreferredTripDuration;
	}
	public void setHasPreferredTripDuration(Long hasPreferredTripDuration) {
		this.hasPreferredTripDuration = hasPreferredTripDuration;
	}
	public Long getHasPreferredMinTimeOfAccompany() {
		return hasPreferredMinTimeOfAccompany;
	}
	public void setHasPreferredMinTimeOfAccompany(
			Long hasPreferredMinTimeOfAccompany) {
		this.hasPreferredMinTimeOfAccompany = hasPreferredMinTimeOfAccompany;
	}
    
    
	
}

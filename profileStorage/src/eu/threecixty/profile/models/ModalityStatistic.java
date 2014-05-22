package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.*;
 
/**
 * Modality statistic of a trip, a number of trips, or a time period, 
 * extended with a number of context variables such as travel objective or weather condition. 
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.ModalityStatistic
 * @author Mobidot
 *
 */
public class ModalityStatistic {
    @Description(hasText = "Chosen modality")
    private ModalityType hasModalityType;
    @Description(hasText = "Travel objective")
    private TravelObjectiveType hasTravelObjectiveType;
    @Description(hasText = "Origin destination on name of type level, e.g. 'home-office','office-home'")
    private String hasOriginDestinationPair=""; 
    @Description(hasText = "Standardized weather condition string, based on the trip weather")
    private String hasWeatherCondition=""; 
    @Description(hasText = "Indicates whether this trip was during rush hour")
    private Boolean hasInRushHour; 
    @Description(hasText = "Total count of trips in this category")
    private Long hasTotalcount; 
    @Description(hasText = "Total time, in seconds")
    private Long hasTotalTime; 
    @Description(hasText = "Total distance, in meter")
    private Double hasTotalDistance=0.0; 
    @Description(hasText = "Average speed, in meter per second")
    private Double hasAverageSpeed=0.0; 
    @Description(hasText = "Total time lost relative to fastest time for a trip, in seconds")
    private Long hasTotalLostTime; 
    @Description(hasText = "Total cost of the travels, in Euros")
    private Double hasTotalCost=0.0; 
    @Description(hasText = "Total calories spent travelling")
    private Double hasTotalCalories;
	public ModalityType getHasModalityType() {
		return hasModalityType;
	}
	public void setHasModalityType(ModalityType hasModalityType) {
		this.hasModalityType = hasModalityType;
	}
	public TravelObjectiveType getHasTravelObjectiveType() {
		return hasTravelObjectiveType;
	}
	public void setHasTravelObjectiveType(TravelObjectiveType hasTravelObjectiveType) {
		this.hasTravelObjectiveType = hasTravelObjectiveType;
	}
	public String getHasOriginDestinationPair() {
		return hasOriginDestinationPair;
	}
	public void setHasOriginDestinationPair(String hasOriginDestinationPair) {
		this.hasOriginDestinationPair = hasOriginDestinationPair;
	}
	public String getHasWeatherCondition() {
		return hasWeatherCondition;
	}
	public void setHasWeatherCondition(String hasWeatherCondition) {
		this.hasWeatherCondition = hasWeatherCondition;
	}
	public Boolean getHasInRushHour() {
		return hasInRushHour;
	}
	public void setHasInRushHour(Boolean hasInRushHour) {
		this.hasInRushHour = hasInRushHour;
	}
	public Long getHasTotalcount() {
		return hasTotalcount;
	}
	public void setHasTotalcount(Long hasTotalcount) {
		this.hasTotalcount = hasTotalcount;
	}
	public Long getHasTotalTime() {
		return hasTotalTime;
	}
	public void setHasTotalTime(Long hasTotalTime) {
		this.hasTotalTime = hasTotalTime;
	}
	public Double getHasTotalDistance() {
		return hasTotalDistance;
	}
	public void setHasTotalDistance(Double hasTotalDistance) {
		this.hasTotalDistance = hasTotalDistance;
	}
	public Double getHasAverageSpeed() {
		return hasAverageSpeed;
	}
	public void setHasAverageSpeed(Double hasAverageSpeed) {
		this.hasAverageSpeed = hasAverageSpeed;
	}
	public Long getHasTotalLostTime() {
		return hasTotalLostTime;
	}
	public void setHasTotalLostTime(Long hasTotalLostTime) {
		this.hasTotalLostTime = hasTotalLostTime;
	}
	public Double getHasTotalCost() {
		return hasTotalCost;
	}
	public void setHasTotalCost(Double hasTotalCost) {
		this.hasTotalCost = hasTotalCost;
	}
	public Double getHasTotalCalories() {
		return hasTotalCalories;
	}
	public void setHasTotalCalories(Double hasTotalCalories) {
		this.hasTotalCalories = hasTotalCalories;
	}
	
    
}

